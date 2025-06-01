package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.Category;
import com.Ankit.Score.Score.Entity.SportSlot;
import com.Ankit.Score.Score.Exceptions.ResourceNotFoundException;
import com.Ankit.Score.Score.Payloads.SportSlotDto;
import com.Ankit.Score.Score.Repo.CategoryRepo;
import com.Ankit.Score.Score.Repo.SportSlotRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SportSlotServiceImpl implements SportSlotService {

    @Autowired
    private SportSlotRepo sportSlotRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public SportSlotDto createSlot(SportSlotDto dto) {
        validateSlotTime(dto);
        checkSlotConflict(dto);

        SportSlot slot = dtoToEntity(dto);

        // Calculate and set totalPrice before saving
        slot.setTotalPrice(calculateTotalPrice(slot));

        SportSlot saved = sportSlotRepo.save(slot);

        SportSlotDto responseDto = entityToDto(saved);
        responseDto.setTotalPrice(saved.getTotalPrice()); // Optional, but clear
        return responseDto;
    }

    @Override
    public List<SportSlotDto> getAllSlots() {
        return sportSlotRepo.findAll().stream()
                .map(slot -> {
                    SportSlotDto dto = entityToDto(slot);
                    dto.setTotalPrice(slot.getTotalPrice());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public SportSlotDto getSlotById(Long slotId) {
        SportSlot slot = sportSlotRepo.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("SportSlot", "id", slotId));
        SportSlotDto dto = entityToDto(slot);
        dto.setTotalPrice(slot.getTotalPrice());
        return dto;
    }

    @Override
    public SportSlotDto updateSlot(Long slotId, SportSlotDto dto) {
        validateSlotTime(dto);
        checkSlotConflictForUpdate(slotId, dto);

        SportSlot slot = sportSlotRepo.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("SportSlot", "id", slotId));

        slot.setDate(dto.getDate());
        slot.setStartTime(dto.getStartTime());
        slot.setEndTime(dto.getEndTime());
        slot.setBooked(dto.isBooked());

        if (dto.getCategory() != null && dto.getCategory().getId() != null) {
            Category category = categoryRepo.findById(dto.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategory().getId()));
            slot.setCategory(category);
        }

        // Set totalPrice before saving
        slot.setTotalPrice(calculateTotalPrice(slot));

        SportSlot updated = sportSlotRepo.save(slot);
        SportSlotDto responseDto = entityToDto(updated);
        responseDto.setTotalPrice(updated.getTotalPrice());
        return responseDto;
    }

    @Override
    public List<SportSlotDto> getSlotsByCategory(String identifier) {
        Category category = fetchCategoryByIdentifier(identifier);

        return sportSlotRepo.findByCategory_Id(category.getId()).stream()
                .map(slot -> {
                    SportSlotDto dto = entityToDto(slot);
                    dto.setTotalPrice(slot.getTotalPrice());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // --------- Utility Methods -----------

    private Category fetchCategoryByIdentifier(String identifier) {
        try {
            Long id = Long.parseLong(identifier);
            return categoryRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        } catch (NumberFormatException e) {
            return categoryRepo.findByName(identifier)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "name", identifier));
        }
    }

    private void validateSlotTime(SportSlotDto dto) {
        if (dto.getStartTime() == null || dto.getEndTime() == null) {
            throw new IllegalArgumentException("Start time and end time must be provided");
        }

        int startMin = dto.getStartTime().getMinute();
        int startSec = dto.getStartTime().getSecond();
        int startNano = dto.getStartTime().getNano();

        int endSec = dto.getEndTime().getSecond();
        int endNano = dto.getEndTime().getNano();

        boolean validStart = (startMin == 0 || startMin == 30) && startSec == 0 && startNano == 0;
        boolean validEndPrecision = endSec == 0 && endNano == 0;

        long duration = Duration.between(dto.getStartTime(), dto.getEndTime()).toMinutes();

        if (!validStart || !validEndPrecision || duration < 60) {
            throw new IllegalArgumentException("Invalid Slot Time: Start time must be at :00 or :30, end time precision must be zero seconds, and duration must be at least 1 hour.");
        }
    }

    private void checkSlotConflict(SportSlotDto dto) {
        List<SportSlot> conflicts = sportSlotRepo.findByCategory_Id(dto.getCategory().getId()).stream()
                .filter(slot -> slot.getDate().equals(dto.getDate()) &&
                        (slot.getStartTime().equals(dto.getStartTime()) || slot.getEndTime().equals(dto.getEndTime()) ||
                                (dto.getStartTime().isBefore(slot.getEndTime()) && dto.getEndTime().isAfter(slot.getStartTime()))))
                .collect(Collectors.toList());
        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("Slot timing overlaps with existing slot for the same category.");
        }
    }

    private void checkSlotConflictForUpdate(Long slotId, SportSlotDto dto) {
        List<SportSlot> conflicts = sportSlotRepo.findByCategory_Id(dto.getCategory().getId()).stream()
                .filter(slot -> !slot.getSlotId().equals(slotId) &&
                        slot.getDate().equals(dto.getDate()) &&
                        (slot.getStartTime().equals(dto.getStartTime()) || slot.getEndTime().equals(dto.getEndTime()) ||
                                (dto.getStartTime().isBefore(slot.getEndTime()) && dto.getEndTime().isAfter(slot.getStartTime()))))
                .collect(Collectors.toList());
        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("Slot timing overlaps with existing slot for the same category.");
        }
    }

    private Integer calculateTotalPrice(SportSlot slot) {
        long hours = Duration.between(slot.getStartTime(), slot.getEndTime()).toHours();
        Category category = slot.getCategory();
        int pricePerHour;

        if (slot.getStartTime().isAfter(LocalTime.of(17, 0))) {
            pricePerHour = category.getEveningPrice();
        } else {
            pricePerHour = category.getBasePrice();
        }

        return (int) (pricePerHour * hours);
    }

    private SportSlot dtoToEntity(SportSlotDto dto) {
        SportSlot slot = modelMapper.map(dto, SportSlot.class);
        if (dto.getCategory() != null && dto.getCategory().getId() != null) {
            Category category = categoryRepo.findById(dto.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategory().getId()));
            slot.setCategory(category);
        }
        return slot;
    }

    private SportSlotDto entityToDto(SportSlot entity) {
        return modelMapper.map(entity, SportSlotDto.class);
    }
}
