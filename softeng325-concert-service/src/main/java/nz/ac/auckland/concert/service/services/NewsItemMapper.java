package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.service.domain.NewsItem;

public class NewsItemMapper {

	static NewsItem toDomainModel(nz.ac.auckland.concert.common.dto.NewsItemDTO dtoNewsItem) {
		
		NewsItem newsItem = new NewsItem(
				dtoNewsItem.getId(),
				dtoNewsItem.getTimetamp(),
				dtoNewsItem.getContent()
				);
		return newsItem;
	}

}
