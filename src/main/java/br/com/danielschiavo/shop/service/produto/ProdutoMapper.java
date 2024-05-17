package br.com.danielschiavo.shop.service.produto;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import br.com.danielschiavo.shop.model.produto.Produto;
import br.com.danielschiavo.shop.model.produto.dto.AlterarProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.CadastrarProdutoDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProdutoMapper extends TipoEntregaProdutoMapper, ArquivoProdutoMapper {

	@Mapping(target = "tiposEntrega", ignore = true)
	@Mapping(target = "arquivosProduto", ignore = true)
	@BeanMapping(builder = @Builder(disableBuilder = true))
	Produto cadastrarProdutoDtoParaProduto(CadastrarProdutoDTO cadastrarProdutoDTO);
	
	@Mapping(target = "tiposEntrega", ignore = true)
	@Mapping(target = "arquivosProduto", ignore = true)
	@BeanMapping(builder = @Builder(disableBuilder = true), nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
	void alterarProdutoDtoParaProduto(AlterarProdutoDTO alterarProdutoDTO, @MappingTarget Produto produto);

}
