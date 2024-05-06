package br.com.danielschiavo.shop.controller.produto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.danielschiavo.shop.model.produto.subcategoria.AlterarSubCategoriaDTO;
import br.com.danielschiavo.shop.model.produto.subcategoria.CadastrarSubCategoriaDTO;
import br.com.danielschiavo.shop.model.produto.subcategoria.MostrarSubCategoriaDTO;
import br.com.danielschiavo.shop.service.produto.SubCategoriaAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/shop")
@Tag(name = "Sub Categorias - Admin", description = "Todos endpoints relacionados com as subcategorias para uso exclusivo dos administradores")
public class SubCategoriaAdminController {
	
	@Autowired
	private SubCategoriaAdminService subCategoriaAdminService;
	
	@DeleteMapping("/admin/sub-categoria/{idSubCategoria}")
	@SecurityRequirement(name = "bearer-key")
	@Operation(summary = "Deleta uma subcategoria com o id fornecido no parametro da requisição")
	public ResponseEntity<?> deletarSubCategoriaPorId(@PathVariable Long idSubCategoria){		
		subCategoriaAdminService.deletarSubCategoriaPorId(idSubCategoria);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/admin/sub-categoria")
	@SecurityRequirement(name = "bearer-key")
	@Operation(summary = "Cria uma nova subcategoria, uma subcategoria tem que ter uma categoria a qual ela está relacionada")
	public ResponseEntity<MostrarSubCategoriaDTO> cadastrarSubCategoria(@RequestBody @Valid CadastrarSubCategoriaDTO dto, UriComponentsBuilder uriBuilder) {
		MostrarSubCategoriaDTO subCategoria = subCategoriaAdminService.cadastrarSubCategoria(dto);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(subCategoria);
	}
	
	@PutMapping("/admin/sub-categoria/{idSubCategoria}")
	@SecurityRequirement(name = "bearer-key")
	@Operation(summary = "Altera o nome de uma subcategoria com o id fornecido no parametro da requisição")
	public ResponseEntity<MostrarSubCategoriaDTO> alterarSubCategoriaPorId(@PathVariable Long idSubCategoria, @RequestBody AlterarSubCategoriaDTO categoryDTO) {
		var subCategoriaDTO = subCategoriaAdminService.alterarSubCategoriaPorId(idSubCategoria, categoryDTO);
		
		return ResponseEntity.ok(subCategoriaDTO);
	}
}
