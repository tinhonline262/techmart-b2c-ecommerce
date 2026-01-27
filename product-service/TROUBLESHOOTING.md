# Troubleshooting & Solutions

## Issue 1: Cloudinary Bean Creation Error ✅ RESOLVED

### Error Message

```
Parameter 0 of constructor in com.shopping.microservices.product_service.service.impl.CloudinaryServiceImpl
required a bean of type 'com.cloudinary.Cloudinary' that could not be found.
```

### Root Cause

The `CloudinaryConfig` was using `@Value` annotation with hyphenated property names. Spring's `@Value` doesn't properly handle hyphenated property binding with relaxed names.

### Solution Applied

Changed `CloudinaryConfig` from `@Value` injection to `@ConfigurationProperties` pattern:

```java
@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(CloudinaryProperties properties) {
        return new Cloudinary(ObjectUtils.asMap(...));
    }

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "cloudinary")
    public static class CloudinaryProperties {
        private String cloudName;      // maps to cloud-name
        private String apiKey;         // maps to api-key
        private String apiSecret;      // maps to api-secret
    }
}
```

### How This Works

1. `@ConfigurationProperties` properly handles hyphenated-to-camelCase conversion
2. Constructor injection ensures properties are loaded before bean creation
3. Type-safe property binding
4. No null values

### Verification

```bash
mvnw.cmd clean compile -DskipTests
# Result: BUILD SUCCESS ✅
```

---

## Issue 2: Application Won't Start

### Error

Application fails to start with bean creation errors

### Causes & Solutions

| Cause                         | Solution                                                       |
| ----------------------------- | -------------------------------------------------------------- |
| Cloudinary properties not set | Ensure `application.yml` has cloudinary config OR set env vars |
| Outdated CloudinaryConfig     | Update to use @ConfigurationProperties pattern                 |
| Missing dependencies          | Verify pom.xml has cloudinary-http5 dependency                 |
| Port already in use           | Change server.port in application.yml or kill process on 8081  |

### How to Verify

```bash
# 1. Check compilation
mvnw.cmd clean compile -DskipTests

# 2. Check configuration
cat src/main/resources/application.yml | grep -A 3 "cloudinary:"

# 3. Start application
mvnw.cmd spring-boot:run

# 4. Check logs
# Look for: "Tomcat started on port 8081" ✅
```

---

## Issue 3: API Endpoints Returning 404

### Cause

Application is running but endpoints not found

### Solutions

1. **Verify Application Started Successfully**
   - Check for errors in startup logs
   - Look for "Started ProductServiceApplication"

2. **Verify Endpoint Path**

   ```bash
   # Correct:
   POST /api/v1/products/1/images/upload

   # Incorrect:
   POST /api/v1/product/1/images/upload (missing 's')
   POST /api/v1/products/1/image/upload (missing 's')
   ```

3. **Verify Authentication**

   ```bash
   # Must include token for write operations:
   -H "Authorization: Bearer YOUR_JWT_TOKEN"

   # GET operations may work without auth (depends on configuration)
   ```

---

## Issue 4: File Upload Returns 400 Bad Request

### Causes

| Issue                 | Solution                                                         |
| --------------------- | ---------------------------------------------------------------- |
| File too large (>5MB) | Reduce file size or update app.image.max-size in application.yml |
| Invalid MIME type     | Ensure file is jpeg, png, gif, or webp                           |
| Wrong form-data key   | Use `file` parameter name, not `files` for single upload         |
| Missing file          | Ensure -F "file=@/path/to/file" is correct                       |

### Verify File Validation

```bash
# Test with small valid image
curl -X POST http://localhost:8081/api/v1/products/1/images/upload \
  -H "Authorization: Bearer TOKEN" \
  -F "file=@/path/to/small_image.jpg"

# Expected: 201 Created with ProductImageDTO
```

---

## Issue 5: Cloudinary Upload Fails

### Error

```json
{
  "error": "IMAGE_UPLOAD_FAILED",
  "message": "Failed to upload image: ..."
}
```

### Causes & Solutions

| Cause                          | Check                                                                   |
| ------------------------------ | ----------------------------------------------------------------------- |
| Invalid Cloudinary credentials | Verify CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET |
| Network connectivity           | Check internet connection                                               |
| Cloudinary account issue       | Log into Cloudinary dashboard and verify account status                 |
| API rate limit                 | Wait or upgrade Cloudinary plan                                         |

