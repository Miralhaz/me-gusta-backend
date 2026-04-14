package school.sptech.megusta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Sabor de Fogazza não encontrada.")
public class CategoriaFogazzaNaoEncontradaException extends RuntimeException {
    public CategoriaFogazzaNaoEncontradaException(String message) {
        super(message);
    }
}
