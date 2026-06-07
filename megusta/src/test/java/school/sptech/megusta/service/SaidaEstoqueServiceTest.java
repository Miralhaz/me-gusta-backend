package school.sptech.megusta.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.model.Motivo;
import school.sptech.megusta.model.SaidaEstoque;
import school.sptech.megusta.model.Usuario;
import school.sptech.megusta.repository.InsumoRepository;
import school.sptech.megusta.repository.MotivoRepository;
import school.sptech.megusta.repository.SaidaEstoqueRepository;
import school.sptech.megusta.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de SaidaEstoqueService")
class SaidaEstoqueServiceTest {

    @Mock
    private SaidaEstoqueRepository saidaRepo;

    @Mock
    private InsumoRepository insumoRepo;

    @Mock
    private UsuarioRepository usuarioRepo;

    @Mock
    private MotivoRepository motivoRepo;

    @InjectMocks
    private SaidaEstoqueService saidaEstoqueService;

    @Nested
    @DisplayName("Método listar")
    class listar {

        @Test
        @DisplayName("Deve listar corretamente")
        void deveListarCorretamente() {
            SaidaEstoque saida = new SaidaEstoque();
            List<SaidaEstoque> lista = new ArrayList<>();
            lista.add(saida);

            Mockito.when(saidaRepo.findAll()).thenReturn(lista);

            Assertions.assertEquals(lista, saidaEstoqueService.listar());
        }

        @Test
        @DisplayName("Deve retornar lista vazia")
        void deveRetornarListaVazia() {
            List<SaidaEstoque> lista = new ArrayList<>();

            Mockito.when(saidaRepo.findAll()).thenReturn(lista);

            Assertions.assertEquals(lista, saidaEstoqueService.listar());
        }
    }

    @Nested
    @DisplayName("Método buscarPorId")
    class buscarPorId {

        @Test
        @DisplayName("Deve buscar corretamente")
        void deveBuscarCorretamente() {
            Integer id = 1;
            SaidaEstoque saida = new SaidaEstoque();
            saida.setId(id);

            Mockito.when(saidaRepo.findById(id))
                    .thenReturn(Optional.of(saida));

            Assertions.assertEquals(saida, saidaEstoqueService.buscarPorId(id));
        }

