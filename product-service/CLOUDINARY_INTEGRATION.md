# Cloudinary Image Upload Implementation

## Overview

This document describes the Cloudinary integration for product image uploads in the TechMart B2C e-commerce platform.

## Architecture

### Components

1. **CloudinaryConfig** (`config/CloudinaryConfig.java`)
   - Spring configuration that creates Cloudinary bean
   - Loads credentials from environment variables via `application.yml`
   - Supports secure HTTPS uploads

2. **CloudinaryService** (`service/CloudinaryService.java` & `service/impl/CloudinaryServiceImpl.java`)
   - Interface and implementation for Cloudinary operations
   - Methods:
     - `uploadFile(MultipartFile file, String folderName)`: Upload file to Cloudinary
     - `deleteFile(String publicId)`: Delete file from Cloudinary

3. **ProductImageService** (`service/ProductImageService.java` & `service/impl/ProductImageServiceImpl.java`)
   - Business logic for product image management
   - Methods:
     - `uploadProductImage(productId, file)`: Upload single image
     - `uploadProductImages(productId, files)`: Upload multiple images
     - `getProductImages(productId)`: Get all images for a product
     - `deleteProductImage(productId, imageId)`: Delete image from product
     - `getImageById(imageId)`: Get specific image details

4. **ImageUploadValidator** (`validator/ImageUploadValidator.java`)
   - Validates image files before upload
   - Checks:
     - File size (max 5MB by default, configurable)
     - MIME type (jpg, png, gif, webp)
     - File extension
   - Throws `InvalidImageFileException` on validation failure

5. **ProductImageController** (`controller/ProductImageController.java`)
   - REST API endpoints for image management
   - Requires `ADMIN` or `SELLER` role for uploads/deletions
   - Endpoints:
     - `POST /api/v1/products/{productId}/images/upload` - Upload single image
     - `POST /api/v1/products/{productId}/images/upload-multiple` - Upload multiple images
     - `GET /api/v1/products/{productId}/images` - Get all product images
     - `GET /api/v1/products/{productId}/images/{imageId}` - Get specific image
     - `DELETE /api/v1/products/{productId}/images/{imageId}` - Delete image

### Database Schema

**product_image table** (enhanced):

```sql
- id (PK)
- product_id (FK)
- image_url (VARCHAR 1000) - Cloudinary secure URL
- cloudinary_public_id (VARCHAR 500) - Unique ID in Cloudinary
- alt_text (VARCHAR 255) - Alternative text for accessibility
- display_order (INT)
- is_primary (BOOLEAN)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
```

## Configuration

### Environment Variables

Create or update your `.env` file:

```env
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

### Application Configuration

In `application.yml`:

```yaml
cloudinary:
  cloud-name: ${CLOUDINARY_CLOUD_NAME:dp4juv71c}
  api-key: ${CLOUDINARY_API_KEY:663884345369589}
  api-secret: ${CLOUDINARY_API_SECRET:rNqLqtyKHURj4QI3F2T64szAm5w}

app:
  image:
    max-size: 5242880 # 5MB in bytes
```

## Exception Handling

### Custom Exceptions

- `ImageUploadException` - Thrown when upload/delete operations fail
- `InvalidImageFileException` - Thrown when file validation fails
- `ProductNotFoundException` - Thrown when product not found

### Error Codes

- `IMAGE_UPLOAD_FAILED` - Upload operation failed
- `IMAGE_FILE_TOO_LARGE` - File size exceeds limit
- `INVALID_IMAGE_FILE` - Invalid file format or MIME type
- `IMAGE_NOT_FOUND` - Image doesn't exist
- `IMAGE_DELETE_FAILED` - Deletion operation failed

## Usage Examples

### Upload Single Image

```bash
curl -X POST \
  http://localhost:8081/api/v1/products/1/images/upload \
  -H 'Authorization: Bearer YOUR_TOKEN' \
  -F 'file=@/path/to/image.jpg'
```

Response:

```json
{
  "id": 1,
  "productId": 1,
  "imageUrl": "https://res.cloudinary.com/...",
  "altText": "image.jpg",
  "isPrimary": false,
  "displayOrder": 0,
  "createdAt": "2025-01-27T10:30:00",
  "updatedAt": "2025-01-27T10:30:00"
}
```

### Upload Multiple Images

```bash
curl -X POST \
  http://localhost:8081/api/v1/products/1/images/upload-multiple \
  -H 'Authorization: Bearer YOUR_TOKEN' \
  -F 'files=@/path/to/image1.jpg' \
  -F 'files=@/path/to/image2.png'
```

### Get Product Images

```bash
curl -X GET \
  http://localhost:8081/api/v1/products/1/images \
  -H 'Authorization: Bearer YOUR_TOKEN'
