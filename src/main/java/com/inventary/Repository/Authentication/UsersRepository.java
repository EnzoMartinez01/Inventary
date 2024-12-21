package com.inventary.Repository.Authentication;

import com.inventary.Model.Authentication.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);

    @Query("SELECT u FROM Users u WHERE " +
            "(:state IS NULL OR u.state = :state) AND " +
            "(:email IS NULL OR u.email LIKE %:email%) AND " +
            "(:username IS NULL OR u.username LIKE %:username%) AND " +
            "(:phoneNumber IS NULL OR u.phoneNumber LIKE %:phoneNumber%) AND " +
            "(:lastName IS NULL OR u.lastName LIKE %:lastName%) AND " +
            "(:name IS NULL OR u.name LIKE %:name%)")
    Page<Users> searchUsers(@Param("state") Boolean state,
                            @Param("email") String email,
                            @Param("username") String username,
                            @Param("phoneNumber") String phoneNumber,
                            @Param("lastName") String lastName,
                            @Param("name") String name,
                            Pageable pageable);
}
