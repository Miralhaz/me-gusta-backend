package school.sptech.megusta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Categoria de Insumo não encontrada.")
public class CategoriaInsumoNaoEncontradaException extends RuntimeException {
    public CategoriaInsumoNaoEncontradaException(String message) {
        super(message);
    }
}
