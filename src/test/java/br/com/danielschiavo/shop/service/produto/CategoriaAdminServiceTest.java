package br.com.danielschiavo.shop.service.produto;

import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.danielschiavo.infra.security.UsuarioAutenticadoService;
import br.com.danielschiavo.repository.produto.CategoriaRepository;
import br.com.danielschiavo.service.produto.CategoriaUtilidadeService;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.cliente.Cliente;
import br.com.danielschiavo.shop.model.produto.categoria.Categoria;
import br.com.danielschiavo.shop.model.produto.categoria.CriarCategoriaDTO;
import br.com.danielschiavo.shop.model.produto.categoria.MostrarCategoriaDTO;

@ExtendWith(MockitoExtension.class)
class CategoriaAdminServiceTest {
	
	@Mock
	private UsuarioAutenticadoService usuarioAutenticadoService;
	
	@InjectMocks
	private CategoriaAdminService categoriaAdminService;
	
	@Mock
	private CategoriaUtilidadeService categoriaUtilidadeService;
	
	@Mock
	private Cliente cliente;
	
	@Mock
	private Categoria categoria;
	
	@Mock
	private CategoriaRepository categoriaRepository;
	
	@Captor
	private ArgumentCaptor<Categoria> categoriaCaptor;
    
    @Test
    @DisplayName("Deletar categoria por id com o id de categoria fornecido existente deve executar normalmente")
    void deletarCategoriaPorId_IdFornecidoCategoriaExiste_NaoDeveLancarExcecao() {
    	//ARRANGE
    	BDDMockito.when(categoriaUtilidadeService.verificarSeExisteCategoriaPorId(1L)).thenReturn(categoria);
    	
    	//ACT
    	Long idCategoria = 1L;
    	categoriaAdminService.deletarCategoriaPorId(idCategoria);
    	
    	//ASSERT
    	BDDMockito.then(categoriaRepository).should().delete(categoria);
    }
    
    @Test
    @DisplayName("Criar categoria deve funcionar normalmente quando nome de categoria ainda não foi cadastrado")
    void criarCategoria_NomeAindaNaoFoiCadastrado_NaoDeveLancarExcecao() {
    	//ARRANGE
    	BDDMockito.when(categoriaRepository.findByNomeLowerCase(any())).thenReturn(Optional.empty());
    	
    	//ACT
    	String nomeCategoriaASerCriada = "Computador";
    	MostrarCategoriaDTO mostrarCategoriaDTO = categoriaAdminService.criarCategoria(nomeCategoriaASerCriada);
    	
    	//ASSERT
    	BDDMockito.then(categoriaRepository).should().save(categoriaCaptor.capture());
    	Assertions.assertEquals(nomeCategoriaASerCriada, mostrarCategoriaDTO.nome());
    }
    
    @Test
    @DisplayName("Criar categoria deve lançar exceção quando nome de categoria ja foi cadastrado")
    void criarCategoria_NomeJaFoiCadastrado_DeveLancarExcecao() {
    	//ARRANGE
    	BDDMockito.when(categoriaRepository.findByNomeLowerCase(any())).thenReturn(Optional.of(categoria));
    	
    	//ASSERT + ACT
    	String nomeCategoriaASerCriada = "Computador";
    	Assertions.assertThrows(ValidacaoException.class, () -> categoriaAdminService.criarCategoria(nomeCategoriaASerCriada));
    }
    
    @Test
    @DisplayName("Alterar nome categoria por id deve executar normalmente quando id categoria a ser alterada existir e nome dto não já existir")
    void alterarNomeCategoriaPorId_IdCategoriaExisteENomeNaoExiste_NaoDeveLancarExcecao() {
    	//ARRANGE
    	Categoria categoria = new Categoria (1L, "Ferramentas", null);
    	BDDMockito.when(categoriaRepository.findByNomeLowerCase(any())).thenReturn(Optional.empty());
    	Long idCategoriaASerAlterada = 1L;
    	BDDMockito.when(categoriaUtilidadeService.verificarSeExisteCategoriaPorId(idCategoriaASerAlterada)).thenReturn(categoria); //verificarSeExisteCategoriaPorId
    	
    	//ACT
    	CriarCategoriaDTO criarCategoriaDTO = new CriarCategoriaDTO("Computadores");
    	MostrarCategoriaDTO mostrarCategoriaDTO = categoriaAdminService.alterarNomeCategoriaPorId(idCategoriaASerAlterada, criarCategoriaDTO);
    	
    	//ASSERT
    	Assertions.assertEquals(criarCategoriaDTO.nome(), mostrarCategoriaDTO.nome());
    }
    
    @Test
    @DisplayName("Alterar nome categoria por id deve lançar exceção quando id categoria a ser alterada for ok e nome dto (nome novo da categoria) já existir")
    void alterarNomeCategoriaPorId_IdCategoriaExisteENomeDtoJaExiste_DeveLancarExcecao() {
    	//ARRANGE
    	Categoria categoria = new Categoria (1L, "Ferramentas", null);
    	BDDMockito.when(categoriaRepository.findByNomeLowerCase(any())).thenReturn(Optional.of(categoria));
    	Long idCategoriaASerAlterada = 1L;
    	
    	//ASSERT + ACT
    	CriarCategoriaDTO criarCategoriaDTO = new CriarCategoriaDTO("Ferramentas");
    	Assertions.assertThrows(ValidacaoException.class, () -> categoriaAdminService.alterarNomeCategoriaPorId(idCategoriaASerAlterada, criarCategoriaDTO));
    }
    
}
