Dưới đây là **bản phác thảo đầy đủ các thành phần (UI Components + Pages)** mà bạn cần **chuẩn bị trước khi bắt đầu design trên Figma** cho dự án TechMart Platform — dựa theo toàn bộ tài liệu nghiệp vụ bạn đã đưa.

---

# ✅ **1. Site Map (Bản đồ trang tổng quan)**

Giúp bạn biết toàn bộ cấu trúc website trước khi đi vào design từng màn hình.

### **1.1. Customer (User-facing)**

* **Home**
* **Category Listing**
* **Product Listing + Filters**
* **Product Detail**
* **Search Result**
* **Cart**
* **Checkout**

  * Choose Address
  * Choose Payment
  * Review Summary
* **Order Success**
* **User Account**

  * Profile Info
  * Address Book
  * Order History
  * Order Detail
  * Notification Center
* **Auth**

  * Login
  * Register (chuyển Keycloak)
  * Forgot Password (Keycloak)
* **Static Pages**

  * About
  * Contact
  * Privacy Policy
  * Terms

### **1.2. Admin**

* Dashboard
* Product management
* Category management
* Brand management
* Inventory management
* Order management
* User management
* Flash sale / Promotion (optional)
* Notification management

---

# ✅ **2. Danh sách tất cả UI Components cần thiết**

(Chuẩn bị trước trong Figma để tái sử dụng)

---

## **2.1. Header Components**

* Logo
* Search bar (autocomplete optional)
* Navigation menu
* Shopping cart icon (+ badge)
* User icon (dropdown: Login, Register, My Account, Logout)
* Category mega menu (optional)

---

## **2.2. Footer Components**

* Company info
* Contact info
* Social links
* Link: Terms, Privacy, Help
* Newsletter subscription

---

## **2.3. Product Components**

* Product card (ảnh, tên, giá, rating)
* Product card small (trong giỏ hàng)
* Product variant selector (màu, dung lượng)
* Product image gallery + zoom
* Product badges (Flash sale, New, Hot)
* Breadcrumbs

---

## **2.4. Form Components**

* Input (text, email, password)
* Dropdown
* Checkbox
* Radio button
* Number counter (quantity selector)
* Address form block
* Payment option selector
* Coupon input field
* Toggle switch (active/inactive)

---

## **2.5. Cart Components**

* Cart item row
* Price summary block
* Empty cart placeholder

---

## **2.6. Checkout Components**

* Address selector card
* Payment method card
* Order summary block
* Success screen block (Order completed)

---

## **2.7. Order Components**

* Order list row (status + price + date)
* Order status timeline
* Order detail item list
* Cancellation dialog

---

## **2.8. Admin Components**

* Sidebar navigation
* Top admin toolbar
* Table (sortable, filterable)
* Pagination
* Modal (add/edit)
* Tags (status: Active, Inactive, Out-of-stock…)
* Upload image component
* Multi-upload gallery layout
* Form layout presets

---

# ✅ **3. Danh sách Màn hình cần thiết (Figma Screens)**

Dựa theo tài liệu nghiệp vụ + user flow của E-commerce.

---

## **3.1. Customer – Storefront**

### **Trang chủ (Homepage)**

* Banner slider
* Category quick links
* Featured products
* Top selling products
* Flash sale block (optional)

### **Trang danh mục (Category Page)**

* Category header
* Filter sidebar (brand, price, attributes)
* Product grid
* Pagination / infinite scroll

### **Kết quả tìm kiếm (Search Results)**

### **Chi tiết sản phẩm (Product Detail)**

* Image gallery
* Rating summary
* Variant selection (SKU, color, capacity)
* Tech specs
* Similar products
* Add to cart CTA

---

## **3.2. Giỏ hàng & Thanh toán**

### **Cart Page**

* Cart product list
* Price summary
* Recommend upsell (optional)

### **Checkout Page**

* Address selection
* Add new address form
* Payment method selection
* Order summary
* Coupon apply
* Place order button

### **Order Success Page**

* Order number
* Button: View order

---

## **3.3. Tài khoản Người dùng (My Account)**

### **My Account Overview**

* Profile menu
* Quick actions

### **Profile Info**

* Name, phone, email
* Edit form

### **Address Book**

* List tất cả address
* Add / Edit / Delete

### **Order History**

* Filter by status
* Order cards

### **Order Detail**

* Timeline
* Items
* Shipping address
* Payment status
* Cancel order (if allowed)

### **Notification Center**

* Read/unread badge
* Mark all as read

---

## **3.4. Authentication (Keycloak UI tùy chỉnh)**

* Login page
* Register page
* Verify email
* Reset password

---

# ✅ **4. Admin Dashboard Screens**

## **4.1. Dashboard**

* Sales summary
* Recent orders
* Low stock warnings
* Top-selling products

## **4.2. Product Management**

* Product list
* Add product
* Edit product
* Upload images
* Variant table (SKU, price, stock)

## **4.3. Category Management**

* Category tree
* Add/Edit category
* Enable/Disable

## **4.4. Brand Management**

* Logos
* Add/Edit brand

## **4.5. Inventory**

* Stock levels
* Restock form
* Low stock alerts

## **4.6. Order Management**

* All orders table
* Order detail (admin view)
* Update status
* Add internal note

## **4.7. Coupon / Flash sale (optional)**

## **4.8. User Management**

* User list
* User detail

---

# ✅ **5. Bộ Style Guide để chuẩn bị trước khi design**

Bạn cần tạo trong Figma:

### **5.1. Colors**

* Primary
* Secondary
* Success / Warning / Danger
* Neutral / Gray scale
* Background & Surface

### **5.2. Typography**

* Heading 1–6
* Body (regular, medium, bold)
* Caption
* Tag / Badge text

### **5.3. UI Elements**

* Buttons (primary, secondary, outline)
* Inputs
* Cards
* Shadows
* Grid system (12 columns)

### **5.4. Icon set**

* Cart
* Search
* User
* Menu
* Category icons
* Order status icons
