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
        SportSlot slot = dtoToEntity(dto);
        return entityToDto(sportSlotRepo.save(slot));
    }

    @Override
    public List<SportSlotDto> getAllSlots() {
        return sportSlotRepo.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SportSlotDto getSlotById(Long slotId) {
        SportSlot slot = sportSlotRepo.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found with ID: " + slotId));
        return entityToDto(slot);
    }

    @Override
    public SportSlotDto updateSlot(Long slotId, SportSlotDto dto) {
        validateSlotTime(dto);

        SportSlot slot = sportSlotRepo.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found with ID: " + slotId));

        slot.setDate(dto.getDate());
        slot.setStartTime(dto.getStartTime());
        slot.setEndTime(dto.getEndTime());
        slot.setBooked(dto.isBooked());

        return entityToDto(sportSlotRepo.save(slot));
    }

    @Override
    public List<SportSlotDto> getSlotsByCategory(String identifier) {
        List<SportSlot> slots;
        try {
            Long id = Long.parseLong(identifier);
            slots = sportSlotRepo.findByCategory_Id(id);
        } catch (NumberFormatException e) {
            slots = sportSlotRepo.findByCategory_Name(identifier);
        }
        return slots.stream().map(this::entityToDto).collect(Collectors.toList());
    }

    // ---------- Utility Methods ----------

    private void validateSlotTime(SportSlotDto dto) {
        int startMin = dto.getStartTime().getMinute();
        int startSec = dto.getStartTime().getSecond();
        int startNano = dto.getStartTime().getNano();
        int endSec = dto.getEndTime().getSecond();
        int endNano = dto.getEndTime().getNano();

        boolean validStart = (startMin == 0 || startMin == 30) && startSec == 0 && startNano == 0;
        boolean validEndPrecision = endSec == 0 && endNano == 0;
        long duration = Duration.between(dto.getStartTime(), dto.getEndTime()).toMinutes();

        if (!validStart || !validEndPrecision || duration < 60) {
            throw new RuntimeException("❌ Invalid Slot Time: Start must be at :00 or :30 and duration at least 1 hour.");
        }
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
