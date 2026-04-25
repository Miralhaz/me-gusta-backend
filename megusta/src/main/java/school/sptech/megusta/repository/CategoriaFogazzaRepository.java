package school.sptech.megusta.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.megusta.model.CategoriaFogazza;

public interface CategoriaFogazzaRepository  extends JpaRepository<CategoriaFogazza, Integer> {

    boolean existsByNome(String nome);
}
