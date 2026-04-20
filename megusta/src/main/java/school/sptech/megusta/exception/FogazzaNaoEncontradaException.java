package school.sptech.megusta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Fogazza não encontrada.")
public class FogazzaNaoEncontradaException extends RuntimeException {
    public FogazzaNaoEncontradaException(String message) {
        super(message);
    }

}
