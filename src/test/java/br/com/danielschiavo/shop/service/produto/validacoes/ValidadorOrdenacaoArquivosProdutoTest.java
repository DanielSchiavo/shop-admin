package br.com.danielschiavo.shop.service.produto.validacoes;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.danielschiavo.shop.models.ValidacaoException;
import br.com.danielschiavo.shop.models.produto.arquivosproduto.ArquivoProdutoDTO;
import br.com.danielschiavo.shop.models.produto.dto.CadastrarProdutoDTO;

@ExtendWith(MockitoExtension.class)
class ValidadorOrdenacaoArquivosProdutoTest {

	@InjectMocks
	private ValidadorOrdenacaoArquivosProduto validador;
	
	@Mock
	private CadastrarProdutoDTO cadastrarProdutoDTO;
	
	@Test
	@DisplayName("Validador ordenacao arquivos produto não deve lançar exceção quando lista está corretamente ordenada")
	void ValidadorOrdenacaoArquivosProduto_ListaCorretamenteOrdenada_NaoDeveLancarExcecao() {
		List<ArquivoProdutoDTO> listaArquivoProduto = new ArrayList<>();
		ArquivoProdutoDTO arquivoProdutoDTO = new ArquivoProdutoDTO("Arquivo 1", (byte) 0);
		ArquivoProdutoDTO arquivoProdutoDTO2 = new ArquivoProdutoDTO("Arquivo 2", (byte) 1);
		ArquivoProdutoDTO arquivoProdutoDTO3 = new ArquivoProdutoDTO("Arquivo 3", (byte) 2);
		listaArquivoProduto.addAll(List.of(arquivoProdutoDTO, arquivoProdutoDTO2, arquivoProdutoDTO3));
		BDDMockito.given(cadastrarProdutoDTO.arquivos()).willReturn(listaArquivoProduto);
		
		Assertions.assertDoesNotThrow(() -> validador.validar(cadastrarProdutoDTO));
	}
	
	@Test
	@DisplayName("Validador ordenacao arquivos produto deve lançar exceção quando lista está erroneamente ordenada")
	void ValidadorOrdenacaoArquivosProduto_ListaErroneamenteOrdenada_DeveLancarExcecao() {
		List<ArquivoProdutoDTO> listaArquivoProduto = new ArrayList<>();
		ArquivoProdutoDTO arquivoProdutoDTO = new ArquivoProdutoDTO("Arquivo 1", (byte) 0);
		ArquivoProdutoDTO arquivoProdutoDTO2 = new ArquivoProdutoDTO("Arquivo 2", (byte) 2);
		ArquivoProdutoDTO arquivoProdutoDTO3 = new ArquivoProdutoDTO("Arquivo 3", (byte) 3);
		listaArquivoProduto.addAll(List.of(arquivoProdutoDTO, arquivoProdutoDTO2, arquivoProdutoDTO3));
		BDDMockito.given(cadastrarProdutoDTO.arquivos()).willReturn(listaArquivoProduto);
		
		Assertions.assertThrows(ValidacaoException.class, () -> validador.validar(cadastrarProdutoDTO));
	}

}
