package school.sptech.megusta.service;

import org.springframework.stereotype.Service;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.model.Insumo;
import school.sptech.megusta.model.Motivo;
import school.sptech.megusta.model.SaidaEstoque;
import school.sptech.megusta.model.Usuario;
import school.sptech.megusta.repository.InsumoRepository;
import school.sptech.megusta.repository.MotivoRepository;
import school.sptech.megusta.repository.SaidaEstoqueRepository;
import school.sptech.megusta.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaidaEstoqueService {

    private final SaidaEstoqueRepository saidaRepo;
    private final InsumoRepository insumoRepo;
    private final UsuarioRepository usuarioRepo;
    private final MotivoRepository motivoRepo;

    public SaidaEstoqueService(SaidaEstoqueRepository saidaRepo,
                               InsumoRepository insumoRepo,
                               UsuarioRepository usuarioRepo, MotivoRepository motivoRepo) {
        this.saidaRepo = saidaRepo;
        this.insumoRepo = insumoRepo;
        this.usuarioRepo = usuarioRepo;
        this.motivoRepo = motivoRepo;
    }

    public List<SaidaEstoque> listar() {
        return saidaRepo.findAll();
    }

    public SaidaEstoque buscarPorId(Integer id) {
        return saidaRepo.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Saída de estoque não encontrada."));
    }

    public SaidaEstoque cadastrar(SaidaEstoque novaSaida) {
         Insumo insumo = insumoRepo.findById(novaSaida.getInsumo().getId())
                 .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo não encontrado."));
         Usuario usuario = usuarioRepo.findById(novaSaida.getUsuario().getId())
                 .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
         Motivo motivo = motivoRepo.findById(novaSaida.getMotivo().getId())
                 .orElseThrow(() -> new RecursoNaoEncontradoException("Motivo não encontrado."));

         novaSaida.setInsumo(insumo);
         novaSaida.setUsuario(usuario);
         novaSaida.setMotivo(motivo);

         if (novaSaida.getDtSaida() == null) {
             novaSaida.setDtSaida(LocalDateTime.now());
         }

         Double quantidadeASubtrair = novaSaida.getQuantidade().doubleValue();
         insumo.setQtdAtual(insumo.getQtdAtual() - quantidadeASubtrair);
         insumoRepo.save(insumo);

         return saidaRepo.save(novaSaida);
     }

    public SaidaEstoque atualizar(SaidaEstoque saidaAtualizada, Integer id) {
         SaidaEstoque existente = saidaRepo.findById(id)
                 .orElseThrow(() -> new RecursoNaoEncontradoException("Saída de estoque não encontrada."));

         Insumo insumo = insumoRepo.findById(saidaAtualizada.getInsumo().getId())
                 .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo não encontrado."));
         Usuario usuario = usuarioRepo.findById(saidaAtualizada.getUsuario().getId())
                 .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
         Motivo motivo = motivoRepo.findById(saidaAtualizada.getMotivo().getId())
                 .orElseThrow(() -> new RecursoNaoEncontradoException("Motivo não encontrado."));

         Double quantidadeAnterior = existente.getQuantidade().doubleValue();
         insumo.setQtdAtual(insumo.getQtdAtual() + quantidadeAnterior);

         existente.setInsumo(insumo);
         existente.setUsuario(usuario);
         existente.setMotivo(motivo);
         existente.setQuantidade(saidaAtualizada.getQuantidade());

         Double novaQuantidade = saidaAtualizada.getQuantidade().doubleValue();
         insumo.setQtdAtual(insumo.getQtdAtual() - novaQuantidade);
         insumoRepo.save(insumo);

         if (saidaAtualizada.getDtSaida() != null) {
             existente.setDtSaida(saidaAtualizada.getDtSaida());
         }

         return saidaRepo.save(existente);
     }

    public void deletar(Integer id) {
        SaidaEstoque existente = saidaRepo.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Saída de estoque não encontrada."));
        saidaRepo.delete(existente);
    }

    public List<SaidaEstoque> buscarPorInsumo(Integer insumoId) {
        insumoRepo.findById(insumoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo não encontrado."));
        return saidaRepo.findByInsumoId(insumoId);
    }

    public List<SaidaEstoque> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return saidaRepo.findByDtSaidaBetween(inicio, fim);
    }
}