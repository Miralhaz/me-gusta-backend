package school.sptech.megusta.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Fogazza já cadastrada.")
public class FogazzaConflitoException extends RuntimeException {
    public FogazzaConflitoException(String message) {
        super(message);
    }
}
