package com.anushka.disaster_backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/jpg");
    private final Path uploadDirectory;

    public FileStorageService(@Value("${app.upload.directory}") String directory) {
        this.uploadDirectory = Path.of(directory).toAbsolutePath().normalize();
    }

    public String store(MultipartFile file) throws IOException {
        if (file.isEmpty()) return null;
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Only JPEG, PNG, and WebP images are supported");
        }
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String extension = original.contains(".") ? original.substring(original.lastIndexOf('.')).toLowerCase(Locale.ROOT) : "";
        if (!Set.of(".jpg", ".jpeg", ".png", ".webp").contains(extension)) {
            throw new IllegalArgumentException("Image file extension is not supported");
        }
        Files.createDirectories(uploadDirectory);
        String filename = UUID.randomUUID() + extension;
        Path destination = uploadDirectory.resolve(filename).normalize();
        if (!destination.startsWith(uploadDirectory)) throw new IllegalArgumentException("Invalid upload filename");
        file.transferTo(destination);
        return "/uploads/" + filename;
    }
}

