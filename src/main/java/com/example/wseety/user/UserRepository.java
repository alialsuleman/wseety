package com.example.wseety.user ;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


import com.example.wseety.user.entity.Role;
import com.example.wseety.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);
  List<User> findByRole(Role role);
  long countByRole(Role role);
  @Query("""
    SELECT u FROM User u
    WHERE
    (:keyword IS NULL OR
     LOWER(u.firstname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
     LOWER(u.lastname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
     LOWER(CONCAT(u.firstname, ' ', COALESCE(u.lastname, '')))
     LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
  Page<User> flexibleSearch(
            @Param("keyword") String keyword,
            Pageable pageable
  );

}
