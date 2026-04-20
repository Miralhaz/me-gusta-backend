package school.sptech.megusta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.megusta.model.Fornecedor;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Integer> {

    boolean existsByNomeAndCnpj(String nome, String cnpj);

    boolean existsByNomeAndCnpjAndIdNot(String nome, String cnpj, Integer id);
}
