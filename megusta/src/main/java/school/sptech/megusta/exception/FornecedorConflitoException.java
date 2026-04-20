package school.sptech.megusta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class FornecedorConflitoException extends RuntimeException {
    public FornecedorConflitoException(String message) {
        super(message);
    }
}