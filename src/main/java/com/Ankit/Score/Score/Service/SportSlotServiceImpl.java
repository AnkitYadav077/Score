package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.Category;
import com.Ankit.Score.Score.Entity.SportSlot;
import com.Ankit.Score.Score.Exceptions.ResourceNotFoundException;
import com.Ankit.Score.Score.Payloads.SportSlotDto;
import com.Ankit.Score.Score.Repo.CategoryRepo;
import com.Ankit.Score.Score.Repo.SportSlotRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SportSlotServiceImpl implements SportSlotService {


    private final SportSlotRepo sportSlotRepo;
    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;

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

        // Handle overnight slots (end time is earlier than start time)
        long durationMinutes;
        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            // Overnight slot - calculate duration across midnight
            durationMinutes = Duration.between(dto.getStartTime(), LocalTime.MAX).toMinutes() + 1 + // until midnight
                    Duration.between(LocalTime.MIN, dto.getEndTime()).toMinutes(); // from midnight
        } else {
            // Normal slot within the same day
            durationMinutes = Duration.between(dto.getStartTime(), dto.getEndTime()).toMinutes();
        }

        if (!validStart || !validEndPrecision || durationMinutes < 60) {
            throw new IllegalArgumentException(
                    "Invalid Slot Time: Start time must be at :00 or :30, " +
                            "end time precision must be zero seconds, and duration must be at least 1 hour."
            );
        }
    }

    private void checkSlotConflict(SportSlotDto dto) {
        List<SportSlot> existingSlots = sportSlotRepo.findByCategory_Id(dto.getCategory().getId());

        List<SportSlot> conflicts = existingSlots.stream()
                .filter(slot -> slot.getDate().equals(dto.getDate()))
                .filter(slot -> {
                    boolean isOvernightSlot = dto.getEndTime().isBefore(dto.getStartTime());
                    boolean existingIsOvernight = slot.getEndTime().isBefore(slot.getStartTime());

                    if (isOvernightSlot && existingIsOvernight) {
                        // Both are overnight slots - they overlap if they share the same date
                        return true;
                    } else if (isOvernightSlot) {
                        // New slot is overnight, existing is normal
                        return dto.getStartTime().isBefore(slot.getEndTime()) ||
                                dto.getEndTime().isAfter(slot.getStartTime());
                    } else if (existingIsOvernight) {
                        // Existing slot is overnight, new is normal
                        return slot.getStartTime().isBefore(dto.getEndTime()) ||
                                slot.getEndTime().isAfter(dto.getStartTime());
                    } else {
                        // Both are normal slots
                        return (dto.getStartTime().isBefore(slot.getEndTime()) &&
                                dto.getEndTime().isAfter(slot.getStartTime()));
                    }
                })
                .collect(Collectors.toList());

        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("Slot timing overlaps with existing slot for the same category.");
        }
    }

    private void checkSlotConflictForUpdate(Long slotId, SportSlotDto dto) {
        List<SportSlot> existingSlots = sportSlotRepo.findByCategory_Id(dto.getCategory().getId());

        List<SportSlot> conflicts = existingSlots.stream()
                .filter(slot -> !slot.getSlotId().equals(slotId) && slot.getDate().equals(dto.getDate()))
                .filter(slot -> {
                    boolean isOvernightSlot = dto.getEndTime().isBefore(dto.getStartTime());
                    boolean existingIsOvernight = slot.getEndTime().isBefore(slot.getStartTime());

                    if (isOvernightSlot && existingIsOvernight) {
                        return true;
                    } else if (isOvernightSlot) {
                        return dto.getStartTime().isBefore(slot.getEndTime()) ||
                                dto.getEndTime().isAfter(slot.getStartTime());
                    } else if (existingIsOvernight) {
                        return slot.getStartTime().isBefore(dto.getEndTime()) ||
                                slot.getEndTime().isAfter(dto.getStartTime());
                    } else {
                        return (dto.getStartTime().isBefore(slot.getEndTime()) &&
                                dto.getEndTime().isAfter(slot.getStartTime()));
                    }
                })
                .collect(Collectors.toList());

        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("Slot timing overlaps with existing slot for the same category.");
        }
    }

    Integer calculateTotalPrice(SportSlot slot) {
        LocalTime startTime = slot.getStartTime();
        LocalTime endTime = slot.getEndTime();
        Category category = slot.getCategory();

        LocalTime eveningStart = LocalTime.of(19, 0);
        LocalTime morningBaseStart = LocalTime.of(6, 0);

        boolean isOvernight = endTime.isBefore(startTime);

        if (isOvernight) {
            return calculateOvernightPriceOptimized(startTime, endTime, category, eveningStart, morningBaseStart);
        } else {
            return calculateNormalPriceOptimized(startTime, endTime, category, eveningStart, morningBaseStart);
        }
    }

    private int calculateOvernightPriceOptimized(LocalTime start, LocalTime end, Category category,
                                                 LocalTime eveningStart, LocalTime morningBaseStart) {
        // Convert to minutes since midnight
        int startMin = start.getHour() * 60 + start.getMinute();
        int endMin = end.getHour() * 60 + end.getMinute();
        int eveningStartMin = eveningStart.getHour() * 60 + eveningStart.getMinute();
        int morningBaseStartMin = morningBaseStart.getHour() * 60 + morningBaseStart.getMinute();

        // Adjust for overnight
        endMin += 24 * 60;

        int totalMinutes = endMin - startMin;

        // Calculate minutes for each price segment
        int eveningPriceMinutes = 0;
        int basePriceMinutes = 0;

        int current = startMin;
        while (current < endMin) {
            int nextHour = current + 60;
            if (nextHour > endMin) {
                nextHour = endMin;
            }

            // Determine which price applies to this segment
            int segmentCurrent = current % (24 * 60); // Normalize to 0-1440 minutes
            int pricePerHour;

            if (segmentCurrent < morningBaseStartMin || segmentCurrent >= eveningStartMin) {
                // Evening price (before 6 AM or after 7 PM)
                pricePerHour = category.getEveningPrice();
                eveningPriceMinutes += (nextHour - current);
            } else {
                // Base price (6 AM to 7 PM)
                pricePerHour = category.getBasePrice();
                basePriceMinutes += (nextHour - current);
            }

            current = nextHour;
        }

        double eveningHours = eveningPriceMinutes / 60.0;
        double baseHours = basePriceMinutes / 60.0;

        return (int) Math.round((category.getEveningPrice() * eveningHours) +
                (category.getBasePrice() * baseHours));
    }

    private int calculateNormalPriceOptimized(LocalTime start, LocalTime end, Category category,
                                              LocalTime eveningStart, LocalTime morningBaseStart) {
        int startMin = start.getHour() * 60 + start.getMinute();
        int endMin = end.getHour() * 60 + end.getMinute();
        int eveningStartMin = eveningStart.getHour() * 60 + eveningStart.getMinute();
        int morningBaseStartMin = morningBaseStart.getHour() * 60 + morningBaseStart.getMinute();

        int eveningMinutes = 0;
        int baseMinutes = 0;

        int current = startMin;
        while (current < endMin) {
            int next = Math.min(current + 60, endMin);

            if (current < morningBaseStartMin || current >= eveningStartMin) {
                eveningMinutes += (next - current);
            } else {
                baseMinutes += (next - current);
            }

            current = next;
        }

        double eveningHours = eveningMinutes / 60.0;
        double baseHours = baseMinutes / 60.0;

        return (int) Math.round((category.getEveningPrice() * eveningHours) +
                (category.getBasePrice() * baseHours));
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