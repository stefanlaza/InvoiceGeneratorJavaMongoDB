package com.invoiceapp.invoice_generator.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.invoiceapp.invoice_generator.model.Invoice;
import com.invoiceapp.invoice_generator.service.InvoiceService;

@Controller
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @GetMapping("/invoices")
    public String getAllInvoices(Model model) {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        model.addAttribute("invoices", invoices);
        return "invoiceList";
    }

    @GetMapping("/invoices/pdf")
    public ResponseEntity<InputStreamResource> generatePdf() throws Exception {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        Context context = new Context();
        context.setVariable("invoices", invoices);

        String htmlContent = templateEngine.process("invoiceList", context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=invoices.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    @GetMapping("/invoice")
    public String getInvoiceByNumber(@RequestParam("number") String invoiceNumber, Model model) {
        Invoice invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
        if (invoice == null) {
            model.addAttribute("error", "Invoice not found");
            return "invoiceError";
        }
        model.addAttribute("invoice", invoice);
        return "invoice";
    }

    @GetMapping("/invoice/pdf")
    public ResponseEntity<InputStreamResource> generateInvoicePdf(@RequestParam("number") String invoiceNumber) throws Exception {
        Invoice invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
        if (invoice == null) {
            throw new Exception("Invoice not found");
        }

        Context context = new Context();
        context.setVariable("invoice", invoice);

        String htmlContent = templateEngine.process("invoice", context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=invoice_" + invoiceNumber + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }
}
