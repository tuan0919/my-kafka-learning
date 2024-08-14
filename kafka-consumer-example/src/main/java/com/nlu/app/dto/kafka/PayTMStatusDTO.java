package com.nlu.app.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PayTMStatusDTO {
    Boolean isPay;
    String username;
    String transactionID;
}
