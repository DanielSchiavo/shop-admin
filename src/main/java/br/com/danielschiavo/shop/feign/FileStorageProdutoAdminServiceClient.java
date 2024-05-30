package br.com.danielschiavo.shop.feign;

import java.util.List;
import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import br.com.danielschiavo.shop.model.produto.arquivosproduto.ArquivoProdutoDTO;

@FeignClient(name = "filestorage-produto-admin-service", url = "${filestorage.service.client.url}")
public interface FileStorageProdutoAdminServiceClient {

	@GetMapping("/publico/produto/{arquivo}")
	ResponseEntity<List<ArquivoProdutoDTO>> verificarSeExisteArquivos(@PathVariable("arquivo") List<String> nomeArquivos);
	
	@DeleteMapping("/admin/produto/{arquivo}")
	ResponseEntity<List<ArquivoProdutoDTO>> deletarArquivosProduto(@PathVariable("arquivo") Set<String> arquivos, @RequestHeader("Authorization") String token);
	
}
