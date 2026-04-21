package school.sptech.megusta.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.megusta.dto.autenticacao.AutenticacaoRequestDto;
import school.sptech.megusta.dto.autenticacao.TokenJWTResponseDto;
import school.sptech.megusta.model.Usuario;
import school.sptech.megusta.security.TokenService;

@RestController
@RequestMapping("/login")
@Tag(name = "01. Autenticação", description = "Endpoints para controle de acesso e geração de tokens JWT")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @Operation(
            summary = "Efetuar login no sistema",
            description = "Autentica um usuário com base no login e senha informados. "
                    + "Retorna um token JWT que deve ser utilizado nas requisições protegidas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login efetuado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenJWTResponseDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados de login malformados", content = @Content)
    })

    @PostMapping
    public ResponseEntity efetuarLogin(@RequestBody @Valid AutenticacaoRequestDto autenticacao){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(autenticacao.getLogin(), autenticacao.getSenha());
        Authentication authentication = manager.authenticate(authenticationToken);

        String tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok().body(new TokenJWTResponseDto(tokenJWT));
    }
}
