package com.erp.service;

import com.erp.dto.UnitOfMeasureDTO;
import com.erp.entity.UnitOfMeasure;
import com.erp.repository.UnitOfMeasureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnitOfMeasureService {

    private final UnitOfMeasureRepository uomRepository;

    public List<UnitOfMeasureDTO> getAllActiveUOMs() {
        return uomRepository.findByIsActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UnitOfMeasureDTO convertToDTO(UnitOfMeasure uom) {
        return UnitOfMeasureDTO.builder()
                .id(uom.getId())
                .code(uom.getCode())
                .nameAr(uom.getNameAr())
                .nameEn(uom.getNameEn())
                .category(uom.getCategory())
                .symbol(uom.getSymbol())
                .isActive(uom.getIsActive())
                .sortOrder(uom.getSortOrder())
                .build();
    }
}