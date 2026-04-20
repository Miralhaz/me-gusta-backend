package school.sptech.megusta.service;

import org.springframework.stereotype.Service;
import school.sptech.megusta.exception.FornecedorConflitoException;
import school.sptech.megusta.exception.FornecedorNaoEncontradoException;
import school.sptech.megusta.model.Fornecedor;
import school.sptech.megusta.repository.FornecedorRepository;

import java.util.List;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }


    public List<Fornecedor> listar(){
        return fornecedorRepository.findAll();
    }

    public Fornecedor cadastrar(Fornecedor fornecedor){
        boolean existe = fornecedorRepository.existsByNomeAndCnpj(fornecedor.getNome(), fornecedor.getCnpj());
        if(existe){
            throw new FornecedorConflitoException("Fornecedor já existente.");
        }
        return fornecedorRepository.save(fornecedor);
    }

    public Fornecedor buscarPorId(Integer id){
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new FornecedorNaoEncontradoException("Fornecedor não encontrado"));

        return fornecedor;
    }


    public Fornecedor atualizar(Integer id, Fornecedor fornecedor){
        fornecedorRepository.findById(id)
                .orElseThrow(() -> new FornecedorNaoEncontradoException("Fornecedor não encontrado"));

        if(fornecedorRepository.existsByNomeAndCnpjAndIdNot(fornecedor.getNome(), fornecedor.getCnpj(), id)){
            throw new FornecedorConflitoException("Fornecedor já existente");
        }
        fornecedor.setId(id);
        return fornecedorRepository.save(fornecedor);
    }

    public void excluir(Integer id){
        fornecedorRepository.findById(id)
                .orElseThrow(() -> new FornecedorNaoEncontradoException("Fornecedor não encontrado."));

        fornecedorRepository.deleteById(id);
    }

}
