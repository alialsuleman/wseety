package com.example.wseety.token;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.wseety.token.Token;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenRepository extends JpaRepository<Token, UUID> {

  @Query(value = """
      select t from Token t inner join User u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
  List<Token> findAllValidTokenByUser(UUID id);

  Optional<Token> findByToken(String token);


  @Transactional
  @Modifying
  @Query("DELETE FROM Token t WHERE t.token = :token")
  void deleteByToken(@Param("token") String token);

}
