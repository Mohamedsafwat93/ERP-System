package com.erp.config;

import com.erp.entity.UnitOfMeasure;
import com.erp.repository.UnitOfMeasureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UnitOfMeasureRepository uomRepository;

    @Override
    public void run(String... args) throws Exception {
        if (uomRepository.count() == 0) {
            log.info("Inserting UOM data...");

            uomRepository.save(createUOM("KG", "كيلوغرام", "Kilogram", "weight", "kg", 1));
            uomRepository.save(createUOM("G", "غرام", "Gram", "weight", "g", 2));
            uomRepository.save(createUOM("MG", "مليغرام", "Milligram", "weight", "mg", 3));
            uomRepository.save(createUOM("LB", "باوند", "Pound", "weight", "lb", 4));
            uomRepository.save(createUOM("OZ", "أونصة", "Ounce", "weight", "oz", 5));
            uomRepository.save(createUOM("L", "لتر", "Liter", "volume", "L", 10));
            uomRepository.save(createUOM("ML", "ملليلتر", "Milliliter", "volume", "ml", 11));
            uomRepository.save(createUOM("GAL", "غالون", "Gallon", "volume", "gal", 12));
            uomRepository.save(createUOM("MTR", "متر", "Meter", "length", "m", 20));
            uomRepository.save(createUOM("CM", "سنتيمتر", "Centimeter", "length", "cm", 21));
            uomRepository.save(createUOM("MM", "ميليمتر", "Millimeter", "length", "mm", 22));
            uomRepository.save(createUOM("PC", "قطعة", "Piece", "quantity", "pc", 40));
            uomRepository.save(createUOM("BOX", "صندوق", "Box", "quantity", "box", 41));
            uomRepository.save(createUOM("CTN", "كرتون", "Carton", "quantity", "ctn", 42));
            uomRepository.save(createUOM("PK", "حزمة", "Pack", "quantity", "pk", 43));
            uomRepository.save(createUOM("DZ", "دستة", "Dozen", "quantity", "dz", 44));
            uomRepository.save(createUOM("SM", "صغير", "Small", "clothing", "S", 51));
            uomRepository.save(createUOM("MD", "وسط", "Medium", "clothing", "M", 52));
            uomRepository.save(createUOM("LG", "كبير", "Large", "clothing", "L", 53));
            uomRepository.save(createUOM("XL", "كبير جداً", "Extra Large", "clothing", "XL", 54));

            log.info("UOM data inserted successfully!");
        }
    }

    private UnitOfMeasure createUOM(String code, String nameAr, String nameEn,
                                    String category, String symbol, int sortOrder) {
        return UnitOfMeasure.builder()
                .code(code)
                .nameAr(nameAr)
                .nameEn(nameEn)
                .category(category)
                .symbol(symbol)
                .sortOrder(sortOrder)
                .isActive(true)
                .build();
    }
}