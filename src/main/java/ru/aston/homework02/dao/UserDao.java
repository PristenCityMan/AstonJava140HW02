package ru.aston.homework02.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import ru.aston.homework02.HibernateUtil;
import ru.aston.homework02.model.User;

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
                System.out.println("Ошибка сохранения: Пользователь с email {} уже существует." + user.getEmail());
            } else {
                System.out.println("Не удалось сохранить пользователя." + e.getMessage());
            }
        }
    }

    public User findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        } catch (Exception e) {
            System.out.println("Ошибка при поиске пользователя по ID: "+id+e.getMessage());
            return null;
        }
    }

    public void update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            System.out.println("Данные пользователя обновлены: ID {}" + user.getId());
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println("Не удалось обновить пользователя с ID: {}" + user.getId() +e.getMessage());
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
                System.out.println("Пользователь с ID" + id + "удален.");
                return true;
            }
            transaction.commit();
            System.out.println("Пользователь с ID " + id + " не найден для удаления.");
            return false;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println("Ошибка при удалении пользователя по ID: " + id + e.getMessage());
            return false;
        }
    }
}
