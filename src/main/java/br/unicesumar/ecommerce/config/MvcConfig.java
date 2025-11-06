package br.unicesumar.ecommerce.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    // Pega o caminho do application.properties
    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Resolve o caminho absoluto
        Path uploadDir = Paths.get(uploadPath);
        String uploadAbsolutePath = uploadDir.toFile().getAbsolutePath();

        // Configura o handler
        registry
            .addResourceHandler("/uploads/**")
            //
            // AQUI ESTÁ A CORREÇÃO:
            //
            // Antes: "file:/" + uploadAbsolutePath + "/"  (ERRADO: criava file:///... ou file://home...)
            // Agora: "file:" + uploadAbsolutePath + "/"   (CORRETO: cria file:/home/kepler/...)
            //
            .addResourceLocations("file:" + uploadAbsolutePath + "/");
    }
}
