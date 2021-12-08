//Teste de unidade: somente a classe especifica sem carregar o componente
package com.jameseng.dscatalog.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.jameseng.dscatalog.dto.ProductDTO;
import com.jameseng.dscatalog.entities.Product;
import com.jameseng.dscatalog.repositories.ProductRepository;
import com.jameseng.dscatalog.services.exceptions.DatabaseException;
import com.jameseng.dscatalog.services.exceptions.ResourceNotFoundException;
import com.jameseng.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class) // (teste de unidade: service/component)
public class ProductServiceTests {
	
	//Não colocar o @Autowrited
	@InjectMocks
	private ProductService service;
	
	@Mock //tem que configurar o comportamento simulado do Mock
	private ProductRepository repository;
	
	private long existingId;
	private long noExistingId;
	private long dependetId;
	private PageImpl<Product> page;
	private Product product;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId=1L;
		noExistingId=1000L;
		dependetId=4L;
		product=Factory.createProduct();
		page=new PageImpl<>(List.of(product));
		
		/*para métodos que não retornam void:
		quando eu chamar o findAll do repository passando um objeto qualquer do tipo pageable,
		essa chamada do findAll deve retornar uma página (page)*/
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		//simular o comportamento do save() do repository
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		//findById passando um Id existente (tem que voltar um optional não vazio) e um não existente (retorna um vazio)
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product)); //retorna um produto optional
		Mockito.when(repository.findById(noExistingId)).thenReturn(Optional.empty()); //retorna um optional vazio
		
		
		//Configurando o comportamento do @Mock:
		//when = quando o método retorna alguma coisa
		//doNothing() = não retorna e não faz nada
		//quando eu chamar o deleteById(existingId), esse método não retorna nada
		Mockito.doNothing().when(repository).deleteById(existingId);
		
		//quando eu chamar o deleteById(noExistingId), lançar uma exceção do tipo EmptyResultDataAccessException
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(noExistingId);
		
		/*quando eu chamar o deleteById(dependetId) com outra entidade que depende dele,
		lançar uma exceção do tipo DataIntegrityViolationException (preservar a integridade)*/
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependetId);
	}
	
	//Testando o método delete (não deve retornar nada, se o ID existir):
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		//Assertions.assertDoesNotThrow = Não deve gerar excessão
		Assertions.assertDoesNotThrow(()->{
			service.delete(existingId);
		});
		
		//vai verificar se o método deleteById foi chamado na ação executada pelo teste
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
		//Mockito.times(n) = chamado n vezes
		//Mockito.never() = chamado nenhuma vez
	}
	
	//Testando o método delete quando o ID não existe (deve retornar a exceção ResourceNotFoundException):
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdNotExists() {
		
		//Deve gerar a exceção ResourceNotFoundException
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			service.delete(noExistingId);
		});
		
		//vai verificar se o método deleteById foi chamado na ação executada pelo teste
		Mockito.verify(repository, Mockito.times(1)).deleteById(noExistingId);
		//Mockito.times(n) = chamado n vezes
		//Mockito.never() = chamado nenhuma vez
	}
	
	//quando eu chamar o deleteById(dependetId) com outra entidade que depende dele,
	//lançar uma exceção do tipo DataIntegrityViolationException (preservar a integridade)
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		
		//Deve gerar a exceção DatabaseException
		Assertions.assertThrows(DatabaseException.class, ()->{
			service.delete(dependetId);
		});
		
		//vai verificar se o método deleteById foi chamado na ação executada pelo teste
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependetId);
	}
	
	//teste de unidade para testar o findAllPages (só para ver se ele retorna uma página)
	@Test
	public void findAllPagedShouldReturnPage() {
		
		Pageable pageable=PageRequest.of(0, 10);
		
		Page<ProductDTO> result=service.findAllPaged(pageable);
		
		//para testar se não é nulo este resultado:
		Assertions.assertNotNull(result);
		
		//para ver se realmente o meu repository.findAllPageable foi chamado dentro do findAllPageable service
		Mockito.verify(repository, Mockito.times(1)).findAll(pageable);	
		//Mockito.times(1) = chamado uma vez (é opcional)
	}
}