package ru.aston.homework02.service;


import ru.aston.homework02.dao.UserDao;
import ru.aston.homework02.model.User;
import java.util.List;
public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void createUser(String name, String email, int age) {
        User user = new User(name, email, age);
        userDao.save(user);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User getUserById(Long id) {
        return userDao.findById(id);
    }

    public boolean updateUserInfo(Long id, String name, String email, int age) {
        User user = userDao.findById(id);
        if (user == null) {
            return false;
        }
        if (name != null && !name.isBlank()) user.setName(name);
        if (email != null && !email.isBlank()) user.setEmail(email);
        if (age != -1) user.setAge(age);

        userDao.update(user);
        return true;
    }

    public boolean deleteUserById(Long id) {
        return userDao.deleteById(id);
    }
}
