package school.sptech.megusta.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
security = @SecurityRequirement(name = "bearerAuth"),
info = @Info(
        title = "Projeto Me Gusta",
        description = "Documentação da API Rest do projeto de Extensão com o beneficiário Me Gusta Fogazzas Artesanais",
        contact = @Contact(
                name = "Breno",
                url = "https://github.com/Miralhaz/me-gusta-backend",
                email = "breno.costa@sptech.school"
        ),
        license = @License(name = "UNLICENSED"),
        version = "1.0.0"
)
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)

public class OpenApiConfig {

}
