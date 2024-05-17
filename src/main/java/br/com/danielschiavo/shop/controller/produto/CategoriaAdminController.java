package br.com.danielschiavo.shop.controller.produto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.produto.categoria.CriarCategoriaDTO;
import br.com.danielschiavo.shop.model.produto.categoria.MostrarCategoriaDTO;
import br.com.danielschiavo.shop.service.produto.CategoriaAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping
@Tag(name = "Categorias - Admin", description = "Todos endpoints relacionados com as categorias dos produtos da loja, para uso exclusivo dos administradores")
public class CategoriaAdminController {
	
	@Autowired
	private CategoriaAdminService categoriaAdminService;
	
	@DeleteMapping("/admin/categoria/{idCategoria}")
	@SecurityRequirement(name = "bearer-key")
	@Operation(summary = "Deleta uma categoria e todas subcategorias que tiverem vinculado a essa categoria", operationId = "04_deletarCategoriaPorId")
	public ResponseEntity<?> deletarCategoriaPorId(@PathVariable Long idCategoria) {
	    try {
	        categoriaAdminService.deletarCategoriaPorId(idCategoria);
	        return ResponseEntity.noContent().build();
	    } catch (DataIntegrityViolationException e) {
	        return ResponseEntity.badRequest().body("Não é possível excluir esta categoria enquanto houver produtos relacionados. Por favor, remova o relacionamento de produto com categoria primeiro.");
	    } catch (Exception e) {
	        return ResponseEntity.internalServerError().body("Erro ao deletar categoria.");
	    }
	}
	
	@PostMapping("/admin/categoria")
	@SecurityRequirement(name = "bearer-key")
	@Operation(summary = "Cria uma categoria", 
	   		   operationId = "03_criarCategoria")
	public ResponseEntity<?> criarCategoria(@RequestBody @Valid CriarCategoriaDTO categoriaDTO) {
		try {
			MostrarCategoriaDTO mostrarCategoriaDTO = categoriaAdminService.criarCategoria(categoriaDTO.nome());
			return ResponseEntity.status(HttpStatus.CREATED).body(mostrarCategoriaDTO);
		} catch (ValidacaoException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
		}
		
	}
	
	@PutMapping("/admin/categoria/{idCategoriaASerAlterada}")
	@SecurityRequirement(name = "bearer-key")
	@Operation(summary = "Altera o nome da categoria", 
	   		   operationId = "02_alterarNomeCategoriaPorId")
	public ResponseEntity<?> alterarNomeCategoriaPorId(@PathVariable Long idCategoriaASerAlterada, @RequestBody @NotNull CriarCategoriaDTO categoriaDTO) {
		MostrarCategoriaDTO mostrarCategoriaDTO = categoriaAdminService.alterarNomeCategoriaPorId(idCategoriaASerAlterada, categoriaDTO);
		
		return ResponseEntity.status(HttpStatus.OK).body(mostrarCategoriaDTO);
	}
}
