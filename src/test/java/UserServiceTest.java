import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.homework02.dao.UserDao;
import ru.aston.homework02.model.User;
import ru.aston.homework02.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User("Test_user", "test@example.com", 99);
    }

    @Test
    void createUser() {

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        userService.createUser("Test_user", "test@example.com", 99);
        verify(userDao, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("Test_user", savedUser.getName());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals(99, savedUser.getAge());
    }

    @Test
    void getAllUsers() {

        List<User> expectedUsers = List.of(sampleUser);
        when(userDao.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.getAllUsers();

        assertEquals(expectedUsers, actualUsers);
        verify(userDao, times(1)).findAll();
    }

    @Test
    void getUserById() {
        Long userId = 1L;
        when(userDao.findById(userId)).thenReturn(sampleUser);

        User actualUser = userService.getUserById(userId);

        assertNotNull(actualUser);
        assertEquals("Test_user", actualUser.getName());
        verify(userDao, times(1)).findById(userId);
    }

    @Test
    void updateUser() {

        Long userId = 1L;
        when(userDao.findById(userId)).thenReturn(sampleUser);

        boolean result = userService.updateUserInfo(userId, "Test_user1", "", -1);

        assertTrue(result);
        assertEquals("Test_user1", sampleUser.getName()); // Имя изменилось
        assertEquals("test@example.com", sampleUser.getEmail()); // Email остался прежним
        assertEquals(99, sampleUser.getAge()); // Возраст остался прежним
        verify(userDao, times(1)).update(sampleUser);
    }

    @Test
    void updateUserInfo() {

        Long userId = 99L;
        when(userDao.findById(userId)).thenReturn(null);

        boolean result = userService.updateUserInfo(userId, "Имя", "email@com", 30);

        assertFalse(result);
        verify(userDao, never()).update(any());
    }

    @Test
    void deleteUserById() {

        Long userId = 1L;
        when(userDao.deleteById(userId)).thenReturn(true);

        boolean result = userService.deleteUserById(userId);

        assertTrue(result);
        verify(userDao, times(1)).deleteById(userId);
    }
}