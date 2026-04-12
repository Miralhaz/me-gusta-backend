package school.sptech.megusta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Unidade de medida não encontrada.")
public class UnidadeMedidaNaoEncontradaException extends RuntimeException {
    public UnidadeMedidaNaoEncontradaException(String message) {
        super(message);
    }
}
