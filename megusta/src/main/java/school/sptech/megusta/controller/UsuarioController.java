package school.sptech.megusta.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.megusta.dto.usuario.UsuarioRequestDto;
import school.sptech.megusta.dto.usuario.UsuarioResponseDto;
import school.sptech.megusta.mapper.UsuarioMapper;
import school.sptech.megusta.model.Usuario;
import school.sptech.megusta.service.UsuarioService;

import java.util.List;


@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> listar(){
        List<Usuario> users = service.listar();
        if(users.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        List<UsuarioResponseDto> response = UsuarioMapper.toResponseDtoList(users);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> buscarPorId(@PathVariable Integer id){
        Usuario user = service.buscarPorId(id);
        return ResponseEntity.ok(UsuarioMapper.toResponseDto(user));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDto> cadastrar(
            @RequestBody UsuarioRequestDto dto
            ){
        return ResponseEntity.status(201).body(service.cadastrar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> atualizar(
            @PathVariable Integer id,
            @RequestBody UsuarioRequestDto dto
    ){
        return ResponseEntity.ok(service.atualizar(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id){
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
