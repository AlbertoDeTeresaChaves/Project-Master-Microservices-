package com.eazybyte.cards.service;

import com.eazybyte.cards.dto.CardsDto;
import com.eazybyte.cards.entity.Cards;

public interface ICardsService {

    void createCard(String mobileNumber);
    CardsDto fetchCard(String mobileNumber);
    boolean updateCard(CardsDto cardsDto);
    boolean deleteCard(String mobileNumber);
}
