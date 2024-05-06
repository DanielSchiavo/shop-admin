package br.com.danielschiavo.shop.service.produto.validacoes;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import br.com.danielschiavo.shop.infra.exceptions.ValidacaoException;
import br.com.danielschiavo.shop.model.produto.arquivosproduto.ArquivoProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.CadastrarProdutoDTO;

@Service
public class ValidadorOrdenacaoArquivosProduto implements ValidadorCadastrarNovoProduto {

	@Override
	public void validar(CadastrarProdutoDTO cadastrarProdutoDTO) {
        List<Byte> posicoesOrdenadas = cadastrarProdutoDTO.arquivos().stream()
									                .map(ArquivoProdutoDTO::posicao)
									                .sorted()
									                .collect(Collectors.toList());

		boolean allMatch = IntStream.range(0, posicoesOrdenadas.size())
									.allMatch(i -> i == posicoesOrdenadas.get(i));
		
		if (allMatch == false) {
			throw new ValidacaoException("As posições dos arquivos do produto não estão seguindo uma ordenação correta");
		}
	}

}
