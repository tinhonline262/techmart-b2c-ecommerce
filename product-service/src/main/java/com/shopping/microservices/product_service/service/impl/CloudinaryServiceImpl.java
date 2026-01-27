package com.shopping.microservices.product_service.service.impl;

import com.cloudinary.Cloudinary;
import com.shopping.microservices.product_service.service.CloudinaryService;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {
    
    private final Cloudinary cloudinary;
    
    @Override
    public Map uploadFile(MultipartFile file, String folderName) throws IOException {
        log.info("Uploading file {} to Cloudinary folder: {}", file.getOriginalFilename(), folderName);
        
        Map result = cloudinary.uploader().upload(file.getBytes(),
            ObjectUtils.asMap(
                "folder", folderName,
                "resource_type", "auto"
            ));
        
        log.info("File uploaded successfully. Public ID: {}", result.get("public_id"));
        return result;
    }
    
    @Override
    public Map deleteFile(String publicId) throws IOException {
        log.info("Deleting file from Cloudinary. Public ID: {}", publicId);
        
        Map result = cloudinary.uploader().destroy(publicId, 
            ObjectUtils.emptyMap());
        
        log.info("File deleted successfully");
        return result;
    }
}
