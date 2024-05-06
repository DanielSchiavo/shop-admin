package br.com.danielschiavo.shop.controller.cliente;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO;
import br.com.danielschiavo.shop.service.cliente.ClienteAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/shop")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Cliente - Admin", description = "Todos endpoints relacionados com o cliente para uso exclusivo dos administradores")
public class ClienteAdminController {

	@Autowired
	private ClienteAdminService clienteAdminService;
	
	@DeleteMapping("/admin/cliente/{idCliente}")
	@Operation(summary = "Deleta o cliente pelo id fornecido no parametro da requisição")
	public ResponseEntity<?> adminDeletarClientePorId(@PathVariable Long idCliente) {
		clienteAdminService.adminDeletarClientePorId(idCliente);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/admin/cliente")
	@Operation(summary = "Mostra todos os clientes cadastrados")
	public ResponseEntity<Page<MostrarClienteDTO>> adminDetalharTodosClientes(Pageable pageable) {
		var client = clienteAdminService.adminDetalharTodosClientes(pageable);
		return ResponseEntity.ok(client);
	}
	
}
