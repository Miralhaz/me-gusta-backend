package school.sptech.megusta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.sptech.megusta.model.CategoriaInsumo;
import school.sptech.megusta.model.Insumo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface InsumoRepository extends JpaRepository<Insumo, Integer> {

    boolean existsByNomeOrCodigoInsumo(String nome, String codigoInsumo);

    boolean existsByCategoriaInsumo(CategoriaInsumo categoria);

    List<Insumo> findByCategoriaInsumoId(Integer categoriaId);

    @Query("""
    SELECT AVG(dailyTotal)
    FROM (
      SELECT SUM(se.quantidade) as dailyTotal
      FROM SaidaEstoque se
      WHERE se.insumo.id = :insumoId
        AND se.dtSaida >= :dataInicio
        AND se.dtSaida <= :dataFim
      GROUP BY CAST(se.dtSaida AS localdate)
    ) as dailyTotals
    """)
    BigDecimal mediaConsumoDiarioPorInsumo(@Param("insumoId") Integer insumoId, @Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);
}
