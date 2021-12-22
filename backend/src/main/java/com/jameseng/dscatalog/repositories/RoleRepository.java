package com.jameseng.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jameseng.dscatalog.entities.Role;

//CAMADA DE ACESSO A DADOS - OPERAÇÕES LIBERADAS NO BANCO DE DADOS (H2 SERÁ USADO)
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

}
