package com.shopping.microservices.location_service.controller;

import com.shopping.microservices.location_service.dto.ApiResponse;
import com.shopping.microservices.location_service.dto.StateCountryNameDTO;
import com.shopping.microservices.location_service.dto.StateOrProvinceDTO;
import com.shopping.microservices.location_service.dto.StateOrProvinceListGetDTO;
import com.shopping.microservices.location_service.dto.StateOrProvincePostDTO;
import com.shopping.microservices.location_service.service.StateOrProvinceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/state-or-provinces")
@RequiredArgsConstructor
public class AdminStateOrProvinceController {

    private final StateOrProvinceService stateOrProvinceService;

    @GetMapping("/paging")
    public ResponseEntity<ApiResponse<Object>> getStateOrProvincesPaging(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long countryId) {
        
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<StateOrProvinceDTO> page = stateOrProvinceService.getStateOrProvincesPaging(pageable, countryId);
        
        return ResponseEntity.ok(ApiResponse.success("States/Provinces retrieved successfully", page, "/api/v1/state-or-provinces/paging"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StateOrProvinceListGetDTO>>> getStateOrProvinces(
            @RequestParam(required = false) Long countryId) {
        
        List<StateOrProvinceListGetDTO> stateOrProvinces = stateOrProvinceService.getAllStateOrProvinces(countryId);
        return ResponseEntity.ok(ApiResponse.success("States/Provinces retrieved successfully", stateOrProvinces, "/api/v1/state-or-provinces"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StateOrProvinceDTO>> getStateOrProvinceById(@PathVariable Long id) {
        StateOrProvinceDTO stateOrProvinceDTO = stateOrProvinceService.getStateOrProvinceById(id);
        return ResponseEntity.ok(ApiResponse.success("State/Province retrieved successfully", stateOrProvinceDTO, "/api/v1/state-or-provinces/" + id));
    }

    @GetMapping("/state-country-names")
    public ResponseEntity<ApiResponse<List<StateCountryNameDTO>>> getStateCountryNames(
            @RequestParam List<Long> stateOrProvinceIds) {
        
        List<StateCountryNameDTO> result = stateOrProvinceService.getStateCountryNames(stateOrProvinceIds);
        return ResponseEntity.ok(ApiResponse.success("State/Country names retrieved successfully", result, "/api/v1/state-or-provinces/state-country-names"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StateOrProvinceDTO>> createStateOrProvince(
            @Valid @RequestBody StateOrProvincePostDTO stateOrProvincePostDTO) {
        
        StateOrProvinceDTO stateOrProvinceDTO = stateOrProvinceService.createStateOrProvince(stateOrProvincePostDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("State/Province created successfully", stateOrProvinceDTO, "/api/v1/state-or-provinces"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StateOrProvinceDTO>> updateStateOrProvince(
            @PathVariable Long id,
            @Valid @RequestBody StateOrProvincePostDTO stateOrProvincePostDTO) {
        
        StateOrProvinceDTO stateOrProvinceDTO = stateOrProvinceService.updateStateOrProvince(id, stateOrProvincePostDTO);
        return ResponseEntity.ok(ApiResponse.success("State/Province updated successfully", stateOrProvinceDTO, "/api/v1/state-or-provinces/" + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStateOrProvince(@PathVariable Long id) {
        stateOrProvinceService.deleteStateOrProvince(id);
        return ResponseEntity.ok(ApiResponse.success("State/Province deleted successfully", null, "/api/v1/state-or-provinces/" + id));
    }
}

    @GetMapping("/paging")
    public ResponseEntity<ApiResponse<Object>> getStateOrProvincesPaging(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long countryId) {
        
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<StateOrProvince> stateOrProvincePage;
        
        if (countryId != null) {
            stateOrProvincePage = stateOrProvinceRepository.findByCountryId(countryId, pageable);
        } else {
            stateOrProvincePage = stateOrProvinceRepository.findAll(pageable);
        }
        
        Object pageResponse = new Object() {
            public int getNumber() { return stateOrProvincePage.getNumber(); }
            public int getSize() { return stateOrProvincePage.getSize(); }
            public long getTotalElements() { return stateOrProvincePage.getTotalElements(); }
            public int getTotalPages() { return stateOrProvincePage.getTotalPages(); }
            public List<StateOrProvinceDTO> getContent() {
                return stateOrProvincePage.getContent().stream()
                        .map(this::toStateOrProvinceDTO)
                        .toList();
            }
            private StateOrProvinceDTO toStateOrProvinceDTO(StateOrProvince s) {
                return new StateOrProvinceDTO(
                        s.getId(),
                        s.getCode(),
                        s.getName(),
                        s.getType(),
                        s.getCountry() != null ? s.getCountry().getId() : null,
                        s.getCountry() != null ? s.getCountry().getName() : null,
                        s.getCreatedAt() != null ? s.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null,
                        s.getUpdatedAt() != null ? s.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null
                );
            }
        };
        
        return ResponseEntity.ok(ApiResponse.success("States/Provinces retrieved successfully", pageResponse, "/api/v1/state-or-provinces/paging"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StateOrProvinceListGetDTO>>> getStateOrProvinces(
            @RequestParam(required = false) Long countryId) {
        
        List<StateOrProvinceListGetDTO> stateOrProvinces;
        
        if (countryId != null) {
            stateOrProvinces = stateOrProvinceRepository.findByCountryId(countryId).stream()
                    .map(s -> new StateOrProvinceListGetDTO(
                            s.getId(),
                            s.getCode(),
                            s.getName(),
                            s.getType(),
                            s.getCountry() != null ? s.getCountry().getId() : null
                    ))
                    .toList();
        } else {
            stateOrProvinces = stateOrProvinceRepository.findAll().stream()
                    .map(s -> new StateOrProvinceListGetDTO(
                            s.getId(),
                            s.getCode(),
                            s.getName(),
                            s.getType(),
                            s.getCountry() != null ? s.getCountry().getId() : null
                    ))
                    .toList();
        }
        
        return ResponseEntity.ok(ApiResponse.success("States/Provinces retrieved successfully", stateOrProvinces, "/api/v1/state-or-provinces"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StateOrProvinceDTO>> getStateOrProvinceById(@PathVariable Long id) {
        StateOrProvince stateOrProvince = stateOrProvinceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("State/Province not found with id: " + id));
        
        StateOrProvinceDTO stateOrProvinceDTO = new StateOrProvinceDTO(
                stateOrProvince.getId(),
                stateOrProvince.getCode(),
                stateOrProvince.getName(),
                stateOrProvince.getType(),
                stateOrProvince.getCountry() != null ? stateOrProvince.getCountry().getId() : null,
                stateOrProvince.getCountry() != null ? stateOrProvince.getCountry().getName() : null,
                stateOrProvince.getCreatedAt() != null ? stateOrProvince.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null,
                stateOrProvince.getUpdatedAt() != null ? stateOrProvince.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null
        );
        
        return ResponseEntity.ok(ApiResponse.success("State/Province retrieved successfully", stateOrProvinceDTO, "/api/v1/state-or-provinces/" + id));
    }

    @GetMapping("/state-country-names")
    public ResponseEntity<ApiResponse<List<StateCountryNameDTO>>> getStateCountryNames(
            @RequestParam List<Long> stateOrProvinceIds) {
        
        List<StateOrProvince> stateOrProvinces = stateOrProvinceRepository.findByIdIn(stateOrProvinceIds);
        
        List<StateCountryNameDTO> result = stateOrProvinces.stream()
                .map(s -> new StateCountryNameDTO(
                        s.getId(),
                        s.getName(),
                        s.getCountry() != null ? s.getCountry().getName() : null
                ))
                .toList();
        
        return ResponseEntity.ok(ApiResponse.success("State/Country names retrieved successfully", result, "/api/v1/state-or-provinces/state-country-names"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StateOrProvinceDTO>> createStateOrProvince(
            @Valid @RequestBody StateOrProvincePostDTO stateOrProvincePostDTO) {
        
        Country country = countryRepository.findById(stateOrProvincePostDTO.countryId())
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + stateOrProvincePostDTO.countryId()));
        
        StateOrProvince stateOrProvince = StateOrProvince.builder()
                .code(stateOrProvincePostDTO.code())
                .name(stateOrProvincePostDTO.name())
                .type(stateOrProvincePostDTO.type())
                .country(country)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        
        StateOrProvince savedStateOrProvince = stateOrProvinceRepository.save(stateOrProvince);
        
        StateOrProvinceDTO stateOrProvinceDTO = new StateOrProvinceDTO(
                savedStateOrProvince.getId(),
                savedStateOrProvince.getCode(),
                savedStateOrProvince.getName(),
                savedStateOrProvince.getType(),
                savedStateOrProvince.getCountry() != null ? savedStateOrProvince.getCountry().getId() : null,
                savedStateOrProvince.getCountry() != null ? savedStateOrProvince.getCountry().getName() : null,
                savedStateOrProvince.getCreatedAt() != null ? savedStateOrProvince.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null,
                savedStateOrProvince.getUpdatedAt() != null ? savedStateOrProvince.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("State/Province created successfully", stateOrProvinceDTO, "/api/v1/state-or-provinces"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StateOrProvinceDTO>> updateStateOrProvince(
            @PathVariable Long id,
            @Valid @RequestBody StateOrProvincePostDTO stateOrProvincePostDTO) {
        
        StateOrProvince stateOrProvince = stateOrProvinceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("State/Province not found with id: " + id));
        
        Country country = countryRepository.findById(stateOrProvincePostDTO.countryId())
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + stateOrProvincePostDTO.countryId()));
        
        stateOrProvince.setCode(stateOrProvincePostDTO.code());
        stateOrProvince.setName(stateOrProvincePostDTO.name());
        stateOrProvince.setType(stateOrProvincePostDTO.type());
        stateOrProvince.setCountry(country);
        stateOrProvince.setUpdatedAt(Instant.now());
        
        StateOrProvince updatedStateOrProvince = stateOrProvinceRepository.save(stateOrProvince);
        
        StateOrProvinceDTO stateOrProvinceDTO = new StateOrProvinceDTO(
                updatedStateOrProvince.getId(),
                updatedStateOrProvince.getCode(),
                updatedStateOrProvince.getName(),
                updatedStateOrProvince.getType(),
                updatedStateOrProvince.getCountry() != null ? updatedStateOrProvince.getCountry().getId() : null,
                updatedStateOrProvince.getCountry() != null ? updatedStateOrProvince.getCountry().getName() : null,
                updatedStateOrProvince.getCreatedAt() != null ? updatedStateOrProvince.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null,
                updatedStateOrProvince.getUpdatedAt() != null ? updatedStateOrProvince.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null
        );
        
        return ResponseEntity.ok(ApiResponse.success("State/Province updated successfully", stateOrProvinceDTO, "/api/v1/state-or-provinces/" + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStateOrProvince(@PathVariable Long id) {
        StateOrProvince stateOrProvince = stateOrProvinceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("State/Province not found with id: " + id));
        
        stateOrProvinceRepository.delete(stateOrProvince);
        
        return ResponseEntity.ok(ApiResponse.success("State/Province deleted successfully", null, "/api/v1/state-or-provinces/" + id));
    }
}
