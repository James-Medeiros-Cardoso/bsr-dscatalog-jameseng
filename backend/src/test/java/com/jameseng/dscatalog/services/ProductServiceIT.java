//TESTE DE INTEGRAÇÃO - 02-36 Nosso primeiro teste de integração
package com.jameseng.dscatalog.services;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.jameseng.dscatalog.dto.ProductDTO;
import com.jameseng.dscatalog.repositories.ProductRepository;
import com.jameseng.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest //@SpringBootTest = vai carregar o contexto da aplicação
@Transactional //depois de cada teste, há um rollback no banco, voltando ao estado original
public class ProductServiceIT {
	
	//testar o service - quero que ele converse de verdade com o repository, por isso não vamos mockar o repository
	@Autowired //vai injetar o que tiver de dependencias
	private ProductService service; //objeto Pro
	
	@Autowired //injetar dependencias do repository
	private ProductRepository repository;
	
	private Long existingId;
	private Long noExistingId;
	private Long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception {
	
		existingId=1L;
		noExistingId=1000L;
		countTotalProducts=25L;
		
	}
	
	@Test //testar de o delete está realmente deletando um objeto do banco - 02-36 Nosso primeiro teste de integração
	public void deleteShouldDeleteResourceWhenIdExists() {
		
		service.delete(existingId);
		
		//verificar quantos produtos ainda tem no banco após a deleção de um produto:
		Assertions.assertEquals(countTotalProducts-1, repository.count());
		//Assertions.assertEquals(valor esperado, valor coletado);
	}
	
	@Test //se deletar com id que não existe, deve retornar ResourceNotFoundException - 02-36 Nosso primeiro teste de integração
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			service.delete(noExistingId);
		});
	}
	
	@Test //teste do findAllPaged - 02-37 Teste de integração para findAllPaged
	public void findAllPagedShouldReturnPageWhenPage0Size10() {
		
		//PageRequest=classe que implementa o Page | of(0, 10) = página=0 e size=10
		PageRequest pageRequest=PageRequest.of(0, 10);
		
		//como o banco de 25 produtos, uma página com 10 tem que ser retornada
		Page<ProductDTO> result=service.findAllPaged(0L, "", pageRequest);
		
		//é false que a variável result está vazia?
		Assertions.assertFalse(result.isEmpty());
		//a página retornada é realmente a página zero?
		Assertions.assertEquals(0, result.getNumber());
		//essa página realmente veio com 10 elementos?
		Assertions.assertEquals(10, result.getSize());
		//o número total de produtos é igual o valor esperado (25)?
		Assertions.assertEquals(countTotalProducts, result.getTotalElements());
	}
	
	//02-37 Teste de integração para findAllPaged
	@Test //teste do findAllPaged = deve retornar uma empty page quando a mesma não existe
	public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExist() {
		
		//PageRequest=classe que implementa o Page | of(50, 10) = página=50 e size=10
		PageRequest pageRequest=PageRequest.of(50, 10);
		
		//como o banco de 25 produtos, uma página com 10 tem que ser retornada
		Page<ProductDTO> result=service.findAllPaged(0L, "", pageRequest);
		
		//é verdade que a variável result está vazia?
		Assertions.assertTrue(result.isEmpty());
	}
	
	// 02-37 Teste de integração para findAllPaged
	@Test //teste do findAllPaged = ver se os dados estão ordenados quando fazemos essa solicitação
	public void findAllPagedShouldReturnSortedPageWhenSortByName() {
		
		//PageRequest=classe que implementa o Page | of(0, 10) = página=0 e size=10
		PageRequest pageRequest=PageRequest.of(0, 10, Sort.by("name")); //Sort.by("name")=critério de ordenação por nome
		
		//como o banco de 25 produtos, uma página com 10 tem que ser retornada
		Page<ProductDTO> result=service.findAllPaged(0L, "", pageRequest);
		
		//é falso que a variável result está vazia?
		Assertions.assertFalse(result.isEmpty());
		//testar os primeiros 3 produtos: o nome do primeiro elemento é igual a Macbook Pro?
		Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName()); //get(0) = posição 0
		Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName()); //get(0) = posição 1
		Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName()); //get(0) = posição 2
	}
}