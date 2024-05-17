package br.com.danielschiavo.shop.service.produto;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import br.com.danielschiavo.shop.model.produto.Produto;
import br.com.danielschiavo.shop.model.produto.arquivosproduto.ArquivoProduto;
import br.com.danielschiavo.shop.model.produto.arquivosproduto.ArquivoProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.AlterarProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.CadastrarProdutoDTO;

@Mapper(componentModel = "spring")
public interface ArquivoProdutoMapper {

	@AfterMapping
	default void arquivoProdutoDTOParaArquivoProduto(@MappingTarget Produto produto, Object produtoDTO) {
		Set<ArquivoProdutoDTO> arquivos = null;
		
		if (produtoDTO instanceof CadastrarProdutoDTO) {
			CadastrarProdutoDTO cadastrarProdutoDTO = (CadastrarProdutoDTO) produtoDTO;
			arquivos = cadastrarProdutoDTO.arquivos();
	    } else if (produtoDTO instanceof AlterarProdutoDTO) {
	    	AlterarProdutoDTO alterarProdutoDTO = (AlterarProdutoDTO) produtoDTO;
	    	arquivos = alterarProdutoDTO.arquivos();
	    }
		
		if (arquivos != null) {
			Set<ArquivoProduto> setArquivoProduto = new HashSet<>();
			arquivos.forEach(arquivo -> {
				ArquivoProduto arquivoProduto = new ArquivoProduto();
				arquivoProduto.setNome(arquivo.nome());
				arquivoProduto.setPosicao(arquivo.posicao());
				arquivoProduto.setProduto(produto);
				setArquivoProduto.add(arquivoProduto);
			});
			produto.adicionarArquivosProduto(setArquivoProduto);
		}
	}
	
}
