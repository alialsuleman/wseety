package com.example.wseety.auth;


import com.example.wseety.auth.dto.AuthenticationRequest;
import com.example.wseety.auth.dto.AuthenticationResponse;
import com.example.wseety.auth.dto.RegisterRequest;
import com.example.wseety.config.JwtService;
import com.example.wseety.exceptionHandler.exception.BadRequestException;
import com.example.wseety.token.Token;
import com.example.wseety.token.TokenRepository;
import com.example.wseety.token.TokenType;
import com.example.wseety.user.UserRepository;
import com.example.wseety.user.dto.ManagerResponse;
import com.example.wseety.user.entity.Role;
import com.example.wseety.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

   public AuthenticationResponse registerAsUser(RegisterRequest request) {


        request.setRole(Role.USER);
        var lastUser = repository.findByEmail(request.getEmail()) ;

        if (lastUser.isPresent() && request.getRole() != Role.ADMIN  )
        {
           throw new BadRequestException("This email already exists.")  ;
        }

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(false)
                .build();

        User savedUser = repository.save(user);

        var authenticationResponse = AuthenticationResponse
                .builder()
                .accessToken(null)
                .refreshToken(null)
                .verified(user.isEnabled())
                .build();

        return  authenticationResponse ;
  }


  @Transactional
  public void deleteManager(UUID managerId) {
      User user = repository.findById(managerId)
              .orElseThrow(() -> new RuntimeException("User not found with id: " + managerId));

      if (user.getRole() != Role.MANAGER) {
          throw new RuntimeException("User is not a manager");
      }

      repository.delete(user);
  }



  public List<ManagerResponse> getAllManagers() {
      List<User> managers = repository.findByRole(Role.MANAGER);
      return managers.stream()
              .map(this::mapToManagerResponse)
              .collect(Collectors .toList());
  }



  private ManagerResponse mapToManagerResponse(User user) {
        return ManagerResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .imagePath(user.getImagePath())
                .role(user.getRole().name())
                .enabled(user.isEnabled())
                .build();
  }
  public AuthenticationResponse authenticate(AuthenticationRequest request) {

     authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );

    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();

    var jwtToken = jwtService.generateToken(user , false);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken , false);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .verified(user.isEnabled())
        .build();
  }


  public AuthenticationResponse changePasswordToken (String email)
  {
      var user = repository.findByEmail(email)
              .orElseThrow();

      var jwtToken = jwtService.generateToken(user , true);
      revokeAllUserTokens(user);
      saveUserToken(user, jwtToken , true);
      return AuthenticationResponse.builder()
              .accessToken(jwtToken)
              .refreshToken(null)
              .verified(user.isEnabled())
              .build();

  }

  // create token
  public void saveUserToken(User user, String jwtToken ,boolean resetPassword) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .resetPassword(resetPassword)
        .build();
    tokenRepository.save(token);
  }







  public AuthenticationResponse refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  )  {

    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return null;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);

    if (userEmail == null) throw new BadRequestException("the refreshToken invalid") ;
    var user = this.repository.findByEmail(userEmail).orElseThrow();

    if (!jwtService.isTokenValid(refreshToken, user)) throw new BadRequestException("the refreshToken invalid") ;
    var accessToken = jwtService.generateToken(user ,false);
    revokeAllUserTokens(user);
    saveUserToken(user, accessToken, false);
    var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .verified(user.isEnabled())
                .build();

    return authResponse ;

  }


  // make all old token Expired and Revoked
  public void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      tokenRepository.delete(token);
    });
  }

    public AuthenticationResponse createJwtResponse  (String email){
        var user = repository.findByEmail(email)
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user , false);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken , false);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .verified(user.isEnabled())
                .build();
    }




}
