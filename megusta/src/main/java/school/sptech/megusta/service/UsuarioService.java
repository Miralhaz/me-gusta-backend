package school.sptech.megusta.service;

import org.springframework.stereotype.Service;
import school.sptech.megusta.exception.UsuarioConflitoException;
import school.sptech.megusta.exception.UsuarioNaoEncontradoException;
import school.sptech.megusta.model.Usuario;
import school.sptech.megusta.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

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

    public Usuario cadastrar(Usuario usuarioParaCadastrar){
        boolean existe = repository.existsByNomeAndEmail(usuarioParaCadastrar.getNome(),
                usuarioParaCadastrar.getEmail());
        if(existe){
           throw new UsuarioConflitoException("Usuário já existe!");
        }
        Usuario usuarioCadastrado = repository.save(usuarioParaCadastrar);
        return usuarioCadastrado;
    }

    public Usuario atualizar(Usuario usuarioParaAtualizar, Integer id){
        if(!repository.existsById(id)){
            throw new UsuarioNaoEncontradoException(id);
        }
        boolean existeEmDuplicidade = repository.existsByNomeAndEmailAndIdNot(usuarioParaAtualizar.getNome(),
                usuarioParaAtualizar.getEmail(), usuarioParaAtualizar.getId());
        if(existeEmDuplicidade){
            throw new UsuarioConflitoException("Usuário já existe!");
        }
        usuarioParaAtualizar.setId(id);
        Usuario usuarioAtualizado = repository.save(usuarioParaAtualizar);
        return usuarioAtualizado;
    }

    public void excluir(Integer id){
        boolean existe = repository.existsById(id);
        if(!existe){
            throw new UsuarioNaoEncontradoException(id);
        }
        repository.deleteById(id);
    }

}
