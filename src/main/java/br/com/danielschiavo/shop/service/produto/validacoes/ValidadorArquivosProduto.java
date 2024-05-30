package br.com.danielschiavo.shop.service.produto.validacoes;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.danielschiavo.shop.feign.FileStorageProdutoAdminServiceClient;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.produto.arquivosproduto.ArquivoProdutoDTO;
import br.com.danielschiavo.shop.model.produto.dto.CadastrarProdutoDTO;

@Service
public class ValidadorArquivosProduto implements ValidadorCadastrarNovoProduto {

	@Autowired
	private FileStorageProdutoAdminServiceClient fileStorageProdutoAdminServiceClient;
	
	@Override
	public void validar(CadastrarProdutoDTO cadastrarProdutoDTO) {
		List<String> nomes = cadastrarProdutoDTO.arquivos().stream().map(a -> a.nome()).collect(Collectors.toList());
		ResponseEntity<List<ArquivoProdutoDTO>> resposta = fileStorageProdutoAdminServiceClient.verificarSeExisteArquivos(nomes);
		if (resposta.getStatusCode().is4xxClientError()) {
			System.out.println(" Está entrando aqui ");
			StringBuilder string = new StringBuilder();
			List<ArquivoProdutoDTO> arquivos = resposta.getBody();
			Boolean first = true;
	        for (int i = 1; i < arquivos.size(); i++) {
	            ArquivoProdutoDTO arquivo = arquivos.get(i);
	            string.append(arquivo.nome());
	            
	            if (i < arquivos.size() && first == false) {
	                string.append(", ");
	            }
	        }
			throw new ValidacaoException("Não foram encontrados os arquivos: " + string.toString());
		}
	}

}
