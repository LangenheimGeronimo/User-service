package com.example.userservice.specification;

import com.example.userservice.model.entity.User;
import com.example.userservice.model.enums.State;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class UserSpecifications {
    
    public static Specification<User> hasFirstName(String firstName) {
        return (root, query, cb) -> !StringUtils.hasText(firstName)
                ? cb.conjunction() 
                : cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<User> hasLastName(String lastName) {
        return (root, query, cb) -> !StringUtils.hasText(lastName)
                ? cb.conjunction() 
                : cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) -> !StringUtils.hasText(email)
                ? cb.conjunction() 
                : cb.equal(root.get("email"), email);
    }

    public static Specification<User> hasState(State state) {
        return (root, query, cb) -> 
            state == null 
                ? cb.conjunction() 
                : cb.equal(root.get("state"), state);
    }

}
