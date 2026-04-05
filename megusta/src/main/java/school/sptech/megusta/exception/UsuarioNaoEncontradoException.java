package school.sptech.megusta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UsuarioNaoEncontradoException extends RuntimeException {

    public UsuarioNaoEncontradoException(Integer id) {
        super("Usuario com id %d não encontrado".formatted(id));
    }
}
