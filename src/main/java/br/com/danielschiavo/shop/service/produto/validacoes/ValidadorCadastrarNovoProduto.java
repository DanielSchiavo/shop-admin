package br.com.danielschiavo.shop.service.produto.validacoes;

import br.com.danielschiavo.shop.model.produto.dto.CadastrarProdutoDTO;

public interface ValidadorCadastrarNovoProduto {
	
	void validar(CadastrarProdutoDTO cadastrarProdutoDTO);

}
