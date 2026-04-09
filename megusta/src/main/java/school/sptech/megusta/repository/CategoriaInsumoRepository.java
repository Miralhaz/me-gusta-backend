package school.sptech.megusta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.megusta.model.CategoriaInsumo;

public interface CategoriaInsumoRepository extends JpaRepository<CategoriaInsumo, Integer> {

    boolean existsByNome(String nome);

}
