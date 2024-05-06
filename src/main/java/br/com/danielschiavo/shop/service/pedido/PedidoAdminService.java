package br.com.danielschiavo.shop.service.pedido;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.danielschiavo.repository.pedido.PedidoRepository;
import br.com.danielschiavo.service.cliente.ClienteUtilidadeService;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.pedido.Pedido;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarPedidoDTO;
import br.com.danielschiavo.shop.model.pedido.dto.MostrarProdutoDoPedidoDTO;
import br.com.danielschiavo.shop.service.filestorage.FileStoragePedidoService;
import lombok.Setter;

@Service
@Setter
public class PedidoAdminService {

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private ClienteUtilidadeService clienteUtilidadeService;
	
	@Autowired
	private FileStoragePedidoService fileStoragePedidoService;
	
	@Autowired
	private PedidoMapper pedidoMapper;

	public Page<MostrarPedidoDTO> pegarPedidosClientePorId(Long id, Pageable pageable) {
		Cliente cliente = clienteUtilidadeService.verificarSeClienteExistePorId(id);

		Page<Pedido> pagePedidos = pedidoRepository.findAllByCliente(cliente, pageable);
		
		List<MostrarPedidoDTO> list = new ArrayList<>();

		for (Pedido pedido : pagePedidos) {
			List<MostrarProdutoDoPedidoDTO> listaMostrarProdutoDoPedidoDTO = pedidoMapper.pedidoParaMostrarProdutoDoPedidoDTO(pedido, fileStoragePedidoService);

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
