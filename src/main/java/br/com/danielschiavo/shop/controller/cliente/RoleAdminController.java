package br.com.danielschiavo.shop.controller.cliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.danielschiavo.shop.model.cliente.role.RoleDTO;
import br.com.danielschiavo.shop.service.cliente.RoleAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Cliente - Admin Roles", description = "Todos endpoints relacionados com a role dos clientes para uso exclusivo dos administradores")
public class RoleAdminController {
	
	@Autowired
	private RoleAdminService roleAdminService;
	
	@PostMapping("/admin/role")
	@Operation(summary = "Adiciona role de um cliente cadastrado")
	public ResponseEntity<?> adicionarRole(@RequestBody @Valid RoleDTO adicionarRoleDTO) {
		try {
			String respostaAdicionarRole = roleAdminService.adicionarRole(adicionarRoleDTO);
			return ResponseEntity.ok(respostaAdicionarRole);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@DeleteMapping("/admin/role")
	@Operation(summary = "Remove role de um cliente cadastrado")
	public ResponseEntity<?> removerRoleDoCliente(@RequestBody @Valid RoleDTO removerRoleDTO) {
		try {
			String respostaRemoverRole = roleAdminService.removerRole(removerRoleDTO);
			return ResponseEntity.ok().body(respostaRemoverRole);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}
