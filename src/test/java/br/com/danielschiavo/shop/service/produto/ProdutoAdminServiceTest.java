package br.com.danielschiavo.shop.service.produto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.danielschiavo.feign.produto.FileStorageProdutoComumServiceClient;
import br.com.danielschiavo.service.produto.CategoriaUtilidadeService;
import br.com.danielschiavo.service.produto.ProdutoUtilidadeService;
import br.com.danielschiavo.service.produto.SubCategoriaUtilidadeService;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import br.com.danielschiavo.shop.model.pedido.TipoEntrega;
import br.com.danielschiavo.shop.model.produto.Produto;
import br.com.danielschiavo.shop.model.produto.Produto.ProdutoBuilder;
import br.com.danielschiavo.shop.model.produto.arquivosproduto.ArquivoProduto;
import br.com.danielschiavo.shop.model.produto.arquivosproduto.ArquivoProdutoDTO;
import br.com.danielschiavo.shop.model.produto.categoria.Categoria;
import br.com.danielschiavo.shop.model.produto.categoria.Categoria.CategoriaBuilder;
import br.com.danielschiavo.shop.model.produto.dto.AlterarProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.AlterarProdutoDTO.AlterarProdutoDTOBuilder;
import br.com.danielschiavo.shop.model.produto.dto.CadastrarProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.CadastrarProdutoDTO.CadastrarProdutoDTOBuilder;
import br.com.danielschiavo.shop.model.produto.tipoentregaproduto.TipoEntregaProduto;
import br.com.danielschiavo.shop.repository.cliente.CarrinhoRepository;
import br.com.danielschiavo.shop.repository.produto.ProdutoRepository;
import br.com.danielschiavo.shop.repository.produto.SubCategoriaRepository;
import br.com.danielschiavo.shop.service.produto.validacoes.ValidadorCadastrarNovoProduto;

@ExtendWith(MockitoExtension.class)
class ProdutoAdminServiceTest {
	
	@InjectMocks
	private ProdutoAdminService produtoAdminService;
	
	@Mock
	private ProdutoRepository produtoRepository;
	
	@Mock
	private CarrinhoRepository carrinhoRepository;
	
	@Mock
	private SubCategoriaRepository subCategoriaRepository;
	
	@Mock
	private ProdutoUtilidadeService produtoUtilidadeService;
	
	@Mock
	private FileStorageProdutoComumServiceClient fileStorageProdutoService;
	
	@Mock
	private CategoriaUtilidadeService categoriaUtilidadeService;
	
	@Mock
	private SubCategoriaUtilidadeService subCategoriaUtilidadeService;
	
	@Spy
	private List<ValidadorCadastrarNovoProduto> validadores = new ArrayList<>();
	
	@Mock
	private ValidadorCadastrarNovoProduto validador1;
	
	@Mock
	private ValidadorCadastrarNovoProduto validador2;
	
	private ProdutoBuilder produtoBuilder = Produto.builder();
	
	private CategoriaBuilder categoriaBuilder = Categoria.builder();
	
	private CadastrarProdutoDTOBuilder cadastrarProdutoDTOBuilder = CadastrarProdutoDTO.builder();
	
	private AlterarProdutoDTOBuilder alterarProdutoDTOBuilder = AlterarProdutoDTO.builder();
	
	@BeforeEach
	public void beforeEach() {
		ProdutoMapper produtoMapper = Mappers.getMapper(ProdutoMapper.class);
		produtoAdminService.setProdutoMapper(produtoMapper);
	}
	
	@Test
	void cadastrarProduto() {
		//ARRANGE
		//Categoria e SubCategoria
		Categoria categoria = categoriaBuilder.categoria(1L, "Computadores")
												  .comSubCategoria(1L, "Teclado")
											  .getCategoria();
		//CadastrarProdutoDTO
		var arquivo = new ArquivoProdutoDTO("Padrao.jpeg", (byte) 0);
		CadastrarProdutoDTO cadastrarProdutoDTO = 
				cadastrarProdutoDTOBuilder.nome("Teclado")
										  .descricao("Descrição teclado")
										  .preco(BigDecimal.valueOf(200.00))
										  .quantidade(999)
										  .ativo(true)
										  .subCategoriaId(1L)
										  .tiposEntrega(Set.of(TipoEntrega.ENTREGA_DIGITAL))
										  .arquivos(Set.of(arquivo)).build();
		//Validadores
		validadores.addAll(List.of(validador1, validador2));
		//ArquivoInfoDTO
		ArquivoInfoDTO arquivoInfoDTO = new ArquivoInfoDTO("Padrao.jpeg", "Bytes do arquivo Padrao.jpeg".getBytes());
		//When
		when(subCategoriaUtilidadeService.verificarSeExisteSubCategoriaPorId(any(Long.class))).thenReturn(categoria.getSubCategorias().get(0));
		when(produtoUtilidadeService.pegarNomePrimeiraImagem(any(Produto.class))).thenReturn(arquivo.nome());
		when(fileStorageProdutoService.pegarArquivoProduto(any(String.class))).thenReturn(arquivoInfoDTO);
		when(categoriaUtilidadeService.verificarSeExisteCategoriaPorId(any(Long.class))).thenReturn(categoria);
		
		//ACT
		Map<String, String> mostrarProdutosDTO = produtoAdminService.cadastrarProduto(cadastrarProdutoDTO);
		
		//ASSERT
		BDDMockito.then(validador1).should().validar(cadastrarProdutoDTO);
		BDDMockito.then(validador2).should().validar(cadastrarProdutoDTO);
		verify(produtoRepository, times(1)).save(any(Produto.class));
	}
	
	@Test
	void alterarProdutoPorId() {
		//ARRANGE
		//Produto
		Produto produto = produtoBuilder.id(1L)
										 .nome("Teclado gamer")
										 .descricao("Descricao Teclado gamer")
										 .preco(BigDecimal.valueOf(200.00))
										 .quantidade(100)
										 .tiposEntrega(Set.of(TipoEntregaProduto.builder().tipoEntrega(TipoEntrega.ENTREGA_DIGITAL).build()))
										 .arquivosProduto(Set.of(ArquivoProduto.builder().nome("Padrao.jpeg").posicao((byte) 0).build()))
										 .subCategoriaId(1L)
										 .build();
		
		Long idProduto = 1L;
		when(produtoUtilidadeService.pegarProdutoPorId(idProduto)).thenReturn(produto);
		
		//ACT
		AlterarProdutoDTO alterarProdutoDTO = alterarProdutoDTOBuilder.nome("Novo nome do teclado").descricao("Nova descricao do Teclado").preco(BigDecimal.valueOf(500.00)).quantidade(500).ativo(true).subCategoriaId(2L).tiposEntrega(Set.of(TipoEntrega.CORREIOS, TipoEntrega.RETIRADA_NA_LOJA)).arquivos(Set.of(ArquivoProdutoDTO.builder().nome("Padrao.jpeg").posicao((byte) 0).build())).build();
		String respostaAlterarProduto = produtoAdminService.alterarProdutoPorId(idProduto, alterarProdutoDTO);
		
		//ASSERT
		//Produto
		Assertions.assertEquals("Produto alterado com sucesso!", respostaAlterarProduto);
	}

}
