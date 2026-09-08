package school.sptech.megusta.service;

import org.springframework.stereotype.Service;
import school.sptech.megusta.dto.movimentacao.MovimentacaoResponse;
import school.sptech.megusta.model.EntradaEstoque;
import school.sptech.megusta.model.SaidaEstoque;
import school.sptech.megusta.repository.EntradaEstoqueRepository;
import school.sptech.megusta.repository.SaidaEstoqueRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MovimentacaoService {

    private final EntradaEstoqueRepository entradaEstoqueRepository;
    private final SaidaEstoqueRepository saidaEstoqueRepository;

    public MovimentacaoService(EntradaEstoqueRepository entradaEstoqueRepository,
                               SaidaEstoqueRepository saidaEstoqueRepository) {
        this.entradaEstoqueRepository = entradaEstoqueRepository;
        this.saidaEstoqueRepository = saidaEstoqueRepository;
    }

    public List<MovimentacaoResponse> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(23, 59, 59);

        List<EntradaEstoque> entradas = entradaEstoqueRepository.findByDtEntradaBetween(inicio, fim);
        List<SaidaEstoque> saidas = saidaEstoqueRepository.findByDtSaidaBetween(inicio, fim);

        List<MovimentacaoResponse> movimentacoes = new ArrayList<>();

        for (EntradaEstoque e : entradas) {
            MovimentacaoResponse m = new MovimentacaoResponse();
            m.setId(e.getId());
            m.setTipo("entrada");
            m.setData(e.getDtEntrada());
            m.setQuantidade(e.getQuantidadeAbsoluta());

            MovimentacaoResponse.InsumoMovimentacao insumo = new MovimentacaoResponse.InsumoMovimentacao();
            insumo.setId(e.getInsumo().getId());
            insumo.setNome(e.getInsumo().getNome());
            insumo.setUnidade(e.getUnidadeMedida().getUnidade());
            m.setInsumo(insumo);

            MovimentacaoResponse.UsuarioMovimentacao usuario = new MovimentacaoResponse.UsuarioMovimentacao();
            usuario.setId(e.getUsuario().getId());
            usuario.setNome(e.getUsuario().getNome());
            m.setUsuario(usuario);

            m.setDetalhe("Lote: " + (e.getLote() != null ? e.getLote() : "-") +
                    (e.getDtValidade() != null ? " | Validade: " + e.getDtValidade() : ""));
            movimentacoes.add(m);
        }

        for (SaidaEstoque s : saidas) {
            MovimentacaoResponse m = new MovimentacaoResponse();
            m.setId(s.getId());
            m.setTipo("saida");
            m.setData(s.getDtSaida());
            m.setQuantidade(s.getQuantidade());

            MovimentacaoResponse.InsumoMovimentacao insumo = new MovimentacaoResponse.InsumoMovimentacao();
            insumo.setId(s.getInsumo().getId());
            insumo.setNome(s.getInsumo().getNome());
            m.setInsumo(insumo);

            MovimentacaoResponse.UsuarioMovimentacao usuario = new MovimentacaoResponse.UsuarioMovimentacao();
            usuario.setId(s.getUsuario().getId());
            usuario.setNome(s.getUsuario().getNome());
            m.setUsuario(usuario);

            m.setDetalhe("Motivo: " + (s.getMotivo() != null ? s.getMotivo().getNome() : "-"));
            movimentacoes.add(m);
        }

        movimentacoes.sort(Comparator.comparing(MovimentacaoResponse::getData).reversed());

        return movimentacoes;
    }
}