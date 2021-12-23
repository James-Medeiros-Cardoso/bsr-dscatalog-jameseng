package com.jameseng.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.jameseng.dscatalog.dto.UserInsertDTO;
import com.jameseng.dscatalog.entities.User;
import com.jameseng.dscatalog.repositories.UserRepository;
import com.jameseng.dscatalog.resources.exceptions.FieldMessage;

//interface do beansValidation:							
public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {
	//UserInsertValid = anotattion
	//UserInsertDTO = tipo da classe que vai receber o anotattion
	
	@Autowired
	private UserRepository repository;
	
	@Override
	public void initialize(UserInsertValid ann) { //alguma lógica para inicializar o objeto
	}

	@Override //isValid = testa se o método é válido ou não. Se retornar 1, não há erros, se retornar zero, há pelo menos um erro
	public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {
		
		List<FieldMessage> list = new ArrayList<>();
		
		// Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à lista

		//busca no banco de dados se o email informado já foi cadastrado
		User user=repository.findByEmail(dto.getEmail());
		if(user!=null) {
			list.add(new FieldMessage("email", "Este email já está cadastrado!")); //adiciona um erro na lista FieldMessage
		}
		
		
		
		// Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à lista
		
		//pega a lista de fieldMessage e insere o erro (ou erros) na lista de erros do beans validation
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty(); //se a lista retornar true (vazia), não há erros
	}
}
