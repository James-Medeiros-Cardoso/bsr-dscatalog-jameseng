package com.jameseng.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.jameseng.dscatalog.dto.UserUpdateDTO;
import com.jameseng.dscatalog.entities.User;
import com.jameseng.dscatalog.repositories.UserRepository;
import com.jameseng.dscatalog.resources.exceptions.FieldMessage;

//interface do beansValidation:							
public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {
	//UserInsertValid = anotattion
	//UserInsertDTO = tipo da classe que vai receber o anotattion
	
	@Autowired
	private HttpServletRequest request; //guarda as informações da requisição, no caso o id
	
	@Autowired
	private UserRepository repository;
	
	@Override
	public void initialize(UserUpdateValid ann) { //alguma lógica para inicializar o objeto
	}

	@Override //isValid = testa se o método é válido ou não. Se retornar 1, não há erros, se retornar zero, há pelo menos um erro
	public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {
		
		//HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE = pega um mapa com os atributos da url e guarda em uriVars
		@SuppressWarnings("unchecked") //para nao aparecer o warnning
		var uriVars= (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

		//para acessar o id que foi passado (recebe como string):
		Long userId=Long.parseLong(uriVars.get("id"));
		
		List<FieldMessage> list = new ArrayList<>();
		
		// Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à lista

		//busca no banco de dados se o email informado já foi cadastrado
		User user=repository.findByEmail(dto.getEmail());
		
		if(user!=null && userId!=user.getId()) {
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
