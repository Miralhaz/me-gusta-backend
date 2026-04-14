package school.sptech.megusta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.megusta.model.TipoStatus;

public interface TipoStatusRepository extends JpaRepository<TipoStatus, Integer> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeAndIdNot(String nome, Integer id);
}
