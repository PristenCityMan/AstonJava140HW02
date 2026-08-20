import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import ru.aston.homework02.dao.UserDao;
import ru.aston.homework02.hibernate.HibernateUtil;
import ru.aston.homework02.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoTest {


    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_pass");

    private UserDao userDao;

    @BeforeAll
    static void beforeAll() throws Exception{
        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());

        System.setProperty("hibernate.hbm2ddl.auto", "validate");

        try (Connection connection = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword())) {

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            // (обычно в src/main/resources)
            String changelogPath = "db/changelog/db.changelog-master.yaml";

            try (Liquibase liquibase = new Liquibase(changelogPath, new ClassLoaderResourceAccessor(), database)) {

                liquibase.update("");
            }
        }
        HibernateUtil.getSessionFactory();
    }

    @AfterAll
    static void afterAll() {

        if (HibernateUtil.getSessionFactory() != null) {
            HibernateUtil.getSessionFactory().close();
        }
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDao();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            tx.commit();
        }
    }

    @Test
    void save_ShouldPersistUser() {

        User user = new User();
        user.setEmail("test@example.com");

        userDao.save(user);


        assertNotNull(user.getId(), "ID должен быть сгенерирован базой данных");

        User savedUser = userDao.findById(user.getId());
        assertNotNull(savedUser);
        assertEquals("test@example.com", savedUser.getEmail());
    }

    @Test
    void findById_ShouldReturnNull_WhenUserDoesNotExist() {

        User foundUser = userDao.findById(999L);


        assertNull(foundUser);
    }

    @Test
    void findAll_ShouldReturnAllUsers() {

        User user1 = new User();
        user1.setEmail("user1@example.com");
        User user2 = new User();
        user2.setEmail("user2@example.com");

        userDao.save(user1);
        userDao.save(user2);


        List<User> users = userDao.findAll();


        assertEquals(2, users.size());
    }

    @Test
    void update_ShouldModifyExistingUser() {

        User user = new User();
        user.setEmail("old@example.com");
        userDao.save(user);


        user.setEmail("new@example.com");
        userDao.update(user);


        User updatedUser = userDao.findById(user.getId());
        assertEquals("new@example.com", updatedUser.getEmail());
    }

    @Test
    void deleteById_ShouldReturnTrueAndRemoveUser() {

        User user = new User();
        user.setEmail("delete@example.com");
        userDao.save(user);


        boolean isDeleted = userDao.deleteById(user.getId());


        assertTrue(isDeleted);
        assertNull(userDao.findById(user.getId()));
    }

    @Test
    void deleteById_ShouldReturnFalse_WhenUserDoesNotExist() {

        boolean isDeleted = userDao.deleteById(999L);


        assertFalse(isDeleted);
    }
}