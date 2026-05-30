package com.erp.service;

import com.erp.entity.Invoice;
import com.erp.repository.InvoiceRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrintService {

    private final InvoiceRepository invoiceRepository;

    public byte[] generateInvoicePdf(Long invoiceId) throws Exception {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        // Company Header
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

        Paragraph title = new Paragraph("INVOICE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // Invoice Info
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);

        infoTable.addCell(new Phrase("Invoice Number:", boldFont));
        infoTable.addCell(new Phrase(invoice.getInvoiceNumber(), normalFont));
        infoTable.addCell(new Phrase("Date:", boldFont));
        infoTable.addCell(new Phrase(invoice.getInvoiceDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), normalFont));
        infoTable.addCell(new Phrase("Customer:", boldFont));
        infoTable.addCell(new Phrase(invoice.getCustomer().getName(), normalFont));

        document.add(infoTable);
        document.add(Chunk.NEWLINE);

        document.close();
        return out.toByteArray();
    }

    public String generateThermalReceipt(Long invoiceId) throws Exception {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        StringBuilder receipt = new StringBuilder();
        receipt.append("====================\n");
        receipt.append("      INVOICE\n");
        receipt.append("====================\n");
        receipt.append("Number: ").append(invoice.getInvoiceNumber()).append("\n");
        receipt.append("Date: ").append(invoice.getInvoiceDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
        receipt.append("Customer: ").append(invoice.getCustomer().getName()).append("\n");
        receipt.append("--------------------\n");
        receipt.append("Subtotal: ").append(invoice.getSubtotal()).append("\n");
        receipt.append("Tax (15%): ").append(invoice.getTaxAmount()).append("\n");
        receipt.append("TOTAL: ").append(invoice.getTotalAmount()).append("\n");
        receipt.append("====================\n");
        receipt.append("Thank you!\n");
        receipt.append("====================\n");

        return receipt.toString();
    }
}