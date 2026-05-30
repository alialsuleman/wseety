package com.example.wseety.otp;


import com.example.wseety.exceptionHandler.exception.BadRequestException;
import com.example.wseety.exceptionHandler.exception.TooManyRequestsException;
import com.example.wseety.mail.EmailService;
import com.example.wseety.user.UserRepository;
import com.example.wseety.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TooManyListenersException;

@Service
@AllArgsConstructor
public class OtpService {

    private final OtpProperties otpProperties;
    private final OtpRepository otpRepository ;
    private final UserRepository userRepository;

    private final EmailService emailService;

    private static final SecureRandom secureRandom = new SecureRandom();

    public SendOtpResponse sendotp (SendOtpRequest sendOtpRequest) throws TooManyListenersException {

        User user =checkingForAnExistingUser(sendOtpRequest.getEmail()) ;
        System.out.println("a");
        SendOtpResponse  sendOtpResponse = canRequestNewOtp(sendOtpRequest) ;
        System.out.println("b");

        String otpCode = generateNumericOtp(otpProperties.getLength()) ;
        System.out.println("c");

        Otp otp = Otp.builder()
                .email(sendOtpRequest.getEmail())
                .otp(otpCode)
                .attempts(0)
                .sendCount(1)
                .expiresAt(sendOtpResponse.getExpiredAt())
                .lastSentAt(LocalDateTime.now())
                .type(sendOtpRequest.getType())
                .build();

        otpRepository.save(otp) ;
        System.out.println("d");

        emailService.sendOtpEmail(sendOtpRequest.getEmail() , otpCode , sendOtpRequest.getType())  ;
        System.out.println("e");

        return sendOtpResponse ;
    }

    public CheckOtpResponse checkOtp (CheckOtpRequest checkOtpRequest)  {

        User user =checkingForAnExistingUser(checkOtpRequest.getEmail()) ;


        Otp otp = CheckForValidOtpCode(checkOtpRequest);

        if (otp.getOtp().equals(checkOtpRequest.getOtpCode())) {
            user.setEnabled(true);
            this.userRepository.save(user) ;
            return new CheckOtpResponse(true) ;
        }
        else {
            otp.setSendCount(otp.getSendCount() + 1 );
            this.otpRepository.save(otp) ;
            CheckOtpResponse checkOtpResponse = CheckOtpResponse.builder()
                    .isVerified(false)
                    .numberOfAllowedAttempts(otp.getAttempts())
                    .sendCount(otp.getSendCount())
                    .expiresAt(otp.getExpiresAt())
                    .build() ;
            throw new BadRequestException("Invalid verification code." , checkOtpResponse) ;
        }
    }


    private User checkingForAnExistingUser (String email)
    {
        Optional<User> user = this.userRepository.findByEmail(email) ;
        if (!user.isPresent())throw new BadRequestException("You must create an account first.") ;
        return user.get() ;
    }

    private Otp CheckForValidOtpCode (CheckOtpRequest checkOtpRequest)
    {

        Optional<Otp> lastOtp =  this.otpRepository.findTopByEmailAndTypeOrderByLastSentAtDesc(
                checkOtpRequest.getEmail() ,
                checkOtpRequest.getType()
        ) ;
        LocalDateTime now = LocalDateTime.now() ;
        System.out.println(lastOtp.get().getId());

        if (!lastOtp.isPresent()) throw new BadRequestException("No valid OTP found. Please request a new verification code." , new CheckOtpResponse()) ;
        Otp otp = lastOtp.get();

        if (
                otp.getAttempts() >= this.otpProperties.getRetry().getLimit() ||
                        now.isAfter(otp.getExpiresAt())
        ) {
            System.out.println(otp.getExpiresAt());
            System.out.println(now);
            System.out.println( otp.getAttempts());
            System.out.println(this.otpProperties.getRetry().getLimit());
            System.out.println("eeee");

            throw new BadRequestException("No valid OTP found. Please request a new verification code.", new CheckOtpResponse());
        }
        return otp ;

    }













    ///////////////////////////////////////////
    private SendOtpResponse canRequestNewOtp(SendOtpRequest sendOtpRequest) throws TooManyListenersException {

        Optional<Otp> lastOtp =  this.otpRepository.findTopByEmailAndTypeOrderByLastSentAtDesc(
                sendOtpRequest.getEmail() ,
                sendOtpRequest.getType()
        ) ;
        LocalDateTime now = LocalDateTime.now() ;

        if (lastOtp.isPresent()){
            Otp otp = lastOtp.get(); // استخراج القيمة
            LocalDateTime lastSent = otp.getLastSentAt();

            SendOtpResponse  sendOtpResponse = SendOtpResponse.builder()
                    .expiredAt(otp.getExpiresAt())
                    .nextResendAt(lastSent.plusSeconds(otpProperties.getRetry().getDelay().getSeconds()))
                    .build();
            System.out.println(lastSent) ;
            System.out.println(now.minusSeconds(otpProperties.getRetry().getDelay().getSeconds()));
             if (lastSent.isAfter(now.minusSeconds(otpProperties.getRetry().getDelay().getSeconds()))) {
                 throw new TooManyRequestsException( "Too many requests. Please wait before retrying.", sendOtpResponse);
             }
        }
        System.out.println(otpProperties.getExpiryMinutes() + " " + now.plusMinutes(otpProperties.getExpiryMinutes()) + " " + now );
        return SendOtpResponse.builder()
                .expiredAt(now.plusMinutes(otpProperties.getExpiryMinutes()))
                .nextResendAt(now.plusSeconds(otpProperties.getRetry().getDelay().getSeconds()))
                .build();

    }


    // Generate numeric OTP
    public String generateNumericOtp(int length) {
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            otp.append(secureRandom.nextInt(10));
        }

        return otp.toString();
    }


}
