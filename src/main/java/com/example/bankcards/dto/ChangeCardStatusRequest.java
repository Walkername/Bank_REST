package com.example.bankcards.dto;

import com.example.bankcards.enums.CardStatus;

public class ChangeCardStatusRequest {

    private CardStatus status;

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }
}
