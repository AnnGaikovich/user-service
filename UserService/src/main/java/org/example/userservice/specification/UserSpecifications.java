package org.example.userservice.specification;

import org.example.userservice.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserSpecifications {

    public static Specification<User> withFilters(String firstName, String surname) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(firstName)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + firstName.toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(surname)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("surname")),
                        "%" + surname.toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<User> isActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("active"), true);
    }
}