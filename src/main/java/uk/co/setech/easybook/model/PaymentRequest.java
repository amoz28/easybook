package uk.co.setech.easybook.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import uk.co.setech.easybook.enums.PaymentType;

public record PaymentRequest(long invoiceId, @Enumerated(EnumType.STRING) PaymentType paymentType, double amountPaid) {}
