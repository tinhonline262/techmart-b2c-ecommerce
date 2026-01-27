# Manual API Testing Guide (cURL)

## 📋 Prerequisites

Before running API tests, ensure:

1. ✅ Application is running on `http://localhost:8081`
2. ✅ Cloudinary credentials are configured
3. ✅ Database is running and migrations applied
4. ✅ You have a valid JWT token (or get one from Identity Service)

---

## 🔑 Getting JWT Token

### Get Token from Identity Service

```bash
# Login request
curl -X POST http://localhost:8082/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "seller@techmart.com",
    "password": "password123"
  }'

# Response:
# {
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "expiresIn": 3600
# }

# Save for later use:
export JWT_TOKEN="your_token_here"
```

### Test Token (Development Only)

If you don't have auth service running:

```bash
# You'll get 401 Unauthorized, which is expected
# To bypass auth in development, comment out @PreAuthorize in controller
```

---

## 🧪 Test Scenarios

### 1️⃣ Upload Single Image (Success)

**Endpoint**: `POST /api/v1/products/{productId}/images/upload`

**Requirements**:

- SELLER or ADMIN role
- Valid product ID
- Valid image file (jpg, png, gif, webp)
- File size < 5MB

**Test Case**:

```bash
# Using a real image file
curl -X POST http://localhost:8081/api/v1/products/1/images/upload \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -F "file=@/path/to/image.jpg"

# Response (201 Created):
# {
#   "id": 100,
#   "productId": 1,
#   "imageUrl": "https://res.cloudinary.com/...",
#   "cloudinaryPublicId": "products/abc123",
#   "altText": "Uploaded product image",
#   "createdAt": "2026-01-27T10:30:00",
#   "updatedAt": "2026-01-27T10:30:00"
# }
```

**Success Indicators**:

- ✅ HTTP Status: 201 Created
- ✅ Response contains `imageUrl`
- ✅ Response contains `cloudinaryPublicId`
- ✅ `id` is auto-generated

---

### 2️⃣ Upload With Invalid Format

**Test Case**: Try uploading a PDF

```bash
curl -X POST http://localhost:8081/api/v1/products/1/images/upload \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -F "file=@document.pdf"

# Response (400 Bad Request):
# {
#   "error": "INVALID_IMAGE_FILE",
#   "message": "Invalid image file: PDF files not allowed"
# }
```

**Success Indicators**:

- ✅ HTTP Status: 400 Bad Request
- ✅ Error code matches validation rule

---

### 3️⃣ Upload File Too Large

**Test Case**: Try uploading 6MB image

```bash
# Create a 6MB test file:
# dd if=/dev/urandom of=large_image.jpg bs=1M count=6

curl -X POST http://localhost:8081/api/v1/products/1/images/upload \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -F "file=@large_image.jpg"

# Response (400 Bad Request):
# {
#   "error": "IMAGE_FILE_TOO_LARGE",
#   "message": "File size exceeds 5MB limit"
# }
```

---

### 4️⃣ Upload Without Authentication

**Test Case**: No JWT token

```bash
curl -X POST http://localhost:8081/api/v1/products/1/images/upload \
  -F "file=@image.jpg"

# Response (401 Unauthorized):
# {
#   "timestamp": "2026-01-27T10:30:00",
#   "status": 401,
#   "error": "Unauthorized",
#   "message": "Full authentication required"
# }
```

---

### 5️⃣ Upload Multiple Images

**Endpoint**: `POST /api/v1/products/{productId}/images/upload-multiple`

**Test Case**:

```bash
curl -X POST http://localhost:8081/api/v1/products/1/images/upload-multiple \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -F "files=@image1.jpg" \
  -F "files=@image2.png" \
  -F "files=@image3.gif"

# Response (201 Created):
# [
#   {
#     "id": 101,
#     "productId": 1,
#     "imageUrl": "https://res.cloudinary.com/.../image1.jpg",
#     "cloudinaryPublicId": "products/xyz1",
#     ...
#   },
#   {
#     "id": 102,
#     "productId": 1,
#     "imageUrl": "https://res.cloudinary.com/.../image2.png",
#     "cloudinaryPublicId": "products/xyz2",
#     ...
#   },
#   {
#     "id": 103,
#     "productId": 1,
#     "imageUrl": "https://res.cloudinary.com/.../image3.gif",
#     "cloudinaryPublicId": "products/xyz3",
#     ...
#   }
# ]
```

