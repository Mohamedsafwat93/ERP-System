package com.erp.config;

import com.erp.entity.*;
import com.erp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UnitOfMeasureRepository uomRepository;
    private final CurrencyRepository currencyRepository;
    private final TaxRateRepository taxRateRepository;
    private final WasteTypeRepository wasteTypeRepository;  // ← Changed from WasteTypesRepository

    @Override
    public void run(String... args) throws Exception {

        // ============================================
        // 1. Initialize UOM Data
        // ============================================
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

        // ============================================
        // 2. Initialize Currency Data
        // ============================================
        if (currencyRepository.count() == 0) {
            log.info("Inserting Currency data...");

            currencyRepository.save(createCurrency("SAR", "ريال سعودي", "Saudi Riyal", "﷼", false, new BigDecimal("0.266667")));
            currencyRepository.save(createCurrency("USD", "دولار أمريكي", "US Dollar", "$", false, new BigDecimal("0.071111")));
            currencyRepository.save(createCurrency("AED", "درهم إماراتي", "UAE Dirham", "د.إ", false, new BigDecimal("0.261111")));
            currencyRepository.save(createCurrency("EUR", "يورو", "Euro", "€", false, new BigDecimal("0.065000")));
            currencyRepository.save(createCurrency("EGP", "جنيه مصري", "Egyptian Pound", "ج.م", true, BigDecimal.ONE));

            log.info("Currency data inserted successfully!");
        }

        // ============================================
        // 3. Initialize Tax Rate Data
        // ============================================
        if (taxRateRepository.count() == 0) {
            log.info("Inserting Tax Rate data...");

            taxRateRepository.save(createTaxRate("SA", "ضريبة القيمة المضافة", "VAT", new BigDecimal("15.00"), false));
            taxRateRepository.save(createTaxRate("AE", "ضريبة القيمة المضافة", "VAT", new BigDecimal("5.00"), false));
            taxRateRepository.save(createTaxRate("US", "ضريبة المبيعات", "Sales Tax", BigDecimal.ZERO, false));
            taxRateRepository.save(createTaxRate("EG", "ضريبة القيمة المضافة", "VAT", new BigDecimal("14.00"), true));

            log.info("Tax Rate data inserted successfully!");
        }

        // ============================================
        // 4. Initialize Waste Types Data
        // ============================================
        if (wasteTypeRepository != null && wasteTypeRepository.count() == 0) {
            log.info("Inserting Waste Types data...");

            wasteTypeRepository.save(createWasteType("DAMAGED", "تالف", "Damaged", "DAMAGED"));
            wasteTypeRepository.save(createWasteType("EXPIRED", "منتهي الصلاحية", "Expired", "EXPIRED"));
            wasteTypeRepository.save(createWasteType("RETURNED", "مرتجع عميل", "Customer Returned", "RETURNED"));
            wasteTypeRepository.save(createWasteType("DEFECTIVE", "عيب تصنيع", "Defective", "DEFECTIVE"));
            wasteTypeRepository.save(createWasteType("OBSOLETE", "منتج قديم", "Obsolete", "OBSOLETE"));

            log.info("Waste Types data inserted successfully!");
        }

        log.info("========================================");
        log.info("All initial data loaded successfully!");
        log.info("========================================");
    }

    // ============================================
    // Helper Methods
    // ============================================

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

    private Currency createCurrency(String code, String nameAr, String nameEn,
                                    String symbol, boolean isBase, BigDecimal exchangeRate) {
        return Currency.builder()
                .code(code)
                .nameAr(nameAr)
                .nameEn(nameEn)
                .symbol(symbol)
                .isBase(isBase)
                .exchangeRate(exchangeRate)
                .isActive(true)
                .build();
    }

    private TaxRate createTaxRate(String countryCode, String taxNameAr,
                                  String taxNameEn, BigDecimal rate, boolean isDefault) {
        return TaxRate.builder()
                .countryCode(countryCode)
                .taxNameAr(taxNameAr)
                .taxNameEn(taxNameEn)
                .rate(rate)
                .isDefault(isDefault)
                .isActive(true)
                .build();
    }

    private WasteType createWasteType(String code, String nameAr, String nameEn, String category) {
        return WasteType.builder()
                .code(code)
                .nameAr(nameAr)
                .nameEn(nameEn)
                .category(category)
                .isActive(true)
                .build();
    }
}