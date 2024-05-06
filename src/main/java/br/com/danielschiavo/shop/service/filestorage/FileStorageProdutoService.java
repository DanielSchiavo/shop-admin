package br.com.danielschiavo.shop.service.filestorage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.FileUrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.danielschiavo.shop.model.FileStorageException;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.filestorage.ArquivoInfoDTO;

@Service
public class FileStorageProdutoService {
	
	private final Path raizProduto = Paths.get("imagens/produto");
	
	public void deletarArquivoProdutoNoDisco(String nome) {
		try {
			Files.delete(this.raizProduto.resolve(nome));
		} catch (IOException e) {
			throw new FileStorageException("Falha ao excluir arquivo de nome " + nome + " no disco. ", e);
		}
	}
	
	public List<ArquivoInfoDTO> mostrarArquivoProdutoPorListaDeNomes(List<String> listNomes) {
		List<ArquivoInfoDTO> listaArquivosInfoDTO = new ArrayList<>();
		listNomes.forEach(nome -> {
			try {
				ArquivoInfoDTO arquivoInfoDTO = this.pegarArquivoProdutoPorNome(nome);
				listaArquivosInfoDTO.add(arquivoInfoDTO);
			} catch (FileStorageException e) {
				listaArquivosInfoDTO.add(ArquivoInfoDTO.comErro(nome, e.getMessage()));
			}
		});
		return listaArquivosInfoDTO;
	}
	
	public ArquivoInfoDTO pegarArquivoProdutoPorNome(String nomeArquivo) {
		byte[] bytes = recuperarBytesArquivoProdutoDoDisco(nomeArquivo);
		return new ArquivoInfoDTO(nomeArquivo, bytes);
	}
	
	public List<ArquivoInfoDTO> persistirArrayArquivoProduto(MultipartFile[] arquivos, UriComponentsBuilder uriBuilderBase) {
	    List<ArquivoInfoDTO> arquivosInfo = new ArrayList<>();

	    for (MultipartFile arquivo : arquivos) {
	    	try {
	    		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uriBuilderBase.toUriString());
	    		String nomeArquivo = gerarNomeArquivoProduto(arquivo);
	    		byte[] bytesArquivo = salvarNoDiscoArquivoProduto(nomeArquivo, arquivo);
	    		URI uri = uriBuilder.path("/arquivo-produto/" + nomeArquivo).build().toUri();
	    		var arquivoInfo = ArquivoInfoDTO.comUriENomeAntigoArquivo(nomeArquivo, arquivo.getOriginalFilename(), uri.toString(), bytesArquivo);
	    		arquivosInfo.add(arquivoInfo);
			} catch (FileStorageException e) {
				arquivosInfo.add(ArquivoInfoDTO.comErro(arquivo.getOriginalFilename(), e.getMessage()));
			}
	    }
	    
	    return arquivosInfo;
	}
	
	public List<ArquivoInfoDTO> alterarArrayArquivoProduto(MultipartFile[] arquivos, String nomesArquivosASeremExcluidos, UriComponentsBuilder uriBuilderBase) {
		if (arquivos.length == 0 || nomesArquivosASeremExcluidos.isEmpty()) {
			throw new ValidacaoException("Você tem que mandar pelo menos um arquivo e um nomeArquivoASerExcluido");
		}
		List<ArquivoInfoDTO> arquivosInfo = new ArrayList<>();
		String[] split = nomesArquivosASeremExcluidos.trim().split(",");
		
		for(String nomeArquivoASerExcluido : split) {
			try {
				deletarArquivoProdutoNoDisco(nomeArquivoASerExcluido);
				
			} catch (FileStorageException e) {
				arquivosInfo.add(ArquivoInfoDTO.comErro(nomeArquivoASerExcluido, e.getMessage()));
			}
		}
		
		for (MultipartFile arquivo : arquivos) {
			try {
				UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uriBuilderBase.toUriString());
				String novoNomeGerado = gerarNomeArquivoProduto(arquivo);
				byte[] bytes = salvarNoDiscoArquivoProduto(novoNomeGerado, arquivo);
				URI uri = uriBuilder.path("/arquivo-produto/" + novoNomeGerado).build().toUri();
				
				arquivosInfo.add(ArquivoInfoDTO.comUri(novoNomeGerado, uri.toString(), bytes));
			} catch (FileStorageException e) {
				arquivosInfo.add(ArquivoInfoDTO.comErro(arquivo.getOriginalFilename(), e.getMessage()));
			}
		}
		return arquivosInfo;
	}

	
//
// METODOS UTILITARIOS DE PRODUTO
//	

	private byte[] salvarNoDiscoArquivoProduto(String nomeArquivo, MultipartFile arquivo) {
		try {
			byte[] bytes = arquivo.getInputStream().readAllBytes();
			Files.copy(arquivo.getInputStream(), this.raizProduto.resolve(nomeArquivo), StandardCopyOption.REPLACE_EXISTING);
			return bytes;
		} catch (Exception e) {
			throw new FileStorageException("Falha ao salvar arquivo de nome "+ nomeArquivo + " no disco. ", e);
		}
	}
	
	private String gerarNomeArquivoProduto(MultipartFile arquivo) {
		String[] contentType = arquivo.getContentType().split("/");
		if (!contentType[0].contains("image") && !contentType[0].contains("video")) {
			throw new FileStorageException("Só é aceito imagens e videos");
		}
		if (!contentType[1].contains("jpg") && !contentType[1].contains("jpeg") && !contentType[1].contains("png")
				&& !contentType[1].contains("mp4") && !contentType[1].contains("avi")) {
			throw new FileStorageException("Os tipos aceitos são jpg, jpeg, png, mp4 e avi");
		}
		String stringUnica = gerarStringUnica();
		return stringUnica + "." + contentType[1];
	}
	
	private static String gerarStringUnica() {
        String string = UUID.randomUUID().toString();
        int divisao = string.length() / 3;
        long timestamp = Instant.now().toEpochMilli();
        String substring = string.substring(0, divisao);
        return substring + timestamp;
    }
    
    public byte[] recuperarBytesArquivoProdutoDoDisco(String nomeArquivoProduto) {
		FileUrlResource fileUrlResource;
		try {
			fileUrlResource = new FileUrlResource(raizProduto + "/" + nomeArquivoProduto);
			return fileUrlResource.getContentAsByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw new FileStorageException("Não foi possivel recuperar os bytes do arquivo nome " + nomeArquivoProduto + ", motivo: " + e);
		}
	}

	public void verificarSeExisteArquivoProdutoPorNome(String nome) {
		try {
			FileUrlResource fileUrlResource = new FileUrlResource(raizProduto + "/" + nome);
			if (!fileUrlResource.exists()) {
				throw new ValidacaoException("Não existe arquivo-produto com o nome " + nome);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
