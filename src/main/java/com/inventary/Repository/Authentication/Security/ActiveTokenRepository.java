package com.inventary.Repository.Authentication.Security;

import com.inventary.Model.Authentication.Security.ActiveToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActiveTokenRepository extends JpaRepository<ActiveToken, Long> {
    Optional<ActiveToken> findByToken(String token);
    void deleteByToken(String token);
}
