package com.subash.SGDisposals.controller;

import com.subash.SGDisposals.dto.*;
import com.subash.SGDisposals.entity.Order;
import com.subash.SGDisposals.exception.InvalidRequestStateException;
import com.subash.SGDisposals.service.EmailService;
import com.subash.SGDisposals.service.IRequestService;
import com.subash.SGDisposals.service.IUserService;
import com.subash.SGDisposals.util.OtpManager;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("api/v2/user/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final IUserService userService;
    private final IRequestService requestService;
    private final OtpManager otpManager;
    private final EmailService emailService;

    @PostMapping("add-user")
    private ResponseEntity<AddUserResDto> addUser(@Valid  @RequestBody UserRegisterReqDto userRegisterReqDto) {

        AddUserResDto response = userService.addUser(userRegisterReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("requests")
    public ResponseEntity<List<AllUserRequestDto>> getAllRequestOfaUser(@RequestParam Long id){
        List<AllUserRequestDto> response = requestService.getAllRequestsForUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginReqDto userLoginReqDto){
        UserLoginresDto result = userService.loginuser(userLoginReqDto);
            return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @PutMapping("cancel")
    public ResponseEntity<CancelReqResDto> cancelRequest(@RequestParam Long id,@RequestParam Long user_id){

        CancelReqResDto response = userService.cancelRequest(id,user_id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("add-request")
    public ResponseEntity<AddNewReqResDto> addNewRequest(@Valid @RequestBody AddNewRequestDto  addNewRequestDto){

        AddNewReqResDto  response = userService.addNewRequest(addNewRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("Get-points")
    public ResponseEntity<Integer> getUserPoints(@RequestParam Long id){
        int userPoints = userService.getUserPoints(id);
        return ResponseEntity.status(HttpStatus.OK).body(userPoints);
    }

    @GetMapping("Get-Orders")
    public ResponseEntity<List<Order>> getAllOrdersForUsers(@Validated @NotNull @RequestParam Long user_id){

        List<Order> orders = userService.getAllOrdersForUser(user_id);
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }

    @GetMapping("Get-Profile")
    public ResponseEntity<ProfileResDto> getUserProfile(@Validated @NotNull @RequestParam Long user_id){
        ProfileResDto resposnse = userService.getUserProfile(user_id);
        return ResponseEntity.status(HttpStatus.OK).body(resposnse);
    }

    @GetMapping("verify-email")
    public boolean verifyEmailUsingOTP(@Validated @NotBlank @RequestParam String email){

        boolean result = userService.sendOTP(email);
        if(result){
            return true;
        }
        throw new InvalidRequestStateException("Something went wrong");
    }

    @PostMapping("verify-otp")
    public boolean verifyEmailOtp(@Validated @NotBlank @RequestParam String email, @Validated @NotBlank @RequestParam String otp){
        return userService.verifyOtp(email,otp);
    }

    @GetMapping("forgot")
    public ResponseEntity<String> forgotOtpRequest(@Validated @NotBlank @Email @RequestParam String email){
        boolean res = userService.forgot(email);
        if(res){
            return ResponseEntity.status(HttpStatus.OK).body("Otp Sent");
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Can't Send OTP");
    }

    @PostMapping("verify-forgot")
    public ResponseEntity<String> verifyForgot(@Validated @NotBlank @Email @RequestParam String email,
                                @Validated @NotBlank @RequestParam String otp){

        String result = userService.verifyForgot(email,otp);
        if(result != ""){
            return  ResponseEntity.status(HttpStatus.OK).body(result);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Can't Verify Otp Right Now");
    }

    @PutMapping("update-password")
    public ResponseEntity<?> updateUserPassword(@Valid @RequestBody ChangePasswordReqDto changePasswordReqDto){
        boolean res = userService.updatePassword(changePasswordReqDto.getEmail(),changePasswordReqDto.getPassword());
        if(res){
            return ResponseEntity.status(HttpStatus.CREATED).body(true);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can't Update Your Password Right Now !...");
    }
}
