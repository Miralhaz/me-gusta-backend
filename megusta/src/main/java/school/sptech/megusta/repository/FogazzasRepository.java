package school.sptech.megusta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.megusta.model.Fogazzas;

import java.util.Optional;

public interface FogazzasRepository extends JpaRepository<Fogazzas, Integer> {
    boolean existsByNome(String nome);

    Optional<Fogazzas> findByNomeIgnoreCase(String nome);
}
