package br.com.danielschiavo.shop.service.produto.validacoes;

import java.util.ArrayList;
import java.util.HashSet;

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
class ValidadorQuantidadeArquivosTest {

	@InjectMocks
	private ValidadorQuantidadeArquivos validador;
	
	@Mock
	private CadastrarProdutoDTO cadastrarProdutoDTO;
	
	private final int MAX_FILES = 10;
	
	@Test
	@DisplayName("Validador primeiro arquivo imagem não deve lançar exceção quando a quantidade de arquivosproduto forem até 10")
	void ValidadorPrimeiroArquivoImagem_10ArquivosProduto_NaoDeveLancarExcecao() {
        var listaArquivos = new HashSet<ArquivoProdutoDTO>();
        for (int i = 0; i < MAX_FILES; i++) {
        	listaArquivos.add(new ArquivoProdutoDTO("arquivo" + i + ".jpeg", (byte) i));
        }
		BDDMockito.given(cadastrarProdutoDTO.arquivos()).willReturn(listaArquivos);
	
		Assertions.assertDoesNotThrow(() -> validador.validar(cadastrarProdutoDTO));
	}
	
	@Test
	@DisplayName("Validador primeiro arquivo imagem deve lançar exceção quando a quantidade de arquivosproduto forem maior do que 10")
	void ValidadorPrimeiroArquivoImagem_MaiorQue10ArquivosProduto_DeveLancarExcecao() {
        var listaArquivos = new HashSet<ArquivoProdutoDTO>();
        for (int i = 0; i < MAX_FILES + 1; i++) {
        	listaArquivos.add(new ArquivoProdutoDTO("arquivo" + i + ".jpeg",  (byte) i));
        }
		BDDMockito.given(cadastrarProdutoDTO.arquivos()).willReturn(listaArquivos);
	
		Assertions.assertThrows(ValidacaoException.class, () -> validador.validar(cadastrarProdutoDTO));
	}

}
