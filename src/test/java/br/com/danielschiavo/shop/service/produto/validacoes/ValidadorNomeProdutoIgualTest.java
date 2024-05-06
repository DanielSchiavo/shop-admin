package br.com.danielschiavo.shop.service.produto.validacoes;

import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.danielschiavo.repository.produto.ProdutoRepository;
import br.com.danielschiavo.shop.models.ValidacaoException;
import br.com.danielschiavo.shop.models.produto.Produto;
import br.com.danielschiavo.shop.models.produto.dto.CadastrarProdutoDTO;

@ExtendWith(MockitoExtension.class)
class ValidadorNomeProdutoIgualTest {

	@InjectMocks
	private ValidadorNomeProdutoIgual validador;
	
	@Mock
	private ProdutoRepository produtoRepository;
	
	@Mock
	private CadastrarProdutoDTO cadastrarProdutoDTO;
	
	@Mock
	private Produto produto;
	
	@Test
	@DisplayName("Validador nome produto igual não deve lançar exceção quando não existir no banco de dados um nome igual")
	void ValidadorNomeProdutoIgual_NaoExisteNomeIgual_NaoDeveLancarExcecao() {
		BDDMockito.given(produtoRepository.findByNomeLowerCase(any())).willReturn(Optional.empty());
		
		Assertions.assertDoesNotThrow(() -> validador.validar(cadastrarProdutoDTO));
	}
	
	@Test
	@DisplayName("Validador nome produto igual deve lançar exceção quando existir no banco de dados um nome igual")
	void ValidadorNomeProdutoIgual_ExisteNomeIgual_DeveLancarExcecao() {
		BDDMockito.given(produtoRepository.findByNomeLowerCase(any())).willReturn(Optional.of(produto));
		
		Assertions.assertThrows(ValidacaoException.class, () -> validador.validar(cadastrarProdutoDTO));
	}

}
