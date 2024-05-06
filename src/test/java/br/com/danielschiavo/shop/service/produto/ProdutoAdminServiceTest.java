package br.com.danielschiavo.shop.service.produto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

import br.com.danielschiavo.mapper.produto.ProdutoMapper;
import br.com.danielschiavo.repository.cliente.CarrinhoRepository;
import br.com.danielschiavo.repository.produto.ProdutoRepository;
import br.com.danielschiavo.repository.produto.SubCategoriaRepository;
import br.com.danielschiavo.service.produto.CategoriaUtilidadeService;
import br.com.danielschiavo.service.produto.ProdutoUtilidadeService;
import br.com.danielschiavo.service.produto.SubCategoriaUtilidadeService;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import br.com.danielschiavo.shop.model.pedido.TipoEntrega;
import br.com.danielschiavo.shop.model.produto.Produto;
import br.com.danielschiavo.shop.model.produto.Produto.ProdutoBuilder;
import br.com.danielschiavo.shop.model.produto.arquivosproduto.ArquivoProdutoDTO;
import br.com.danielschiavo.shop.model.produto.categoria.Categoria;
import br.com.danielschiavo.shop.model.produto.categoria.Categoria.CategoriaBuilder;
import br.com.danielschiavo.shop.model.produto.dto.AlterarProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.AlterarProdutoDTO.AlterarProdutoDTOBuilder;
import br.com.danielschiavo.shop.model.produto.dto.CadastrarProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.CadastrarProdutoDTO.CadastrarProdutoDTOBuilder;
import br.com.danielschiavo.shop.model.produto.dto.DetalharProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.MostrarProdutosDTO;
import br.com.danielschiavo.shop.service.filestorage.FileStorageProdutoService;
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
	private FileStorageProdutoService fileStorageProdutoService;
	
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
		CadastrarProdutoDTO cadastrarProdutoDTO = 
				cadastrarProdutoDTOBuilder.nome("Teclado")
										  .descricao("Descrição teclado")
										  .preco(BigDecimal.valueOf(200.00))
										  .quantidade(999)
										  .ativo(true)
										  .idSubCategoria(1L)
										  .tipoEntrega(Set.of(TipoEntrega.ENTREGA_DIGITAL))
										  .arquivos(List.of(new ArquivoProdutoDTO("Padrao.jpeg", (byte) 0))).build();
		//Validadores
		validadores.addAll(List.of(validador1, validador2));
		//ArquivoInfoDTO
		ArquivoInfoDTO arquivoInfoDTO = new ArquivoInfoDTO("Padrao.jpeg", "Bytes do arquivo Padrao.jpeg".getBytes());
		//When
		when(subCategoriaUtilidadeService.verificarSeExisteSubCategoriaPorId(any(Long.class))).thenReturn(categoria.getSubCategorias().get(0));
		when(produtoUtilidadeService.pegarNomePrimeiraImagem(any(Produto.class))).thenReturn(cadastrarProdutoDTO.arquivos().get(0).nome());
		when(fileStorageProdutoService.pegarArquivoProdutoPorNome(any(String.class))).thenReturn(arquivoInfoDTO);
		when(categoriaUtilidadeService.verificarSeExisteCategoriaPorId(any(Long.class))).thenReturn(categoria);
		
		//ACT
		MostrarProdutosDTO mostrarProdutosDTO = produtoAdminService.cadastrarProduto(cadastrarProdutoDTO);
		
		//ASSERT
		BDDMockito.then(validador1).should().validar(cadastrarProdutoDTO);
		BDDMockito.then(validador2).should().validar(cadastrarProdutoDTO);
		verify(produtoRepository, times(1)).save(any(Produto.class));
		//Produto
		Assertions.assertEquals(cadastrarProdutoDTO.nome(), mostrarProdutosDTO.getNome());
		Assertions.assertEquals(cadastrarProdutoDTO.preco(), mostrarProdutosDTO.getPreco());
		Assertions.assertEquals(cadastrarProdutoDTO.quantidade(), mostrarProdutosDTO.getQuantidade());
		Assertions.assertEquals(cadastrarProdutoDTO.ativo(), mostrarProdutosDTO.getAtivo());
		Assertions.assertArrayEquals(arquivoInfoDTO.bytesArquivo(), mostrarProdutosDTO.getPrimeiraImagem());
		//Categoria
		Assertions.assertEquals(categoria.getId(), mostrarProdutosDTO.getCategoria().getId());
		Assertions.assertEquals(categoria.getNome(), mostrarProdutosDTO.getCategoria().getNome());
		//SubCategoria
		Assertions.assertEquals(cadastrarProdutoDTO.idSubCategoria(), mostrarProdutosDTO.getCategoria().getSubCategoria().id());
		Assertions.assertEquals(categoria.getSubCategorias().get(0).getNome(), mostrarProdutosDTO.getCategoria().getSubCategoria().nome());
	}
	
	@Test
	void alterarProdutoPorId() {
		//ARRANGE
		//Categoria e SubCategoria
		Categoria categoria = categoriaBuilder.categoria(1L, "Computadores")
												  .comSubCategoria(1L, "Teclado")
												  .comSubCategoria(2L, "Outronome")
											  .getCategoria();
		//Produto
		Produto produto = produtoBuilder.id(1L)
										 .nome("Teclado gamer")
										 .descricao("Descricao Teclado gamer")
										 .preco(200.00)
										 .quantidade(100)
										 .tipoEntregaIdTipo(4L, TipoEntrega.ENTREGA_DIGITAL)
										 .arquivoProdutoIdNomePosicao(2L, "Padrao.jpeg", (byte) 0)
										 .subCategoria(categoria.getSubCategorias().get(1))
										 .getProduto();
		
		System.out.println(" TESTrapazE1 " + produto.getSubCategoria().getCategoria().getId());
		Long idProduto = 1L;
		when(produtoUtilidadeService.verificarSeProdutoExistePorId(idProduto)).thenReturn(produto);
		when(subCategoriaUtilidadeService.verificarSeExisteSubCategoriaPorId(idProduto)).thenReturn(categoria.getSubCategorias().get(0));
		when(categoriaUtilidadeService.verificarSeExisteCategoriaPorId(categoria.getId())).thenReturn(categoria);
		
		//ACT
		AlterarProdutoDTO alterarProdutoDTO = alterarProdutoDTOBuilder.nome("Novo nome do teclado").descricao("Nova descricao do Teclado").preco(BigDecimal.valueOf(500.00)).quantidade(500).ativo(true).idSubCategoria(categoria.getSubCategorias().get(0).getId()).tipoEntrega(Set.of(TipoEntrega.CORREIOS, TipoEntrega.RETIRADA_NA_LOJA)).arquivos(List.of(new ArquivoProdutoDTO("Padrao.jpeg", (byte) 0))).build();
		DetalharProdutoDTO detalharProdutoDTO = produtoAdminService.alterarProdutoPorId(idProduto, alterarProdutoDTO);
		
		//ASSERT
		//Produto
		Assertions.assertEquals(produto.getId(), detalharProdutoDTO.getId());
		Assertions.assertEquals(produto.getNome(), detalharProdutoDTO.getNome());
		Assertions.assertEquals(produto.getDescricao(), detalharProdutoDTO.getDescricao());
		Assertions.assertEquals(produto.getPreco(), detalharProdutoDTO.getPreco());
		Assertions.assertEquals(produto.getQuantidade(), detalharProdutoDTO.getQuantidade());
		Assertions.assertEquals(produto.getAtivo(), detalharProdutoDTO.getAtivo());
		detalharProdutoDTO.getArquivos().forEach(arquivo -> {
			Assertions.assertEquals(produto.getArquivosProduto().get(0).getNome(), arquivo.nomeArquivo());
			Assertions.assertNotNull(arquivo.bytesArquivo());
		});
		// Categoria
		Assertions.assertEquals(categoria.getId(), detalharProdutoDTO.getCategoria().getId());
		Assertions.assertEquals(categoria.getNome(), detalharProdutoDTO.getCategoria().getNome());
		// SubCategoria
		Assertions.assertEquals(produto.getSubCategoria().getId(), detalharProdutoDTO.getCategoria().getSubCategoria().id());
		Assertions.assertEquals(produto.getSubCategoria().getNome(), detalharProdutoDTO.getCategoria().getSubCategoria().nome());
	}

}
