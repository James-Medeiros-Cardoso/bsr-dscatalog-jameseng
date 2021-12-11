//TESTES DA CAMADA WEB - RESOURCE (CONTROLADORES) - simula os comportamentos do ProductService
//AULA 02-30 Começando testes na camada web

package com.jameseng.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jameseng.dscatalog.dto.ProductDTO;
import com.jameseng.dscatalog.services.ProductService;
import com.jameseng.dscatalog.services.exceptions.DatabaseException;
import com.jameseng.dscatalog.services.exceptions.ResourceNotFoundException;
import com.jameseng.dscatalog.tests.Factory;

@WebMvcTest(ProductResource.class) //teste só do controlador mocando o service
public class ProductResourceTests {

	//tem que fazer requisições (chamar os andpoints)
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper; //para o teste de update - 02-33 Testando o update
	
	//Dependência do ProductService
	@MockBean //service será mocado
	private ProductService service;
	
	//Declarações para o teste do findAllPaged:
	private ProductDTO productDTO;
	//page deve ser retornada no teste do findAllPaged()
	private PageImpl<ProductDTO> page; //PageImpl=é um objeto concreto e pode receber o comando new
	
	//Declarações para o teste do findById:
	private Long existingId;
	private Long noExistingId;
	private Long dependentId; //para o teste do delete()
	
	@BeforeEach
	void setUp() throws Exception {
		
		/* 02-30 Começando testes na camada web
		instanciações para o teste do findAllPaged*/	//	--------------------------------------------
		productDTO=Factory.createProductDTO();
		page=new PageImpl<>(List.of(productDTO));
		
		//Simulando o comportamento do service: testar de unidade do findAll, que recebe um Pageable e retorna um Page
		//quando chamar o findAllPaged(any())), deve retornar uma página
		when(service.findAllPaged(any())).thenReturn(page);
		//any() = qualquer argumento						--------------------------------------------	
		
		/* 02-32 Testando o findById	--------------------------------------------
		Simulando o comportamento do service: teste de unidade do findById*/
		existingId=1L;
		noExistingId=2L;
		when(service.findById(existingId)).thenReturn(productDTO); //para id existente
		when(service.findById(noExistingId)).thenThrow(ResourceNotFoundException.class); //para id não existente
		// 02-32 Testando o findById	--------------------------------------------
		
		/* 02-33 Testando o update	--------------------------------------------
		Simulando o comportamento do service: teste de unidade do update - update(id, productDTO)*/
		when(service.update(eq(existingId), any())).thenReturn(productDTO); //para id existente
		when(service.update(eq(noExistingId), any())).thenThrow(ResourceNotFoundException.class); //para id não existente
		/*quando usar o any(), os outros argumentos não podem ser simples como o existingId
		usar assim: eq(existingId)*/
		// 02-33 Testando o update	--------------------------------------------
		
		/* 02-34 Simulando outros comportamentos do ProductService (DELETE)	--------------------------------------------
		Simulando o comportamento do service: teste de unidade do delete - delete(id)*/
		doNothing().when(service).delete(existingId); //para id existente
		doThrow(ResourceNotFoundException.class).when(service).delete(noExistingId); //para id não existente
		dependentId=3L;
		doThrow(DatabaseException.class).when(service).delete(dependentId); //para violação de integridade
		// 02-34 Simulando outros comportamentos do ProductService (DELETE)	--------------------------------------------
		
		/* 02-34 Simulando outros comportamentos do ProductService (INSERT)	--------------------------------------------
		Simulando o comportamento do service: teste de unidade do insert*/
		when(service.insert(any())).thenReturn(productDTO); //CORRETO - DE ACORDO COM A CORREÇÃO EM AULA
		// 02-34 Simulando outros comportamentos do ProductService (DELETE)	--------------------------------------------
	}
	
