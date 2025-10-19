package nhom17.OneShop.service.impl;

import nhom17.OneShop.service.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService {

    // Gốc lưu trữ tĩnh: map /uploads/** -> file:uploads/ (WebMvcConfigurer)
    private final Path rootStorageFolder = Paths.get("OneShop", "uploads");

    public StorageServiceImpl() {
        try {
            Files.createDirectories(this.rootStorageFolder);
        } catch (IOException e) {
            throw new RuntimeException("Cannot initialize storage", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subFolder) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File rỗng");
            }

            // Tạo thư mục uploads/<subFolder> nếu chưa có
            Path destinationFolder = (subFolder == null || subFolder.isBlank())
                    ? this.rootStorageFolder
                    : this.rootStorageFolder.resolve(subFolder).normalize();
            Files.createDirectories(destinationFolder);

            // Tạo tên file ngắn gọn + giữ phần mở rộng (nếu có)
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            String base = UUID.randomUUID().toString().replace("-", "");
            String generatedFileName = (ext == null || ext.isBlank())
                    ? base
                    : base + "." + ext.toLowerCase();

            Path destinationFilePath = destinationFolder.resolve(generatedFileName).normalize();

            // Sao chép nội dung (ghi đè nếu trùng)
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
            }

            // QUAN TRỌNG: Trả về CHỈ TÊN FILE để ghép "/uploads/<subFolder>/"+name ở view
            return generatedFileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return;
        try {
            Path file = this.rootStorageFolder.resolve(filePath).normalize();
            Files.deleteIfExists(file);
        } catch (IOException e) {
            System.err.println("Could not delete file: " + filePath + ". Error: " + e.getMessage());
        }
    }
}
