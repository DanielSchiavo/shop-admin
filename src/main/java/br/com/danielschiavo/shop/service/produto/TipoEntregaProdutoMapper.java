package br.com.danielschiavo.shop.service.produto;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import br.com.danielschiavo.shop.model.pedido.TipoEntrega;
import br.com.danielschiavo.shop.model.produto.Produto;
import br.com.danielschiavo.shop.model.produto.dto.AlterarProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.CadastrarProdutoDTO;
import br.com.danielschiavo.shop.model.produto.tipoentregaproduto.TipoEntregaProduto;

@Mapper(componentModel = "spring")
public interface TipoEntregaProdutoMapper {
	
    @AfterMapping
	default void setTiposEntregaParaSetTiposEntregaProduto(@MappingTarget Produto produto, Object produtoDTO) {
		Set<TipoEntrega> tiposEntrega = null;
		
		if (produtoDTO instanceof CadastrarProdutoDTO) {
			CadastrarProdutoDTO cadastrarProdutoDTO = (CadastrarProdutoDTO) produtoDTO;
			tiposEntrega = cadastrarProdutoDTO.tiposEntrega();
	    } else if (produtoDTO instanceof AlterarProdutoDTO) {
	    	AlterarProdutoDTO alterarProdutoDTO = (AlterarProdutoDTO) produtoDTO;
	    	tiposEntrega = alterarProdutoDTO.tiposEntrega();
	    }
    	
		if (tiposEntrega != null) {
			Set<TipoEntregaProduto> setTipoEntregaProduto = new HashSet<>();
			tiposEntrega.forEach(tipoEntrega -> {
				TipoEntregaProduto tipoEntregaProduto = new TipoEntregaProduto();
				tipoEntregaProduto.setTipoEntrega(tipoEntrega);
				tipoEntregaProduto.setProduto(produto);
				setTipoEntregaProduto.add(tipoEntregaProduto);
			});
			produto.adicionarTiposEntrega(setTipoEntregaProduto);
		}
	}
	
}
