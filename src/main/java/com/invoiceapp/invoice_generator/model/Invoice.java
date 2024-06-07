package com.invoiceapp.invoice_generator.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "invoices")
public class Invoice {
    @Id
    private String id;
    private String invoiceNumber;  // Add this field
    private String customerName;
    private String customerEmail;
    private String date;
    private List<InvoiceItem> items;
    private double totalAmount;

    @Data
    public static class InvoiceItem {
        private String description;
        private int quantity;
        private double price;
    }
}
