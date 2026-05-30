package com.erp.service;

import com.erp.dto.WasteTypeDTO;
import com.erp.entity.WasteType;
import com.erp.mapper.WasteTypeMapper;
import com.erp.repository.WasteTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WasteTypeService {

    private final WasteTypeRepository wasteTypeRepository;
    private final WasteTypeMapper wasteTypeMapper;

    @Transactional(readOnly = true)
    public List<WasteTypeDTO> getAllWasteTypes() {
        return wasteTypeRepository.findByIsActiveTrue()
                .stream()
                .map(wasteTypeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WasteTypeDTO getWasteTypeByCode(String code) {
        WasteType wasteType = wasteTypeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Waste type not found: " + code));
        return wasteTypeMapper.toDTO(wasteType);
    }

    @Transactional(readOnly = true)
    public List<WasteTypeDTO> getWasteTypesByCategory(String category) {
        return wasteTypeRepository.findByCategory(category)
                .stream()
                .map(wasteTypeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public WasteTypeDTO createWasteType(WasteTypeDTO dto) {
        if (wasteTypeRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Waste type code already exists: " + dto.getCode());
        }

        WasteType wasteType = wasteTypeMapper.toEntity(dto);
        wasteType.setIsActive(true);
        WasteType saved = wasteTypeRepository.save(wasteType);
        log.info("Waste type created: {} - {}", saved.getCode(), saved.getNameAr());
        return wasteTypeMapper.toDTO(saved);
    }

    @Transactional
    public WasteTypeDTO updateWasteType(Long id, WasteTypeDTO dto) {
        WasteType wasteType = wasteTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waste type not found with id: " + id));

        wasteType.setNameAr(dto.getNameAr());
        wasteType.setNameEn(dto.getNameEn());
        wasteType.setCategory(dto.getCategory());
        wasteType.setGlAccountCode(dto.getGlAccountCode());

        WasteType saved = wasteTypeRepository.save(wasteType);
        log.info("Waste type updated: {}", saved.getCode());
        return wasteTypeMapper.toDTO(saved);
    }

    @Transactional
    public void deleteWasteType(Long id) {
        WasteType wasteType = wasteTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waste type not found with id: " + id));
        wasteType.setIsActive(false);
        wasteTypeRepository.save(wasteType);
        log.info("Waste type deactivated: {}", wasteType.getCode());
    }
}