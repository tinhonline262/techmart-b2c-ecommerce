package com.shopping.microservices.product_service.validator;

import com.shopping.microservices.product_service.exception.ErrorCode;
import com.shopping.microservices.product_service.exception.InvalidImageFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class ImageUploadValidator {

    @Value("${app.image.max-size:5242880}")
    private long maxImageSize;

    private static final Set<String> ALLOWED_MIME_TYPES = new HashSet<>(Arrays.asList(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/webp"
    ));

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
        "jpg", "jpeg", "png", "gif", "webp"
    ));

    /**
     * Validates an image file for upload
     * @param file the MultipartFile to validate
     * @throws InvalidImageFileException if file fails validation
     */
    public void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("Attempted to upload empty file");
            throw new InvalidImageFileException(
                ErrorCode.INVALID_IMAGE_FILE,
                "File cannot be empty"
            );
        }

        validateFileSize(file);
        validateMimeType(file);
        validateFileExtension(file);
    }

    /**
     * Validates file size against maximum allowed size
     */
    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > maxImageSize) {
            log.warn("File size {} exceeds maximum allowed size {}", file.getSize(), maxImageSize);
            throw new InvalidImageFileException(
                ErrorCode.IMAGE_FILE_TOO_LARGE,
                "Image file exceeds maximum allowed size of " + (maxImageSize / 1024 / 1024) + "MB"
            );
        }
    }

    /**
     * Validates MIME type
     */
    private void validateMimeType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            log.warn("Invalid MIME type: {}", contentType);
            throw new InvalidImageFileException(
                ErrorCode.INVALID_IMAGE_FILE,
                "Invalid image format. Allowed formats: JPG, PNG, GIF, WebP"
            );
        }
    }

    /**
     * Validates file extension
     */
    private void validateFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            log.warn("Filename is null");
            throw new InvalidImageFileException(
                ErrorCode.INVALID_IMAGE_FILE,
                "Invalid file name"
            );
        }

        String extension = getFileExtension(filename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            log.warn("Invalid file extension: {}", extension);
            throw new InvalidImageFileException(
                ErrorCode.INVALID_IMAGE_FILE,
                "Invalid image format. Allowed formats: JPG, PNG, GIF, WebP"
            );
        }
    }

    /**
     * Extracts file extension from filename
     */
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot + 1) : "";
    }
}
