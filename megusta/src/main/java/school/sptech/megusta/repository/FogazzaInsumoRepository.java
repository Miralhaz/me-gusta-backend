package school.sptech.megusta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.sptech.megusta.model.FogazzaInsumo;
import school.sptech.megusta.model.FogazzaInsumoId;

import java.util.List;

public interface FogazzaInsumoRepository extends JpaRepository<FogazzaInsumo, FogazzaInsumoId> {

    @Query("SELECT fi FROM FogazzaInsumo fi INNER JOIN fi.insumo WHERE fi.fogazza.id = :fogazzaId")
    List<FogazzaInsumo> findByFogazzaId(@Param("fogazzaId") Integer fogazzaId);
}