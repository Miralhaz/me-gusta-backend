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
import school.sptech.megusta.exception.RecursoConflitoException;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.model.CategoriaInsumo;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.model.TipoStatus;
import school.sptech.megusta.model.UnidadeMedida;
import school.sptech.megusta.repository.CategoriaInsumoRepository;
import school.sptech.megusta.repository.InsumoRepository;
import school.sptech.megusta.repository.TipoStatusRepository;
import school.sptech.megusta.repository.UnidadeMedidaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da classe InsumoService")
class InsumoServiceTest {

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private CategoriaInsumoRepository categoriaInsumoRepository;

    @Mock
    private UnidadeMedidaRepository unidadeMedidaRepository;

    @Mock
    private TipoStatusRepository tipoStatusRepository;

    @InjectMocks
    private InsumoService insumoService;

    @Nested
    @DisplayName("Método listar")
    class listar {

        @Test
        @DisplayName("deve listar corretamente")
        void deveListarCorretamente(){
            Insumo insumo = new Insumo();
            List<Insumo> insumos = new ArrayList<>();

            insumos.add(insumo);

            Mockito.when(insumoRepository.findAll()).thenReturn(insumos);

            Assertions.assertEquals(insumos, insumoService.listar());
        }

        @Test
        @DisplayName("deve retornar lista vazia")
        void deveRetornarListaVazia(){
            List<Insumo> insumos = new ArrayList<>();

            Mockito.when(insumoRepository.findAll()).thenReturn(insumos);

            Assertions.assertEquals(insumos, insumoService.listar());
        }
    }

    @Nested
    @DisplayName("Método buscarPorId")
    class buscarPorId {

        @Test
        @DisplayName("deve buscar corretamente")
        void deveBuscarCorretamente(){
            Insumo insumo = new Insumo();
            insumo.setId(1);

            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.of(insumo));

            Assertions.assertEquals(insumo, insumoService.buscarPorId(insumo.getId()));
        }

