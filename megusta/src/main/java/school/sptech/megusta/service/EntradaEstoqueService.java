package school.sptech.megusta.service;

import org.springframework.stereotype.Service;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.model.*;
import school.sptech.megusta.repository.*;

import java.time.LocalDate;
import java.util.List;

@Service
public class EntradaEstoqueService {

    private final EntradaEstoqueRepository entradaEstoqueRepository;
    private final InsumoRepository insumoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FornecedorRepository fornecedorRepository;
    private final TipoStatusRepository tipoStatusRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;

    public EntradaEstoqueService(
            EntradaEstoqueRepository entradaEstoqueRepository,
            InsumoRepository insumoRepository,
            UsuarioRepository usuarioRepository,
            FornecedorRepository fornecedorRepository,
            TipoStatusRepository tipoStatusRepository,
            UnidadeMedidaRepository unidadeMedidaRepository) {
        this.entradaEstoqueRepository = entradaEstoqueRepository;
        this.insumoRepository = insumoRepository;
        this.usuarioRepository = usuarioRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.tipoStatusRepository = tipoStatusRepository;
        this.unidadeMedidaRepository = unidadeMedidaRepository;
    }

    public List<EntradaEstoque> listar() {
        return entradaEstoqueRepository.findAll();
    }

    public EntradaEstoque buscarPorId(Integer id) {
        return entradaEstoqueRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Entrada de estoque não encontrada."));
    }

    public EntradaEstoque cadastrar(EntradaEstoque entradaEstoque) {
        Insumo insumo = insumoRepository.findById(entradaEstoque.getInsumo().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo não encontrado."));

        Usuario usuario = usuarioRepository.findById(entradaEstoque.getUsuario().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        Fornecedor fornecedor = fornecedorRepository.findById(entradaEstoque.getFornecedor().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado."));

        TipoStatus tipoStatus = tipoStatusRepository.findById(entradaEstoque.getTipoStatus().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de status não encontrado."));

        UnidadeMedida unidadeMedida = unidadeMedidaRepository.findById(entradaEstoque.getUnidadeMedida().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade de medida não encontrada."));

        entradaEstoque.setInsumo(insumo);
        entradaEstoque.setUsuario(usuario);
        entradaEstoque.setFornecedor(fornecedor);
        entradaEstoque.setTipoStatus(tipoStatus);
        entradaEstoque.setUnidadeMedida(unidadeMedida);

        return entradaEstoqueRepository.save(entradaEstoque);
    }

    public EntradaEstoque atualizar(EntradaEstoque entradaEstoque, Integer id) {
        EntradaEstoque entradaExistente = entradaEstoqueRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Entrada de estoque não encontrada."));

        Insumo insumo = insumoRepository.findById(entradaEstoque.getInsumo().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo não encontrado."));

        Usuario usuario = usuarioRepository.findById(entradaEstoque.getUsuario().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        Fornecedor fornecedor = fornecedorRepository.findById(entradaEstoque.getFornecedor().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado."));

        TipoStatus tipoStatus = tipoStatusRepository.findById(entradaEstoque.getTipoStatus().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de status não encontrado."));

        UnidadeMedida unidadeMedida = unidadeMedidaRepository.findById(entradaEstoque.getUnidadeMedida().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade de medida não encontrada."));

        entradaExistente.setInsumo(insumo);
        entradaExistente.setUsuario(usuario);
        entradaExistente.setFornecedor(fornecedor);
        entradaExistente.setTipoStatus(tipoStatus);
        entradaExistente.setUnidadeMedida(unidadeMedida);
        entradaExistente.setQuantidade(entradaEstoque.getQuantidade());
        entradaExistente.setLote(entradaEstoque.getLote());
        entradaExistente.setDtValidade(entradaEstoque.getDtValidade());
        entradaExistente.setDtPedido(entradaEstoque.getDtPedido());
        entradaExistente.setVlTotal(entradaEstoque.getVlTotal());

        return entradaEstoqueRepository.save(entradaExistente);
    }

    public void deletar(Integer id) {
        EntradaEstoque entradaEstoque = entradaEstoqueRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Entrada de estoque não encontrada."));
        entradaEstoqueRepository.delete(entradaEstoque);
    }

    public List<EntradaEstoque> buscarPorInsumo(Integer insumoId) {
        insumoRepository.findById(insumoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo não encontrado."));
        return entradaEstoqueRepository.findByInsumoId(insumoId);
    }

    public List<EntradaEstoque> buscarPorFornecedor(Integer fornecedorId) {
        fornecedorRepository.findById(fornecedorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado."));
        return entradaEstoqueRepository.findByFornecedorId(fornecedorId);
    }

    public List<EntradaEstoque> buscarPorDataPedido(LocalDate dataInicio, LocalDate dataFim) {
        return entradaEstoqueRepository.findByDtEntradaBetween(dataInicio, dataFim);
    }

}