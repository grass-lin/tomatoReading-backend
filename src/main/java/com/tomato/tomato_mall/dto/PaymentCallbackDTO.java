package com.tomato.tomato_mall.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentCallbackDTO {
    private Map<String, String> parameters;
}