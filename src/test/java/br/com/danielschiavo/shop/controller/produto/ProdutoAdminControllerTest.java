package br.com.danielschiavo.shop.controller.produto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.danielschiavo.JwtUtilTest;
import br.com.danielschiavo.shop.model.pedido.TipoEntrega;
import br.com.danielschiavo.shop.model.produto.arquivosproduto.ArquivoProdutoDTO;
import br.com.danielschiavo.shop.model.produto.categoria.MostrarCategoriaComSubCategoriaDTO;
import br.com.danielschiavo.shop.model.produto.categoria.MostrarCategoriaComSubCategoriaDTO.MostrarCategoriaComSubCategoriaDTOBuilder;
import br.com.danielschiavo.shop.model.produto.dto.AlterarProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.AlterarProdutoDTO.AlterarProdutoDTOBuilder;
import br.com.danielschiavo.shop.model.produto.dto.CadastrarProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.CadastrarProdutoDTO.CadastrarProdutoDTOBuilder;
import br.com.danielschiavo.shop.model.produto.dto.DetalharProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.DetalharProdutoDTO.DetalharProdutoDTOBuilder;
import br.com.danielschiavo.shop.model.produto.dto.MostrarProdutosDTO;
import br.com.danielschiavo.shop.model.produto.dto.MostrarProdutosDTO.MostrarProdutosDTOBuilder;
import br.com.danielschiavo.shop.service.produto.ProdutoAdminService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("dev")
class ProdutoAdminControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	private String tokenUser = JwtUtilTest.generateTokenUser();
	
	private String tokenAdmin = JwtUtilTest.generateTokenAdmin();
	
    @MockBean
    private ProdutoAdminService produtoAdminService;
    
	@Autowired
	private JacksonTester<DetalharProdutoDTO> detalharProdutoDTOJson;
	
	@Autowired
	private JacksonTester<MostrarProdutosDTO> mostrarProdutosDTOJson;

	@Autowired
	private JacksonTester<CadastrarProdutoDTO> cadastrarProdutoDTOJson;
	
	@Autowired
	private JacksonTester<AlterarProdutoDTO> alterarProdutoDTOJson;
	
	private MostrarCategoriaComSubCategoriaDTOBuilder categoriaBuilder = MostrarCategoriaComSubCategoriaDTO.builder();
	
	private MostrarProdutosDTOBuilder mostrarProdutosDTOBuilder = MostrarProdutosDTO.builder();
	
	private CadastrarProdutoDTOBuilder cadastrarProdutoDTOBuilder = CadastrarProdutoDTO.builder();
	
	private AlterarProdutoDTOBuilder alterarProdutoDTOBuilder = AlterarProdutoDTO.builder();
	
	private 		DetalharProdutoDTOBuilder detalharProdutoDTOBuilderBuilder = DetalharProdutoDTO.builder();
	
	@Test
	@DisplayName("Admin deletar produto por id deve retornar http 201 quando token valido e id produto é enviado")
	void deletarProdutoPorId_AdminValidoEIdProduto_DeveRetornarNoContent() throws IOException, Exception {
		//ARRANGE
		doNothing().when(produtoAdminService).deletarProdutoPorId(any());
		
		//ACT
		Long idProduto = 2L;
		var response = mvc.perform(delete("/shop/admin/produto/{idProduto}", idProduto)
								  .header("Authorization", "Bearer " + tokenAdmin))
								  .andReturn().getResponse();
				
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
	
	@Test
	@DisplayName("Admin deletar produto por id deve retornar http 403 quando token de usuario comum é enviado")
	void deletarProdutoPorId_TokenUser_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		Long idProduto = 2L;
		var response = mvc.perform(delete("/shop/admin/produto/{idProduto}", idProduto)
								  .header("Authorization", "Bearer " + tokenUser))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Admin deletar produto por id deve retornar http 403 quando nenhum token é enviado")
	void deletarProdutoPorId_TokenNaoEnviado_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		Long idProduto = 2L;
		var response = mvc.perform(delete("/shop/admin/produto/{idProduto}", idProduto))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Cadastrar produto deve retornar http 201 quando informacoes validas são enviadas")
	void cadastrarProduto_DtoETokenAdminValido_DeveRetornarCreated() throws IOException, Exception {
		//ARRANGE
		Map<String, String> resposta = new HashMap<>();
		resposta.put("id", String.valueOf(1L));
		resposta.put("mensagem", "Produto cadastrado com sucesso!");
		when(produtoAdminService.cadastrarProduto(any())).thenReturn(resposta);
		
		//ACT
		Set<TipoEntrega> tipoEntrega = Set.of(TipoEntrega.CORREIOS,TipoEntrega.RETIRADA_NA_LOJA);
		ArquivoProdutoDTO arquivoProdutoDTO = new ArquivoProdutoDTO("NomeArquivo.jpeg", (byte) 0);
		ArquivoProdutoDTO arquivoProdutoDTO2 = new ArquivoProdutoDTO("NomeVideo.avi", (byte) 1);
		Set<ArquivoProdutoDTO> listaArquivoProdutoDTO = Set.of(arquivoProdutoDTO, arquivoProdutoDTO2);
		CadastrarProdutoDTO cadastrarProdutoDTO = cadastrarProdutoDTOBuilder.nome("Produto1").descricao("Descricao produto1").preco(BigDecimal.valueOf(200.00)).quantidade(5).ativo(true).subCategoriaId(1L).tiposEntrega(tipoEntrega).arquivos(listaArquivoProdutoDTO).build();
		var response = mvc.perform(post("/shop/admin/produto")
								  .header("Authorization", "Bearer " + tokenAdmin)
								  .contentType(MediaType.APPLICATION_JSON)
								  .content(cadastrarProdutoDTOJson.write(cadastrarProdutoDTO).getJson()))
						.andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(resposta.get("mensagem"));
	}
	
	@Test
	@DisplayName("Cadastrar produto deve retornar http 403 quando usuario comum tenta acessar o endpoint")
	void cadastrarProduto_DtoValidoETokenUser_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		var response = mvc.perform(post("/shop/admin/produto")
								  .header("Authorization", "Bearer " + tokenUser))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Cadastrar produto deve retornar http 403 quando token não é enviado")
	void cadastrarProduto_TokenNaoEnviado_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		var response = mvc.perform(post("/shop/admin/produto"))
								  .andReturn().getResponse();
			
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Alterar produto por id deve retornar http 200 quando informacoes estão válidas")
	void alterarProdutoPorId_DtoETokenAdminValido_DeveRetornarOk() throws IOException, Exception {
		//ARRANGE
		String mensagem = "Produto alterado com sucesso!";
		when(produtoAdminService.alterarProdutoPorId(any(), any())).thenReturn(mensagem);
		
		//ACT
		Set<TipoEntrega> tipoEntrega = Set.of(TipoEntrega.CORREIOS,TipoEntrega.RETIRADA_NA_LOJA);
		ArquivoProdutoDTO arquivoProdutoDTO = new ArquivoProdutoDTO("NomeArquivo.jpeg", (byte) 0);
		ArquivoProdutoDTO arquivoProdutoDTO2 = new ArquivoProdutoDTO("NomeVideo.avi", (byte) 1);
		Set<ArquivoProdutoDTO> listaArquivoProdutoDTO = Set.of(arquivoProdutoDTO, arquivoProdutoDTO2);
		AlterarProdutoDTO alterarProdutoDTO = alterarProdutoDTOBuilder.nome("Nome produto").descricao("descricao").preco(BigDecimal.valueOf(200.00)).quantidade(5).ativo(true).subCategoriaId(1L).tiposEntrega(tipoEntrega).arquivos(listaArquivoProdutoDTO).build();
		Long idProduto = 1L;
		var response = mvc.perform(put("/shop/admin/produto/{idProduto}", idProduto)
								  .header("Authorization", "Bearer " + tokenAdmin)
								  .contentType(MediaType.APPLICATION_JSON)
								  .content(alterarProdutoDTOJson.write(alterarProdutoDTO).getJson()))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(mensagem);
	}
	
	@Test
	@DisplayName("Alterar produto por id deve retornar http 403 quando usuario comum tenta acessar o endpoint")
	void alterarProdutoPorId_TokenUser_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		Long idProduto = 1L;
		var response = mvc.perform(put("/shop/admin/produto/{idProduto}", idProduto)
								  .header("Authorization", "Bearer " + tokenUser))
						.andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Alterar produto por id deve retornar http 403 quando tenta acessar o endpoint sem token")
	void alterarProdutoPorId_TokenNaoEnviado_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		Long idProduto = 1L;
		var response = mvc.perform(put("/shop/admin/produto/{idProduto}", idProduto))
						.andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
}
