package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Payloads.SportSlotDto;

import java.util.List;


public interface SportSlotService {
    SportSlotDto createSlot(SportSlotDto slotDto);
    List<SportSlotDto> getAllSlots();
    SportSlotDto getSlotById(Long slotId);
    SportSlotDto updateSlot(Long slotId, SportSlotDto slotDto);
    List<SportSlotDto> getSlotsByCategory(String categoryIdentifier);


}
