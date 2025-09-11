package com.example.bankcards.specifications;

import com.example.bankcards.entity.Card;
import com.example.bankcards.enums.CardStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class CardSpecifications {

    public static Specification<Card> byUserId(Long userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("owner").get("id"), userId);
    }

    public static Specification<Card> hasStatus(CardStatus status) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Card> balanceGreaterThan(BigDecimal minBalance) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("balance"), minBalance);
    }

    public static Specification<Card> balanceLessThan(BigDecimal maxBalance) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("balance"), maxBalance);
    }

}
