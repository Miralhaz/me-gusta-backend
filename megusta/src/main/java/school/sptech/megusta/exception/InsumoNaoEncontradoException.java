package school.sptech.megusta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Insumo não encontrado.")
public class InsumoNaoEncontradoException extends RuntimeException {
    public InsumoNaoEncontradoException(String message) {
        super(message);
    }
}
