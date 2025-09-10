package com.example.bankcards.util;

import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BankModelMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public BankModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User convertToUser(AuthRequest dto) {
        return modelMapper.map(dto, User.class);
    }

    public UserResponse convertToUserResponse(User user) {
        return modelMapper.map(user, UserResponse.class);
    }

    public CardResponse convertToCardResponse(Card card) {
        return modelMapper.map(card, CardResponse.class);
    }

}
