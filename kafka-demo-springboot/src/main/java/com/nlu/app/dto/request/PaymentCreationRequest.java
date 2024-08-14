package com.nlu.app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(makeFinal = true)
@Builder
public class PaymentCreationRequest {
    Boolean isPay;
    String username;
}
