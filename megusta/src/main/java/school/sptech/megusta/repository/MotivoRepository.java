package school.sptech.megusta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.megusta.model.Motivo;

public interface MotivoRepository extends JpaRepository<Motivo, Integer> {

    Boolean existsByNomeIgnoreCase(String nome);
}
