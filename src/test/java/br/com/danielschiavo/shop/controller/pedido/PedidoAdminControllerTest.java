package br.com.danielschiavo.shop.controller.pedido;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

import br.com.danielschiavo.JwtUtilTest;
import br.com.danielschiavo.shop.model.pedido.StatusPedido;
import br.com.danielschiavo.shop.model.pedido.TipoEntrega;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarPedidoDTO.MostrarPedidoDTOBuilder;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarProdutoDoPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarProdutoDoPedidoDTO.MostrarProdutoDoPedidoDTOBuilder;
import br.com.danielschiavo.shop.model.pedido.entrega.MostrarEntregaDTO;
import br.com.danielschiavo.shop.model.pedido.entrega.MostrarEntregaDTO.MostrarEntregaDTOBuilder;
import br.com.danielschiavo.shop.model.pedido.pagamento.MetodoPagamento;
import br.com.danielschiavo.shop.model.pedido.pagamento.MostrarPagamentoDTO;
import br.com.danielschiavo.shop.model.pedido.pagamento.MostrarPagamentoDTO.MostrarPagamentoDTOBuilder;
import br.com.danielschiavo.shop.model.pedido.pagamento.StatusPagamento;
import br.com.danielschiavo.shop.service.pedido.PedidoAdminService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("dev")
class PedidoAdminControllerTest {

	@Autowired
	private MockMvc mvc;
	
	private String tokenUser = JwtUtilTest.generateTokenUser();
	
	private String tokenAdmin = JwtUtilTest.generateTokenAdmin();
	
    @MockBean
    private PedidoAdminService pedidoAdminService;
    
	@Autowired
	private JacksonTester<Page<MostrarPedidoDTO>> pageMostrarPedidoDTOJson;
	
	private MostrarEntregaDTOBuilder mostrarEntregaDTOBuilder = MostrarEntregaDTO.builder();
	
	private MostrarPagamentoDTOBuilder mostrarPagamentoDTOBuilder = MostrarPagamentoDTO.builder();
	
	private MostrarProdutoDoPedidoDTOBuilder mostrarProdutoDoPedidoDTOBuilder = MostrarProdutoDoPedidoDTO.builder();
	
	private MostrarPedidoDTOBuilder mostrarPedidoDTOBuilder = MostrarPedidoDTO.builder();
	
	@Test
	@DisplayName("Pegar pedidos cliente por id token deve retornar http 200 quando token enviado é válido")
	void pegarPedidosClientePorId_ClienteETokenAdminValido_DeveRetornarOk() throws IOException, Exception {
		//ARRANGE
		MostrarEntregaDTO mostrarEntregaDTO = mostrarEntregaDTOBuilder.tipoEntrega(TipoEntrega.RETIRADA_NA_LOJA).build();
		MostrarPagamentoDTO mostrarPagamentoDTO = mostrarPagamentoDTOBuilder.metodoPagamento(MetodoPagamento.BOLETO).statusPagamento(StatusPagamento.PENDENTE).build();
		byte[] bytesImagem = "Hello world".getBytes();
		MostrarProdutoDoPedidoDTO mostrarProdutoDoPedidoDTO = mostrarProdutoDoPedidoDTOBuilder.idProduto(1L).nomeProduto("Produto 1").preco(BigDecimal.valueOf(400.00)).quantidade(2).subTotal(BigDecimal.valueOf(800.00)).primeiraImagem(bytesImagem).build();
		MostrarProdutoDoPedidoDTO mostrarProdutoDoPedidoDTO2 = mostrarProdutoDoPedidoDTOBuilder.idProduto(2L).nomeProduto("Produto 2").preco(BigDecimal.valueOf(200.00)).quantidade(2).subTotal(BigDecimal.valueOf(400.00)).primeiraImagem(bytesImagem).build();
		Long idCliente = 2L;
		MostrarPedidoDTO mostrarPedidoDTO = mostrarPedidoDTOBuilder.idPedido(UUID.randomUUID()).idCliente(idCliente).valorTotal(BigDecimal.valueOf(1200.00)).dataPedido(LocalDateTime.now()).statusPedido(StatusPedido.A_PAGAR).entrega(mostrarEntregaDTO).pagamento(mostrarPagamentoDTO).produtos(new ArrayList<>(List.of(mostrarProdutoDoPedidoDTO, mostrarProdutoDoPedidoDTO2))).build();
		Page<MostrarPedidoDTO> pageMostrarPedidoDTO = new PageImpl<>(List.of(mostrarPedidoDTO));
		when(pedidoAdminService.pegarPedidosClientePorId(any(), any())).thenReturn(pageMostrarPedidoDTO);
		
		//ACT
		var response = mvc.perform(get("/shop/admin/pedido/{idCliente}", idCliente)
								  .header("Authorization", "Bearer " + tokenAdmin))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        var jsonEsperado = pageMostrarPedidoDTOJson.write(pageMostrarPedidoDTO).getJson();
        JSONAssert.assertEquals(jsonEsperado, response.getContentAsString(), JSONCompareMode.LENIENT);
	}
	
	@Test
	@DisplayName("Pegar pedidos cliente por id token deve retornar http 403 quando usuario comum tenta acessar o endpoint")
	void pegarPedidosClientePorId_TokenUser_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		Long idCliente = 2L;
		var response = mvc.perform(get("/shop/admin/pedido/{idCliente}", idCliente)
								  .header("Authorization", "Bearer " + tokenUser))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
	
	@Test
	@DisplayName("Pegar pedidos cliente por id token deve retornar http 403 quando nenhum token é enviado")
	void pegarPedidosClientePorId_TokenNaoEnviado_DeveRetornarForbidden() throws IOException, Exception {
		//ACT
		Long idCliente = 2L;
		var response = mvc.perform(get("/shop/admin/pedido/{idCliente}", idCliente))
								  .andReturn().getResponse();
		
		//ASSERT
		assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}
}
