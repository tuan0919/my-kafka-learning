package com.nlu.app.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(makeFinal = true)
@Builder
public class PayTMStatusDTO {
    Boolean isPay;
    String username;
    String transactionID;
}
