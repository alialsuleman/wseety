package com.example.wseety.user.entity;

import com.example.wseety.token.Token;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

    // ─────────────────────────────────────────────
    //  Identity
    // ─────────────────────────────────────────────

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstname;
    private String lastname;

    @Column(name = "image_path")
    private String imagePath;


    // ─────────────────────────────────────────────
    //  Credentials & Role
    // ─────────────────────────────────────────────

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email required")
    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled;

    private String countryCode ;
    private String internationalPhoneNumber ;

    // ─────────────────────────────────────────────
    //  Relations
    // ─────────────────────────────────────────────



    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Token> tokens;



    // ─────────────────────────────────────────────
    //  UserDetails
    // ─────────────────────────────────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}