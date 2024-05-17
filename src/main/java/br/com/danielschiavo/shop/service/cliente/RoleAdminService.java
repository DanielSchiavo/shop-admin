package br.com.danielschiavo.shop.service.cliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.danielschiavo.service.cliente.ClienteUtilidadeService;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.role.RoleDTO;
import br.com.danielschiavo.shop.model.cliente.role.NomeRole;
import br.com.danielschiavo.shop.model.cliente.role.Role;
import br.com.danielschiavo.shop.repository.cliente.ClienteRepository;
import jakarta.validation.constraints.NotNull;

@Service
public class RoleAdminService {
	
	@Autowired
	private ClienteUtilidadeService clienteUtilidadeService;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private RoleMapper roleMapper;

	public String adicionarRole(RoleDTO adicionarRoleDTO) {
		NomeRole nomeRole = adicionarRoleDTO.role();
		if (nomeRole != NomeRole.ADMIN) {
			throw new ValidacaoException("Envie uma role válida! não existe a role " + nomeRole);
		}
		
		Cliente cliente = clienteUtilidadeService.pegarClientePorId(adicionarRoleDTO.idCliente());
		Role role = roleMapper.stringRoleParaRoleEntity(adicionarRoleDTO, cliente);
		cliente.adicionarRole(role);
		clienteRepository.save(cliente);
		
		return "Usuario promovido a " + nomeRole + " com sucesso!";
	}

	public String removerRole(RoleDTO removerRoleDTO) {
		if (removerRoleDTO.role() != NomeRole.ADMIN) {
			throw new ValidacaoException("Envie uma role válida! não existe a role " + removerRoleDTO.role());
		}
		Cliente cliente = clienteUtilidadeService.pegarClientePorId(removerRoleDTO.idCliente());
		Role role = cliente.getRoles().stream()
						.filter(r -> r.getRole().equals(removerRoleDTO.role()))
						.findFirst().orElseThrow(() -> new ValidacaoException("O cliente não possui a role de nome " + removerRoleDTO.role()));
		cliente.removerRole(role);
		clienteRepository.save(cliente);
		
		return "Removido permissão de " + removerRoleDTO.role() + " do usuario com sucesso!";
	}
	
}
