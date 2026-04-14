package school.sptech.megusta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import school.sptech.megusta.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    boolean existsByNomeAndEmail(String nome, String email);

    boolean existsByNomeAndEmailAndIdNot(String nome, String email, Integer id);

    UserDetails findByNome (String login);
}
