package com.example.userservice.repository;

import com.example.userservice.model.enums.State;
import com.example.userservice.model.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>{
	

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    List<User> findByFirstName(String firstName);

    List<User> findByLastName(String lastName);

    List<User> findAllByState(State state);

	List<User> findByFirstNameAndState(String firstName, State state);

	List<User> findByLastNameAndState(String lastName, State state);
}	
	
	
	
	
	
	
	

