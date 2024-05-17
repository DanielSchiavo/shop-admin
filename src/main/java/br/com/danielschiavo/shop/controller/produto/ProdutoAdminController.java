package br.com.danielschiavo.shop.controller.produto;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.danielschiavo.shop.model.produto.dto.AlterarProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.CadastrarProdutoDTO;
import br.com.danielschiavo.shop.service.produto.ProdutoAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping
@Tag(name = "Produto - Admin", description = "Todos endpoints relacionados com os produtos da loja, para uso exclusivo dos administradores")
public class ProdutoAdminController {

	@Autowired
	private ProdutoAdminService produtoService;
	
	@DeleteMapping("/admin/produto/{idProduto}")
	@Operation(summary = "Deleta um produto com o id fornecido no parametro da requisição")
	@SecurityRequirement(name = "bearer-key")
	public ResponseEntity<?> deletarProdutoPorId(@PathVariable @NotNull Long idProduto) {
		String respostaDeletarProduto = produtoService.deletarProdutoPorId(idProduto);
		return ResponseEntity.ok(respostaDeletarProduto);
	}
	
	@PostMapping(path = "/admin/produto")
	@ResponseBody
	@SecurityRequirement(name = "bearer-key")
	@Operation(summary = "Cadastra um novo produto")
	public ResponseEntity<?> cadastrarProduto(
			@RequestBody @Valid CadastrarProdutoDTO cadastrarProdutoDTO,
			UriComponentsBuilder uriBuilder
 			) {
		try {
			Map<String, String> respostaCadastrarProduto = produtoService.cadastrarProduto(cadastrarProdutoDTO);
			
			var uri = uriBuilder.path("/products/{id}").buildAndExpand(respostaCadastrarProduto.get("id")).toUri();
			return ResponseEntity.created(uri).body(respostaCadastrarProduto.get("mensagem"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/admin/produto/{idProduto}")
	@SecurityRequirement(name = "bearer-key")
	@Operation(summary = "Altera um produto com o id fornecido no parametro da requisição")
	public ResponseEntity<?> alterarProdutoPorId(
			@PathVariable Long idProduto,
			@RequestBody AlterarProdutoDTO alterarProdutoDTO
			) {
		try {
			String respostaAlterarProduto = produtoService.alterarProdutoPorId(idProduto, alterarProdutoDTO);
			
			return ResponseEntity.ok(respostaAlterarProduto);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}
