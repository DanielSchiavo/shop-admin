package br.com.danielschiavo.shop.repository.cliente;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.danielschiavo.shop.model.cliente.cartao.Cartao;

public interface CartaoRepository extends JpaRepository<Cartao, Long>{

}
