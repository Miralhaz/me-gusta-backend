package school.sptech.megusta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.megusta.model.SaidaEstoque;

import java.time.LocalDateTime;
import java.util.List;

public interface SaidaEstoqueRepository extends JpaRepository<SaidaEstoque, Integer> {

    List<SaidaEstoque> findByInsumoId(Integer insumoId);

    List<SaidaEstoque> findByUsuarioId(Integer usuarioId);

    List<SaidaEstoque> findByDtSaidaBetween(LocalDateTime inicio, LocalDateTime fim);

}