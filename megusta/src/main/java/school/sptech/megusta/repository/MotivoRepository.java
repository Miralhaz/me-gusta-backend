package school.sptech.megusta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.megusta.model.Motivo;

import java.util.Optional;

public interface MotivoRepository extends JpaRepository<Motivo, Integer> {

    Boolean existsByNomeIgnoreCase(String nome);

    Optional<Motivo> findByNomeIgnoreCase(String nome);
}
