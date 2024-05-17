package br.com.danielschiavo.shop.service.pedido;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.danielschiavo.feign.pedido.FileStoragePedidoComumServiceClient;
import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.mapper.PedidoComumMapper;
import br.com.danielschiavo.service.cliente.ClienteUtilidadeService;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.pedido.Pedido;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarProdutoDoPedidoDTO;
import br.com.danielschiavo.shop.repository.pedido.PedidoRepository;
import lombok.Setter;

@Service
@Setter	
public class PedidoAdminService {

	@Autowired
	private UsuarioAutenticadoService usuarioAutenticadoService;
	
	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private ClienteUtilidadeService clienteUtilidadeService;
	
	@Autowired
	private FileStoragePedidoComumServiceClient fileStoragePedidoService;
	
	@Autowired
	private PedidoComumMapper pedidoMapper;

	public Page<MostrarPedidoDTO> pegarPedidosClientePorId(Long id, Pageable pageable) {
		Cliente cliente = clienteUtilidadeService.pegarClientePorId(id);
		String tokenComBearer = usuarioAutenticadoService.getTokenComBearer();

		Page<Pedido> pagePedidos = pedidoRepository.findAllByCliente(cliente, pageable);
		List<MostrarPedidoDTO> list = new ArrayList<>();

		for (Pedido pedido : pagePedidos) {
			List<MostrarProdutoDoPedidoDTO> listaMostrarProdutoDoPedidoDTO = pedidoMapper.pedidoParaMostrarProdutoDoPedidoDTO(pedido, fileStoragePedidoService, tokenComBearer);

			var mostrarPedidoDTO = new MostrarPedidoDTO(pedido, listaMostrarProdutoDoPedidoDTO);
			list.add(mostrarPedidoDTO);
		}
		return new PageImpl<>(list, pagePedidos.getPageable(),
				pagePedidos.getTotalElements());
	}
	

//	------------------------------
//	------------------------------
//	METODOS UTILITÁRIOS
//	------------------------------
//	------------------------------
	
}
