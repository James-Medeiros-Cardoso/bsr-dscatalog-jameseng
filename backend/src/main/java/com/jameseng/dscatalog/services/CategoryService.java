package com.jameseng.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jameseng.dscatalog.dto.CategoryDTO;
import com.jameseng.dscatalog.entities.Category;
import com.jameseng.dscatalog.repositories.CategoryRepository;
import com.jameseng.dscatalog.services.exceptions.DatabaseException;
import com.jameseng.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly=true)
	public Page<CategoryDTO> findAllPaged(PageRequest pageRequest) {
		Page<Category> list=repository.findAll(pageRequest);
		
		return list.map(x -> new CategoryDTO(x));
		
		//List<CategoryDTO> listDto=list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
		/*List<CategoryDTO> listDto=new ArrayList<>();
		for(Category cat : list)
		{
			listDto.add(new CategoryDTO(cat));
		}
		return listDto;*/
	}

	@Transactional(readOnly=true)
	public CategoryDTO findById(Long id) {
		Optional<Category> obj=repository.findById(id);
		Category entity=obj.orElseThrow(() -> new ResourceNotFoundException("Entity not Found."));
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity=new Category();
		entity.setName(dto.getName());
		entity=repository.save(entity);
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		try {
			Category entity=repository.getOne(id); //getOne para atualizar dados
			entity.setName(dto.getName());
			entity=repository.save(entity);
			return new CategoryDTO(entity);
		}
		catch(EntityNotFoundException e) {
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
}