	@Test //teste do findAll do ProductResource - AULA 02-30 Começando testes na camada web
	public void findAllShouldReturnPage() throws Exception {
		
		//mockMvc.perform(get("/products")).andExpect(status().isOk());
		/*performe(get("/products")) = faz uma requisição com o get no caminho "/products"
		andExpect(status().isOk()) = e o status da resposta seja "OK" (código 200)*/
		
		//Aula 02-31 Legibilidade e negociação de conteúdo - pode também ser feito desta forma:
		/*ResultActions result=mockMvc.perform(get("/products"));
		result.andExpect(status().isOk());*/
		
		ResultActions result=mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));
		/*Negociação de MediaType:
		.accept(MediaType.APPLICATION_JSON) = essa requisição vai aceitar como resposta o tipo JSON*/
	
		result.andExpect(status().isOk());
	}
	
	@Test //teste do findById do ProductResource - 02-32 Testando o findById
	public void findByIdShouldReturnProductWhenIdExistis() throws Exception {
		
		ResultActions result=mockMvc.perform(get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		
		//para ver se realmente retornou um produto:
		result.andExpect(jsonPath("$.id").exists()); //$ = vai acessar o objeto da resposta
		//jsonPath("$.id").exists() = no corpo da resposta tem que existir um campo id
		result.andExpect(jsonPath("$.name").exists()); //no corpo da resposta tem que existir um campo name
		result.andExpect(jsonPath("$.description").exists()); //no corpo da resposta tem que existir um campo description
	}
	
	@Test //teste do findById do ProductResource - 02-32 Testando o findById - NotFound (código 404)
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExistis() throws Exception {
		
		ResultActions result=mockMvc.perform(get("/products/{id}", noExistingId).accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound()); //Tem que retornar um código NotFound, pois o id nao existe
	}
	
	@Test //teste do update do ProductResource - 02-33 Testando o update
	public void updateShouldReturnNotProductDTOWhenIdExistis() throws Exception {
		
		//put = essa requisição tem corpo
		//corpo da requisição http (json) não é um objeto java, é um texto. Convertendo para um objeto java:
		//private ObjectMapper objectMapper; //nas declarações
		
		//convertendo o objectMapper em um string:
		String jsonbody=objectMapper.writeValueAsString(productDTO); //dentro do teste
		
		//.content(jsonbody) = passando o jsonbody na requisição
		//contentType(MediaType.APPLICATION_JSON) = negociar o tipo de dados da requisição, nao só o da resposta (accept)
	
		ResultActions result=mockMvc.perform(put("/products/{id}", existingId).
				content(jsonbody). //passando o jsonbody na requisição
				contentType(MediaType.APPLICATION_JSON). //negociar o tipo de dados da requisição, nao só o da resposta (accept)
				accept(MediaType.APPLICATION_JSON)); //a requisição vai aceitar como resposta o tipo JSON*/
		
		//Assertions:
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists()); //no corpo da resposta tem que existir um campo id
		result.andExpect(jsonPath("$.name").exists()); //no corpo da resposta tem que existir um campo name
		result.andExpect(jsonPath("$.description").exists()); //no corpo da resposta tem que existir um campo description
	}
	
	@Test //teste do update do ProductResource - 02-33 Testando o update
	public void updateShouldReturnNotFoundWhenIdDoesNotExistis() throws Exception {
		
		String jsonbody=objectMapper.writeValueAsString(productDTO); //dentro do teste
		
		ResultActions result=mockMvc.perform(put("/products/{id}", noExistingId).
				content(jsonbody).
				contentType(MediaType.APPLICATION_JSON).
				accept(MediaType.APPLICATION_JSON));
		
		//Assertions:
		result.andExpect(status().isNotFound()); //Tem que retornar um código NotFound, pois o id nao existe
	}
	
	@Test //teste do delete com id existente do ProductResource - 02-35 Exercício testes na camada web
	public void deleteShouldReturnNoContentWhenIdExistis() throws Exception {
		
		//delete não tem corpo na resposta
		ResultActions result=mockMvc.perform(delete("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));
		
		//Assertions:
		result.andExpect(status().isNoContent());
	}
	
	@Test //teste do delete com id não existente do ProductResource - 02-35 Exercício testes na camada web
	public void deleteShouldReturnNotFoundWhenIdDoesNotExistis() throws Exception {
		
		//delete não tem corpo na resposta
		ResultActions result=mockMvc.perform(delete("/products/{id}", noExistingId).accept(MediaType.APPLICATION_JSON));
		
		//Assertions:
		result.andExpect(status().isNotFound()); //Tem que retornar um código NotFound, pois o id nao existe
	}
	
	@Test //teste do insert do ProductResource - 02-35 Exercício testes na camada web
	public void insertShouldReturnProductDTOCreated() throws Exception {
		
		String jsonbody=objectMapper.writeValueAsString(productDTO); //dentro do teste
		
		ResultActions result=mockMvc.perform(post("/products"). //só ("/products") dentro do post
				content(jsonbody). //passando o jsonbody na requisição
				contentType(MediaType.APPLICATION_JSON). //negociar o tipo de dados da requisição, nao só o da resposta (accept)
				accept(MediaType.APPLICATION_JSON)); //a requisição vai aceitar como resposta o tipo JSON*/
		
		//Assertions:
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists()); //no corpo da resposta tem que existir um campo id
		result.andExpect(jsonPath("$.name").exists()); //no corpo da resposta tem que existir um campo name
		result.andExpect(jsonPath("$.description").exists()); //no corpo da resposta tem que existir um campo description
	}
}