package com.shopping.microservices.product_service.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    
    Map uploadFile(MultipartFile file, String folderName) throws IOException;
    
    Map deleteFile(String publicId) throws IOException;
}
