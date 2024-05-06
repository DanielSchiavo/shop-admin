package br.com.danielschiavo.shop.service.cliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.danielschiavo.mapper.cliente.RoleMapper;
import br.com.danielschiavo.repository.cliente.ClienteRepository;
import br.com.danielschiavo.service.cliente.ClienteUtilidadeService;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.role.AdicionarRoleDTO;
import br.com.danielschiavo.shop.model.cliente.role.Role;

@Service
public class RoleAdminService {
	
	@Autowired
	private ClienteUtilidadeService clienteUtilidadeService;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private RoleMapper roleMapper;

	public void adicionarRole(AdicionarRoleDTO adicionarRoleDTO) {
		String nomeRole = adicionarRoleDTO.role();
		if (!nomeRole.equals("ADMIN")) {
			throw new ValidacaoException("Envie uma role válida! não existe a role " + nomeRole);
		}
		
		Cliente cliente = clienteUtilidadeService.verificarSeClienteExistePorId(adicionarRoleDTO.idCliente());
		Role role = roleMapper.stringRoleParaRoleEntity(nomeRole, cliente);
		cliente.adicionarRole(role);
		clienteRepository.save(cliente);
	}

	public void removerRole(Long idCliente, String nomeRole) {
		if (!nomeRole.equals("ADMIN")) {
			throw new ValidacaoException("Envie uma role válida! não existe a role " + nomeRole);
		}
		Cliente cliente = clienteUtilidadeService.verificarSeClienteExistePorId(idCliente);
		Role role = cliente.getRoles().stream()
						.filter(r -> r.getRole().toString().endsWith(nomeRole))
						.findFirst().orElseThrow(() -> new ValidacaoException("O cliente não possui a role de nome " + nomeRole));
		cliente.removerRole(role);
		clienteRepository.save(cliente);
	}
	
}
