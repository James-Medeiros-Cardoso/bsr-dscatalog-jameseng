package com.jameseng.dscatalog.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jameseng.dscatalog.dto.CategoryDTO;
import com.jameseng.dscatalog.dto.ProductDTO;
import com.jameseng.dscatalog.entities.Category;
import com.jameseng.dscatalog.entities.Product;
import com.jameseng.dscatalog.repositories.CategoryRepository;
import com.jameseng.dscatalog.repositories.ProductRepository;
import com.jameseng.dscatalog.services.exceptions.DatabaseException;
import com.jameseng.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	/*@Transactional(readOnly=true)
	public Page<ProductDTO> findAllPaged(Pageable pageable) {
		Page<Product> list=repository.findAll(pageable);
		
		return list.map(x -> new ProductDTO(x));
		
		//List<ProductDTO> listDto=list.stream().map(x -> new ProductDTO(x)).collect(Collectors.toList());
		/*List<ProductDTO> listDto=new ArrayList<>();
		for(Product cat : list)
		{
			listDto.add(new ProductDTO(cat));
		}
		return listDto;*/
	//}
	
	@Transactional(readOnly=true)
	public Page<ProductDTO> findAllPaged(Long categoryId, String name, Pageable pageable) {
		List<Category> categories = (categoryId == 0) ? null : Arrays.asList(categoryRepository.getOne(categoryId));
		
		Page<Product> page=repository.find(categories, name, pageable); // Método implementado no repository
		
		repository.findProductsWithCategories(page.getContent());
		
		return page.map(x -> new ProductDTO(x, x.getCategories()));
	}

	@Transactional(readOnly=true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj=repository.findById(id);
		Product entity=obj.orElseThrow(() -> new ResourceNotFoundException("Entity not Found."));
		return new ProductDTO(entity, entity.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity=new Product();
		copyDtoToEntity(dto, entity); //entity.setName(dto.getName());
		entity=repository.save(entity);
		return new ProductDTO(entity);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		try {
			Product entity=repository.getOne(id); //getOne para atualizar dados
			copyDtoToEntity(dto, entity); //entity.setName(dto.getName());
			entity=repository.save(entity);
			return new ProductDTO(entity);
		}
		catch(EntityNotFoundException e) { //EntityNotFoundException = exceção da JPA
			throw new ResourceNotFoundException("Id not found "+id);
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		}
		catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found "+id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
	
	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setDate(dto.getDate());
		entity.setImgUrl(dto.getImgUrl());
		entity.setPrice(dto.getPrice());
		
		entity.getCategories().clear();
		for(CategoryDTO catDto : dto.getCategories()) {
			Category category=categoryRepository.getOne(catDto.getId());
			entity.getCategories().add(category);	
		}
	}
}
