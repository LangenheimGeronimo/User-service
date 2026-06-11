package com.example.userservice.repository;

import com.example.userservice.model.entity.User;
import com.example.userservice.model.enums.State;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>{
	
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    //Se utiliza JPQL con CURRENT_TIMESTAMP delegado a la base de datos para evitar desfasajes 
    // horarios entre el servidor de la aplicación (JVM) y el motor de persistencia.
    @Query("SELECT u FROM User u WHERE u.state = :banned AND u.banUntil < CURRENT_TIMESTAMP")
    List<User> findExpiredBans(State banned);

}	
	
	
	
	
	
	
	

