package br.com.danielschiavo.shop.service.produto.validacoes;

import org.springframework.stereotype.Service;

import br.com.danielschiavo.shop.infra.exceptions.ValidacaoException;
import br.com.danielschiavo.shop.model.produto.dto.CadastrarProdutoDTO;

@Service
public class ValidadorQuantidadeArquivos implements ValidadorCadastrarNovoProduto {

	private final int MAX_FILES = 10;
	
	@Override
	public void validar(CadastrarProdutoDTO cadastrarProdutoDTO) {
		if (cadastrarProdutoDTO.arquivos().size() > MAX_FILES) {
			throw new ValidacaoException("O máximo de arquivos para produto são " + MAX_FILES);
		}
	}


}
