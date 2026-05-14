package com.example.userservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.userservice.model.dto.UserCreateDTO;
import com.example.userservice.model.dto.UserResponseDTO;
import com.example.userservice.model.entity.User;
import com.example.userservice.model.enums.Role;
import com.example.userservice.model.enums.State;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.exception.EmailAlreadyExistsException;
import com.example.userservice.exception.UserIsAlreadyDeletedException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.mapper.UserMapper; 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    void createUser_ShouldEncryptPasswordAndSave_WhenUserIsValid() {
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
    @Test
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

    //editUser:
    @Test
    void editUser_ShouldReturnUpdatedUserResponseDTO_WhenUserExists() {
        UserCreateDTO dto = new UserCreateDTO("Geronimo", "Langenheim", "geronimo@email.com", "mipassword1234", 
                        LocalDate.of(2004, 4, 19), Role.USER);
        Long idUser = 1L;
        User userEntity = User.builder().firstName("Geronimo").lastName("Langenheim")
                        .email("geronimo@email.com").build();
        User savedUser = User.builder().id(1L).firstName("Geronimo").lastName("Langenheim")
                        .email("geronimo@email.com").state(State.ACTIVE).build();
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "Geronimo", 
                            "Langenheim", "geronimo@email.com", 
                            LocalDate.of(2004, 4, 19), Role.USER, State.ACTIVE, List.of());
        
        when(userRepository.findById(idUser)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.encode(dto.password())).thenReturn(("passwordHash"));
        when(userRepository.save(any(User.class))).thenReturn(savedUser); 
        when(userMapper.toResponseDto(savedUser)).thenReturn(responseDTO);

        UserResponseDTO resultado = userService.editUser(idUser, dto);

        assertNotNull(resultado);
        assertEquals("Geronimo", resultado.firstName());
        
        verify(userRepository).findById(idUser);
        verify(userMapper).updateEntityFromDto(eq(dto), eq(userEntity)); 
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void editUser_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        Long idInexistente = 99L;
        UserCreateDTO dto = new UserCreateDTO("Nombre", "Apellido", "email@test.com", "pass123", 
                                LocalDate.of(2004, 4, 19), Role.USER);
        
        when(userRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.editUser(idInexistente, dto);
        });

        verify(userRepository, times(1)).findById(idInexistente);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userMapper, never()).updateEntityFromDto(any(), any());
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void editUser_ShouldThrowUserIsAlreadyDeletedException_WhenUserIsDeleted() {
        Long idUser = 1L;
        UserCreateDTO dto = new UserCreateDTO("Geronimo", "Langenheim", "gero@email.com", 
                "pass", LocalDate.now(), Role.USER);
        User deletedUser = User.builder().id(idUser).state(State.DELETED).build();

        when(userRepository.findById(idUser)).thenReturn(Optional.of(deletedUser));

        assertThrows(UserIsAlreadyDeletedException.class, () -> userService.editUser(idUser, dto));

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }


    @Test
    void editUser_ShouldThrowEmailAlreadyExistsException_WhenNewEmailIsTaken() {
        Long idUser = 1L;
        String nuevoEmail = "otro@email.com";
        UserCreateDTO dto = new UserCreateDTO("Geronimo", "Langenheim", nuevoEmail, "pass", LocalDate.now(), Role.USER);
        User userInDb = User.builder().id(idUser).email("viejo@email.com").state(State.ACTIVE).build();

        when(userRepository.findById(idUser)).thenReturn(Optional.of(userInDb));
        when(userRepository.existsByEmail(nuevoEmail)).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.editUser(idUser, dto));

        verify(userMapper, never()).updateEntityFromDto(any(), any());
        verify(userRepository, never()).save(any());
    }


    //deleteUser:
    @Test
    void deleteUser_ShouldChangeStateToDeletedAndSave_WhenUserExists() {
        Long idUser = 1L;
        User user = User.builder().id(idUser).state(State.ACTIVE).build();

        when(userRepository.findById(idUser)).thenReturn(Optional.of(user));

        userService.deleteUser(idUser);

        assertEquals(State.DELETED, user.getState()); 
        verify(userRepository, times(1)).findById(idUser);
        verify(userRepository, times(1)).save(user); 
    }


    @Test
    void deleteUser_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        Long idInexistente = 99L;
        when(userRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(idInexistente));

        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void deleteUser_ShouldThrowUserIsAlreadyDeletedException_WhenUserIsAlreadyDeleted() {
        Long idUser = 1L;
        User deletedUser = User.builder().id(idUser).state(State.DELETED).build();

        when(userRepository.findById(idUser)).thenReturn(Optional.of(deletedUser));

        assertThrows(UserIsAlreadyDeletedException.class, () -> userService.deleteUser(idUser));

        verify(userRepository, never()).save(any(User.class));
    }


    //getUserByEmail:
    @Test
    void getState_ShouldReturnState_WhenUserExists() {
        Long idUser = 1L;
        User user = User.builder().id(idUser).state(State.ACTIVE).build();

        when(userRepository.findById(idUser)).thenReturn(Optional.of(user));

        State resultado = userService.getState(idUser);

        assertNotNull(resultado);
        assertEquals(State.ACTIVE, resultado);
        verify(userRepository, times(1)).findById(idUser);
    }


    @Test
    void getState_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        Long idInexistente = 99L;
        when(userRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getState(idInexistente));

        verify(userRepository, times(1)).findById(idInexistente);
    }

    //getUserByEmail:

    @Test
    void getUserByEmail_ShouldReturnUserResponseDTO_WhenEmailExists() {
        String email = "geronimo@email.com";
        User user = User.builder().id(1L).email(email).firstName("Geronimo").state(State.ACTIVE).build();
                
        UserResponseDTO responseDTO = new UserResponseDTO(
                1L, "Geronimo", "Langenheim", email, 
                LocalDate.of(2004, 4, 19), Role.USER, State.ACTIVE, List.of()
        );

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(responseDTO);

        UserResponseDTO resultado = userService.getUserByEmail(email);

        assertNotNull(resultado);
        assertEquals(email, resultado.email());
        verify(userRepository, times(1)).findByEmail(email);
    }


    @Test
    void getUserByEmail_ShouldThrowUserNotFoundException_WhenEmailDoesNotExist() {
        String emailInexistente = "noexiste@email.com";
        when(userRepository.findByEmail(emailInexistente)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(emailInexistente));

        verify(userRepository, times(1)).findByEmail(emailInexistente);
        verify(userMapper, never()).toResponseDto(any());
    }


    //changeState:

    @Test
    void changeState_ShouldUpdateStateAndSave_WhenUserExists() {
        Long idUser = 1L;
        State nuevoEstado = State.BANNED;
        User user = User.builder().id(idUser).state(State.ACTIVE).build();

        when(userRepository.findById(idUser)).thenReturn(Optional.of(user));

        userService.changeState(idUser, nuevoEstado);

        assertEquals(nuevoEstado, user.getState()); 
        verify(userRepository, times(1)).findById(idUser);
        verify(userRepository, times(1)).save(user); 
    }

    @Test
    void changeState_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        Long idInexistente = 99L;
        State nuevoEstado = State.ACTIVE;
        when(userRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> 
            userService.changeState(idInexistente, nuevoEstado)
        );

        verify(userRepository, times(1)).findById(idInexistente);
        verify(userRepository, never()).save(any(User.class));
    }

    //getUsers:

    @Test
    void getUsers_ShouldReturnPageOfUserResponseDTO_WhenFiltersAreApplied() {
        Pageable pageable = PageRequest.of(0, 10);
        User user = User.builder().id(1L).firstName("Geronimo").email("geronimo@email.com").build();
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "Geronimo", "Langenheim", 
                                        "geronimo@email.com", LocalDate.of(2004, 4, 19), 
                                        Role.USER, State.ACTIVE, List.of());
        
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(ArgumentMatchers.<Specification<User>>any(), eq(pageable))).thenReturn(userPage);
        when(userMapper.toResponseDto(user)).thenReturn(responseDTO);

        Page<UserResponseDTO> resultado = userService.getUsers("Geronimo", null, null, null, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Geronimo", resultado.getContent().get(0).firstName());
        
        verify(userRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

}