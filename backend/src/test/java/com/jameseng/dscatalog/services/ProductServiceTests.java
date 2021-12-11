//Teste de unidade: somente a classe especifica sem carregar o componente
package com.jameseng.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

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
import com.jameseng.dscatalog.entities.Category;
import com.jameseng.dscatalog.entities.Product;
import com.jameseng.dscatalog.repositories.CategoryRepository;
import com.jameseng.dscatalog.repositories.ProductRepository;
import com.jameseng.dscatalog.services.exceptions.DatabaseException;
import com.jameseng.dscatalog.services.exceptions.ResourceNotFoundException;
import com.jameseng.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class) // (teste de unidade: service/component) - não carrega o contexto da aplicação Spring
public class ProductServiceTests {
	
	//Não colocar o @Autowrited
	@InjectMocks
	private ProductService service;
	
	@Mock //tem que configurar o comportamento simulado do Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private long existingId;
	private long noExistingId;
	private long dependetId;
	private PageImpl<Product> page; //é um tipo concreto que repsenta uma página de dados
	private Product product;
	private Category category; //FEITO POR JAMES - CORRETO PELA AULA
	
	
	//private ProductDTO produtoDTO; //FEITO POR JAMES
	
	@BeforeEach
	void setUp() throws Exception {
		existingId=1L;
		noExistingId=2L;
		dependetId=3L;
		product=Factory.createProduct();
		category=Factory.createCategory();
		page=new PageImpl<>(List.of(product));
		
		//produtoDTO=createProductDTO(); //FEITO POR JAMES
		
		
		/* 1 - Configurando o comportamento do @Mock | para funções void =  Mockito.(faça isso).(quando acontecer isso)
		when = quando o método retorna alguma coisa
		doNothing() = não retorna e não faz nada
		quando eu chamar o deleteById(existingId), esse método não retorna nada*/
		Mockito.doNothing().when(repository).deleteById(existingId);
		
		// 2 - quando eu chamar o deleteById(noExistingId), lançar uma exceção do tipo EmptyResultDataAccessException
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(noExistingId);
		
		/* 3 - quando eu chamar o deleteById(dependetId) com outra entidade que depende dele,
		lançar uma exceção do tipo DataIntegrityViolationException (preservar a integridade)*/
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependetId);
		
		
		/*para métodos que não retornam void = Mockito.(quando fazer isso).(então retonar isso)
		quando eu chamar o repository.findAll() passando um objeto qualquer do tipo pageable,
		essa chamada do findAll deve retornar uma página (page)*/
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		//quando eu chamar repository.save() passando qualquer objeto, então retorne um produto (product)
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		//5 - quando eu chamar repository.findById(existingId), então retornar um Optional<Product> product não vazio
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product)); //retorna um produto optional
		//6 - quando eu chamar repository.findById(noExistingId), então retornar um Optional vazio
		Mockito.when(repository.findById(noExistingId)).thenReturn(Optional.empty()); //retorna um optional vazio
		
		//7 - quando eu chamar repository.getOne(existingId), então retornar um product e category
		Mockito.when(repository.getOne(existingId)).thenReturn(product); //feito por James - CORRETO de acordo com a correção
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category); //feito pela aula de correção
		//8 - quando eu chamar repository.getOne(noExistingId), então retornar EntityNotFoundException
		//Mockito.doThrow(EntityNotFoundException.class).when(repository).getOne(noExistingId); //FEITO POR JAMES
		Mockito.when(repository.getOne(noExistingId)).thenThrow(EntityNotFoundException.class); //feito pela aula de correção
		Mockito.when(categoryRepository.getOne(noExistingId)).thenThrow(EntityNotFoundException.class);//feito pela aula de correção
	}
	
	
	// 1 - FEITO EM AULA - Testando o método delete (não deve retornar nada, se o ID existir):
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
	
	
	// 2 - FEITO EM AULA - Testando o método delete quando o ID não existe (deve retornar a exceção ResourceNotFoundException):
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
	
	
	// 3 - FEITO EM AULA - Quando eu chamar o deleteById(dependetId) com outra entidade que depende dele,
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
	
	
	// 4 - FEITO EM AULA - Teste de unidade para testar o findAllPages (só para ver se ele retorna uma página)
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
	
	
	@Test //5 - CORREÇÃO (EM AULA) - Exercícios: testes de unidade com Mockito
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		
		//Criado um "result" do tipo "ProductDTO" para guardar o produto retornado pelo service.findById(existingId);
		//service.findById(existingId) = retorna um ProductDTO
		ProductDTO result=service.findById(existingId);
		
		//para testar se "result" não é nulo neste resultado:
		Assertions.assertNotNull(result);
	}
	/*findByIdShouldReturnNoEmptyOptionalWhenIdExists()
	@Test //FEITO POR JAMES - Exercícios: testes de repository
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		
		//Criado um "produto" do tipo "Product" para guarder o produto retornado pelo repository.findById(existingId);
		//repository.findById(existingId) = retorna Optional<Product>
		Optional<Product> produto=repository.findById(existingId);
		
		//para testar se "produto" não é nulo neste resultado:
		Assertions.assertNotNull(produto);
		
		//para ver se realmente o meu repository.findById(existingId) foi chamado dentro do findById service
		Mockito.verify(repository, Mockito.times(1)).findById(existingId);	
		//Mockito.times(1) = chamado uma vez (é opcional)
	}*/
	
	
	@Test //6 - CORREÇÃO (EM AULA) - Exercícios: testes de unidade com Mockito
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		//Deve gerar a exceção ResourceNotFoundException quando chamado o service.findById(noExistingId) e o id não existe.
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			service.findById(noExistingId);
		});
	}
	/*@Test //FEITO POR JAMES - Exercícios: testes de repository
	public void findByIdShouldReturnOptionalEmptyWhenNoExistingId() {
		
		//Criado um "produto" do tipo "Product" para guarder o produto retornado pelo repository.findById(existingId);
		//repository.findById(existingId) = retorna Optional<Product>
		Optional<Product> produto=repository.findById(noExistingId);
		
		//para testar se "produto" é não nulo neste resultado:
		Assertions.assertNotNull(produto);
		
		//para ver se realmente o meu repository.findById(existingId) foi chamado dentro do findById service
		Mockito.verify(repository, Mockito.times(1)).findById(noExistingId);	
		//Mockito.times(1) = chamado uma vez (é opcional)
	}*/
	
	@Test //7 - FEITO POR JAMES - Exercícios: testes de unidade com Mockito - update retorna ProductDTO quando existe id
	public void updateShouldReturnProductDTOWhenIdExists() { //CORRETO = PELA CORREÇÃO DA AULA
		
		//ProductDTO productDTO=new ProductDTO();
		ProductDTO pDTO=Factory.createProductDTO(); //feito na aula de correção
		
		ProductDTO result=service.update(existingId, pDTO);
		
		//para testar se "result" não é nulo neste resultado:
		Assertions.assertNotNull(result);
	}
	
	@Test //8 - FEITO POR JAMES - Exercícios: testes de unidade com Mockito - update retorna exception quando id não existe
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			ProductDTO dto=new ProductDTO();
			//ProductDTO result=service.update(noExistingId, dto);
			service.update(noExistingId, dto);
		});
	}
}