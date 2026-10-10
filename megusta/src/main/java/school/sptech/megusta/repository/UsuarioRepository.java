package school.sptech.megusta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import school.sptech.megusta.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findTopByOrderByIdDesc();

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Integer id);

    boolean existsByNome(String nome);

    boolean existsByNomeAndIdNot(String nome, Integer id);

    UserDetails findByEmail(String login);
}
