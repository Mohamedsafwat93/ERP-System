package com.erp.controller;

import com.erp.service.PrintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/print")
@RequiredArgsConstructor
@Slf4j
public class PrintController {

    private final PrintService printService;

    @GetMapping("/invoice/{id}/pdf")
    public ResponseEntity<byte[]> printInvoicePdf(@PathVariable Long id) {
        try {
            byte[] pdf = printService.generateInvoicePdf(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "invoice_" + id + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdf);

        } catch (Exception e) {
            log.error("Failed to generate PDF: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/invoice/{id}/thermal")
    public ResponseEntity<String> printThermalReceipt(@PathVariable Long id) {
        try {
            String receipt = printService.generateThermalReceipt(id);
            return ResponseEntity.ok(receipt);
        } catch (Exception e) {
            log.error("Failed to generate thermal receipt: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}