**Success Indicators**:

- ✅ HTTP Status: 201 Created
- ✅ Returns array with all 3 images
- ✅ Each image has unique `id` and `cloudinaryPublicId`

---

### 6️⃣ Get All Images for Product

**Endpoint**: `GET /api/v1/products/{productId}/images`

**Test Case**:

```bash
curl -X GET http://localhost:8081/api/v1/products/1/images

# Response (200 OK):
# [
#   {
#     "id": 100,
#     "productId": 1,
#     "imageUrl": "https://res.cloudinary.com/.../img1.jpg",
#     "cloudinaryPublicId": "products/abc1",
#     "altText": "Product image 1",
#     "createdAt": "2026-01-27T10:15:00",
#     "updatedAt": "2026-01-27T10:15:00"
#   },
#   {
#     "id": 101,
#     "productId": 1,
#     "imageUrl": "https://res.cloudinary.com/.../img2.jpg",
#     "cloudinaryPublicId": "products/abc2",
#     ...
#   }
# ]
```

**Success Indicators**:

- ✅ HTTP Status: 200 OK
- ✅ Returns array of images
- ✅ Each image includes metadata

---

### 7️⃣ Get Specific Image

**Endpoint**: `GET /api/v1/products/{productId}/images/{imageId}`

**Test Case**:

```bash
curl -X GET http://localhost:8081/api/v1/products/1/images/100

# Response (200 OK):
# {
#   "id": 100,
#   "productId": 1,
#   "imageUrl": "https://res.cloudinary.com/.../img.jpg",
#   "cloudinaryPublicId": "products/abc",
#   "altText": "Product image",
#   "createdAt": "2026-01-27T10:15:00",
#   "updatedAt": "2026-01-27T10:15:00"
# }
```

---

### 8️⃣ Get Non-Existent Image

**Test Case**:

```bash
curl -X GET http://localhost:8081/api/v1/products/1/images/99999

# Response (404 Not Found):
# {
#   "error": "IMAGE_NOT_FOUND",
#   "message": "Image not found"
# }
```

---

### 9️⃣ Delete Image

**Endpoint**: `DELETE /api/v1/products/{productId}/images/{imageId}`

**Requirements**: SELLER or ADMIN role

**Test Case**:

```bash
curl -X DELETE http://localhost:8081/api/v1/products/1/images/100 \
  -H "Authorization: Bearer $JWT_TOKEN"

# Response (204 No Content):
# (empty body)
```

**Success Indicators**:

- ✅ HTTP Status: 204 No Content
- ✅ Image removed from Cloudinary
- ✅ Image removed from database

---

### 🔟 Delete Without Permission

**Test Case**: Customer without SELLER role

```bash
curl -X DELETE http://localhost:8081/api/v1/products/1/images/100 \
  -H "Authorization: Bearer $CUSTOMER_TOKEN"

# Response (403 Forbidden):
# {
#   "timestamp": "2026-01-27T10:35:00",
#   "status": 403,
#   "error": "Forbidden",
#   "message": "Access Denied"
# }
```

---

## 📊 Complete Test Script

Save as `test_api.sh`:

