package nz.ac.auckland.concert.service.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.service.domain.User;


public class UserMapper {

	static User toDomainModel(nz.ac.auckland.concert.common.dto.UserDTO dtoUser) {
		
		User user = new User(dtoUser.getUsername(),
				dtoUser.getPassword(),
				dtoUser.getLastname(),
				dtoUser.getFirstname());
		return user;
		
	}

	public static UserDTO toDto(User user) {
		UserDTO userDTO = new UserDTO(user.getUsername(),
				user.getPassword(),
				user.getLastname(),
				user.getFirstname() );
		return userDTO;

	}
	
}
