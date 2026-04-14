package school.sptech.megusta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class StatusConflitoException extends RuntimeException {
    public StatusConflitoException(String message) {
        super(message);
    }
}
