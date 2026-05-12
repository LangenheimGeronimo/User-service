package com.example.userservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.userservice.model.dto.ReportCreateDTO;
import com.example.userservice.model.dto.UserCreateDTO;
import com.example.userservice.model.dto.UserResponseDTO;
import com.example.userservice.model.entity.Report;
import com.example.userservice.model.entity.User;
import com.example.userservice.model.enums.Role;
import com.example.userservice.model.enums.State;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.exception.EmailAlreadyExistsException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.mapper.UserMapper; // Si usas MapStruct

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;
    

    //createUser:

    @Test
    void saveUser_ShouldEncryptPasswordAndSave_WhenUserIsValid() {
        UserCreateDTO dto = new UserCreateDTO("Geronimo", "Langenheim", 
        "geronimo@email.com", "mipassword1234", 
        LocalDate.of(2004, 4, 19), Role.USER);

        User userEntity = User.builder().firstName("Geronimo").lastName("Langenheim")
                        .email("geronimo@email.com").build();
        User savedUser = User.builder().id(1L).firstName("Geronimo").lastName("Langenheim")
                        .email("geronimo@email.com").state(State.ACTIVE).build();
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "Geronimo", 
                            "Langenheim", "geronimo@email.com", 
                            LocalDate.of(2004, 4, 19), Role.USER, State.ACTIVE, List.of());
        

        when(userRepository.existsByEmail("geronimo@email.com")).thenReturn((false));
        when(userMapper.toEntity(dto)).thenReturn((userEntity));
        when(passwordEncoder.encode(dto.password())).thenReturn(("passwordHash"));
        when(userRepository.save(any(User.class))).thenReturn(savedUser); 
        when(userMapper.toResponseDto(savedUser)).thenReturn(responseDTO);
        
        UserResponseDTO resultado = userService.createUser(dto);

        assertNotNull(resultado);
        assertEquals("Geronimo", resultado.firstName());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder).encode("mipassword1234");
    }

    @Test
    void saveUser_ShouldThrowException_WhenEmailAlreadyExists() {
        String emailDuplicado = "geronimo@email.com";
        UserCreateDTO dto = new UserCreateDTO("Geronimo", "Langenheim", 
                                emailDuplicado, "mipassword1234", 
                                LocalDate.of(2004, 4, 19), Role.USER);

        when(userRepository.existsByEmail(emailDuplicado)).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.createUser(dto);
        });

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userMapper, never()).toEntity(any());
    }


    //getUser:

    void getUser_ShouldReturnUserResponseDTO_WhenUserExists() {
        Long idUser = 1L;
        User user = User.builder().id(1L).firstName("Geronimo").lastName("Langenheim")
                    .email("geronimo@email.com").state(State.ACTIVE).build();
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "Geronimo", 
                            "Langenheim", "geronimo@email.com", 
                            LocalDate.of(2004, 4, 19), Role.USER, State.ACTIVE, List.of());           
        
        when(userRepository.findById(idUser)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn((responseDTO));
        
        UserResponseDTO resultado = userService.getUser(idUser);
        
        assertNotNull(resultado);
        assertEquals("Geronimo", resultado.firstName());
        verify(userRepository, times(1)).findById(idUser);
    }

    @Test
    void getUser_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        Long idInexistente = 99L;
        
        when(userRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUser(idInexistente);
        });

        verify(userRepository, times(1)).findById(idInexistente);
        verify(userMapper, never()).toResponseDto(any());
    }



}