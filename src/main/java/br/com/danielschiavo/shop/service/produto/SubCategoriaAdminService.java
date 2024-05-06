package br.com.danielschiavo.shop.service.produto;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.danielschiavo.repository.produto.SubCategoriaRepository;
import br.com.danielschiavo.service.produto.CategoriaUtilidadeService;
import br.com.danielschiavo.service.produto.SubCategoriaUtilidadeService;
import br.com.danielschiavo.shop.model.ValidacaoException;
import br.com.danielschiavo.shop.model.produto.categoria.Categoria;
import br.com.danielschiavo.shop.model.produto.subcategoria.AlterarSubCategoriaDTO;
import br.com.danielschiavo.shop.model.produto.subcategoria.CadastrarSubCategoriaDTO;
import br.com.danielschiavo.shop.model.produto.subcategoria.MostrarSubCategoriaDTO;
import br.com.danielschiavo.shop.model.produto.subcategoria.SubCategoria;
import jakarta.validation.Valid;

@Service
public class SubCategoriaAdminService {

	@Autowired
	private SubCategoriaRepository subCategoriaRepository;

	@Autowired
	private SubCategoriaUtilidadeService subCategoriaUtilidadeService;
	
	@Autowired
	private CategoriaUtilidadeService categoriaUtilidadeService;

	@Transactional
	public void deletarSubCategoriaPorId(Long id) {
		SubCategoria subCategoria = subCategoriaUtilidadeService.verificarSeExisteSubCategoriaPorId(id);
		subCategoriaRepository.delete(subCategoria);
	}
	
	@Transactional
	public MostrarSubCategoriaDTO cadastrarSubCategoria(@Valid CadastrarSubCategoriaDTO subCategoriaDTO) {
		Long idCategoria = subCategoriaDTO.categoria_id();
		Categoria categoria = categoriaUtilidadeService.verificarSeExisteCategoriaPorId(idCategoria);
		String novoNome = subCategoriaDTO.nome();
		Optional<SubCategoria> optionalSubCategoria = subCategoriaRepository.findByNomeLowerCase(novoNome);
		if (optionalSubCategoria.isPresent()) {
			throw new ValidacaoException("A Sub Categoria de nome " + novoNome + " já existe");
		}
		SubCategoria subCategoria = new SubCategoria(null, subCategoriaDTO.nome(), categoria);

		subCategoriaRepository.save(subCategoria);
		
		return new MostrarSubCategoriaDTO(subCategoria.getId(), subCategoria.getNome());
	}
	
	@Transactional
	public MostrarSubCategoriaDTO alterarSubCategoriaPorId(Long idSubCategoria, AlterarSubCategoriaDTO alterarSubCategoriaDTO) {
		SubCategoria subCategoria = subCategoriaUtilidadeService.verificarSeExisteSubCategoriaPorId(idSubCategoria);
		if (alterarSubCategoriaDTO.nome() != null) {
			Optional<SubCategoria> optionalSubCategoria = subCategoriaRepository.findByNomeLowerCaseQueNaoSejaONomeDaCategoriaAtual(alterarSubCategoriaDTO.nome(), subCategoria.getNome());
			if (optionalSubCategoria.isPresent()) {
				throw new ValidacaoException("A Sub Categoria de nome " + alterarSubCategoriaDTO.nome() + " já existe");
			}
			subCategoria.setNome(alterarSubCategoriaDTO.nome());
		}
		if (alterarSubCategoriaDTO.idCategoria() != null) {
			Categoria categoria = categoriaUtilidadeService.verificarSeExisteCategoriaPorId(alterarSubCategoriaDTO.idCategoria());
			subCategoria.setCategoria(categoria);
		}

		subCategoriaRepository.save(subCategoria);

		return new MostrarSubCategoriaDTO(subCategoria.getId(), subCategoria.getNome());
	}

}
