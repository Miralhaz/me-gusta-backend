package school.sptech.megusta.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsuarioConflitoException extends RuntimeException {
    public UsuarioConflitoException(String message) {
        super(message);
    }
}
