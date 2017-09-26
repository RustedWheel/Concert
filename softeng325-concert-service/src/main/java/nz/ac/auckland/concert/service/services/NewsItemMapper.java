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

	static nz.ac.auckland.concert.common.dto.NewsItemDTO totoDto(NewsItem NewsItem) {
		
		nz.ac.auckland.concert.common.dto.NewsItemDTO newsItem = new nz.ac.auckland.concert.common.dto.NewsItemDTO(
				NewsItem.getId(),
				NewsItem.getTimetamp(),
				NewsItem.getContent()
				);
		return newsItem;
	}
}
