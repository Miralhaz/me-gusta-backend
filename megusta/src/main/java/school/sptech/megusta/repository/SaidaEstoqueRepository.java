package school.sptech.megusta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.sptech.megusta.model.SaidaEstoque;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface SaidaEstoqueRepository extends JpaRepository<SaidaEstoque, Integer> {

    List<SaidaEstoque> findByInsumoId(Integer insumoId);

    List<SaidaEstoque> findByUsuarioId(Integer usuarioId);

    List<SaidaEstoque> findByDtSaidaBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT SUM(s.quantidade) FROM SaidaEstoque s WHERE s.insumo.id = :fkInsumo AND s.dtSaida BETWEEN :dtInicio AND :dtFim")
    BigDecimal sumQuantidadeSaidasByInsumoAndDateBetween(
            @Param("fkInsumo") Integer fkInsumo,
            @Param("dtInicio") LocalDateTime dtInicio,
            @Param("dtFim") LocalDateTime dtFim
    );

    // Saídas por motivo
    @Query("""
    SELECT s.motivo.nome, SUM(s.quantidade) FROM SaidaEstoque s WHERE s.dtSaida BETWEEN :inicio AND :fim GROUP BY s.motivo.nome """)
    List<Object[]> somarSaidasPorMotivo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    // Insumos com maior quantidade de saída
    @Query("""
    SELECT s.insumo.nome, SUM(s.quantidade) FROM SaidaEstoque s WHERE s.dtSaida BETWEEN :inicio AND :fim GROUP BY s.insumo.nome ORDER BY SUM(s.quantidade) DESC """)
    List<Object[]> buscarInsumosMaisUtilizados(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

}