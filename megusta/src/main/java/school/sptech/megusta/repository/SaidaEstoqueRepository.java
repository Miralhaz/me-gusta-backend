package school.sptech.megusta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.sptech.megusta.model.SaidaEstoque;

import java.math.BigDecimal;
import java.time.LocalDate;
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
}