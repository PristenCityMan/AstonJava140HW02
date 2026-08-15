package ru.aston.homework02.dialog;

import ru.aston.homework02.hibernate.HibernateUtil;
import ru.aston.homework02.dao.UserDao;
import ru.aston.homework02.model.User;

import java.util.List;
import java.util.Scanner;

public class Dialog {
    private static final UserDao userDao = new UserDao();
    private static final Scanner scanner = new Scanner(System.in);

    public static void dialog() {
        System.out.println("=== DIALOG START ===");
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> createUser();
                case "2" -> showAllUsers();
                case "3" -> findUserById();
                case "4" -> updateUser();
                case "5" -> deleteUser();
                case "0" -> {
                    running = false;
                    HibernateUtil.shutdown();
                    System.out.println("Завершение работы приложения.");
                }
                default -> System.out.println("Неверный ввод. Попробуйте еще раз.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- МЕНЮ ---");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Показать всех пользователей");
        System.out.println("3. Найти пользователя по ID");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выйти");
        System.out.print("Выберите опцию: ");
    }

    private static void createUser() {
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();
        System.out.print("Введите email: ");
        String email = scanner.nextLine();
        System.out.print("Введите возраст: ");
        int age = Integer.parseInt(scanner.nextLine());

        User user = new User(name, email, age);
        userDao.save(user);
    }

    private static void showAllUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("База данных пуста.");
        } else {
            users.forEach(System.out::println);
        }
    }

    private static void findUserById() {
        System.out.print("Введите ID пользователя: ");
        Long id = Long.parseLong(scanner.nextLine());
        User user = userDao.findById(id);
        if (user != null) {
            System.out.println(user);
        } else {
            System.out.println("Пользователь с таким ID не найден.");
        }
    }

    private static void updateUser() {
        System.out.print("Введите ID пользователя для обновления: ");
        Long id = Long.parseLong(scanner.nextLine());
        User user = userDao.findById(id);

        if (user == null) {
            System.out.println("Пользователь не найден.");
            return;
        }

        System.out.print("Введите новое имя (оставьте пустым для пропуска): ");
        String name = scanner.nextLine();
        if (!name.isBlank()) user.setName(name);

        System.out.print("Введите новый email (оставьте пустым для пропуска): ");
        String email = scanner.nextLine();
        if (!email.isBlank()) user.setEmail(email);

        System.out.print("Введите новый возраст (или -1 для пропуска): ");
        int age = Integer.parseInt(scanner.nextLine());
        if (age != -1) user.setAge(age);

        userDao.update(user);
    }

    private static void deleteUser() {
        System.out.print("Введите ID пользователя для удаления: ");
        Long id = Long.parseLong(scanner.nextLine());
        boolean deleted = userDao.deleteById(id);
        if (deleted) {
            System.out.println("Операция удаления завершена успешно.");
        } else {
            System.out.println("Не удалось удалить пользователя.");
        }
    }
}
