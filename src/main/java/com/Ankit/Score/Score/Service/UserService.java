package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Payloads.OrderHistoryDto;
import com.Ankit.Score.Score.Payloads.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto updateUser(UserDto userDto, Long userId);
    UserDto getUserById(Long userId);
    List<UserDto> getAllUser();
     List<OrderHistoryDto> getUserOrderHistory(Long userId);
}
