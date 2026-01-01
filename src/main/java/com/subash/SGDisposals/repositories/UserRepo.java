package com.subash.SGDisposals.repositories;

import com.subash.SGDisposals.entity.User;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long>{

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findById(Long id);

    Optional <User> findByEmail(@NotBlank @Email String email);
}
