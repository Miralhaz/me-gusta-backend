package school.sptech.megusta.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.megusta.dto.AutenticacaoRequestDto;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager manager;

    @PostMapping
    public ResponseEntity efetuarLogin(@RequestBody @Valid AutenticacaoRequestDto autenticacao){
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(autenticacao.getLogin(), autenticacao.getSenha());
        Authentication authentication = manager.authenticate(token);

        return ResponseEntity.ok().build();
    }
}
