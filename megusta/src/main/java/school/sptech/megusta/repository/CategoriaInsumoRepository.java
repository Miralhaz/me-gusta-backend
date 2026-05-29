package school.sptech.megusta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.sptech.megusta.dto.consumo_categoria.ConsumoCategoriaResponseDto;
import school.sptech.megusta.model.CategoriaInsumo;

import java.time.LocalDateTime;
import java.util.List;

public interface CategoriaInsumoRepository extends JpaRepository<CategoriaInsumo, Integer> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Integer id);

    @Query("""
    SELECT new school.sptech.megusta.dto.consumo_categoria.ConsumoCategoriaResponseDto(SUM(se.quantidade), se.dtSaida)
    FROM SaidaEstoque se
    JOIN se.insumo i
    JOIN i.categoriaInsumo ci
    WHERE ci.nome = :nomeCategoria AND se.dtSaida >= :dataAnterior
    GROUP BY se.dtSaida
""")
    List<ConsumoCategoriaResponseDto> consumoPorCategoriaEspecifica(String nomeCategoria, LocalDateTime dataAnterior);
}