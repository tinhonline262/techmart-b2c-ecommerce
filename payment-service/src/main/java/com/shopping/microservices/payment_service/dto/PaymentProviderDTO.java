package com.shopping.microservices.payment_service.dto;

import com.shopping.microservices.payment_service.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProviderDTO {
    
    private String id;
    
    private String name;
    
    private boolean enabled;
    
    private String logoUrl;
    
    private List<PaymentMethod> supportedMethods;
    
    private String configureUrl;
}
