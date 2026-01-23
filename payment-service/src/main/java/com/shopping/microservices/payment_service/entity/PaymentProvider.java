package com.shopping.microservices.payment_service.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;

import java.util.HashMap;
import java.util.Map;

@DynamicUpdate
@Entity
@Table(name = "payment_provider")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentProvider extends AbstractAuditEntity implements Persistable<String> {
    
    @Id
    @Column(length = 50)
    private String id;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(name = "configure_url", length = 500)
    private String configureUrl;
    
    @Column(name = "landing_view_component_name")
    private String landingViewComponentName;
    
    @Column(name = "additional_settings", columnDefinition = "TEXT")
    private String additionalSettings; // JSON string for provider-specific config
    
    @Column(name = "media_id")
    private Long mediaId; // Logo/icon reference
    
    @Version
    private Integer version;
    
    @Transient
    @Builder.Default
    private boolean isNew = false;
    
    @Override
    public boolean isNew() {
        return isNew || version == null;
    }
    
    // Business methods
    public boolean isActive() {
        return enabled;
    }
    
    public Map<String, Object> getAdditionalSettingsAsMap() {
        if (additionalSettings == null || additionalSettings.isEmpty()) {
            return new HashMap<>();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(additionalSettings, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
    
    public void setAdditionalSettingsFromMap(Map<String, Object> settings) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.additionalSettings = mapper.writeValueAsString(settings);
        } catch (Exception e) {
            this.additionalSettings = "{}";
        }
    }
}