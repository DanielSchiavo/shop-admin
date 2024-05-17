package br.com.danielschiavo.shop.service.produto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.service.produto.ProdutoUtilidadeService;
import br.com.danielschiavo.shop.feign.FileStorageProdutoAdminServiceClient;
import br.com.danielschiavo.shop.model.produto.Produto;
import br.com.danielschiavo.shop.model.produto.dto.AlterarProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.CadastrarProdutoDTO;
import br.com.danielschiavo.shop.repository.produto.ProdutoRepository;
import br.com.danielschiavo.shop.service.produto.validacoes.ValidadorCadastrarNovoProduto;
import lombok.Setter;

@Service
public class ProdutoAdminService {
	
	@Autowired
	private UsuarioAutenticadoService usuarioAutenticadoService;
	
	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private FileStorageProdutoAdminServiceClient fileStorageProdutoAdminServiceClient;

	@Autowired
	private List<ValidadorCadastrarNovoProduto> validador;
	
	@Setter
	@Autowired
	private ProdutoMapper produtoMapper;
	
	@Autowired
	private ProdutoUtilidadeService produtoUtilidadeService;
	
	@Transactional
	public String deletarProdutoPorId(Long id) {
		String tokenComBearer = usuarioAutenticadoService.getTokenComBearer();
		
		Produto produto = produtoUtilidadeService.pegarProdutoPorId(id);
		List<String> nomeTodosArquivos = produtoUtilidadeService.pegarNomeTodosArquivos(produto);
		
		System.out.println(" TESTE " + nomeTodosArquivos.size());
		
		fileStorageProdutoAdminServiceClient.deletarArquivosProduto(new HashSet<>(nomeTodosArquivos), tokenComBearer);
		
		produtoRepository.delete(produto);
		return "Produto deletado com sucesso!";
	}
	
	@Transactional
	public Map<String, String> cadastrarProduto(CadastrarProdutoDTO cadastrarProdutoDTO) {
		validador.forEach(v -> v.validar(cadastrarProdutoDTO));
		
		Produto produto = produtoMapper.cadastrarProdutoDtoParaProduto(cadastrarProdutoDTO);
		produtoRepository.save(produto);
		
		Map<String, String> resposta = new HashMap<>();
		resposta.put("id", produto.getId().toString());
		resposta.put("mensagem", "Produto cadastrado com sucesso!");
		return resposta;
	}

	@Transactional
	public String alterarProdutoPorId(Long id, AlterarProdutoDTO alterarProdutoDTO) {
		String tokenComBearer = usuarioAutenticadoService.getTokenComBearer();
		Produto produto = produtoUtilidadeService.pegarProdutoPorId(id);
		
		produtoMapper.alterarProdutoDtoParaProduto(alterarProdutoDTO, produto);

		Set<String> listaNomesArquivos = alterarProdutoDTO.arquivos().stream().map(a -> a.nome()).collect(Collectors.toSet());
		fileStorageProdutoAdminServiceClient.deletarArquivosProduto(listaNomesArquivos, tokenComBearer);
		
		produtoRepository.save(produto);
		
		return "Produto alterado com sucesso!";
	}

	
//	------------------------------
//	------------------------------
//	METODOS UTILITARIOS
//	------------------------------
//	------------------------------
	

	
}
