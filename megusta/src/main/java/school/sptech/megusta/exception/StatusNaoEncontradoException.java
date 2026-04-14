package school.sptech.megusta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StatusNaoEncontradoException extends RuntimeException {
    public StatusNaoEncontradoException(String message) {
        super(message);
    }
}
