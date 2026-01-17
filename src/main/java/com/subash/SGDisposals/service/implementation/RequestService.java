package com.subash.SGDisposals.service.implementation;

import com.subash.SGDisposals.RoleEnum;
import com.subash.SGDisposals.StatusEnum;
import com.subash.SGDisposals.dto.AllUserRequestDto;
import com.subash.SGDisposals.dto.CollectReqDto;
import com.subash.SGDisposals.dto.CollectedResDto;
import com.subash.SGDisposals.entity.CollectionRequest;
import com.subash.SGDisposals.entity.PointsSystem;
import com.subash.SGDisposals.entity.User;
import com.subash.SGDisposals.exception.InvalidRequestStateException;
import com.subash.SGDisposals.exception.ResourceNotFoundException;
import com.subash.SGDisposals.exception.UnauthorizedRequestException;
import com.subash.SGDisposals.repositories.CollectionRepo;
import com.subash.SGDisposals.repositories.PointsRepo;
import com.subash.SGDisposals.repositories.UserRepo;
import com.subash.SGDisposals.service.EmailService;
import com.subash.SGDisposals.service.IRequestService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RequestService implements IRequestService {

    private static final Logger log = LoggerFactory.getLogger(RequestService.class);
    private final CollectionRepo collectionRepo;
    private final UserRepo userRepo;
    private final PointsRepo pointsRepo;
    private final CollectorService collectorService;
    private final EmailService emailService;

    @Cacheable
    @Override
    public List<AllUserRequestDto> getAllRequestsForUser(Long id) {

        User user = userRepo.findById(id).orElseThrow(() -> {throw new
                UnauthorizedRequestException("Invalid User Please Check You Credentials");});
        if(user.getRole() == RoleEnum.USER){
            List<CollectionRequest> result = collectionRepo.findByUserAndDeletedFalse(user).orElseThrow();
            return result.stream().map(item -> {
                AllUserRequestDto allUserRequestDto = new AllUserRequestDto();
                allUserRequestDto.setName(item.getUser().getName());
                allUserRequestDto.setUser_id(item.getUser().getId());
                allUserRequestDto.setId(item.getId());
                allUserRequestDto.setAddress(item.getAddress());
                return allUserRequestDto;
            }).toList();
        }

        else{
            throw new ResourceNotFoundException("No Request for this User");
        }
    }

    @Cacheable("requestforcollector")
    @Override
    public List<AllUserRequestDto> getAllRequestsForCollector() {
        log.info("Entering getAllRequestsForCollector - cacheable");
        List<CollectionRequest> result = collectionRepo.findByStatusAndDeletedFalse(StatusEnum.REQUESTED).orElseThrow();
        return result.stream().map(item -> {
            AllUserRequestDto allUserRequestDto = new AllUserRequestDto();
            allUserRequestDto.setStatus(item.getStatus());
            allUserRequestDto.setCreated_at(Instant.now());
            allUserRequestDto.setName(item.getUser().getName());
            allUserRequestDto.setUser_id(item.getUser().getId());
            allUserRequestDto.setId(item.getId());
            allUserRequestDto.setAddress(item.getAddress());
            return allUserRequestDto;
        }).toList();
    }

    protected Long calculatePoint(Map<String, Long> wastes) {
        List<PointsSystem> points_sys = pointsRepo.findAll();

        if(points_sys.isEmpty()){
            throw new ResourceNotFoundException("No Points System Data Found At the Moment");
        }

        final long[] points = {0};

        wastes.forEach((key, value) ->
        {
            boolean exists = points_sys.stream() .anyMatch(type -> type.getType().equals(key));
            if (!exists) {
                throw new InvalidRequestStateException("Invalid Waste type in Body: " + key);
            }
        }
        );

        wastes.forEach((key, value) -> {
            points_sys.forEach(type -> {
                if (type.getType().equals(key)) {
                    points[0] += value * type.getPoints();
                }
            });
        });
        return points[0];
    }

    @CacheEvict(value = "requestforcollector", allEntries = true)
    @Transactional
    public CollectedResDto collectAndMark(CollectReqDto dto) {
        Long points = collectRequest(dto);
        CollectedResDto res = collectorService.markAsCollected(dto.getCollection_id(), dto.getCollector_id());
        res.setPoints(points);
        return res;
    }


    @Transactional(readOnly = false)
    @Override
    public Long collectRequest(CollectReqDto collectReqDto) {

        User collector = userRepo.findById(collectReqDto.getCollector_id()).orElseThrow();
        if (collector.getRole() != RoleEnum.COLLECTOR || collector.getDeleted()){
            throw new UnauthorizedRequestException("You are not allowed to collect this request");
        }

        User user = userRepo.findById(collectReqDto.getUser_id()).orElseThrow(
                () -> {throw new UnauthorizedRequestException("User Not Found for this Credentials");}
        );

        if(user.getDeleted()){
            throw new UnauthorizedRequestException("This User has been deleted Already!");
        }

        CollectionRequest collectionRequest = collectionRepo.findById(collectReqDto.getCollection_id()).orElseThrow();
        if (collectionRequest.getStatus() != StatusEnum.REQUESTED){
            throw new InvalidRequestStateException("Invalid request To Process");
        }

        if(collectionRequest.getDeleted()){
            throw new InvalidRequestStateException("Request has been deleted Already");
        }

        Long points = calculatePoint(collectReqDto.getWeights());
        Long pointsToUpdate = points + user.getPoints();
        user.setPoints(Math.toIntExact(pointsToUpdate));
        userRepo.save(user);

        emailService.sendCollectionRecipt(user.getEmail(),collectReqDto.getWeights(), Math.toIntExact(pointsToUpdate));

        return points;
    }
}