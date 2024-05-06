package br.com.danielschiavo.shop.service.pedido;

import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.repository.pedido.PedidoRepository;
import br.com.danielschiavo.service.cliente.ClienteUtilidadeService;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.Cliente.ClienteBuilder;
import br.com.danielschiavo.shop.model.cliente.endereco.Endereco;
import br.com.danielschiavo.shop.model.cliente.endereco.Endereco.EnderecoBuilder;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import br.com.danielschiavo.shop.model.pedido.Pedido;
import br.com.danielschiavo.shop.model.pedido.Pedido.PedidoBuilder;
import br.com.danielschiavo.shop.model.pedido.TipoEntrega;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarProdutoDoPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.entrega.EnderecoPedido;
import br.com.danielschiavo.shop.model.pedido.entrega.Entrega;
import br.com.danielschiavo.shop.model.pedido.entrega.MostrarEnderecoPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.entrega.MostrarEntregaDTO;
import br.com.danielschiavo.shop.model.pedido.itempedido.ItemPedido;
import br.com.danielschiavo.shop.model.pedido.pagamento.MetodoPagamento;
import br.com.danielschiavo.shop.model.pedido.pagamento.MostrarPagamentoDTO;
import br.com.danielschiavo.shop.model.pedido.pagamento.Pagamento;
import br.com.danielschiavo.shop.model.produto.Produto;
import br.com.danielschiavo.shop.model.produto.Produto.ProdutoBuilder;
import br.com.danielschiavo.shop.service.filestorage.FileStoragePedidoService;

@ExtendWith(MockitoExtension.class)
class PedidoAdminServiceTest {
	
	@Mock
	private UsuarioAutenticadoService usuarioAutenticadoService;
	
	@InjectMocks
	private PedidoAdminService pedidoAdminService;
	
	@Mock
	private PedidoRepository pedidoRepository;
	
	@Mock
	private FileStoragePedidoService fileStoragePedidoService;
	
	@Mock
	private Cliente cliente;
	
	@Mock
	private ClienteUtilidadeService clienteUtilidadeService;
	
	private ProdutoBuilder produtoBuilder = Produto.builder();
	
	private EnderecoBuilder enderecoBuilder = Endereco.builder();
	
	private PedidoBuilder pedidoBuilder = Pedido.builder();
	
	private ClienteBuilder clienteBuilder = Cliente.builder();
	
    @BeforeEach
    void setUp() {
        // Configura explicitamente o mock no spy
    	PedidoMapperImpl pedidoMapper = new PedidoMapperImpl();
    	pedidoAdminService.setPedidoMapper(pedidoMapper);
    }
	
