package com.example.wseety.token;

import com.example.wseety.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(unique = true)
  public String token;

  @Enumerated(EnumType.STRING)
  public TokenType tokenType = TokenType.BEARER;

  private String kkk ;
  public boolean revoked;

  public boolean expired;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @JsonIgnore
  public User user;
}
