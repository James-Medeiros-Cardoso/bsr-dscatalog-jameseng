//02-38 Teste de integração na camada web findAll
package com.jameseng.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jameseng.dscatalog.dto.ProductDTO;
import com.jameseng.dscatalog.tests.Factory;

@SpringBootTest //teste de integração
@AutoConfigureMockMvc //teste da camada web
@Transactional //fazer rollback a cada teste no banco de dados
public class ProductResourceIT {
	
	@Autowired
	private MockMvc mockMvc; //fazer requisições
	
	@Autowired
	private ObjectMapper objectMapper; //para o teste de update - 02-33 Testando o update
	
	private Long existingId;
	private Long noExistingId;
	private Long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId=1L;
		noExistingId=1000L;
		countTotalProducts=25L;
	}
	
	//02-38 Teste de integração na camada web findAll
	@Test //verificar se a página da resposta da requisição está ordenada por nome conforme solicitado
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
		
		ResultActions result=mockMvc.perform(get("/products?page=0&size=12&sort=name,asc").
				accept(MediaType.APPLICATION_JSON));
		
		//esperar o status da resposta como "ok", código 200
		result.andExpect(status().isOk());
		//testar se o total de elementos (não só da página) é igual ao total esperado (25)
		result.andExpect(jsonPath("$.content").exists());
		//testar se o conteúdo existe
		result.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
		
		//testar se o primeiro será o Macbook Pro, o segundo será o PC Gamer e o terceiro o PC Gamer Alfa
		//testar se o primeiro será o Macbook Pro
		result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		//testar se o primeiro será o PC Gamer
		result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		//testar se o primeiro será o PC Gamer Alfa
		result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));	
	}
	
	//02-39 Teste de integração na camada web update
	@Test //testando o update quando o id existe
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

		ProductDTO productDTO=Factory.createProductDTO();
		
		String jsonbody = objectMapper.writeValueAsString(productDTO); // dentro do teste
		
		String expectedName=productDTO.getName(); //grava o nome para testar após a ação
		String expectedDescription=productDTO.getDescription(); //grava a descrição que será salva no banco de dados

		ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
				.content(jsonbody) // passando o jsonbody na requisição
				.contentType(MediaType.APPLICATION_JSON) // negociar o tipo de dados da requisição, nao só o da resposta
				.accept(MediaType.APPLICATION_JSON)); // a requisição vai aceitar como resposta o tipo JSON*/

		//esperar o status da resposta como "ok", código 200
		result.andExpect(status().isOk());
		//para ver se manteve o mesmo id
		result.andExpect(jsonPath("$.id").value(existingId));
		//para ver se foi salvo no banco com o nome enviado pelo método put, gravado em expectedName
		result.andExpect(jsonPath("$.name").value(expectedName));
		//para ver se foi salvo no banco com a descrição enviada pelo método put, gravado em expectedDescription
		result.andExpect(jsonPath("$.description").value(expectedDescription));
	}
	
	// 02-39 Teste de integração na camada web update
	@Test //testando o método update quando o id não existe
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

		ProductDTO productDTO=Factory.createProductDTO();
		
		String jsonbody = objectMapper.writeValueAsString(productDTO); // dentro do teste

		ResultActions result = mockMvc.perform(put("/products/{id}", noExistingId)
				.content(jsonbody) // passando o jsonbody na requisição
				.contentType(MediaType.APPLICATION_JSON) // negociar o tipo de dados da requisição, nao só o da resposta
				.accept(MediaType.APPLICATION_JSON)); // a requisição vai aceitar como resposta o tipo JSON*/

		//esperar o status da resposta Not Found
		result.andExpect(status().isNotFound());
	}
}
