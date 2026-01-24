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
import com.subash.SGDisposals.repositories.ProductRepo;
import com.subash.SGDisposals.repositories.UserRepo;
import com.subash.SGDisposals.service.EmailService;
import com.subash.SGDisposals.service.IUserService;
import com.subash.SGDisposals.util.JwtUtil;
import com.subash.SGDisposals.util.OtpManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class UserService implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepo userRepo;
    private final CollectionRepo collectionRepo;
    private final OrderRepo ordersRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepo productRepo;
    private final EmailService emailService;
    private final OtpManager otpManager;

    @Transactional
    @Override
    public AddUserResDto addUser(UserRegisterReqDto userRegisterReqDto) {
        AddUserResDto  addUserResDto = new AddUserResDto();
        try{
            User user = new User();
            String hashedPassword = passwordEncoder.encode(userRegisterReqDto.getPassword());
            userRegisterReqDto.setPassword(hashedPassword);
            BeanUtils.copyProperties(userRegisterReqDto, user);
            user.setTransactional_password(userRegisterReqDto.getTransactionalPassword());
            user.setCreatedAt(LocalDateTime.now());
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

    @CacheEvict(value = "requestforcollector")
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

    @CacheEvict(value = "requestforcollector", allEntries = true)
    @Transactional
    @Override
    public AddNewReqResDto addNewRequest(AddNewRequestDto addNewRequestDto) {

        User user = userRepo.findById(addNewRequestDto.getUser()).orElseThrow(() -> {throw new UnauthorizedRequestException("No User Found");});

        if(user.getDeleted()){
            throw new UnauthorizedRequestException("user has been deleted!");
        }

        if(user.getRole() != RoleEnum.USER){
            throw  new UnauthorizedRequestException("Only USER can cancel requests");
        }

        else{
            AddNewReqResDto addNewReqResDto = new AddNewReqResDto();
            CollectionRequest coreRequest = new CollectionRequest();
            BeanUtils.copyProperties(addNewRequestDto, coreRequest);
            coreRequest.setUser(user);
            coreRequest.setCreatedAt(LocalDateTime.now());
            coreRequest.setUpdatedAt(LocalDateTime.now());
            coreRequest.setDeleted(false);
            coreRequest.setStatus(StatusEnum.REQUESTED);

            collectionRepo.save(coreRequest);
            addNewReqResDto.setMessage("Request has been added!");
            addNewReqResDto.setUser_id(user.getId());
            addNewReqResDto.setAddress(addNewRequestDto.getAddress());

            emailService.sendRequestAck(user.getEmail(),addNewRequestDto.getAddress());

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
            if(passwordEncoder.matches(userLoginReqDto.getPassword(),user.get().getPassword())){
                String token = jwtUtil.generateToken(userLoginReqDto.getEmail(),user.get().getRole());
                BeanUtils.copyProperties(user.get(), userLoginresDto);
                userLoginresDto.setToken(token);
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

    @Override
    public ProfileResDto getUserProfile(Long user_id) {
        User user = userRepo.findById(user_id)
                .orElseThrow(() -> new UnauthorizedRequestException("No User Found"));

        List<Order> orders = ordersRepo.findByuserId(user_id);

        float points = 0;

        if (!orders.isEmpty()) {
            points += (float) orders.stream()
                    .mapToDouble(item -> {
                        long id = Long.parseLong(String.valueOf(item.getProductId().intValue()));
                        return productRepo.findById(id)
                                .orElseThrow(() -> new RuntimeException("Product not found"))
                                .getPoints() * item.getQuanity();
                    })
                    .sum();
        }
        points += user.getPoints();
        ProfileResDto profileResDto = new ProfileResDto();
        profileResDto.setUser_name(user.getName());
        profileResDto.setEmail(user.getEmail());
        profileResDto.setRole(user.getRole());
        profileResDto.setJoined_at(user.getCreatedAt().toLocalDate());
        profileResDto.setTotal_points(points);
        profileResDto.setCurrent_points(user.getPoints());
        return profileResDto;
    }

    @Override
    public boolean sendOTP(String email) {

    int otp = otpManager.generateRandomOtp(email);
    log.info("The Generated OTP is : "+ otp);
        try{
            emailService.sendOtp(email,String.valueOf(otp));
            return true;
        }

        catch (Exception ee){
            throw new InvalidRequestStateException("Failed To Send Verification Email");
        }
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        return otpManager.verifyOtp(email,otp);
    }

    @Override
    public boolean forgot(String email) {
        try{
            int otp = otpManager.generateRandomOtp(email);
            emailService.forgotOTP(email,String.valueOf(otp));

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String verifyForgot(String email, String otp) {
        boolean res = otpManager.verifyForgot(email,otp);
        if(res){
            User user = userRepo.findByEmail(email).orElseThrow(() -> {throw new UnauthorizedRequestException("No Account Found for this Credentials");});
            String token = jwtUtil.generateToken(email,user.getRole());
            return token;
        }
        return "";
    }

    @Override
    public boolean updatePassword(String email, String password) {
        try{
            User user = userRepo.findByEmail(email).orElseThrow(() -> {throw new UnauthorizedRequestException("No User Found");});
            String hashedPassword = passwordEncoder.encode(password);
            user.setPassword(hashedPassword);
            userRepo.save(user);
            return  true;
        }
        catch (Exception e) {
            log.error(e.toString());
            return false;
        }
    }
}
