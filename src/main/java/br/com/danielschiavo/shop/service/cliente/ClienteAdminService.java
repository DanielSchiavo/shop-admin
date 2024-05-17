package br.com.danielschiavo.shop.service.cliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.danielschiavo.feign.FileStoragePerfilComumServiceClient;
import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.mapper.ClienteComumMapper;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.cliente.dto.MostrarClienteDTO;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;
import br.com.danielschiavo.shop.repository.cliente.ClienteRepository;
import lombok.Setter;

@Service
@Setter
public class ClienteAdminService {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private FileStoragePerfilComumServiceClient fileService;
	
	@Autowired
	private ClienteComumMapper clienteMapper;
	
	@Autowired
	private UsuarioAutenticadoService usuarioAutenticadoService;
	

	public void adminDeletarClientePorId(Long idCliente) {
		clienteRepository.deleteById(idCliente);
	}
	
	public Page<MostrarClienteDTO> adminDetalharTodosClientes(Pageable pageable) {
		String tokenComBearer = usuarioAutenticadoService.getTokenComBearer();
		Page<Cliente> pageClientes = clienteRepository.findAll(pageable);
		return pageClientes.map(cliente -> {
			ArquivoInfoDTO fotoPerfil = fileService.getFotoPerfil(cliente.getFotoPerfil(), tokenComBearer);
			return clienteMapper.clienteParaMostrarClienteDTO(cliente, fotoPerfil);
		});
	}

//	------------------------------
//	------------------------------
//	METODOS UTILITARIOS
//	------------------------------
//	------------------------------

	
}
