package org.example.userservice.specification;

import org.example.userservice.entity.PaymentCard;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class PaymentCardSpecifications {

    public static Specification<PaymentCard> withFilters(String holder, String number) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(holder)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("holder")),
                        "%" + holder.toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(number)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("number")),
                        "%" + number.toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}