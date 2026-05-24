package school.sptech.megusta.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
public class OpenApiConfig {
        @Bean
        public OpenAPI customOpenAPI() {
                String logoBase64 = "";
                try {
                        byte[] imageBytes = getClass()
                                .getResourceAsStream("/static/images/Logo.png")
                                .readAllBytes();
                        logoBase64 = Base64.getEncoder().encodeToString(imageBytes);
                } catch (Exception e) {
                        // sem logo se não encontrar
                }

                String logoTag = logoBase64.isEmpty()
                        ? ""
                        : "<img src='data:image/png;base64," + logoBase64 + "' width='200' height='200' alt='Me Gusta Logo'>";
                return new OpenAPI()
                        .components(new Components()
                                .addSecuritySchemes("Bearer",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("Bearer")
                                                .bearerFormat("JWT")
                                                .in(SecurityScheme.In.HEADER)))
                        .addSecurityItem(new SecurityRequirement().addList("Bearer"))
                        .info(new Info()
                                .title("Projeto Me Gusta")
                                .version("2.0.0")
                                .description("<div align='center'>"
                                        + logoTag
                                        + "  <h1>Me Gusta Fogazzaria</h1>"
                                        + "</div>\n\n"
                                        + "### Documentação da API do Sistema Me Gusta\n\n"
                                        + "Esta API foi desenvolvida para gerenciar o ecossistema da Me Gusta, uma fogazzaria artesanal. "
                                        + "O sistema permite o controle de usuários, autenticação, gestão de insumos, fornecedores e o cardápio de fogazzas.\n\n"
                                        + "#### Principais Funcionalidades:\n"
                                        + "- **Autenticação:** Controle de acesso via JWT (JSON Web Token).\n"
                                        + "- **Gestão de Usuários:** Cadastro e manutenção de perfis com suporte a upload de imagem.\n"
                                        + "- **Controle de Insumos:** Monitoramento de estoque e unidades de medida.\n"
                                        + "- **Cardápio:** Gestão de sabores e categorias de fogazzas.\n\n"
                                        + "#### Códigos de Resposta Padrão:\n"
                                        + "| Código | Descrição |\n"
                                        + "|---|---|\n"
                                        + "| **200** | Operação realizada com sucesso. |\n"
                                        + "| **201** | Recurso criado com sucesso. |\n"
                                        + "| **204** | Sucesso, mas sem conteúdo de retorno. |\n"
                                        + "| **400** | Erro de validação ou requisição malformada. |\n"
                                        + "| **401** | Não autorizado - Token ausente ou inválido. |\n"
                                        + "| **403** | Proibido - Sem permissão para acessar o recurso. |\n"
                                        + "| **404** | Recurso não encontrado. |\n"
                                        + "| **409** | Conflito - Regra de negócio violada (ex: duplicidade). |\n"
                                        + "| **500** | Erro interno no servidor. |")
                                .contact(new Contact()
                                        .name("Equipe de desenvolvimento Me Gusta")
                                        .url("https://github.com/Miralhaz/me-gusta-backend")
                                        .email("breno.costa@sptech.school"))
                                .license(new License()
                                        .name("UNLICENSED")))
                        .addServersItem(new Server()
                                .url("http://localhost:8080")
                                .description("Servidor Local de Desenvolvimento"));
        }
}