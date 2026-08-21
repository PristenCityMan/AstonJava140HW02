package ru.aston.homework02.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import ru.aston.homework02.hibernate.HibernateUtil;
import ru.aston.homework02.model.User;

import java.util.Collections;
import java.util.List;

public class UserDao {


    public void save(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            System.out.println("Пользователь успешно сохранен: "+ user.getEmail());
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            if (e.getCause() instanceof ConstraintViolationException || e.getMessage().contains("duplicate key")) {
                System.out.println("Ошибка сохранения: Пользователь с таким email уже существует: " + user.getEmail());
            } else {
                System.out.println("Не удалось сохранить пользователя. " + e.getMessage());
            }
        }
    }

    public User findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        } catch (Exception e) {
            System.out.println("Ошибка при поиске пользователя по id: "+id+e.getMessage());
            return null;
        }
    }

    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception e) {
            System.out.println("Ошибка при получении списка пользователей. " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public void update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            System.out.println("Данные пользователя обновлены, id: " + user.getId());
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println("Не удалось обновить пользователя с id: " + user.getId() +e.getMessage());
        }
    }

    public boolean deleteById(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                System.out.println("Пользователь с id " + id + " удален.");
                return true;
            }
            transaction.commit();
            System.out.println("Пользователь с id " + id + " не найден для удаления.");
            return false;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println("Ошибка при удалении пользователя по id: " + id + e.getMessage());
            return false;
        }
    }
}
