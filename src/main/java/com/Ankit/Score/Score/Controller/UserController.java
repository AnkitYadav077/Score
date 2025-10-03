package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.OrderHistoryDto;
import com.Ankit.Score.Score.Payloads.UserDto;
import com.Ankit.Score.Score.Security.JwtHelper;
import com.Ankit.Score.Score.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtHelper jwtHelper;

    private Long getAuthenticatedUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtHelper.getUserIdFromToken(token);
        }
        throw new RuntimeException("Invalid token");
    }

    // Create user - Public access (for registration)
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // Update my profile - User can update themselves using token
    @PutMapping("/my-profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDto> updateMyProfile(
            @RequestBody UserDto userDto,
            HttpServletRequest request) {
        Long userId = getAuthenticatedUserId(request);
        UserDto updatedUser = userService.updateUser(userDto, userId);
        return ResponseEntity.ok(updatedUser);
    }

    // Get my profile - User can view themselves using token
    @GetMapping("/my-profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDto> getMyProfile(HttpServletRequest request) {
        Long userId = getAuthenticatedUserId(request);
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(userDto);
    }

    // Get all users - Only Admin can view all users
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUB_ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUser();
        return ResponseEntity.ok(users);
    }

    // Get my order history - User can view their own history using token
    @GetMapping("/my-order-history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderHistoryDto>> getMyOrderHistory(HttpServletRequest request) {
        Long userId = getAuthenticatedUserId(request);
        List<OrderHistoryDto> orderHistory = userService.getUserOrderHistory(userId);
        return ResponseEntity.ok(orderHistory);
    }

    // Admin can get any user by ID
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUB_ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(userDto);
    }
}