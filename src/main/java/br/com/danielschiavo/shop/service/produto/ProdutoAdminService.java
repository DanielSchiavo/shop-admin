package br.com.danielschiavo.shop.service.produto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.danielschiavo.mapper.produto.ProdutoMapper;
import br.com.danielschiavo.repository.produto.ProdutoRepository;
import br.com.danielschiavo.service.produto.CategoriaUtilidadeService;
import br.com.danielschiavo.service.produto.ProdutoUtilidadeService;
import br.com.danielschiavo.service.produto.SubCategoriaUtilidadeService;
import br.com.danielschiavo.shop.model.produto.Produto;
import br.com.danielschiavo.shop.model.produto.dto.AlterarProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.CadastrarProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.DetalharProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.MostrarProdutosDTO;
import br.com.danielschiavo.shop.model.produto.subcategoria.SubCategoria;
import br.com.danielschiavo.shop.service.filestorage.FileStorageProdutoService;
import br.com.danielschiavo.shop.service.produto.validacoes.ValidadorCadastrarNovoProduto;
import lombok.Setter;

@Service
@Setter
public class ProdutoAdminService {

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private FileStorageProdutoService fileStorageProdutoService;

	@Autowired
	private List<ValidadorCadastrarNovoProduto> validador;
	
	@Autowired
	private ProdutoMapper produtoMapper;
	
	@Autowired
	private SubCategoriaUtilidadeService subCategoriaUtilidadeService;
	
	@Autowired
	private ProdutoUtilidadeService produtoUtilidadeService;
	
	@Autowired
	private CategoriaUtilidadeService categoriaUtilidadeService;

	@Transactional
	public void deletarProdutoPorId(Long id) {
		Produto produto = produtoUtilidadeService.verificarSeProdutoExistePorId(id);
		produtoRepository.delete(produto);
	}
	
	@Transactional
	public MostrarProdutosDTO cadastrarProduto(CadastrarProdutoDTO cadastrarProdutoDTO) {
		validador.forEach(v -> v.validar(cadastrarProdutoDTO));
		
		SubCategoria subCategoria = subCategoriaUtilidadeService.verificarSeExisteSubCategoriaPorId(cadastrarProdutoDTO.idSubCategoria());
		Produto produto = produtoMapper.cadastrarProdutoDtoParaProduto(cadastrarProdutoDTO, subCategoria);
		produtoRepository.save(produto);
		
		return produtoMapper.produtoParaMostrarProdutosDTO(produto, fileStorageProdutoService, produtoUtilidadeService, categoriaUtilidadeService);
	}

	@Transactional
	public DetalharProdutoDTO alterarProdutoPorId(Long id, AlterarProdutoDTO alterarProdutoDTO) {
		Produto produto = produtoUtilidadeService.verificarSeProdutoExistePorId(id);
		alterarProdutoDTO.arquivos().forEach(arquivo -> {
			fileStorageProdutoService.verificarSeExisteArquivoProdutoPorNome(arquivo.nome());
		});
		SubCategoria subCategoria = subCategoriaUtilidadeService
				.verificarSeExisteSubCategoriaPorId(alterarProdutoDTO.idSubCategoria());

		produtoMapper.alterarProdutoDtoParaProduto(alterarProdutoDTO, produto, subCategoria);
		produtoRepository.save(produto);
		
		return produtoMapper.produtoParaDetalharProdutoDTO(produto, fileStorageProdutoService, produtoUtilidadeService, categoriaUtilidadeService);
	}

	
//	------------------------------
//	------------------------------
//	METODOS UTILITARIOS
//	------------------------------
//	------------------------------
	

	
}