        @Test
        @DisplayName("deve lançar exception caso insumo não encontrado")
        void deveLancarExcecaoCasoInsumoNaoEncontrado(){
            Insumo insumo = new Insumo();
            insumo.setId(1);

            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> insumoService.buscarPorId(insumo.getId()));
        }
    }

    @Nested
    @DisplayName("Método cadastrar")
    class cadastrar {

        @Test
        @DisplayName("deve cadastrar corretamente")
        void deveCadastrarCorretamente(){
            Insumo insumo = new Insumo();
            insumo.setNome("queijo");
            insumo.setCodigoInsumo("abc");

            CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
            categoriaInsumo.setId(1);

            UnidadeMedida unidadeMedida = new UnidadeMedida();
            unidadeMedida.setId(1);

            TipoStatus tipoStatus = new TipoStatus();
            tipoStatus.setId(1);

            insumo.setCategoriaInsumo(categoriaInsumo);
            insumo.setUnidadeMedida(unidadeMedida);
            insumo.setTipoStatus(tipoStatus);

            Mockito.when(insumoRepository.existsByNomeOrCodigoInsumo(insumo.getNome(), insumo.getCodigoInsumo())).thenReturn(false);
            Mockito.when(categoriaInsumoRepository.findById(categoriaInsumo.getId())).thenReturn(Optional.of(categoriaInsumo));
            Mockito.when(unidadeMedidaRepository.findById(unidadeMedida.getId())).thenReturn(Optional.of(unidadeMedida));
            Mockito.when(tipoStatusRepository.findById(tipoStatus.getId())).thenReturn(Optional.of(tipoStatus));
            Mockito.when(insumoRepository.save(insumo)).thenReturn(insumo);

            Assertions.assertEquals(insumo, insumoService.cadastrar(insumo));
        }

        @Test
        @DisplayName("deve lançar exception caso tipoStatus não encontrado")
        void deveLancarExceptionCasoTipoStatusNaoEncontrado(){
            Insumo insumo = new Insumo();
            insumo.setNome("queijo");
            insumo.setCodigoInsumo("abc");

            CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
            categoriaInsumo.setId(1);

            UnidadeMedida unidadeMedida = new UnidadeMedida();
            unidadeMedida.setId(1);

            TipoStatus tipoStatus = new TipoStatus();
            tipoStatus.setId(1);

            insumo.setCategoriaInsumo(categoriaInsumo);
            insumo.setUnidadeMedida(unidadeMedida);
            insumo.setTipoStatus(tipoStatus);

            Mockito.when(insumoRepository.existsByNomeOrCodigoInsumo(insumo.getNome(), insumo.getCodigoInsumo())).thenReturn(false);
            Mockito.when(categoriaInsumoRepository.findById(categoriaInsumo.getId())).thenReturn(Optional.of(categoriaInsumo));
            Mockito.when(unidadeMedidaRepository.findById(unidadeMedida.getId())).thenReturn(Optional.of(unidadeMedida));
            Mockito.when(tipoStatusRepository.findById(tipoStatus.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> insumoService.cadastrar(insumo));
        }

        @Test
        @DisplayName("deve lançar exception caso unidadeMedida não encontrado")
        void deveLancarExceptionCasoUnidadeMedidaNaoEncontrado(){
            Insumo insumo = new Insumo();
            insumo.setNome("queijo");
            insumo.setCodigoInsumo("abc");

            CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
            categoriaInsumo.setId(1);

            UnidadeMedida unidadeMedida = new UnidadeMedida();
            unidadeMedida.setId(1);

            TipoStatus tipoStatus = new TipoStatus();
            tipoStatus.setId(1);

            insumo.setCategoriaInsumo(categoriaInsumo);
            insumo.setUnidadeMedida(unidadeMedida);
            insumo.setTipoStatus(tipoStatus);

            Mockito.when(insumoRepository.existsByNomeOrCodigoInsumo(insumo.getNome(), insumo.getCodigoInsumo())).thenReturn(false);
            Mockito.when(categoriaInsumoRepository.findById(categoriaInsumo.getId())).thenReturn(Optional.of(categoriaInsumo));
            Mockito.when(unidadeMedidaRepository.findById(unidadeMedida.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> insumoService.cadastrar(insumo));
        }

        @Test
        @DisplayName("deve lançar exception caso categoriaInsumo não encontrado")
        void deveLancarExceptionCasoCategoriaInsumoNaoEncontrado(){
            Insumo insumo = new Insumo();
            insumo.setNome("queijo");
            insumo.setCodigoInsumo("abc");

            CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
            categoriaInsumo.setId(1);

            UnidadeMedida unidadeMedida = new UnidadeMedida();
            unidadeMedida.setId(1);

            TipoStatus tipoStatus = new TipoStatus();
            tipoStatus.setId(1);

            insumo.setCategoriaInsumo(categoriaInsumo);
            insumo.setUnidadeMedida(unidadeMedida);
            insumo.setTipoStatus(tipoStatus);

            Mockito.when(insumoRepository.existsByNomeOrCodigoInsumo(insumo.getNome(), insumo.getCodigoInsumo())).thenReturn(false);
            Mockito.when(categoriaInsumoRepository.findById(categoriaInsumo.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> insumoService.cadastrar(insumo));
        }

        @Test
        @DisplayName("deve lançar exception caso insumo existe")
        void deveLancarExceptionCasoInsumoExiste(){
            Insumo insumo = new Insumo();
            insumo.setNome("queijo");
            insumo.setCodigoInsumo("abc");

            CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
            categoriaInsumo.setId(1);

            UnidadeMedida unidadeMedida = new UnidadeMedida();
            unidadeMedida.setId(1);

            TipoStatus tipoStatus = new TipoStatus();
            tipoStatus.setId(1);

            insumo.setCategoriaInsumo(categoriaInsumo);
            insumo.setUnidadeMedida(unidadeMedida);
            insumo.setTipoStatus(tipoStatus);

            Mockito.when(insumoRepository.existsByNomeOrCodigoInsumo(insumo.getNome(), insumo.getCodigoInsumo())).thenReturn(true);

            Assertions.assertThrows(RecursoConflitoException.class, () -> insumoService.cadastrar(insumo));
        }
    }

    @Nested
    @DisplayName("Método atualizar")
    class atualizar {

        @Test
        @DisplayName("deve atualizar corretamente")
        void deveAtualizarCorretamente(){
            Insumo insumo = new Insumo();
            insumo.setNome("queijo");
            insumo.setCodigoInsumo("abc");
            insumo.setId(1);

            CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
            categoriaInsumo.setId(1);

            UnidadeMedida unidadeMedida = new UnidadeMedida();
            unidadeMedida.setId(1);

            TipoStatus tipoStatus = new TipoStatus();
            tipoStatus.setId(1);

            insumo.setCategoriaInsumo(categoriaInsumo);
            insumo.setUnidadeMedida(unidadeMedida);
            insumo.setTipoStatus(tipoStatus);

            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.of(insumo));
            Mockito.when(categoriaInsumoRepository.findById(categoriaInsumo.getId())).thenReturn(Optional.of(categoriaInsumo));
            Mockito.when(unidadeMedidaRepository.findById(unidadeMedida.getId())).thenReturn(Optional.of(unidadeMedida));
            Mockito.when(tipoStatusRepository.findById(tipoStatus.getId())).thenReturn(Optional.of(tipoStatus));
            Mockito.when(insumoRepository.save(insumo)).thenReturn(insumo);

            Assertions.assertEquals(insumo, insumoService.atualizar(insumo, insumo.getId()));
        }

        @Test
        @DisplayName("deve lançar exception caso tipoStatus não encontrado")
        void deveLancarExceptionCasoTipoStatusNaoEncontrado(){
            Insumo insumo = new Insumo();
            insumo.setNome("queijo");
            insumo.setCodigoInsumo("abc");

            CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
            categoriaInsumo.setId(1);

            UnidadeMedida unidadeMedida = new UnidadeMedida();
            unidadeMedida.setId(1);

            TipoStatus tipoStatus = new TipoStatus();
            tipoStatus.setId(1);

            insumo.setCategoriaInsumo(categoriaInsumo);
            insumo.setUnidadeMedida(unidadeMedida);
            insumo.setTipoStatus(tipoStatus);

            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.of(insumo));
            Mockito.when(categoriaInsumoRepository.findById(categoriaInsumo.getId())).thenReturn(Optional.of(categoriaInsumo));
            Mockito.when(unidadeMedidaRepository.findById(unidadeMedida.getId())).thenReturn(Optional.of(unidadeMedida));
            Mockito.when(tipoStatusRepository.findById(tipoStatus.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> insumoService.atualizar(insumo, insumo.getId()));
        }

        @Test
        @DisplayName("deve lançar exception caso unidadeMedida não encontrado")
        void deveLancarExceptionCasoUnidadeMedidaNaoEncontrado(){
            Insumo insumo = new Insumo();
            insumo.setNome("queijo");
            insumo.setCodigoInsumo("abc");

            CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
            categoriaInsumo.setId(1);

            UnidadeMedida unidadeMedida = new UnidadeMedida();
            unidadeMedida.setId(1);

            TipoStatus tipoStatus = new TipoStatus();
            tipoStatus.setId(1);

            insumo.setCategoriaInsumo(categoriaInsumo);
            insumo.setUnidadeMedida(unidadeMedida);
            insumo.setTipoStatus(tipoStatus);

            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.of(insumo));
            Mockito.when(categoriaInsumoRepository.findById(categoriaInsumo.getId())).thenReturn(Optional.of(categoriaInsumo));
            Mockito.when(unidadeMedidaRepository.findById(unidadeMedida.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> insumoService.atualizar(insumo, insumo.getId()));
        }

        @Test
        @DisplayName("deve lançar exception caso categoriaInsumo não encontrado")
        void deveLancarExceptionCasoCategoriaInsumoNaoEncontrado(){
            Insumo insumo = new Insumo();
            insumo.setNome("queijo");
            insumo.setCodigoInsumo("abc");

            CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
            categoriaInsumo.setId(1);

            UnidadeMedida unidadeMedida = new UnidadeMedida();
            unidadeMedida.setId(1);

            TipoStatus tipoStatus = new TipoStatus();
            tipoStatus.setId(1);

            insumo.setCategoriaInsumo(categoriaInsumo);
            insumo.setUnidadeMedida(unidadeMedida);
            insumo.setTipoStatus(tipoStatus);

            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.of(insumo));
            Mockito.when(categoriaInsumoRepository.findById(categoriaInsumo.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> insumoService.atualizar(insumo, insumo.getId()));
        }

        @Test
        @DisplayName("deve lançar exception caso insumo existe")
        void deveLancarExceptionCasoInsumoExiste(){
            Insumo insumo = new Insumo();
            insumo.setNome("queijo");
            insumo.setCodigoInsumo("abc");

            CategoriaInsumo categoriaInsumo = new CategoriaInsumo();
            categoriaInsumo.setId(1);

            UnidadeMedida unidadeMedida = new UnidadeMedida();
            unidadeMedida.setId(1);

            TipoStatus tipoStatus = new TipoStatus();
            tipoStatus.setId(1);

            insumo.setCategoriaInsumo(categoriaInsumo);
            insumo.setUnidadeMedida(unidadeMedida);
            insumo.setTipoStatus(tipoStatus);

            Mockito.when(insumoRepository.findById(insumo.getId())).thenReturn(Optional.empty());

            Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> insumoService.atualizar(insumo, insumo.getId()));
        }
    }
}