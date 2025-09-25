package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.SportSlotDto;
import com.Ankit.Score.Score.Service.SportSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class SportSlotController {

    @Autowired
    private SportSlotService sportSlotService;

    // Create slot - Only Admin can create slots
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUB_ADMIN')")
    public ResponseEntity<SportSlotDto> createSlot(@RequestBody SportSlotDto dto) {
        return ResponseEntity.ok(sportSlotService.createSlot(dto));
    }

    // Get all slots - Public access
    @GetMapping
    public ResponseEntity<List<SportSlotDto>> getAllSlots() {
        return ResponseEntity.ok(sportSlotService.getAllSlots());
    }

    // Get slot by ID - Public access
    @GetMapping("/{id}")
    public ResponseEntity<SportSlotDto> getSlot(@PathVariable Long id) {
        return ResponseEntity.ok(sportSlotService.getSlotById(id));
    }

    // Update slot - Only Admin can update slots
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUB_ADMIN')")
    public ResponseEntity<SportSlotDto> updateSlot(@PathVariable Long id, @RequestBody SportSlotDto dto) {
        return ResponseEntity.ok(sportSlotService.updateSlot(id, dto));
    }

    // Get slots by category - Public access
    @GetMapping("/category/{identifier}")
    public ResponseEntity<List<SportSlotDto>> getSlotsByCategory(@PathVariable String identifier) {
        return ResponseEntity.ok(sportSlotService.getSlotsByCategory(identifier));
    }
}