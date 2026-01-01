package com.subash.SGDisposals.service.implementation;

import com.subash.SGDisposals.RoleEnum;
import com.subash.SGDisposals.StatusEnum;
import com.subash.SGDisposals.dto.CollectedResDto;
import com.subash.SGDisposals.entity.CollectionRequest;
import com.subash.SGDisposals.entity.User;
import com.subash.SGDisposals.exception.InvalidRequestStateException;
import com.subash.SGDisposals.exception.ResourceNotFoundException;
import com.subash.SGDisposals.repositories.CollectionRepo;
import com.subash.SGDisposals.repositories.UserRepo;
import com.subash.SGDisposals.service.ICollectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CollectorService implements ICollectorService {

    private final UserRepo userRepo;
    private final CollectionRepo  collectionRepo;

    @Transactional(readOnly = false)
    @Override
    public CollectedResDto markAsCollected(Long id, Long user_id) {

        User user = userRepo.findById(user_id).orElse(null);
        if (user.getRole() == RoleEnum.COLLECTOR && !user.getDeleted()) {
            CollectionRequest request = collectionRepo.findById(id).orElse(null);

            if (request.getStatus() == StatusEnum.COLLECTED) {
                throw new InvalidRequestStateException("Collector already collected");
            }

            if (request != null && request.getStatus() == StatusEnum.REQUESTED) {
                CollectedResDto  collectedResDto = new CollectedResDto();
                request.setStatus(StatusEnum.COLLECTED);
                collectionRepo.save(request);
                collectedResDto.setRequest_id(request.getId());
                collectedResDto.setMessage("Now Changed to Collected Status");
                collectedResDto.setCollector(user.getName());
                return  collectedResDto;
            }

            else{
                throw new ResourceNotFoundException("No User Found For that Credentials Or Not Suitable Request Status");
            }
        }
        throw new InvalidRequestStateException("You Are Not Eligible For this Operation");
    }
}
