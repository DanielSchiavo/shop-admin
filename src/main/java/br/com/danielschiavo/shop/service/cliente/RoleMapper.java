package br.com.danielschiavo.shop.service.cliente;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.role.Role;
import br.com.danielschiavo.shop.model.cliente.role.RoleDTO;

@Mapper(componentModel = "spring")
public abstract class RoleMapper {

	@Mapping(target = "dataEHoraAtribuicao", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "cliente", source = "cliente")
	public abstract Role stringRoleParaRoleEntity(RoleDTO adicionarRoleDTO, Cliente cliente);
}
