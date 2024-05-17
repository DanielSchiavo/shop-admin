package br.com.danielschiavo.shop.service.produto.validacoes;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.produto.arquivosproduto.ArquivoProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.CadastrarProdutoDTO;

@ExtendWith(MockitoExtension.class)
class ValidadorPrimeiroArquivoImagemTest {

	@InjectMocks
	private ValidadorPrimeiroArquivoImagem validador;
	
	@Mock
	private CadastrarProdutoDTO cadastrarProdutoDTO;
	
	@Test
	@DisplayName("Validador primeiro arquivo imagem não deve lançar exceção quando o arquivo na posição 0 realmente for uma imagem")
	void ValidadorPrimeiroArquivoImagem_PrimeiroArquivoImagem_NaoDeveLancarExcecao() {
		ArquivoProdutoDTO arquivoProdutoDTO = new ArquivoProdutoDTO("Imagem.jpeg", (byte) 0);
		BDDMockito.given(cadastrarProdutoDTO.arquivos()).willReturn(Set.of(arquivoProdutoDTO));
		
		Assertions.assertDoesNotThrow(() -> validador.validar(cadastrarProdutoDTO));
	}

	@Test
	@DisplayName("Validador primeiro arquivo imagem deve lançar exceção quando o arquivo na posição 0 não for uma imagem")
	void ValidadorPrimeiroArquivoImagem_PrimeiroArquivoNaoEImagem_DeveLancarExcecao() {
		ArquivoProdutoDTO arquivoProdutoDTO = new ArquivoProdutoDTO("Imagem.avi", (byte) 0);
		BDDMockito.given(cadastrarProdutoDTO.arquivos()).willReturn(Set.of(arquivoProdutoDTO));
		
		Assertions.assertThrows(ValidacaoException.class, () -> validador.validar(cadastrarProdutoDTO));
	}
}
