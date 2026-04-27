package com.hutech.buiduongtin.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp", ".gif");
    private static final String UPLOAD_DIR = "src/main/resources/static/images";

    public String store(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
        String extension = extractExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalStateException("Only image files .jpg, .jpeg, .png, .webp, .gif are allowed");
        }

        Path uploadPath = getUploadPath();
        Files.createDirectories(uploadPath);

        String safeBaseName = extractBaseName(originalFilename)
                .replaceAll("[^a-zA-Z0-9-_]", "_")
                .replaceAll("_+", "_");
        if (safeBaseName.isBlank()) {
            safeBaseName = "image";
        }

        String fileName = UUID.randomUUID() + "_" + safeBaseName + extension;
        Files.copy(imageFile.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    public boolean isStorageReady() {
        try {
            Path uploadPath = getUploadPath();
            Files.createDirectories(uploadPath);
            return Files.exists(uploadPath) && Files.isDirectory(uploadPath) && Files.isWritable(uploadPath);
        } catch (IOException ex) {
            return false;
        }
    }

    public String getStorageLocation() {
        return getUploadPath().toAbsolutePath().toString();
    }

    private Path getUploadPath() {
        return Paths.get(UPLOAD_DIR);
    }

    private String extractExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) {
            throw new IllegalStateException("Image file extension is required");
        }
        return filename.substring(dotIndex).toLowerCase(Locale.ROOT);
    }

    private String extractBaseName(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex < 0 ? filename : filename.substring(0, dotIndex);
    }
}
