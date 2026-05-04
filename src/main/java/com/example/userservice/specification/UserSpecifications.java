package com.example.userservice.specification;

import com.example.userservice.model.entity.User;
import com.example.userservice.model.enums.State;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {
    
    public static Specification<User> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> 
            firstName == null || firstName.isEmpty() 
                ? criteriaBuilder.conjunction() 
                : criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<User> hasLastName(String lastName) {
        return (root, query, criteriaBuilder) -> 
            lastName == null || lastName.isEmpty() 
                ? criteriaBuilder.conjunction() 
                : criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> 
            email == null || email.isEmpty() 
                ? criteriaBuilder.conjunction() 
                : criteriaBuilder.equal(root.get("email"), email);
    }

    public static Specification<User> hasState(State state) {
        return (root, query, criteriaBuilder) -> 
            state == null 
                ? criteriaBuilder.conjunction() 
                : criteriaBuilder.equal(root.get("state"), state);
    }

}
