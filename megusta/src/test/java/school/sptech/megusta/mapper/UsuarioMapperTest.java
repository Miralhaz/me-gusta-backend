package school.sptech.megusta.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.sptech.megusta.dto.usuario.UsuarioRequestDto;
import school.sptech.megusta.dto.usuario.UsuarioResponseDto;
import school.sptech.megusta.model.Usuario;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Testes de UsuarioMapper")
class UsuarioMapperTest {

    @Test
    @DisplayName("Deve copiar o telefone do DTO para a entidade")
    void deveCopiarTelefoneParaEntidade() {

        UsuarioRequestDto dto = new UsuarioRequestDto(
                "Breno", "Senha@123", "breno@megusta.com", "(11) 91234-5678");

        Usuario usuario = UsuarioMapper.toEntity(dto);

        assertEquals("(11) 91234-5678", usuario.getTelefone());
    }

    @Test
    @DisplayName("Deve copiar o telefone da entidade para o DTO de resposta")
    void deveCopiarTelefoneParaResponseDto() {

        Usuario usuario = new Usuario(1, "Breno", "breno@megusta.com", "hash", "(11) 91234-5678");

        UsuarioResponseDto dto = UsuarioMapper.toResponseDto(usuario);

        assertEquals(1, dto.getId());
        assertEquals("Breno", dto.getNome());
        assertEquals("breno@megusta.com", dto.getEmail());
        assertEquals("(11) 91234-5678", dto.getTelefone());
    }

    @Test
    @DisplayName("Deve preservar o telefone no round-trip toEntity -> toResponseDto")
    void devePreservarTelefoneNoRoundTrip() {

        UsuarioRequestDto dto = new UsuarioRequestDto(
                "Bianca", "Senha@123", "bi@teste.com", "(21) 99888-7777");

        Usuario usuario = UsuarioMapper.toEntity(dto);
        usuario.setId(10);

        UsuarioResponseDto resposta = UsuarioMapper.toResponseDto(usuario);

        assertEquals("(21) 99888-7777", resposta.getTelefone());
    }

    @Test
    @DisplayName("Deve incluir o telefone de cada item na listagem")
    void deveIncluirTelefoneNaListagem() {

        Usuario primeiro = new Usuario(1, "Breno", "breno@megusta.com", "hash1", "(11) 91234-5678");
        Usuario segundo = new Usuario(2, "Bianca", "bi@teste.com", "hash2", "(21) 99888-7777");

        List<UsuarioResponseDto> lista = UsuarioMapper.toResponseDtoList(List.of(primeiro, segundo));

        assertEquals(2, lista.size());
        assertEquals("(11) 91234-5678", lista.get(0).getTelefone());
        assertEquals("(21) 99888-7777", lista.get(1).getTelefone());
    }

    @Test
    @DisplayName("Deve devolver telefone nulo quando a entidade não tiver telefone")
    void deveDevolverTelefoneNulo() {

        Usuario usuario = new Usuario(1, "Breno", "breno@megusta.com", "hash");

        UsuarioResponseDto dto = UsuarioMapper.toResponseDto(usuario);

        assertNull(dto.getTelefone());
    }
}
