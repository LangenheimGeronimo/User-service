package com.example.userservice.repository;

import com.example.userservice.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>{
	

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

}	
	
	
	
	
	
	
	

