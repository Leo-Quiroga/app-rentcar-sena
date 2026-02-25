package com.autoreserve.backend.domain.repository;

import com.autoreserve.backend.domain.entity.Role;
import com.autoreserve.backend.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setName("CLIENT");
        entityManager.persist(role);

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedPassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPhone("987654321");
        testUser.setRole(role);
        entityManager.persist(testUser);
        entityManager.flush();
    }

    @Test
    void findByEmail_ExistingUser_ReturnsUser() {
        Optional<User> result = userRepository.findByEmail("test@example.com");
        
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Test");
    }

    @Test
    void findByEmail_NonExistingUser_ReturnsEmpty() {
        Optional<User> result = userRepository.findByEmail("notfound@example.com");
        
        assertThat(result).isEmpty();
    }

    @Test
    void save_NewUser_PersistsUser() {
        Role role = entityManager.find(Role.class, testUser.getRole().getId());
        
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setPasswordHash("hash");
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setRole(role);
        
        User saved = userRepository.save(newUser);
        
        assertThat(saved.getId()).isNotNull();
        assertThat(userRepository.findById(saved.getId())).isPresent();
    }
}