```

### Delete Image

```bash
curl -X DELETE \
  http://localhost:8081/api/v1/products/1/images/1 \
  -H 'Authorization: Bearer YOUR_TOKEN'
```

## Supported Image Formats

- JPEG (.jpg, .jpeg)
- PNG (.png)
- GIF (.gif)
- WebP (.webp)

## File Size Limits

- Default maximum: 5MB
- Configurable via `app.image.max-size` in application.yml

## Folder Organization

Images are organized in Cloudinary as:

```
products/{productId}/{filename}
```

This keeps images organized by product for easy management and retrieval.

## Error Responses

### Invalid File

```json
{
  "timestamp": "2025-01-27T10:30:00",
  "status": 400,
  "error": "Invalid image file. Supported formats: JPG, PNG, GIF, WebP",
  "path": "/api/v1/products/1/images/upload"
}
```

### Product Not Found

```json
{
  "timestamp": "2025-01-27T10:30:00",
  "status": 404,
  "error": "The requested resource was not found.",
  "path": "/api/v1/products/999/images/upload"
}
```

### File Too Large

```json
{
  "timestamp": "2025-01-27T10:30:00",
  "status": 400,
  "error": "Image file exceeds maximum allowed size of 5MB",
  "path": "/api/v1/products/1/images/upload"
}
```

## Database Migration

Run Flyway migration:

```sql
-- V7__add_cloudinary_fields_to_product_image.sql
-- Adds image_url, cloudinary_public_id, alt_text, created_at, updated_at fields
```

## Security Considerations

1. **Authentication**: Endpoints require `ADMIN` or `SELLER` role
2. **File Validation**:
   - MIME type validation
   - File extension validation
   - File size validation
3. **Cloudinary Security**:
   - Uses secure HTTPS URLs
   - API credentials stored in environment variables
   - Never expose API secret in client-side code
4. **Data Integrity**:
   - Images linked to products via foreign key
   - Cascading delete removes images when product is deleted
   - Atomic transactions ensure consistency

## Performance Optimization

1. **Database Indexes**:
   - `idx_cloudinary_public_id` - Fast lookup by Cloudinary ID
   - `idx_product_id_image` - Fast retrieval of product images
   - `idx_created_at` - Sorting and filtering by date

2. **Caching** (Future Enhancement):
   - Cache product images in Redis
   - Cache Cloudinary URLs

3. **Async Upload** (Future Enhancement):
   - Implement async file uploads for better performance
   - Add progress tracking for large files

## Dependencies

```xml
<!-- Cloudinary HTTP5 Client -->
<dependency>
    <groupId>com.cloudinary</groupId>
    <artifactId>cloudinary-http5</artifactId>
    <version>2.0.0</version>
</dependency>

<!-- Environment Variables Management -->
<dependency>
    <groupId>io.github.cdimascio</groupId>
    <artifactId>dotenv-java</artifactId>
    <version>2.2.4</version>
</dependency>
```

## Testing

### Unit Tests

- Test image validation (size, type, extension)
- Test mapper functions
- Test service layer

### Integration Tests

- Test end-to-end upload flow
- Test Cloudinary integration
- Test error handling

### Manual Testing

1. Upload single image via Postman
2. Upload multiple images
3. Verify images appear in Cloudinary dashboard
4. Verify images appear in database
5. Delete images and verify removal from both Cloudinary and database
6. Test validation with invalid files

## Troubleshooting

### Common Issues

**Issue**: 401 Unauthorized from Cloudinary

- **Cause**: Invalid API credentials
- **Solution**: Verify `CLOUDINARY_API_KEY` and `CLOUDINARY_API_SECRET` in environment

**Issue**: File size validation failing unexpectedly

- **Cause**: Incorrect `app.image.max-size` configuration
- **Solution**: Check application.yml for correct byte value (5242880 = 5MB)

**Issue**: Images not appearing in Cloudinary

- **Cause**: Network issues or misconfigured cloud name
- **Solution**: Check `CLOUDINARY_CLOUD_NAME` and network connectivity

**Issue**: Database schema mismatch

- **Cause**: Migration not applied
- **Solution**: Run Flyway migration or check migration file order

## Future Enhancements

1. **Image Optimization**
   - Automatic image resizing
   - Format conversion (e.g., to WebP)
   - Thumbnail generation

2. **Advanced Features**
   - Batch uploads
   - Drag-and-drop upload UI
   - Image cropping/editing
   - Image tagging

3. **Performance**
   - Async uploads
   - Upload progress tracking
   - Caching layer

4. **Administration**
   - Image management dashboard
   - Bulk operations
   - Image analytics

## References

- [Cloudinary Documentation](https://cloudinary.com/documentation)
- [Cloudinary Java SDK](https://github.com/cloudinary/cloudinary_java)
- [Spring Boot File Upload](https://spring.io/blog/2015/12/21/spring-framework-4-2-ga-released)
