package com.jameseng.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jameseng.dscatalog.dto.RoleDTO;
import com.jameseng.dscatalog.dto.UserDTO;
import com.jameseng.dscatalog.dto.UserInsertDTO;
import com.jameseng.dscatalog.dto.UserUpdateDTO;
import com.jameseng.dscatalog.entities.Role;
import com.jameseng.dscatalog.entities.User;
import com.jameseng.dscatalog.repositories.RoleRepository;
import com.jameseng.dscatalog.repositories.UserRepository;
import com.jameseng.dscatalog.services.exceptions.DatabaseException;
import com.jameseng.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class UserService implements UserDetailsService {
	
	//instanciando o Logger
	private static Logger Logger=LoggerFactory.getLogger(UserService.class);

	@Autowired
	private BCryptPasswordEncoder passwordEncoder; //vai usar esse objeto para codificar a senha do usuário
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Transactional(readOnly=true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		Page<User> list=repository.findAll(pageable); //findAllPaged = retorna uma página (Page)
		
		return list.map(x -> new UserDTO(x));
		
		//List<UserDTO> listDto=list.stream().map(x -> new UserDTO(x)).collect(Collectors.toList());
		/*List<UserDTO> listDto=new ArrayList<>();
		for(User cat : list)
		{
			listDto.add(new UserDTO(cat));
		}
		return listDto;*/
	}

	@Transactional(readOnly=true)
	public UserDTO findById(Long id) {
		Optional<User> obj=repository.findById(id);
		User entity=obj.orElseThrow(() -> new ResourceNotFoundException("Entity not Found."));
		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		User entity=new User();
		copyDtoToEntity(dto, entity); //entity.setName(dto.getName());
		
		//passwordEncoder.encode() = vai codificar a senha informada pelo usuário
		entity.setPassword(passwordEncoder.encode(dto.getPassword()));
		
		entity=repository.save(entity);
		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO update(Long id, UserUpdateDTO dto) {
		try {
			User entity=repository.getOne(id); //getOne para atualizar dados, ele instancia uma entidade monitorada do Jpa
			copyDtoToEntity(dto, entity); //entity.setName(dto.getName());
			entity=repository.save(entity);
			return new UserDTO(entity);
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
	
	private void copyDtoToEntity(UserDTO dto, User entity) {
		
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());

		entity.getRoles().clear();
		for (RoleDTO roleDto : dto.getRoles()){
			Role role=roleRepository.getOne(roleDto.getId());
			entity.getRoles().add(role);
		}
	}

	
	//UserDetailsService: 	------------------------------------------------------------------
	@Override //retorna username (email) de acordo com o UserDetails
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user=repository.findByEmail(username);
		if(user==null) {
			Logger.error("User not found: "+username);
			throw new UsernameNotFoundException("Email not found");
		}
		Logger.info("User found: "+username);
		return user;
	}
}