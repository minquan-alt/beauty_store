<h1>Adeline Beauty Store</h1> 

Adeline Beauty Store là một ứng dụng web thương mại điện tử dành cho các sản phẩm làm đẹp, được xây dựng bằng Spring Boot.

<h2>🚀 Công nghệ sử dụng </h2>

- Java 17
- Spring Boot 3.4.3
- Maven
- Thymeleaf 
- CSS ( framework: bootstrap,...)
- Git / GitHub

<h2>📂 Cấu trúc thư mục</h2>

<pre>
/adeline
│── src/
│   ├── main/
│   │   ├── java/com/beautystore/adeline/ (Code Java chính)
│   │   ├── resources/
│   │   │   ├── static/ (CSS, JS, hình ảnh...)
│   │   │   ├── templates/ (HTML / Thymeleaf)
│   │   │   ├── application.properties
│── pom.xml (Cấu hình Maven)
│── README.md
</pre>


<h2>💻 Cách chạy dự án</h2>
Tiền điều kiện: <br>
  - Đã cài đặt JDK 17, Git, Maven Apache - 4.0.0 (hoặc Maven Extension nếu dùng VS Code) <br>

<h4>1️⃣ Clone repository</h4>

<pre>
  Terminal:
    git clone https://github.com/minquan-alt/beauty_store.git
    cd beauty_store
</pre>

<h4>2️⃣ Chạy dự án bằng Maven</h4>
<pre>Tạo file cấu hình application.properties</pre>
<pre>Terminal: mvn spring-boot:run</pre>
<pre>Nếu dùng IDE như IntelliJ: Chạy file AdelineBeautyStoreApplication.java trong package com.beautystore.adeline</pre>

<h4>3️⃣ Truy cập ứng dụng</h4> 

Mở trình duyệt và vào http://localhost:8080 (hoặc số PORT bạn cấu hình trong application.properties
