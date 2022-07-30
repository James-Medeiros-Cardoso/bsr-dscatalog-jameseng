package com.jameseng.dscatalog.resources;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jameseng.dscatalog.dto.ProductDTO;
import com.jameseng.dscatalog.services.ProductService;

@RestController
@RequestMapping(value="/products")
public class ProductResource {
	
	@Autowired
	private ProductService service;
	
	/*@GetMapping
	public ResponseEntity<Page<ProductDTO>> findAll(Pageable pageable
			
			//Parâmetros: page, size, sort
			//page=page, size=linesPerPage, sort=Direction.valueOf(direction), orderBy
			
			/*@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
			@RequestParam(value = "direction", defaultValue = "ASC") String direction,	
			@RequestParam(value = "orderBy", defaultValue = "name") String orderBy*/
		//) {
		//PageRequest pageRequest=PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		
		/*Page<ProductDTO> list=service.findAllPaged(pageable);
		
		return ResponseEntity.ok().body(list);
	}*/
	
	@GetMapping
	public ResponseEntity<Page<ProductDTO>> findAll(
			@RequestParam(value = "categoryId", defaultValue = "0") Long categoryId,
			Pageable pageable

			/*@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
			@RequestParam(value = "direction", defaultValue = "ASC") String direction,	
			@RequestParam(value = "orderBy", defaultValue = "name") String orderBy*/
		) {
		
		Page<ProductDTO> list=service.findAllPaged(categoryId, pageable);
		
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping(value="/{id}")
	public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
		ProductDTO dto=service.findById(id);
		return ResponseEntity.ok().body(dto);
	}
	
	@PostMapping
	public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO dto) { 
		//@Valid = para valer as anotations de campos do DTO
		dto=service.insert(dto);
		URI uri=ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(dto.getId()).toUri();
		return ResponseEntity.created(uri).body(dto);
	}
	
	@PutMapping(value="/{id}")
	public ResponseEntity<ProductDTO> update(@Valid @PathVariable Long id, @RequestBody ProductDTO dto) {
		dto=service.update(id, dto);
		return ResponseEntity.ok().body(dto);
	}
	
	@DeleteMapping(value="/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
