package com.erp.mapper;

import com.erp.dto.WasteTypeDTO;
import com.erp.entity.WasteType;
import org.springframework.stereotype.Component;

@Component
public class WasteTypeMapper {

    public WasteTypeDTO toDTO(WasteType wasteType) {
        if (wasteType == null) return null;

        return WasteTypeDTO.builder()
                .id(wasteType.getId())
                .code(wasteType.getCode())
                .nameAr(wasteType.getNameAr())
                .nameEn(wasteType.getNameEn())
                .category(wasteType.getCategory())
                .glAccountCode(wasteType.getGlAccountCode())
                .isActive(wasteType.getIsActive())
                .build();
    }

    public WasteType toEntity(WasteTypeDTO dto) {
        if (dto == null) return null;

        return WasteType.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .nameAr(dto.getNameAr())
                .nameEn(dto.getNameEn())
                .category(dto.getCategory())
                .glAccountCode(dto.getGlAccountCode())
                .isActive(dto.getIsActive())
                .build();
    }
}