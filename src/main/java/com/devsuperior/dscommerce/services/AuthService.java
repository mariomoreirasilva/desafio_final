package com.devsuperior.dscommerce.services;

import org.apache.coyote.http11.upgrade.UpgradeServletOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;

//finalidade de ter regra de negócio relacionado a acessos dos usuários
@Service
public class AuthService {
	
	@Autowired
	private UserService userService;
	
	public void validateSelfOrAdmin(long userID) {
		//pegar o usuário logado
		User me = userService.authenticated();
		//teste se é admin ou se é o mesmo usuário logado dono do pedido.
		if (!me.hasRole("ROLE_ADMIN")  && !me.getId().equals(userID)) {
			throw new ForbiddenException("Acesso negado");
			
		}
		
	}

}
