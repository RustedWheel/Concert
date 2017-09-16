package nz.ac.auckland.concert.service.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nz.ac.auckland.concert.service.domain.User;


public class UserMapper {

	static User toDomainModel(nz.ac.auckland.concert.common.dto.UserDTO dtoUser) {
		
		User user = new User(dtoUser.getUsername(),
				dtoUser.getPassword(),
				dtoUser.getFirstname(), 
				dtoUser.getLastname());
		return user;
		
	}
	
}
