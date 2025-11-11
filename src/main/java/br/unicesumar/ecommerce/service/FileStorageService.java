package br.unicesumar.ecommerce.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${upload.path}") String uploadPath) {
        this.fileStorageLocation = Paths.get(uploadPath)
            .toAbsolutePath()
            .normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException(
                "Não foi possível criar o diretório para salvar os arquivos.",
                ex
            );
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(
            file.getOriginalFilename()
        );

        try {
            if (originalFileName.contains("..")) {
                throw new RuntimeException(
                    "Nome de arquivo inválido: " + originalFileName
                );
            }

            String fileExtension = "";
            try {
                fileExtension = originalFileName.substring(
                    originalFileName.lastIndexOf(".")
                );
            } catch (Exception e) {
                fileExtension = "";
            }
            String uniqueFileName =
                UUID.randomUUID().toString() + fileExtension;

            Path targetLocation = this.fileStorageLocation.resolve(
                uniqueFileName
            );

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(
                    inputStream,
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
                );
            }

            return uniqueFileName;
        } catch (IOException ex) {
            throw new RuntimeException(
                "Não foi possível salvar o arquivo " + originalFileName,
                ex
            );
        }
    }
}
