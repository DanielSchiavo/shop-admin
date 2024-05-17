package br.com.danielschiavo.shop.controller.produto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.IOException;

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
import br.com.danielschiavo.shop.model.produto.categoria.CriarCategoriaDTO;
import br.com.danielschiavo.shop.model.produto.categoria.MostrarCategoriaDTO;
import br.com.danielschiavo.shop.service.produto.CategoriaAdminService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("dev")
class CategoriaAdminControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	private String tokenUser = JwtUtilTest.generateTokenUser();
	
	private String tokenAdmin = JwtUtilTest.generateTokenAdmin();
	
	@Autowired
	private JacksonTester<CriarCategoriaDTO> criarCategoriaDTOJson;
	
	@Autowired
	private JacksonTester<MostrarCategoriaDTO> mostrarCategoriaDTOJson;
	
	@MockBean
	private CategoriaAdminService categoriaAdminService;
	
	@Test
	@DisplayName("Deletar categoria por id deve retornar http 204 quando token e id de categoria válido são enviados")
	void deletarCategoriaPorId_TokenAdminEIdCategoriaValido_DeveRetornarOkNoContent() throws IOException, Exception {
		//ARRANGE
		doNothing().when(categoriaAdminService).deletarCategoriaPorId(any());
		
		//ACT
		Long idCategoria = 2L;
		var response = mvc.perform(delete("/shop/admin/categoria/{idCategoria}", idCategoria)
								  .header("Authorization", "Bearer " + tokenAdmin))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
	
	@Test
	@DisplayName("Deletar categoria por id deve retornar http 403 quando token não é enviado")
	void deletarCategoriaPorId_TokenNaoEnviado_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		Long idCategoria = 2L;
		var response = mvc.perform(delete("/shop/admin/categoria/{idCategoria}", idCategoria))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Deletar categoria por id deve retornar http 403 quando token de user normal é enviado")
	void deletarCategoriaPorId_TokenUserEnviado_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		Long idCategoria = 2L;
		var response = mvc.perform(delete("/shop/admin/categoria/{idCategoria}", idCategoria)
								  .header("Authorization", "Bearer " + tokenUser))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
    @Test
    @DisplayName("Criar categoria deve retornar http 201 quando dados válidos são enviados")
    void criarCategoria_DadosValidos_DeveRetornarCreated() throws Exception {
        //ARRANGE
    	CriarCategoriaDTO criarCategoriaDTO = new CriarCategoriaDTO("Eletronicos");
        MostrarCategoriaDTO mostrarCategoriaDTO = new MostrarCategoriaDTO(1L, "Eletronicos");
        when(categoriaAdminService.criarCategoria(any())).thenReturn(mostrarCategoriaDTO);

        //ACT
		var response = mvc.perform(post("/shop/admin/categoria")
				  .header("Authorization", "Bearer " + tokenAdmin)
				  .contentType(MediaType.APPLICATION_JSON)
				  .content(criarCategoriaDTOJson.write(criarCategoriaDTO).getJson()))
				  .andReturn().getResponse();
        
		//ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        var jsonEsperado = mostrarCategoriaDTOJson.write(mostrarCategoriaDTO).getJson();
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
    }
    
    @Test
    @DisplayName("Criar categoria deve retornar http 403 quando token de usuário comum é enviado")
    void criarCategoria_TokenUser_DeveRetornarForbidden() throws Exception {
    	//ACT
    	CriarCategoriaDTO categoriaDTO = new CriarCategoriaDTO("Eletrônicos");
        var response = mvc.perform(post("/shop/admin/categoria")
        		.header("Authorization", "Bearer " + tokenUser) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(criarCategoriaDTOJson.write(categoriaDTO).getJson()))
                .andReturn().getResponse();

        //ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }
    
    @Test
    @DisplayName("Criar categoria deve retornar http 403 quando nenhum token é enviado")
    void criarCategoria_TokenNaoEnviado_DeveRetornarForbidden() throws Exception {
    	//ACT
    	CriarCategoriaDTO categoriaDTO = new CriarCategoriaDTO("Eletrônicos");
        var response = mvc.perform(post("/shop/admin/categoria")
                .contentType(MediaType.APPLICATION_JSON)
                .content(criarCategoriaDTOJson.write(categoriaDTO).getJson()))
                .andReturn().getResponse();

        //ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Alterar o nome da categoria por ID deve retornar http 200 quando dados válidos são enviados")
    void alterarNomeCategoriaPorId_DadosValidos_DeveRetornarOk() throws Exception {
        //ARRANGE
    	Long idCategoria = 1L;
        CriarCategoriaDTO criarCategoriaDTO = new CriarCategoriaDTO("Tecnologia");
        MostrarCategoriaDTO mostrarCategoriaDTO = new MostrarCategoriaDTO(idCategoria, "Tecnologia");
        when(categoriaAdminService.alterarNomeCategoriaPorId(any(), any())).thenReturn(mostrarCategoriaDTO);

        //ACT
        var response = mvc.perform(put("/shop/admin/categoria/{idCategoria}", idCategoria)
        						.header("Authorization", "Bearer " + tokenAdmin)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(criarCategoriaDTOJson.write(criarCategoriaDTO)
				                		.getJson())
				                )
				                .andReturn().getResponse();

        //ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        var jsonEsperado = mostrarCategoriaDTOJson.write(mostrarCategoriaDTO).getJson();
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
    }
    
    @Test
    @DisplayName("Alterar o nome da categoria por ID deve retornar http 403 quando token de usuário comum é enviado")
    void alterarNomeCategoriaPorId_TokenUser_DeveRetornarForbidden() throws Exception {
    	//ARRANGE
    	Long idCategoria = 1L;
    	CriarCategoriaDTO categoriaDTO = new CriarCategoriaDTO("Tecnologia");
        var response = mvc.perform(put("/shop/admin/categoria/{idCategoria}", idCategoria)
        		.header("Authorization", "Bearer " + tokenUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(criarCategoriaDTOJson.write(categoriaDTO).getJson()))
                .andReturn().getResponse();

        //ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }
    
    @Test
    @DisplayName("Alterar o nome da categoria por ID deve retornar http 403 quando nenhum token é enviado")
    void alterarNomeCategoriaPorId_SemToken_DeveRetornarForbidden() throws Exception {
    	//ACT
    	Long idCategoria = 1L;
    	CriarCategoriaDTO categoriaDTO = new CriarCategoriaDTO("Tecnologia");
        var response = mvc.perform(put("/shop/admin/categoria/{idCategoria}", idCategoria)
                .contentType(MediaType.APPLICATION_JSON)
                .content(criarCategoriaDTOJson.write(categoriaDTO).getJson()))
                .andReturn().getResponse();

        //ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }
}
