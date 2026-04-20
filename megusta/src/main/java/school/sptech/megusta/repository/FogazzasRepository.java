package school.sptech.megusta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.megusta.model.Fogazzas;

public interface FogazzasRepository extends JpaRepository<Fogazzas, Integer> {
    boolean existsByNome(String nome);
}
