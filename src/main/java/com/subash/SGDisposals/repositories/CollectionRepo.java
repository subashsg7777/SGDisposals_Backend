package com.subash.SGDisposals.repositories;

import com.subash.SGDisposals.StatusEnum;
import com.subash.SGDisposals.dto.AllUserRequestDto;
import com.subash.SGDisposals.entity.CollectionRequest;
import com.subash.SGDisposals.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRepo extends JpaRepository<CollectionRequest,Long> {

    Optional<List<CollectionRequest>> findByUserAndDeletedFalse(User user);

    Optional<List<CollectionRequest>> findByStatusAndDeletedFalse(@NotNull StatusEnum status);
    CollectionRequest findByUserAndId(User user, Long id);
}
