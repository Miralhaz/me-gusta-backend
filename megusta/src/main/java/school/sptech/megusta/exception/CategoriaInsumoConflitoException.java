package school.sptech.megusta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoriaInsumoConflitoException extends RuntimeException {
    public CategoriaInsumoConflitoException(String message) {
        super(message);
    }
}
