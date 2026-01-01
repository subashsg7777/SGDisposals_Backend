package com.subash.SGDisposals.service.implementation;

import com.subash.SGDisposals.RoleEnum;
import com.subash.SGDisposals.StatusEnum;
import com.subash.SGDisposals.dto.*;
import com.subash.SGDisposals.entity.CollectionRequest;
import com.subash.SGDisposals.entity.Order;
import com.subash.SGDisposals.entity.User;
import com.subash.SGDisposals.exception.InvalidRequestStateException;
import com.subash.SGDisposals.exception.ResourceNotFoundException;
import com.subash.SGDisposals.exception.UnauthorizedRequestException;
import com.subash.SGDisposals.repositories.CollectionRepo;
import com.subash.SGDisposals.repositories.OrderRepo;
import com.subash.SGDisposals.repositories.UserRepo;
import com.subash.SGDisposals.service.IUserService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class UserService implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepo userRepo;
    private final CollectionRepo collectionRepo;
    private final OrderRepo ordersRepo;

    @Transactional
    @Override
    public AddUserResDto addUser(UserRegisterReqDto userRegisterReqDto) {
        AddUserResDto  addUserResDto = new AddUserResDto();
        try{
            User user = new User();
            BeanUtils.copyProperties(userRegisterReqDto, user);
            user.setCreatedAt(Instant.now());
            user.setPoints(0);
            userRepo.save(user);
            addUserResDto.setMessage("User has been successfully registered!");
            addUserResDto.setName(user.getName());
            addUserResDto.setEmail(user.getEmail());
            addUserResDto.setRole(user.getRole());
            addUserResDto.setId(user.getId());
            return addUserResDto;
        }

        catch(Exception e){
            addUserResDto.setName(userRegisterReqDto.getName());
            addUserResDto.setEmail(userRegisterReqDto.getEmail());
            addUserResDto.setRole(userRegisterReqDto.getRole());
            addUserResDto.setMessage("Error in adding user "+ e.getMessage());
            return addUserResDto;
        }
    }

    @Transactional
    @Override
    public CancelReqResDto cancelRequest(Long id, Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CollectionRequest request = collectionRepo.findByUserAndId(user, id);

        if (request == null) {
            throw new ResourceNotFoundException("Request not found");
        }

        if (request.getDeleted()){
            throw new InvalidRequestStateException("Request has been deleted!");
        }

        if (user.getRole() != RoleEnum.USER) {
            throw  new UnauthorizedRequestException("Only USER can cancel requests");
        }

        if (request.getStatus() == StatusEnum.CANCELLED) {
            throw new InvalidRequestStateException("Request already cancelled");
        }

        if (request.getStatus() != StatusEnum.REQUESTED) {
            throw new UnauthorizedRequestException("Only REQUESTED requests can be cancelled");
        }

        CancelReqResDto  cancelReqResDto = new CancelReqResDto();
        request.setStatus(StatusEnum.CANCELLED);
        collectionRepo.save(request);

        cancelReqResDto.setMessage("Request has been cancelled!");
        cancelReqResDto.setId(user.getId());
        return cancelReqResDto;
    }

    @Transactional
    @Override
    public AddNewReqResDto addNewRequest(AddNewRequestDto addNewRequestDto) {

        Optional<User> user = userRepo.findById(addNewRequestDto.getUser());
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        if(user.get().getDeleted()){
            throw new UnauthorizedRequestException("user has been deleted!");
        }

        if(user.get().getRole() != RoleEnum.USER){
            throw  new UnauthorizedRequestException("Only USER can cancel requests");
        }

        else{
            AddNewReqResDto addNewReqResDto = new AddNewReqResDto();
            CollectionRequest coreRequest = new CollectionRequest();
            BeanUtils.copyProperties(addNewRequestDto, coreRequest);
            coreRequest.setUser(user.orElseThrow());
            coreRequest.setCreatedAt(Instant.now());
            coreRequest.setUpdatedAt(Instant.now());
            coreRequest.setDeleted(false);
            coreRequest.setStatus(StatusEnum.REQUESTED);

            collectionRepo.save(coreRequest);
            addNewReqResDto.setMessage("Request has been added!");
            addNewReqResDto.setUser_id(user.get().getId());
            addNewReqResDto.setAddress(addNewRequestDto.getAddress());
            return  addNewReqResDto;
        }

    }


    @Override
    public UserLoginresDto loginuser(UserLoginReqDto userLoginReqDto) {
        UserLoginresDto userLoginresDto = new UserLoginresDto();
        Optional <User> user = Optional.ofNullable(userRepo.findByEmail(userLoginReqDto.getEmail()).orElseThrow(() -> {
            throw new UnauthorizedRequestException("No User Found For This Email");
        }));

        if(user.isPresent()){
            if(user.get().getPassword().equals(userLoginReqDto.getPassword())){
                BeanUtils.copyProperties(user.get(), userLoginresDto);
                userLoginresDto.setMessage("User has been successfully logged in!");
                userLoginresDto.setId(user.get().getId());
                return userLoginresDto;
            }
        }
        else{
            throw new UnauthorizedRequestException("No User Found For This Email");
        }
        return userLoginresDto;
    }

    @Transactional(readOnly = false)
    @Override
    public int getUserPoints(Long id) {

        List<User> userPoints = userRepo.findAllById(Collections.singleton(id));
        int points = userPoints.get(0).getPoints();
        return  points;
    }

    @Override
    public List<Order> getAllOrdersForUser(Long user_id) {

        User user = userRepo.findById(user_id).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        List<Order> orders = ordersRepo.findByuserId(user.getId());
        if(orders.size() <1) {
            throw new ResourceNotFoundException("No Orders Placed For This User Account");
        }
        return orders;
    }
}
