package com.subash.SGDisposals.service;

import com.subash.SGDisposals.dto.*;
import com.subash.SGDisposals.entity.Order;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IUserService {

    AddUserResDto addUser(UserRegisterReqDto userRegisterReqDto);
    CancelReqResDto cancelRequest(Long id, Long user_id);
    AddNewReqResDto addNewRequest(AddNewRequestDto addNewRequestDto);
    UserLoginresDto loginuser(UserLoginReqDto userLoginReqDto);
    int getUserPoints(Long id);
    List<Order> getAllOrdersForUser(Long user_id);
    ProfileResDto getUserProfile(Long user_id);
}
