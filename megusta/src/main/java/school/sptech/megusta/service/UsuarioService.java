package school.sptech.megusta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import school.sptech.megusta.dto.usuario.UsuarioRequestDto;
import school.sptech.megusta.dto.usuario.UsuarioResponseDto;
import school.sptech.megusta.exception.UsuarioConflitoException;
import school.sptech.megusta.exception.UsuarioNaoEncontradoException;
import school.sptech.megusta.mapper.UsuarioMapper;
import school.sptech.megusta.model.Usuario;
import school.sptech.megusta.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

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
        Optional<Usuario> usuarioOptional = repository.findById(id);
        if(usuarioOptional.isEmpty()){
            throw new UsuarioNaoEncontradoException(id);
        }
        return usuarioOptional.get();
    }

    public UsuarioResponseDto cadastrar(UsuarioRequestDto requestDto){
        requestDto.setSenha(passwordEncoder.encode(requestDto.getSenha()));
        Usuario usuarioParaCadastrar = UsuarioMapper.toEntity(requestDto);

        boolean existe = repository.existsByNomeAndEmail(usuarioParaCadastrar.getNome(),
                usuarioParaCadastrar.getEmail());
        if(existe){
           throw new UsuarioConflitoException("Usuário já existe!");
        }
        Usuario usuarioCadastrado = repository.save(usuarioParaCadastrar);
        return UsuarioMapper.toResponseDto(usuarioCadastrado);
    }

    public UsuarioResponseDto atualizar(UsuarioRequestDto requestDto, Integer id){
        requestDto.setSenha(passwordEncoder.encode(requestDto.getSenha()));
        Usuario usuarioParaAtualizar = UsuarioMapper.toEntity(requestDto);

        if(!repository.existsById(id)){
            throw new UsuarioNaoEncontradoException(id);
        }
        boolean existeEmDuplicidade = repository.existsByNomeAndEmailAndIdNot(usuarioParaAtualizar.getNome(),
                usuarioParaAtualizar.getEmail(), id);
        if(existeEmDuplicidade){
            throw new UsuarioConflitoException("Usuário já existe!");
        }
        usuarioParaAtualizar.setId(id);
        Usuario usuarioAtualizado = repository.save(usuarioParaAtualizar);
        return UsuarioMapper.toResponseDto(usuarioAtualizado);
    }

    public void excluir(Integer id){
        boolean existe = repository.existsById(id);
        if(!existe){
            throw new UsuarioNaoEncontradoException(id);
        }
        repository.deleteById(id);
    }

}