```bash
#!/bin/bash

# Configuration
API_BASE="http://localhost:8081"
JWT_TOKEN="${1:-}"  # Pass as argument or set JWT_TOKEN env var
PRODUCT_ID=1

if [ -z "$JWT_TOKEN" ]; then
    echo "⚠️  Warning: No JWT token provided"
    echo "Usage: ./test_api.sh <jwt_token>"
    echo "Proceeding with read-only tests..."
fi

echo "=========================================="
echo "TESTING CLOUDINARY IMAGE API"
echo "=========================================="
echo ""

# Test 1: Get all images
echo "1️⃣  GET /api/v1/products/$PRODUCT_ID/images"
curl -s -X GET "$API_BASE/api/v1/products/$PRODUCT_ID/images" | jq '.'
echo ""

# Test 2: Get specific image
echo "2️⃣  GET /api/v1/products/$PRODUCT_ID/images/100"
curl -s -X GET "$API_BASE/api/v1/products/$PRODUCT_ID/images/100" | jq '.'
echo ""

if [ -n "$JWT_TOKEN" ]; then
    # Test 3: Upload image
    echo "3️⃣  POST /api/v1/products/$PRODUCT_ID/images/upload"

    # Create test image if it doesn't exist
    if [ ! -f "test_image.jpg" ]; then
        echo "Creating test image..."
        convert -size 100x100 xc:blue test_image.jpg
    fi

    curl -s -X POST "$API_BASE/api/v1/products/$PRODUCT_ID/images/upload" \
      -H "Authorization: Bearer $JWT_TOKEN" \
      -F "file=@test_image.jpg" | jq '.'
    echo ""

    # Test 4: Upload multiple images
    echo "4️⃣  POST /api/v1/products/$PRODUCT_ID/images/upload-multiple"

    if [ ! -f "test_image2.png" ]; then
        echo "Creating test image 2..."
        convert -size 100x100 xc:red test_image2.png
    fi

    curl -s -X POST "$API_BASE/api/v1/products/$PRODUCT_ID/images/upload-multiple" \
      -H "Authorization: Bearer $JWT_TOKEN" \
      -F "files=@test_image.jpg" \
      -F "files=@test_image2.png" | jq '.'
    echo ""

    # Test 5: Delete image
    echo "5️⃣  DELETE /api/v1/products/$PRODUCT_ID/images/100"
    curl -s -X DELETE "$API_BASE/api/v1/products/$PRODUCT_ID/images/100" \
      -H "Authorization: Bearer $JWT_TOKEN" \
      -w "\nHTTP Status: %{http_code}\n"
    echo ""
fi

echo "=========================================="
echo "TESTS COMPLETED"
echo "=========================================="
```

Run the script:

```bash
chmod +x test_api.sh
./test_api.sh $JWT_TOKEN
```

---

## 🔍 Verification Checklist

After running tests, verify:

- [ ] All requests return expected HTTP status codes
- [ ] Upload returns valid Cloudinary URLs
- [ ] Images appear in Cloudinary dashboard
- [ ] Deleted images are removed from Cloudinary
- [ ] Database contains image records
- [ ] Authentication/authorization is enforced
- [ ] File validation works correctly
- [ ] Error messages are clear and helpful

---

## 🐛 Debugging Failed Tests

### Check Application Logs

```bash
# Terminal running the app:
tail -f logs/product-service.log

# Look for:
# - Cloudinary configuration errors
# - Database connection issues
# - Security/authentication errors
# - File upload processing logs
```

### Verify Cloudinary Configuration

```bash
# Check environment variables
echo $CLOUDINARY_CLOUD_NAME
echo $CLOUDINARY_API_KEY
echo $CLOUDINARY_API_SECRET

# Or check application.yml:
grep -A 3 "cloudinary:" src/main/resources/application.yml
```

### Check Database

```bash
# Connect to MySQL
mysql -u root -p techmart_product_service

# Check if table exists:
SHOW TABLES;
DESCRIBE product_image;

# Check records:
SELECT * FROM product_image;
```

---

## 📈 Performance Testing

### Load Test (Upload 10 images)

```bash
for i in {1..10}; do
  echo "Uploading image $i..."
  curl -X POST http://localhost:8081/api/v1/products/1/images/upload \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -F "file=@test_image.jpg"
done
```

### Concurrent Upload Test

```bash
for i in {1..5}; do
  (
    curl -X POST http://localhost:8081/api/v1/products/$i/images/upload \
      -H "Authorization: Bearer $JWT_TOKEN" \
      -F "file=@test_image.jpg"
  ) &
done
wait
```

---

## ✅ Expected Outcomes

| Test                  | Expected Status | Key Validation                        |
| --------------------- | --------------- | ------------------------------------- |
| Upload valid image    | 201             | Contains imageUrl, cloudinaryPublicId |
| Upload invalid format | 400             | Error code INVALID_IMAGE_FILE         |
| Upload too large      | 400             | Error code IMAGE_FILE_TOO_LARGE       |
| Get images            | 200             | Array of ProductImageDTOs             |
| Get non-existent      | 404             | Error code IMAGE_NOT_FOUND            |
| Delete image          | 204             | Empty response                        |
| Delete unauthorized   | 403             | Forbidden error                       |
| No auth               | 401             | Unauthorized error                    |

---

**Status**: ✅ API testing guide complete
**Last Updated**: January 27, 2026
