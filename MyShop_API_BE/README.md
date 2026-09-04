# 🛒 MyShop_API

MyShop_API là một RESTful API được xây dựng bằng Spring Boot để phục vụ cho hệ thống thương mại điện tử. Dự án hỗ trợ
quản lý người dùng, sản phẩm, đơn hàng, giỏ hàng, thanh toán và xác thực bảo mật.

## 🚀 Công nghệ sử dụng

- Java 17
- Spring Boot 3.3.5
- Spring Security & OAuth2
- Spring Data JPA
- MySQL
- MapStruct
- Lombok
- Swagger (SpringDoc OpenAPI)
- Dotenv (quản lý biến môi trường)

 <!-- JUnit + Spring Test + MockMvc -->

## 📦 Cài đặt

```bash
git clone https://github.com/Buihaidang2k4/MyShop_API.git
cd myshop-api
./mvnw clean install
```

## ⚙️ Cấu hình

Tạo file .env trong thư mục gốc để cấu hình biến môi trường

```bash

DB_URL=jdbc:mysql://localhost:3306/myshop
DB_USERNAME=root
DB_PASSWORD=yourpassword
JWT_SECRET=your_jwt_secret
```

## API documentation

* API documentation is available via Swagger UI at http://localhost:8080/swagger-ui/index.html

## 📚 API Controllers(Swagger-ui)

<img width="1911" height="515" alt="Screenshot 2025-09-23 221055" src="https://github.com/user-attachments/assets/52c4eecc-014d-48b7-941d-d9117ce76851" />
<img width="1832" height="352" alt="Screenshot 2025-09-23 221401" src="https://github.com/user-attachments/assets/b410438f-24d1-46c1-a202-3db16d3acc4c" />
<img width="1857" height="418" alt="Screenshot 2025-09-23 221424" src="https://github.com/user-attachments/assets/7b5a25f8-ad49-4481-821b-745dea47be30" />
<img width="1890" height="565" alt="Screenshot 2025-09-23 221443" src="https://github.com/user-attachments/assets/92f49ccf-1d10-4a1e-aeba-ecb5a25337fe" />
<img width="1844" height="433" alt="Screenshot 2025-09-23 221502" src="https://github.com/user-attachments/assets/aba37eb9-37c7-480c-ac37-16f2c17b6bba" />
<img width="1881" height="365" alt="Screenshot 2025-09-23 221520" src="https://github.com/user-attachments/assets/f1ff338c-3293-45f2-a05b-6a59e7142c60" />
<img width="1859" height="429" alt="Screenshot 2025-09-23 221540" src="https://github.com/user-attachments/assets/6860b34b-6675-4c78-b9d3-8dff9a6788eb" />
<img width="1837" height="414" alt="Screenshot 2025-09-23 221602" src="https://github.com/user-attachments/assets/50f28dd1-80de-4535-9bf4-c1d4641c80e1" />
<img width="1866" height="347" alt="Screenshot 2025-09-23 221617" src="https://github.com/user-attachments/assets/19d8f3f9-d712-479d-b255-5d0e9ba5adc7" />
<img width="1849" height="426" alt="Screenshot 2025-09-23 221632" src="https://github.com/user-attachments/assets/33515177-6094-4443-a087-18b32525935a" />
<img width="1881" height="352" alt="Screenshot 2025-09-23 221648" src="https://github.com/user-attachments/assets/e578f819-57a1-49f6-b7a0-f6390a19ca20" />

## ER-Diagram

![img.png](images/img.png)
=======

## ER-Diagram

<img width="1425" height="698" alt="Screenshot 2025-09-23 220258" src="https://github.com/user-attachments/assets/0aff5177-6193-4cb7-878f-b95fa851f92d" />

> > > > > > > ee2011cda548f7192737c4797eb02c72f4a70596