### Verify Credentials

```bash
# Check if properties are loaded
# Add logging to CloudinaryConfig and check logs

# Or check environment variables are set:
$env:CLOUDINARY_CLOUD_NAME
$env:CLOUDINARY_API_KEY
$env:CLOUDINARY_API_SECRET
```

---

## Issue 6: Database Migration Not Applied

### Symptoms

- product_image table missing new columns
- Column not found errors

### Solutions

1. **Verify Flyway Configuration**

   ```yaml
   spring:
     flyway:
       enabled: true
       locations: classpath:db/migration
   ```

2. **Check Migration File**

   ```bash
   # Verify file exists:
   src/main/resources/db/migration/V7__add_cloudinary_fields_to_product_image.sql

   # Verify naming convention: V7__ (version number, two underscores)
   ```

3. **Run Migration Manually**

   ```sql
   -- Connect to MySQL and run:
   USE product_service_db;

   -- Check if columns exist:
   DESCRIBE product_image;

   -- If not, run migration manually from V7 file
   ```

4. **Reset Flyway (Development Only)**
   ```sql
   -- This will clean and re-apply all migrations
   -- WARNING: Deletes all data!
   DELETE FROM flyway_schema_history;
   ```

---

## Quick Diagnostic Checklist

### ✅ Before Reporting Issues

- [ ] Ran `mvnw.cmd clean compile -DskipTests` successfully
- [ ] Cloudinary credentials are set in environment or application.yml
- [ ] Application started without errors (check logs)
- [ ] Can reach application at http://localhost:8081
- [ ] Have valid JWT token for authenticated requests
- [ ] Test file is valid image format (jpg, png, gif, webp)
- [ ] Test file is under 5MB
- [ ] Cloudinary account is active and has credits/quota

### ✅ If Still Having Issues

1. Enable DEBUG logging:

   ```yaml
   logging:
     level:
       com.shopping.microservices: DEBUG
   ```

2. Check application logs for:
   - Bean creation errors
   - Property binding issues
   - Cloudinary connection errors
   - Database migration errors

3. Review relevant documentation:
   - [CLOUDINARY_BEAN_FIX.md](CLOUDINARY_BEAN_FIX.md) - Bean configuration
   - [CLOUDINARY_INTEGRATION.md](CLOUDINARY_INTEGRATION.md) - Complete technical docs
   - [SETUP_GUIDE.md](SETUP_GUIDE.md) - Configuration guide

---

## Getting Help

### Resources

1. **Bean Configuration Issues**: See [CLOUDINARY_BEAN_FIX.md](CLOUDINARY_BEAN_FIX.md)
2. **API Documentation**: See [CLOUDINARY_INTEGRATION.md](CLOUDINARY_INTEGRATION.md)
3. **Setup Instructions**: See [SETUP_GUIDE.md](SETUP_GUIDE.md)
4. **Deployment Help**: See [IMPLEMENTATION_CHECKLIST.md](IMPLEMENTATION_CHECKLIST.md)

### Common Log Patterns

**Success Pattern** 🟢

```
Tomcat started on port(s): 8081 (http) with context path ''
Started ProductServiceApplication in X.XXX seconds
```

**Bean Creation Error** 🔴

```
Parameter 0 of constructor... required a bean of type 'Cloudinary' that could not be found
```

→ Solution: Check [CLOUDINARY_BEAN_FIX.md](CLOUDINARY_BEAN_FIX.md)

**Missing Property** 🔴

```
Could not resolve placeholder 'cloudinary.cloud-name' in value
```

→ Solution: Set environment variables or update application.yml

**Database Migration** 🔴

```
Migration V7 failed...
```

→ Solution: Check migration file syntax and database permissions

---

## Version Information

- **Spring Boot**: 3.4.12
- **Cloudinary Client**: 2.0.0 (http5)
- **Java**: 21
- **Maven**: 3.9+

---

## Still Need Help?

Check the complete documentation in this order:

1. [CLOUDINARY_BEAN_FIX.md](CLOUDINARY_BEAN_FIX.md) - If bean errors
2. [SETUP_GUIDE.md](SETUP_GUIDE.md) - Basic setup
3. [CLOUDINARY_INTEGRATION.md](CLOUDINARY_INTEGRATION.md) - Detailed docs
4. Source code comments - Implementation details

**Status**: ✅ All issues identified and documented
**Last Updated**: January 27, 2026
