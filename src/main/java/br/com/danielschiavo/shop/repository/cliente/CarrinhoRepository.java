package br.com.danielschiavo.shop.repository.cliente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.danielschiavo.shop.model.cliente.carrinho.Carrinho;

@Repository
public interface CarrinhoRepository extends JpaRepository <Carrinho, Long>{

}
