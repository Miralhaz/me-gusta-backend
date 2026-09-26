package school.sptech.megusta.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import school.sptech.megusta.dto.usuario.UsuarioRequestDto;
import school.sptech.megusta.dto.usuario.UsuarioResponseDto;
import school.sptech.megusta.dto.usuario.UsuarioUpdateDto;
import school.sptech.megusta.model.Usuario;

import java.util.List;

@DisplayName("Testes de UsuarioMapper")
class UsuarioMapperTest {

    @Nested
    @DisplayName("toEntity(UsuarioRequestDto)")
    class toEntityRequest {

        @Test
        @DisplayName("Deve mapear nome, email, senha e telefone para a entidade")
        void deveMapearTodosOsCampos() {
            UsuarioRequestDto dto = new UsuarioRequestDto(
                    "Breno", "Senha@123", "breno@megusta.com", "11999999999");

            Usuario usuario = UsuarioMapper.toEntity(dto);

            Assertions.assertEquals("Breno", usuario.getNome());
            Assertions.assertEquals("breno@megusta.com", usuario.getEmail());
            Assertions.assertEquals("Senha@123", usuario.getSenha());
            Assertions.assertEquals("11999999999", usuario.getTelefone());
            Assertions.assertNull(usuario.getId());
        }
    }

    @Nested
    @DisplayName("toEntity(UsuarioUpdateDto, Usuario)")
    class toEntityUpdate {

        @Test
        @DisplayName("Deve sobrescrever nome, email e telefone preservando id e senha")
        void devePreservarIdESenha() {
            Usuario existente = new Usuario(
                    7, "Bianca", "bi@teste.com", "hashAntigo", "11988888888");

            UsuarioUpdateDto dto = new UsuarioUpdateDto(
                    "Bianca Souza", "bi.nova@teste.com", "11977777777");

            Usuario resultado = UsuarioMapper.toEntity(dto, existente);

            Assertions.assertSame(existente, resultado);
            Assertions.assertEquals(7, resultado.getId());
            Assertions.assertEquals("hashAntigo", resultado.getSenha());
            Assertions.assertEquals("Bianca Souza", resultado.getNome());
            Assertions.assertEquals("bi.nova@teste.com", resultado.getEmail());
            Assertions.assertEquals("11977777777", resultado.getTelefone());
        }
    }

    @Nested
    @DisplayName("toResponseDto(Usuario)")
    class toResponseDto {

        @Test
        @DisplayName("Deve mapear id, nome, email e telefone para a resposta")
        void deveMapearTodosOsCampos() {
            Usuario usuario = new Usuario(
                    1, "Breno", "breno@megusta.com", "hash", "11999999999");

            UsuarioResponseDto dto = UsuarioMapper.toResponseDto(usuario);

            Assertions.assertEquals(1, dto.getId());
            Assertions.assertEquals("Breno", dto.getNome());
            Assertions.assertEquals("breno@megusta.com", dto.getEmail());
            Assertions.assertEquals("11999999999", dto.getTelefone());
        }

        @Test
        @DisplayName("Deve devolver telefone nulo para usuário legado sem telefone")
        void deveDevolverTelefoneNuloParaUsuarioLegado() {
            Usuario usuario = new Usuario(
                    2, "Antigo", "antigo@megusta.com", "hash", null);

            UsuarioResponseDto dto = UsuarioMapper.toResponseDto(usuario);

            Assertions.assertNull(dto.getTelefone());
        }
    }

    @Nested
    @DisplayName("toResponseDtoList(List<Usuario>)")
    class toResponseDtoList {

        @Test
        @DisplayName("Deve incluir o telefone de todos os usuários da lista")
        void deveIncluirTelefoneDeTodos() {
            List<Usuario> usuarios = List.of(
                    new Usuario(1, "Breno", "breno@megusta.com", "hash", "11999999999"),
                    new Usuario(2, "Bianca", "bi@teste.com", "hash", "11988888888"));

            List<UsuarioResponseDto> resposta = UsuarioMapper.toResponseDtoList(usuarios);

            Assertions.assertEquals(2, resposta.size());
            Assertions.assertEquals("11999999999", resposta.get(0).getTelefone());
            Assertions.assertEquals("11988888888", resposta.get(1).getTelefone());
        }
    }
}
