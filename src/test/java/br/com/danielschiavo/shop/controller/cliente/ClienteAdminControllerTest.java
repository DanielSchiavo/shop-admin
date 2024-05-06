package br.com.danielschiavo.shop.controller.cliente;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.danielschiavo.shop.JwtUtilTest;
import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO;
import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO.MostrarClienteDTOBuilder;
import br.com.danielschiavo.shop.service.cliente.ClienteAdminService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("dev")
class ClienteAdminControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	private String tokenUser = JwtUtilTest.generateTokenUser();
	
	private String tokenAdmin = JwtUtilTest.generateTokenAdmin();
	
	@Autowired
	private JacksonTester<Page<MostrarClienteDTO>> pageMostrarClienteDTOJson;
	
    @MockBean
    private ClienteAdminService clienteService;
    
    private MostrarClienteDTOBuilder mostrarClienteDTOBuilder = MostrarClienteDTO.builder();
    
	@Test
	@DisplayName("Admin deletar cliente deve retornar http 201 quando informacoes estão válidas")
	void deletarClientePorId_AdminValido_DeveRetornarNoContent() throws IOException, Exception {
		//ARRANGE 
		doNothing().when(clienteService).adminDeletarClientePorId(any());
		
		//ACT
		Long idCliente = 2L;
		var response = mvc.perform(delete("/shop/admin/cliente/{idCliente}", idCliente)
								  .header("Authorization", "Bearer " + tokenAdmin))
								  .andReturn().getResponse();

		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
	
	@Test
	@DisplayName("Admin deletar cliente deve retornar http 403 quando não-administradores tentarem usar o endpoint")
	void deletarClientePorId_AdminInvalido_DeveRetornarForbidden() throws IOException, Exception {
		//ARRANGE
		doNothing().when(clienteService).adminDeletarClientePorId(any());
		
		//ACT
		Long idCliente = 3L;
		var response = mvc.perform(delete("/shop/admin/cliente/{idCliente}", idCliente)
				.header("Authorization", "Bearer " + tokenUser))
				.andReturn().getResponse();

		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Admin detalhar todos clientes deve retornar http 200 quando token informado é válido")
	void adminDetalharTodosClientes_AdminValido_DeveRetornarOk() throws IOException, Exception {
		//ARRANGE
		MostrarClienteDTO mostrarClienteDTO = mostrarClienteDTOBuilder.id(1L).cpf("12345671012").nome("Junior").sobrenome("da Silva").dataNascimento(LocalDate.of(2000, 3, 3)).dataCriacaoConta(LocalDate.now()).email("juniordasilva@gmail.com").celular("27996101055").build();
		MostrarClienteDTO mostrarClienteDTO2 = mostrarClienteDTOBuilder.id(1L).cpf("12345612342").nome("Anderson").sobrenome("Emiliano").dataNascimento(LocalDate.of(2000, 2, 6)).dataCriacaoConta(LocalDate.now()).email("jorlan@gmail.com").celular("27999833653").build();
		Page<MostrarClienteDTO> pageCliente = new PageImpl<>(List.of(mostrarClienteDTO, mostrarClienteDTO2));
        when(clienteService.adminDetalharTodosClientes(any())).thenReturn(pageCliente);
        
        //ACT
		var response = mvc.perform(get("/shop/admin/cliente")
								  .header("Authorization", "Bearer " + tokenAdmin))
								  .andReturn().getResponse();

		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        var jsonEsperado = pageMostrarClienteDTOJson.write(pageCliente).getJson();
        JSONAssert.assertEquals(jsonEsperado, response.getContentAsString(), JSONCompareMode.LENIENT);
	}
	
	@Test
	@DisplayName("Admin detalhar todos clientes deve retornar http 403 quando não-administradores tentarem usar o endpoint")
	void adminDetalharTodosClientes_AdminInvalido_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		var response = mvc.perform(get("/shop/admin/cliente")
								  .header("Authorization", "Bearer " + tokenUser))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}

}
