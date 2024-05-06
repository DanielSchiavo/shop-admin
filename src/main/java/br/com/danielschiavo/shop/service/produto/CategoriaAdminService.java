package br.com.danielschiavo.shop.service.produto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.danielschiavo.repository.produto.CategoriaRepository;
import br.com.danielschiavo.service.produto.CategoriaUtilidadeService;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.produto.categoria.Categoria;
import br.com.danielschiavo.shop.model.produto.categoria.CriarCategoriaDTO;
import br.com.danielschiavo.shop.model.produto.categoria.MostrarCategoriaDTO;

@Service
public class CategoriaAdminService {

	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private CategoriaUtilidadeService categoriaUtilidadeService;
	
	@Transactional
	public void deletarCategoriaPorId(Long id) {
		Categoria categoria = categoriaUtilidadeService.verificarSeExisteCategoriaPorId(id);
		categoriaRepository.delete(categoria);
	}

	@Transactional
	public MostrarCategoriaDTO criarCategoria(String nomeCategoria) {
		verificarSeNomeCategoriaJaExiste(nomeCategoria);

		Categoria categoria = new Categoria(null, nomeCategoria, null);
		categoriaRepository.save(categoria);
		return new MostrarCategoriaDTO(categoria.getId(), categoria.getNome());
	}

	@Transactional
	public MostrarCategoriaDTO alterarNomeCategoriaPorId(Long idCategoriaASerAlterada, CriarCategoriaDTO categoriaDTO) {
		String novoNome = categoriaDTO.nome();
		verificarSeNomeCategoriaJaExiste(novoNome);
		
		Categoria categoria = categoriaUtilidadeService.verificarSeExisteCategoriaPorId(idCategoriaASerAlterada);
		categoria.setNome(novoNome);
		categoriaRepository.save(categoria);
		return new MostrarCategoriaDTO(categoria.getId(), categoria.getNome());
	}
	
	
//	------------------------------
//	------------------------------
//	METODOS UTILITARIOS
//	------------------------------
//	------------------------------

	private void verificarSeNomeCategoriaJaExiste(String nomeCategoria) {
		categoriaRepository.findByNomeLowerCase(nomeCategoria)
					.ifPresent(c -> {throw new ValidacaoException("A categoria de nome " + nomeCategoria + " já existe");});
	}
}
