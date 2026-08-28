package ru.aston.homework02.dialog;

import ru.aston.homework02.hibernate.HibernateUtil;
import ru.aston.homework02.dao.UserDao;
import ru.aston.homework02.model.User;
import ru.aston.homework02.service.UserService;

import java.util.List;
import java.util.Scanner;

public class Dialog {
    private  final UserDao userDao = new UserDao();
    private  final Scanner scanner = new Scanner(System.in);
    private UserService userService = new UserService(userDao);
    public  void dialog() {
        System.out.println("=== DIALOG START ===");
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> createUser(scanner);
                case "2" -> showAllUsers(scanner);
                case "3" -> findUserById(scanner);
                case "4" -> updateUser(scanner);
                case "5" -> deleteUser(scanner);
                case "0" -> {
                    running = false;
                    HibernateUtil.shutdown();
                    System.out.println("Завершение работы приложения.");
                }
                default -> System.out.println("Неверный ввод. Попробуйте еще раз.");
            }
        }
    }

    private  void printMenu() {
        System.out.println("\n--- МЕНЮ ---");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Показать всех пользователей");
        System.out.println("3. Найти пользователя по ID");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выйти");
        System.out.print("Выберите опцию: ");
    }

    private  void createUser(Scanner scanner) {
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();
        System.out.print("Введите email: ");
        String email = scanner.nextLine();
        System.out.print("Введите возраст: ");
        int age = Integer.parseInt(scanner.nextLine());

        userService.createUser(name, email, age);
    }

    private  void showAllUsers(Scanner scanner) {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("База данных пуста.");
        } else {
            users.forEach(System.out::println);
        }
    }

    private  void findUserById(Scanner scanner) {
        System.out.print("Введите ID пользователя: ");
        Long id = Long.parseLong(scanner.nextLine());
        User user = userService.getUserById(id);
        if (user != null) {
            System.out.println(user);
        } else {
            System.out.println("Пользователь с таким ID не найден.");
        }
    }

    private void updateUser(Scanner scanner) {
        System.out.print("Введите ID пользователя для обновления: ");
        Long id = Long.parseLong(scanner.nextLine());

        System.out.print("Введите новое имя (оставьте пустым для пропуска): ");
        String name = scanner.nextLine();

        System.out.print("Введите новый email (оставьте пустым для пропуска): ");
        String email = scanner.nextLine();

        System.out.print("Введите новый возраст (или -1 для пропуска): ");
        int age = Integer.parseInt(scanner.nextLine());

        boolean updated = userService.updateUserInfo(id, name, email, age);
        if (!updated) {
            System.out.println("Пользователь не найден.");
        }
    }

    private  void deleteUser(Scanner scanner) {
        System.out.print("Введите ID пользователя для удаления: ");
        Long id = Long.parseLong(scanner.nextLine());
        boolean deleted = userService.deleteUserById(id);
        if (deleted) {
            System.out.println("Операция удаления завершена успешно.");
        } else {
            System.out.println("Не удалось удалить пользователя.");
        }
    }
}
