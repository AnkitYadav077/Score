package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.SportSlotDto;
import com.Ankit.Score.Score.Service.SportSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class SportSlotController {

    @Autowired
    private SportSlotService sportSlotService;

    @PostMapping
    public ResponseEntity<SportSlotDto> createSlot(@RequestBody SportSlotDto dto) {
        return ResponseEntity.ok(sportSlotService.createSlot(dto));
    }

    @GetMapping
    public ResponseEntity<List<SportSlotDto>> getAllSlots() {
        return ResponseEntity.ok(sportSlotService.getAllSlots());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SportSlotDto> getSlot(@PathVariable Long id) {
        return ResponseEntity.ok(sportSlotService.getSlotById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SportSlotDto> updateSlot(@PathVariable Long id, @RequestBody SportSlotDto dto) {
        return ResponseEntity.ok(sportSlotService.updateSlot(id, dto));
    }

    @GetMapping("/category/{identifier}")
    public ResponseEntity<List<SportSlotDto>> getSlotsByCategory(@PathVariable String identifier) {
        return ResponseEntity.ok(sportSlotService.getSlotsByCategory(identifier));
    }
}
