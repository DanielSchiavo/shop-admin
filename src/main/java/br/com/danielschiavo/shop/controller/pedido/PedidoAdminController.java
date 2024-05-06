package br.com.danielschiavo.shop.controller.pedido;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.danielschiavo.shop.model.pedido.dto.MostrarPedidoDTO;
import br.com.danielschiavo.shop.service.pedido.PedidoAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/shop")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Pedido - Admin", description = "Todos endpoints relacionados com os pedidos dos clientes para uso exclusivo dos administradores")
public class PedidoAdminController {

	@Autowired
	private PedidoAdminService pedidoService;
	
	@GetMapping("/admin/pedido/{idCliente}")
	@Operation(summary = "Pega todos pedidos do cliente com id fornecido no parametro da requisição")
	public ResponseEntity<Page<MostrarPedidoDTO>> pegarPedidosClientePorId(@PathVariable Long idCliente, Pageable pageable) {
		Page<MostrarPedidoDTO> pagePedidosAConfirmar = pedidoService.pegarPedidosClientePorId(idCliente, pageable);
		return ResponseEntity.ok(pagePedidosAConfirmar);
	}
}
