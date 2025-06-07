package com.beautystore.adeline.controller;

import com.beautystore.adeline.dto.response.ApiResponse;
import com.beautystore.adeline.dto.response.FileUploadResponse;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private static final String UPLOAD_DIR = "uploads/images/";

    @PostMapping
    public ApiResponse<FileUploadResponse> uploadImage(
            @RequestParam("fileImage") MultipartFile file) {
    
        try {
            if (file == null || file.isEmpty()) {
               throw new AppException(ErrorCode.FILE_IS_EMPTY);
            }
    
            if (file.getSize() > 2 * 1024 * 1024) { //2MB
                throw new AppException(ErrorCode.INVALID_FILE_SIZE);

            }
    
            String contentType = file.getContentType();
            if (contentType == null || 
                !(contentType.equals("image/png") ||
                  contentType.equals("image/jpeg") ||
                  contentType.equals("image/jpg") ||
                  contentType.equals("image/webp"))) {
    
                throw new AppException(ErrorCode.INVALID_FILE_TYPE);

            }
    
            // 4. Tạo thư mục nếu chưa tồn tại
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();
    
            // 5. Sinh tên file mới
            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + "." + ext;
    
            // 6. Ghi file
            Path path = Paths.get(UPLOAD_DIR, fileName);
            Files.write(path, file.getBytes());
    
            // 7. Tạo URL trả về
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/images/")
                    .path(fileName)
                    .toUriString();
    
            // 8. Trả kết quả
            return ApiResponse.<FileUploadResponse>builder()
                    .message("Upload thành công")
                    .result(FileUploadResponse.builder().imageUrl(imageUrl).build())
                    .build();
    
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.<FileUploadResponse>builder()
                    .code(500)
                    .message("Lỗi hệ thống: " + e.getMessage())
                    .build();
        }
    }
    
}
