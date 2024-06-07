package com.invoiceapp.invoice_generator.repository;

import com.invoiceapp.invoice_generator.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    Invoice findByInvoiceNumber(String invoiceNumber);
}
