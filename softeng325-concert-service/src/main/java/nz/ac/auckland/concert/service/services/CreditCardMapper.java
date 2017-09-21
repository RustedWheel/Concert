package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.service.domain.CreditCard;

public class CreditCardMapper {

	static CreditCard toDomainModel(nz.ac.auckland.concert.common.dto.CreditCardDTO dtoCreditCard) {
		
		CreditCard creditCard = new CreditCard(dtoCreditCard.getType(),
				dtoCreditCard.getName(),
				dtoCreditCard.getNumber(), 
				dtoCreditCard.getExpiryDate());
		
		return creditCard;
	}
	
}
