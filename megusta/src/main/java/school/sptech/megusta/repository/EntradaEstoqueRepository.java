package school.sptech.megusta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.megusta.model.EntradaEstoque;
import school.sptech.megusta.model.Insumo;

import java.time.LocalDate;
import java.util.List;

public interface EntradaEstoqueRepository extends JpaRepository<EntradaEstoque, Integer> {

    List<EntradaEstoque> findByInsumoId(Integer insumoId);

    List<EntradaEstoque> findByFornecedorId(Integer fornecedorId);

    List<EntradaEstoque> findByUsuarioId(Integer usuarioId);

    List<EntradaEstoque> findByDtEntradaBetween(LocalDate dataInicio, LocalDate dataFim);

    List<EntradaEstoque> findByLote(String lote);

}