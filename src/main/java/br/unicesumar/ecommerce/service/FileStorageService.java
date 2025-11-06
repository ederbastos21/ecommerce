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

    // Injeta o caminho do 'application.properties'
    public FileStorageService(@Value("${upload.path}") String uploadPath) {
        // Resolve o caminho para o diretório de uploads
        this.fileStorageLocation = Paths.get(uploadPath)
            .toAbsolutePath()
            .normalize();

        try {
            // Cria o diretório se ele não existir
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException(
                "Não foi possível criar o diretório para salvar os arquivos.",
                ex
            );
        }
    }

    /**
     * Salva o arquivo no disco e retorna o nome único gerado.
     */
    public String storeFile(MultipartFile file) {
        // 1. Limpa e extrai o nome original do arquivo
        String originalFileName = StringUtils.cleanPath(
            file.getOriginalFilename()
        );

        try {
            // 2. Verifica se o nome do arquivo é válido
            if (originalFileName.contains("..")) {
                throw new RuntimeException(
                    "Nome de arquivo inválido: " + originalFileName
                );
            }

            // 3. Gera um nome de arquivo único (para evitar sobreposição)
            // Ex: 8f4d9f6a-1b5e-4b9d-8c1a-5d4a6f7b8c3d-meu-produto.jpg
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

            // 4. Resolve o caminho completo de destino
            Path targetLocation = this.fileStorageLocation.resolve(
                uniqueFileName
            );

            // 5. Copia o arquivo para o destino
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(
                    inputStream,
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
                );
            }

            // 6. Retorna o nome único do arquivo salvo
            return uniqueFileName;
        } catch (IOException ex) {
            throw new RuntimeException(
                "Não foi possível salvar o arquivo " + originalFileName,
                ex
            );
        }
    }
}
