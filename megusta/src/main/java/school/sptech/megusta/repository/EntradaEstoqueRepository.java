package school.sptech.megusta.repository;

import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.sptech.megusta.model.EntradaEstoque;
import school.sptech.megusta.model.Insumo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EntradaEstoqueRepository extends JpaRepository<EntradaEstoque, Integer> {

    List<EntradaEstoque> findByInsumoId(Integer insumoId);

    List<EntradaEstoque> findByFornecedorId(Integer fornecedorId);

    List<EntradaEstoque> findByUsuarioId(Integer usuarioId);

    List<EntradaEstoque> findByDtEntradaBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    List<EntradaEstoque> findByLote(String lote);

    List<EntradaEstoque> findByDtValidadeGreaterThanEqualOrderByDtValidadeAsc(LocalDate data);

    List<EntradaEstoque> findByInsumoIdAndDtValidadeGreaterThanEqualOrderByDtValidadeAsc(
            Integer insumoId, LocalDate data);

    @Query("SELECT SUM(e.quantidadeAbsoluta * e.quantidadeRelativa) FROM EntradaEstoque e WHERE e.insumo.id = :fkInsumo AND e.dtEntrada BETWEEN :dtInicio AND :dtFim")
    BigDecimal sumQuantidadeTotalEntradaByInsumoAndDateBetween(
            @Param("fkInsumo") Integer fkInsumo,
            @Param("dtInicio") LocalDateTime dtInicio,
            @Param("dtFim") LocalDateTime dtFim
    );

}