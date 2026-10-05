package school.sptech.megusta.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.sptech.megusta.model.EntradaEstoque;

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

    @Query("SELECT e FROM EntradaEstoque e WHERE LOWER(e.insumo.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(e.fornecedor.nome) LIKE LOWER(CONCAT('%', :termo, '%'))")
    Page<EntradaEstoque> findByBusca(@Param("termo") String termo, Pageable pageable);

    @Query("SELECT e FROM EntradaEstoque e WHERE (LOWER(e.insumo.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(e.fornecedor.nome) LIKE LOWER(CONCAT('%', :termo, '%'))) AND e.dtPedido BETWEEN :dataInicio AND :dataFim")
    Page<EntradaEstoque> findByBuscaAndDataPedidoBetween(@Param("termo") String termo, @Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim, Pageable pageable);

    @Query("SELECT e FROM EntradaEstoque e WHERE e.tipoStatus.nome = :status")
    Page<EntradaEstoque> findByTipoStatusNome(@Param("status") String status, Pageable pageable);

    @Query("SELECT e FROM EntradaEstoque e WHERE e.tipoStatus.nome = :status AND e.dtPedido BETWEEN :dataInicio AND :dataFim")
    Page<EntradaEstoque> findByTipoStatusNomeAndDtPedidoBetween(@Param("status") String status, @Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim, Pageable pageable);

    @Query("SELECT e FROM EntradaEstoque e WHERE e.dtPedido BETWEEN :dataInicio AND :dataFim")
    Page<EntradaEstoque> findByDtPedidoBetween(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim, Pageable pageable);

    Page<EntradaEstoque> findAll(Pageable pageable);

    @Query("""
    SELECT e.fornecedor.nome, SUM(e.vlTotal)
    FROM EntradaEstoque e
    WHERE e.dtEntrada BETWEEN :inicio AND :fim
    GROUP BY e.fornecedor.nome
    ORDER BY SUM(e.vlTotal) DESC
    """)
    List<Object[]> buscarValorCompradoPorFornecedor(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    @Query("""
    SELECT SUM(e.vlTotal)
    FROM EntradaEstoque e
    WHERE e.dtEntrada BETWEEN :inicio AND :fim
    """)
    BigDecimal buscarValorTotalEntradas(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    @Query("""
    SELECT e
    FROM EntradaEstoque e
    WHERE e.dtValidade BETWEEN :inicio AND :fim
    ORDER BY e.dtValidade ASC
    """)
    List<EntradaEstoque> buscarVencimentosEntre(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    @Query("""
SELECT e.fornecedor.id, e.fornecedor.nome, COUNT(e.id), SUM(e.vlTotal)
FROM EntradaEstoque e
WHERE e.dtEntrada BETWEEN :inicio AND :fim
GROUP BY e.fornecedor.id, e.fornecedor.nome
ORDER BY COUNT(e.id) DESC
""")
    List<Object[]> buscarFornecedoresQueMaisAbasteceram(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    @Query("""
SELECT e
FROM EntradaEstoque e
WHERE e.dtValidade < :data
ORDER BY e.dtValidade ASC
""")
    List<EntradaEstoque> buscarItensVencidos(
            @Param("data") LocalDate data
    );


}