        @Test
        @DisplayName("Deve lançar exception caso saída não encontrada")
        void deveLancarExceptionCasoSaidaNaoEncontrada() {
            Integer id = 1;

            Mockito.when(saidaRepo.findById(id))
                    .thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> saidaEstoqueService.buscarPorId(id));
        }
    }

    @Nested
    @DisplayName("Método cadastrar")
    class cadastrar {

        @Test
        @DisplayName("Deve cadastrar corretamente")
        void deveCadastrarCorretamente() {
            Insumo insumo = new Insumo();
            insumo.setId(1);
            insumo.setQtdAtual(20.0);

            Usuario usuario = new Usuario();
            usuario.setId(1);

            Motivo motivo = new Motivo();
            motivo.setId(1);

            SaidaEstoque saida = new SaidaEstoque();
            saida.setInsumo(insumo);
            saida.setUsuario(usuario);
            saida.setMotivo(motivo);
            saida.setQuantidade(BigDecimal.valueOf(5.0));
            saida.setDtSaida(null);

            Mockito.when(insumoRepo.findById(1)).thenReturn(Optional.of(insumo));
            Mockito.when(usuarioRepo.findById(1)).thenReturn(Optional.of(usuario));
            Mockito.when(motivoRepo.findById(1)).thenReturn(Optional.of(motivo));
            Mockito.when(saidaRepo.save(Mockito.any(SaidaEstoque.class))).thenReturn(saida);

            SaidaEstoque resultado = saidaEstoqueService.cadastrar(saida);

            Assertions.assertNotNull(saida.getDtSaida());
            Assertions.assertEquals(saida, resultado);
        }

        @Test
        @DisplayName("Deve lançar exception caso insumo não encontrado")
        void deveLancarExceptionCasoInsumoNaoEncontrado() {
            Insumo insumo = new Insumo();
            insumo.setId(1);

            Usuario usuario = new Usuario();
            usuario.setId(1);

            Motivo motivo = new Motivo();
            motivo.setId(1);

            SaidaEstoque saida = new SaidaEstoque();
            saida.setInsumo(insumo);
            saida.setUsuario(usuario);
            saida.setMotivo(motivo);

            Mockito.when(insumoRepo.findById(1)).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> saidaEstoqueService.cadastrar(saida));
        }

        @Test
        @DisplayName("Deve lançar exception caso usuário não encontrado")
        void deveLancarExceptionCasoUsuarioNaoEncontrado() {
            Insumo insumo = new Insumo();
            insumo.setId(1);

            Usuario usuario = new Usuario();
            usuario.setId(1);

            Motivo motivo = new Motivo();
            motivo.setId(1);

            SaidaEstoque saida = new SaidaEstoque();
            saida.setInsumo(insumo);
            saida.setUsuario(usuario);
            saida.setMotivo(motivo);

            Mockito.when(insumoRepo.findById(1)).thenReturn(Optional.of(insumo));
            Mockito.when(usuarioRepo.findById(1)).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> saidaEstoqueService.cadastrar(saida));
        }

        @Test
        @DisplayName("Deve lançar exception caso motivo não encontrado")
        void deveLancarExceptionCasoMotivoNaoEncontrado() {
            Insumo insumo = new Insumo();
            insumo.setId(1);

            Usuario usuario = new Usuario();
            usuario.setId(1);

            Motivo motivo = new Motivo();
            motivo.setId(1);

            SaidaEstoque saida = new SaidaEstoque();
            saida.setInsumo(insumo);
            saida.setUsuario(usuario);
            saida.setMotivo(motivo);

            Mockito.when(insumoRepo.findById(1)).thenReturn(Optional.of(insumo));
            Mockito.when(usuarioRepo.findById(1)).thenReturn(Optional.of(usuario));
            Mockito.when(motivoRepo.findById(1)).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> saidaEstoqueService.cadastrar(saida));
        }

        @Test
        @DisplayName("Deve manter dtSaida quando já preenchida")
        void deveManterDtSaidaQuandoJaPreenchida() {
            LocalDateTime dataExistente = LocalDateTime.of(2025, 9, 23, 16, 0);

            Insumo insumo = new Insumo();
            insumo.setId(1);
            insumo.setQtdAtual(20.0);

            Usuario usuario = new Usuario();
            usuario.setId(1);

            Motivo motivo = new Motivo();
            motivo.setId(1);

            SaidaEstoque saida = new SaidaEstoque();
            saida.setInsumo(insumo);
            saida.setUsuario(usuario);
            saida.setMotivo(motivo);
            saida.setQuantidade(BigDecimal.valueOf(5.0));
            saida.setDtSaida(dataExistente);

            Mockito.when(insumoRepo.findById(1)).thenReturn(Optional.of(insumo));
            Mockito.when(usuarioRepo.findById(1)).thenReturn(Optional.of(usuario));
            Mockito.when(motivoRepo.findById(1)).thenReturn(Optional.of(motivo));
            Mockito.when(saidaRepo.save(Mockito.any(SaidaEstoque.class))).thenReturn(saida);

            saidaEstoqueService.cadastrar(saida);

            Assertions.assertEquals(dataExistente, saida.getDtSaida());
        }
    }

    @Nested
    @DisplayName("Método atualizar")
    class atualizar {

        @Test
        @DisplayName("Deve atualizar corretamente")
        void deveAtualizarCorretamente() {
            Integer id = 1;
            LocalDateTime data = LocalDateTime.now();

            Insumo insumo = new Insumo();
            insumo.setId(1);
            insumo.setQtdAtual(20.0);

            Usuario usuario = new Usuario();
            usuario.setId(1);

            Motivo motivo = new Motivo();
            motivo.setId(1);

            SaidaEstoque saida = new SaidaEstoque();
            saida.setId(id);
            saida.setInsumo(insumo);
            saida.setUsuario(usuario);
            saida.setMotivo(motivo);
            saida.setQuantidade(BigDecimal.valueOf(5.0));
            saida.setDtSaida(data);

            Mockito.when(saidaRepo.findById(id)).thenReturn(Optional.of(saida));
            Mockito.when(insumoRepo.findById(1)).thenReturn(Optional.of(insumo));
            Mockito.when(usuarioRepo.findById(1)).thenReturn(Optional.of(usuario));
            Mockito.when(motivoRepo.findById(1)).thenReturn(Optional.of(motivo));
            Mockito.when(saidaRepo.save(Mockito.any(SaidaEstoque.class))).thenReturn(saida);

            Assertions.assertEquals(saida, saidaEstoqueService.atualizar(saida, id));
        }

        @Test
        @DisplayName("Deve lançar exception caso saída não encontrada")
        void deveLancarExceptionCasoSaidaNaoEncontrada() {
            Integer id = 1;

            Insumo insumo = new Insumo();
            insumo.setId(1);

            Usuario usuario = new Usuario();
            usuario.setId(1);

            Motivo motivo = new Motivo();
            motivo.setId(1);

            SaidaEstoque saida = new SaidaEstoque();
            saida.setInsumo(insumo);
            saida.setUsuario(usuario);
            saida.setMotivo(motivo);

            Mockito.when(saidaRepo.findById(id)).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> saidaEstoqueService.atualizar(saida, id));
        }

        @Test
        @DisplayName("Deve lançar exception caso insumo não encontrado")
        void deveLancarExceptionCasoInsumoNaoEncontrado() {
            Integer id = 1;

            Insumo insumo = new Insumo();
            insumo.setId(1);

            Usuario usuario = new Usuario();
            usuario.setId(1);

            Motivo motivo = new Motivo();
            motivo.setId(1);

            SaidaEstoque existente = new SaidaEstoque();
            existente.setQuantidade(BigDecimal.valueOf(3.0));

            SaidaEstoque saida = new SaidaEstoque();
            saida.setInsumo(insumo);
            saida.setUsuario(usuario);
            saida.setMotivo(motivo);

            Mockito.when(saidaRepo.findById(id)).thenReturn(Optional.of(existente));
            Mockito.when(insumoRepo.findById(1)).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> saidaEstoqueService.atualizar(saida, id));
        }

        @Test
        @DisplayName("Deve lançar exception caso usuário não encontrado")
        void deveLancarExceptionCasoUsuarioNaoEncontrado() {
            Integer id = 1;

            Insumo insumo = new Insumo();
            insumo.setId(1);
            insumo.setQtdAtual(20.0);

            Usuario usuario = new Usuario();
            usuario.setId(1);

            Motivo motivo = new Motivo();
            motivo.setId(1);

            SaidaEstoque existente = new SaidaEstoque();
            existente.setQuantidade(BigDecimal.valueOf(3.0));

            SaidaEstoque saida = new SaidaEstoque();
            saida.setInsumo(insumo);
            saida.setUsuario(usuario);
            saida.setMotivo(motivo);

            Mockito.when(saidaRepo.findById(id)).thenReturn(Optional.of(existente));
            Mockito.when(insumoRepo.findById(1)).thenReturn(Optional.of(insumo));
            Mockito.when(usuarioRepo.findById(1)).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> saidaEstoqueService.atualizar(saida, id));
        }

        @Test
        @DisplayName("Deve lançar exception caso motivo não encontrado")
        void deveLancarExceptionCasoMotivoNaoEncontrado() {
            Integer id = 1;

            Insumo insumo = new Insumo();
            insumo.setId(1);
            insumo.setQtdAtual(20.0);

            Usuario usuario = new Usuario();
            usuario.setId(1);

            Motivo motivo = new Motivo();
            motivo.setId(1);

            SaidaEstoque existente = new SaidaEstoque();
            existente.setQuantidade(BigDecimal.valueOf(3.0));

            SaidaEstoque saida = new SaidaEstoque();
            saida.setInsumo(insumo);
            saida.setUsuario(usuario);
            saida.setMotivo(motivo);

            Mockito.when(saidaRepo.findById(id)).thenReturn(Optional.of(existente));
            Mockito.when(insumoRepo.findById(1)).thenReturn(Optional.of(insumo));
            Mockito.when(usuarioRepo.findById(1)).thenReturn(Optional.of(usuario));
            Mockito.when(motivoRepo.findById(1)).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> saidaEstoqueService.atualizar(saida, id));
        }

        @Test
        @DisplayName("Deve manter dtSaida da saída existente quando nova dtSaida é nula")
        void deveManterDtSaidaExistenteQuandoNovaDtSaidaEhNula() {
            Integer id = 1;
            LocalDateTime dataExistente = LocalDateTime.of(2025, 9, 23, 16, 0);

            Insumo insumo = new Insumo();
            insumo.setId(1);
            insumo.setQtdAtual(20.0);

            Usuario usuario = new Usuario();
            usuario.setId(1);

            Motivo motivo = new Motivo();
            motivo.setId(1);

            SaidaEstoque existente = new SaidaEstoque();
            existente.setId(id);
            existente.setQuantidade(BigDecimal.valueOf(3.0));
            existente.setDtSaida(dataExistente);

            SaidaEstoque saidaAtualizada = new SaidaEstoque();
            saidaAtualizada.setInsumo(insumo);
            saidaAtualizada.setUsuario(usuario);
            saidaAtualizada.setMotivo(motivo);
            saidaAtualizada.setQuantidade(BigDecimal.valueOf(5.0));
            saidaAtualizada.setDtSaida(null);

            Mockito.when(saidaRepo.findById(id)).thenReturn(Optional.of(existente));
            Mockito.when(insumoRepo.findById(1)).thenReturn(Optional.of(insumo));
            Mockito.when(usuarioRepo.findById(1)).thenReturn(Optional.of(usuario));
            Mockito.when(motivoRepo.findById(1)).thenReturn(Optional.of(motivo));
            Mockito.when(saidaRepo.save(Mockito.any(SaidaEstoque.class))).thenReturn(existente);

            SaidaEstoque resultado = saidaEstoqueService.atualizar(saidaAtualizada, id);

            Assertions.assertEquals(dataExistente, resultado.getDtSaida());
        }
    }

    @Nested
    @DisplayName("Método deletar")
    class deletar {

        @Test
        @DisplayName("Deve deletar corretamente")
        void deveDeletarCorretamente() {
            Integer id = 1;
            SaidaEstoque saida = new SaidaEstoque();
            saida.setId(id);

            Mockito.when(saidaRepo.findById(id)).thenReturn(Optional.of(saida));

            saidaEstoqueService.deletar(id);

            Mockito.verify(saidaRepo, Mockito.times(1)).delete(saida);
        }

        @Test
        @DisplayName("Deve lançar exception caso saída não encontrada")
        void deveLancarExceptionCasoSaidaNaoEncontrada() {
            Integer id = 1;

            Mockito.when(saidaRepo.findById(id)).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> saidaEstoqueService.deletar(id));
        }
    }

    @Nested
    @DisplayName("Método buscarPorInsumo")
    class buscarPorInsumo {

        @Test
        @DisplayName("Deve buscar por insumo corretamente")
        void deveBuscarPorInsumoCorretamente() {
            Integer insumoId = 1;
            Insumo insumo = new Insumo();
            insumo.setId(insumoId);

            List<SaidaEstoque> lista = new ArrayList<>();

            Mockito.when(insumoRepo.findById(insumoId)).thenReturn(Optional.of(insumo));
            Mockito.when(saidaRepo.findByInsumoId(insumoId)).thenReturn(lista);

            Assertions.assertEquals(lista, saidaEstoqueService.buscarPorInsumo(insumoId));
        }

        @Test
        @DisplayName("Deve lançar exception caso insumo não encontrado")
        void deveLancarExceptionCasoInsumoNaoEncontrado() {
            Integer insumoId = 1;

            Mockito.when(insumoRepo.findById(insumoId)).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class,
                    () -> saidaEstoqueService.buscarPorInsumo(insumoId));
        }
    }

    @Nested
    @DisplayName("Método buscarPorPeriodo")
    class buscarPorPeriodo {

        @Test
        @DisplayName("Deve buscar por período corretamente")
        void deveBuscarPorPeriodoCorretamente() {
            LocalDateTime inicio = LocalDateTime.now().minusDays(7);
            LocalDateTime fim = LocalDateTime.now();

            List<SaidaEstoque> lista = new ArrayList<>();

            Mockito.when(saidaRepo.findByDtSaidaBetween(inicio, fim)).thenReturn(lista);

            Assertions.assertEquals(lista, saidaEstoqueService.buscarPorPeriodo(inicio, fim));
        }
    }
}

