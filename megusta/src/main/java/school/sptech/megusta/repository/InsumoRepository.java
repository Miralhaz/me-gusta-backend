package school.sptech.megusta.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.megusta.model.CategoriaInsumo;
import school.sptech.megusta.model.Insumo;

import java.util.List;

public interface InsumoRepository extends JpaRepository<Insumo, Integer> {

    boolean existsByNomeOrCodigoInsumo(String nome, String codigoInsumo);

    boolean existsByCategoriaInsumo(CategoriaInsumo categoria);

    List<Insumo> findByCategoriaInsumoId(Integer categoriaId);

    Page<Insumo> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<Insumo> findByNomeContainingIgnoreCaseAndCategoriaInsumoNome(String nome, String categoria, Pageable pageable);
}