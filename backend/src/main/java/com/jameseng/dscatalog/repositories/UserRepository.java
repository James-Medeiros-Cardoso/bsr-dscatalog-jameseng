package com.jameseng.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jameseng.dscatalog.entities.User;

//CAMADA DE ACESSO A DADOS - OPERAÇÕES LIBERADAS NO BANCO DE DADOS (H2 SERÁ USADO)
@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	//método que busca no banco de dados um usuário por email:
	User findByEmail(String email);
}