	@Test
	void pegarPedidosClientePorId() {
		//ARRANGE
		//Cliente
		Cliente cliente = clienteBuilder.id(1L).cpf("12345678994").nome("Silvana").sobrenome("Pereira da silva").dataNascimento(LocalDate.of(2000, 3, 3)).dataCriacaoConta(LocalDate.now()).email("silvana.dasilva@gmail.com").senha("{noop}123456").celular("27999833653").fotoPerfil("Qualquerfoto.jpeg").getCliente();
		//Produto
		Produto produto = produtoBuilder.id(1L).nome("Mouse gamer").descricao("Descricao Mouse gamer").preco(200.00).quantidade(100).arquivoProdutoIdNomePosicao(1l, "Padrao.jpeg", (byte) 0).getProduto();
		Produto produto2 = produtoBuilder.id(2L).nome("Teclado gamer").descricao("Descricao Teclado gamer").preco(200.00).quantidade(100).arquivoProdutoIdNomePosicao(1l, "Padrao.jpeg", (byte) 0).getProduto();
		//Endereco
		Endereco endereco = enderecoBuilder.cep("12345678").rua("Divinopolis").numero("15").complemento("Sem complemento").bairro("Bela vista").cidade("Cariacica").estado("ES").build();
		//Pedido
		Pedido pedido = pedidoBuilder.cliente(cliente).comItemPedidoIdQuantidadeProduto(1L, 5, produto).comItemPedidoIdQuantidadeProduto(2L, 3, produto2).pagamentoIdMetodo(1L, MetodoPagamento.BOLETO).entregaIdTipo(1L, TipoEntrega.CORREIOS).entregaEndereco(endereco).getPedido();
		Pedido pedido2 = pedidoBuilder.cliente(cliente).comItemPedidoIdQuantidadeProduto(2L, 3, produto2).pagamentoIdMetodo(1L, MetodoPagamento.BOLETO).entregaIdTipo(1L, TipoEntrega.CORREIOS).entregaEndereco(endereco).getPedido();
		List<Pedido> listaPedido = new ArrayList<>(List.of(pedido, pedido2));
		Page<Pedido> pagePedido =  new PageImpl<>(listaPedido);
		
		byte[] bytesImagem = "Hello world".getBytes();
		ArquivoInfoDTO arquivoInfoDTO = new ArquivoInfoDTO("Padrao.jpeg", bytesImagem);
		BDDMockito.when(fileStoragePedidoService.pegarImagemPedidoPorNome(any())).thenReturn(arquivoInfoDTO);		Long idCliente = 1L;
		BDDMockito.when(clienteUtilidadeService.verificarSeClienteExistePorId(idCliente)).thenReturn(cliente);
		Pageable pageable = PageRequest.of(0, 10);
		BDDMockito.when(pedidoRepository.findAllByCliente(cliente, pageable)).thenReturn(pagePedido);
		
		//ACT
		Page<MostrarPedidoDTO> pageMostrarPedidoDTO = pedidoAdminService.pegarPedidosClientePorId(idCliente, pageable);
		
		//ASSERT
		Assertions.assertEquals(pagePedido.getTotalElements(), pageMostrarPedidoDTO.getTotalElements(), "O número total de elementos deve ser igual");
		List<MostrarPedidoDTO> listaMostrarPedidoDTO = pageMostrarPedidoDTO.getContent();

	    for (int i = 0; i < listaPedido.size(); i++) {
	        Pedido pedidoVerificar = listaPedido.get(i);
	        MostrarPedidoDTO dto = listaMostrarPedidoDTO.get(i);
	        
	        Assertions.assertEquals(pedidoVerificar.getCliente().getId(), dto.idCliente());
	        Assertions.assertEquals(pedidoVerificar.getValorTotal(), dto.valorTotal());
	        Assertions.assertEquals(pedidoVerificar.getDataPedido(), dto.dataPedido());
	        Assertions.assertEquals(pedidoVerificar.getStatusPedido(), dto.statusPedido());
	        
	        // Comparando entrega
	        Entrega entregaVerificar = pedidoVerificar.getEntrega();
	        MostrarEntregaDTO entregaDTO = dto.entrega();
	        Assertions.assertEquals(entregaVerificar.getTipoEntrega(), entregaDTO.tipoEntrega());
	        
	        // Comparando endereço de entrega
	        EnderecoPedido enderecoPedido = entregaVerificar.getEnderecoPedido();
	        MostrarEnderecoPedidoDTO enderecoDTO = entregaDTO.endereco();
	        if (enderecoPedido != null && enderecoDTO != null) {
	            Assertions.assertEquals(enderecoPedido.getCep(), enderecoDTO.cep());
	            Assertions.assertEquals(enderecoPedido.getRua(), enderecoDTO.rua());
	            Assertions.assertEquals(enderecoPedido.getNumero(), enderecoDTO.numero());
	            Assertions.assertEquals(enderecoPedido.getComplemento(), enderecoDTO.complemento());
	            Assertions.assertEquals(enderecoPedido.getBairro(), enderecoDTO.bairro());
	            Assertions.assertEquals(enderecoPedido.getCidade(), enderecoDTO.cidade());
	            Assertions.assertEquals(enderecoPedido.getEstado(), enderecoDTO.estado());
	        }
	        
	        // Comparando pagamento
	        Pagamento pagamentoVerificar = pedidoVerificar.getPagamento();
	        MostrarPagamentoDTO pagamentoDTO = dto.pagamento();
	        Assertions.assertEquals(pagamentoVerificar.getMetodoPagamento(), pagamentoDTO.metodoPagamento());
	        Assertions.assertEquals(pagamentoVerificar.getStatusPagamento(), pagamentoDTO.statusPagamento());
	        
	        // Comparando itens do pedido
	        List<ItemPedido> itensPedido = pedidoVerificar.getItemsPedido();
	        List<MostrarProdutoDoPedidoDTO> itensPedidoDTO = dto.produtos();
	        Assertions.assertEquals(itensPedido.size(), itensPedidoDTO.size(), "Os tamanhos das listas de itens do pedido devem ser iguais");
	        
	        for (int j = 0; j < itensPedido.size(); j++) {
	            ItemPedido item = itensPedido.get(j);
	            MostrarProdutoDoPedidoDTO itemDTO = itensPedidoDTO.get(j);
	            Assertions.assertEquals(item.getProdutoId(), itemDTO.idProduto());
	            Assertions.assertEquals(item.getNomeProduto(), itemDTO.nomeProduto());
	            Assertions.assertEquals(item.getPreco(), itemDTO.preco());
	            Assertions.assertEquals(item.getQuantidade(), itemDTO.quantidade());
	            Assertions.assertEquals(item.getSubTotal(), itemDTO.subTotal());
	            Assertions.assertArrayEquals(bytesImagem, itemDTO.primeiraImagem());
	        }
	    }
	}

}
