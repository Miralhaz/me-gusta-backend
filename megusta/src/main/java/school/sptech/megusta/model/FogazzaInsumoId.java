package school.sptech.megusta.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FogazzaInsumoId implements Serializable {

    private Integer fogazza;

    private Integer insumo;
}