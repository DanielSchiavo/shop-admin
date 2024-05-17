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
import br.com.danielschiavo.shop.model.produto.subcategoria.AlterarSubCategoriaDTO;
import br.com.danielschiavo.shop.model.produto.subcategoria.CadastrarSubCategoriaDTO;
import br.com.danielschiavo.shop.model.produto.subcategoria.MostrarSubCategoriaDTO;
import br.com.danielschiavo.shop.service.produto.SubCategoriaAdminService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("dev")
class SubCategoriaAdminControllerTest {

	@Autowired
	private MockMvc mvc;
	
	private String tokenUser = JwtUtilTest.generateTokenUser();
	
	private String tokenAdmin = JwtUtilTest.generateTokenAdmin();
	
	@Autowired
	private JacksonTester<CadastrarSubCategoriaDTO> cadastrarSubCategoriaDTOJson;
	
	@Autowired
	private JacksonTester<AlterarSubCategoriaDTO> alterarSubCategoriaDTOJson;
	
	@MockBean
	private SubCategoriaAdminService subCategoriaAdminService;
	
	@Test
	@DisplayName("Deletar sub categoria por id deve retornar http 204 quando token e id de sub categoria válido são enviados")
	void deletarSubCategoriaPorId_TokenAdminEIdSubCategoriaValido_DeveRetornarOkNoContent() throws IOException, Exception {
		//ARRANGE
		doNothing().when(subCategoriaAdminService).deletarSubCategoriaPorId(any());
		
		//ACT
		Long idCategoria = 2L;
		var response = mvc.perform(delete("/shop/admin/sub-categoria/{idSubCategoria}", idCategoria)
								  .header("Authorization", "Bearer " + tokenAdmin))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
	
	@Test
	@DisplayName("Deletar sub categoria por id deve retornar http 403 quando token não é enviado")
	void deletarSubCategoriaPorId_TokenNaoEnviado_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		Long idCategoria = 2L;
		var response = mvc.perform(delete("/shop/admin/sub-categoria/{idSubCategoria}", idCategoria))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Deletar sub categoria por id deve retornar http 403 quando token de user normal é enviado")
	void deletarSubCategoriaPorId_TokenUserEnviado_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		Long idCategoria = 2L;
		var response = mvc.perform(delete("/shop/admin/sub-categoria/{idSubCategoria}", idCategoria)
								  .header("Authorization", "Bearer " + tokenUser))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
    @Test
    @DisplayName("Criar sub categoria deve retornar http 201 quando dados válidos são enviados")
    void cadastrarSubCategoria_DtoETokenAdminValido_DeveRetornarCreated() throws Exception {
    	//ASSERT
    	MostrarSubCategoriaDTO mostrarSubCategoriaDTO = new MostrarSubCategoriaDTO(1L, "Mouses");
        when(subCategoriaAdminService.alterarSubCategoriaPorId(any(), any())).thenReturn(mostrarSubCategoriaDTO);

        //ACT
		var response = mvc.perform(post("/shop/admin/sub-categoria")
				  .header("Authorization", "Bearer " + tokenAdmin)
				  .contentType(MediaType.APPLICATION_JSON)
				  .content(cadastrarSubCategoriaDTOJson.write(
						  new CadastrarSubCategoriaDTO("Mouses", 1L))
						  .getJson()))
				  .andReturn().getResponse();
        
		//ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }
    
    @Test
    @DisplayName("Criar sub categoria deve retornar http 403 quando token de usuario é enviado")
    void cadastrarSubCategoria_TokenUser_DeveRetornarForbidden() throws Exception {
		//ACT
    	var response = mvc.perform(post("/shop/admin/sub-categoria")
				  .header("Authorization", "Bearer " + tokenUser))
				  .andReturn().getResponse();
        
    	//ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }
    
    @Test
    @DisplayName("Criar sub categoria deve retornar http 403 quando token não é enviado")
    void cadastrarSubCategoria_TokenNaoEnviado_DeveRetornarForbidden() throws Exception {
		//ACT
    	var response = mvc.perform(post("/shop/admin/sub-categoria"))
				  .andReturn().getResponse();
        
    	//ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }
    
    @Test
    @DisplayName("Alterar o nome da sub categoria por ID deve retornar http 200 quando dados válidos são enviados")
    void alterarSubCategoriaPorId_DadosValidos_DeveRetornarOk() throws Exception {
    	//ARRANGE
    	MostrarSubCategoriaDTO mostrarSubCategoriaDTO = new MostrarSubCategoriaDTO(1L, "Mouses");
        when(subCategoriaAdminService.alterarSubCategoriaPorId(any(), any())).thenReturn(mostrarSubCategoriaDTO);

        //ACT
        Long idCategoria = 1L;
        var response = mvc.perform(put("/shop/admin/categoria/{idCategoria}", idCategoria)
        						.header("Authorization", "Bearer " + tokenAdmin)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(alterarSubCategoriaDTOJson.write(new AlterarSubCategoriaDTO("Mouses", 1L)).getJson()))
				                .andReturn().getResponse();

        //ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
    
    @Test
    @DisplayName("Alterar o nome da sub categoria por ID deve retornar http 403 quando usuario comum tenta acessar o endpoint")
    void alterarSubCategoriaPorId_TokenUser_DeveRetornarForbidden() throws Exception {
        //ACT
    	Long idCategoria = 1L;
        var response = mvc.perform(put("/shop/admin/categoria/{idCategoria}", idCategoria)
        						.header("Authorization", "Bearer " + tokenUser))
				                .andReturn().getResponse();

        //ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }
    
    @Test
    @DisplayName("Alterar o nome da sub categoria por ID deve retornar http 403 quando token não é enviado")
    void alterarSubCategoriaPorId_TokenNaoEnviado_DeveRetornarForbidden() throws Exception {
    	//ACT
    	Long idCategoria = 1L;
        var response = mvc.perform(put("/shop/admin/categoria/{idCategoria}", idCategoria))
				                .andReturn().getResponse();

        //ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

}
