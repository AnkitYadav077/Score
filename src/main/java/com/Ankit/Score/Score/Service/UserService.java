package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Payloads.UserDto;
import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);
    UserDto updateUser(UserDto user, Long userId);
    UserDto getUserById(Long userId);
    List<UserDto> getAllUser();
}
