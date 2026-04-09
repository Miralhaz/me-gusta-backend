package school.sptech.megusta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoriaInsumoNaoEncontradaException extends RuntimeException {
    public CategoriaInsumoNaoEncontradaException(Integer id) {
        super("Categoria de Insumo com id %d não encontrado".formatted(id));
    }
}
