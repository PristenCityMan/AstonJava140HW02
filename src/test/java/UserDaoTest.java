import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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

    static {
        postgres.start();
        System.setProperty("db.url", postgres.getJdbcUrl());
        System.setProperty("db.username", postgres.getUsername());
        System.setProperty("db.password", postgres.getPassword());
    }

    private UserDao userDao;
    private static SessionFactory sessionFactory;

    @BeforeAll
    static void beforeAll() throws Exception{


        try (Connection connection = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword())) {

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));


            String changelogPath = "changelog/db.changelog-master-test.yaml";

            try (Liquibase liquibase = new Liquibase(changelogPath, new ClassLoaderResourceAccessor(), database)) {

                liquibase.update("");
            }
        }

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
            Transaction transaction = session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    void saveTest() {

        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test_user");
        user.setAge(99);

        userDao.save(user);


        assertNotNull(user.getId(), "ID должен быть сгенерирован базой данных");

        User savedUser = userDao.findById(user.getId());
        assertNotNull(savedUser);
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("Test_user", savedUser.getName());
        assertEquals(99, savedUser.getAge());
    }

    @Test
    void findByIdNotExistTest() {

        User foundUser = userDao.findById(999L);


        assertNull(foundUser);
    }

    @Test
    void findAllTest() {

        User user1 = new User();
        user1.setEmail("test@example.com");
        user1.setName("Test_user");
        user1.setAge(99);

        User user2 = new User();
        user2.setEmail("test1@example.com");
        user2.setName("Test_user2");
        user2.setAge(98);

        userDao.save(user1);
        userDao.save(user2);


        List<User> users = userDao.findAll();


        assertEquals(2, users.size());
    }

    @Test
    void updateTest() {

        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test_user");
        user.setAge(99);
        userDao.save(user);


        user.setEmail("newemail@example.com");
        userDao.update(user);


        User updatedUser = userDao.findById(user.getId());
        assertEquals("newemail@example.com", updatedUser.getEmail());
    }

    @Test
    void deleteById() {

        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test_user");
        user.setAge(99);
        userDao.save(user);


        boolean isDeleted = userDao.deleteById(user.getId());


        assertTrue(isDeleted);
        assertNull(userDao.findById(user.getId()));
    }

    @Test
    void deleteByIdNotExistTest() {

        boolean isDeleted = userDao.deleteById(999L);


        assertFalse(isDeleted);
    }
}