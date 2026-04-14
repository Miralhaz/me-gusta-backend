package school.sptech.megusta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Sabor de Fogazza já cadastrada.")
public class CategoriaFogazzaConflitoException extends RuntimeException {
    public CategoriaFogazzaConflitoException(String message) {
        super(message);
    }
}
