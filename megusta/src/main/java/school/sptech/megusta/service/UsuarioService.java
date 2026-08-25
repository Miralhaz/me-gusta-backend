package school.sptech.megusta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import school.sptech.megusta.dto.usuario.UsuarioRequestDto;
import school.sptech.megusta.dto.usuario.UsuarioResponseDto;
import school.sptech.megusta.dto.usuario.UsuarioUpdateDto;
import school.sptech.megusta.exception.AcessoNegadoException;
import school.sptech.megusta.exception.RecursoConflitoException;
import school.sptech.megusta.exception.RecursoNaoEncontradoException;
import school.sptech.megusta.mapper.UsuarioMapper;
import school.sptech.megusta.model.Usuario;
import school.sptech.megusta.repository.UsuarioRepository;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<Usuario> listar(){
        return repository.findAll();
    }

    public Usuario buscarPorId(Integer id){
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }

    public UsuarioResponseDto cadastrar(UsuarioRequestDto requestDto){
        requestDto.setSenha(passwordEncoder.encode(requestDto.getSenha()));
        Usuario usuarioParaCadastrar = UsuarioMapper.toEntity(requestDto);

        boolean existe = repository.existsByNomeAndEmail(usuarioParaCadastrar.getNome(),
                usuarioParaCadastrar.getEmail());
        if(existe){
           throw new RecursoConflitoException("Usuário já existe!");
        }
        Usuario usuarioCadastrado = repository.save(usuarioParaCadastrar);
        return UsuarioMapper.toResponseDto(usuarioCadastrado);
    }

    // 1° vulnerabilidade: Conseguir editar informações de usuário mesmo sendo de IDs diferentes
    // Correção: o ID da URL deve ser comparado com o ID do usuário autenticado
    public UsuarioResponseDto atualizar(UsuarioUpdateDto requestDto, Integer id, Integer idAutenticado){

        if (!id.equals(idAutenticado)) {
            throw new AcessoNegadoException("Sem permissão para alterar este usuário");
        }

        Usuario existente = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        boolean existeEmDuplicidade = repository.existsByNomeAndEmailAndIdNot(
                requestDto.getNome(), requestDto.getEmail(), id);
        if (existeEmDuplicidade) {
            throw new RecursoConflitoException("Usuário já existe!");
        }

        existente.setNome(requestDto.getNome());
        existente.setEmail(requestDto.getEmail());
        Usuario usuarioAtualizado = repository.save(existente);
        return UsuarioMapper.toResponseDto(usuarioAtualizado);
    }

    public void excluir(Integer id, Integer idAutenticado){
        if (!id.equals(idAutenticado)) {
            throw new AcessoNegadoException("Sem permissão para excluir este usuário");
        }
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado");
        }
        repository.deleteById(id);
    }

}
