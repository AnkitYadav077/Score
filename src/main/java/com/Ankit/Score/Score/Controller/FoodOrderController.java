package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.FoodOrderDto;
import com.Ankit.Score.Score.Service.FoodOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class FoodOrderController {

    @Autowired
    private FoodOrderService foodOrderService;

    // Create a new food order
    @PostMapping
    public ResponseEntity<FoodOrderDto> createOrder(@RequestBody FoodOrderDto foodOrderDto) {
        FoodOrderDto createdOrder = foodOrderService.createOrder(foodOrderDto);
        return ResponseEntity.status(201).body(createdOrder);
    }

    // Get a specific order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<FoodOrderDto> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(foodOrderService.getOrderById(orderId));
    }

    // Get all orders
    @GetMapping
    public ResponseEntity<List<FoodOrderDto>> getAllOrders() {
        return ResponseEntity.ok(foodOrderService.getAllOrders());
    }

    // Get orders for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FoodOrderDto>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(foodOrderService.getOrdersByUser(userId));
    }

    // Update an order
    @PutMapping("/{orderId}")
    public ResponseEntity<FoodOrderDto> updateOrder(
            @PathVariable Long orderId,
            @RequestBody FoodOrderDto foodOrderDto
    ) {
        return ResponseEntity.ok(foodOrderService.updateOrder(orderId, foodOrderDto));
    }
}
