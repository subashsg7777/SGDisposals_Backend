package com.subash.SGDisposals.controller;

import com.subash.SGDisposals.dto.*;
import com.subash.SGDisposals.entity.Order;
import com.subash.SGDisposals.service.IRequestService;
import com.subash.SGDisposals.service.IUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("api/v2/user/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final IUserService userService;
    private final IRequestService requestService;

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
}
