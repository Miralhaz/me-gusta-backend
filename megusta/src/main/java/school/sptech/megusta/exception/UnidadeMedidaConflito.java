package school.sptech.megusta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Unidade de medida já cadastrada!")
public class UnidadeMedidaConflito extends RuntimeException {
    public UnidadeMedidaConflito(String message) {
        super(message);
    }
}
