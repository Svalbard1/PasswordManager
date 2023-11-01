import java.util.Scanner;

public class PasswordManagerApp {
    public static void main(String[] args) {
        PasswordManager passwordManager = new PasswordManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1.Добавить пароль");
            System.out.println("2.Получить пароль");
            System.out.println("3.Удалить пароль");
            System.out.println("4.Выход");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Введите имя пользователя: ");
                    String username = scanner.nextLine();
                    System.out.print("Введите пароль: ");
                    String password = scanner.nextLine();
                    passwordManager.addPassword(username, password);
                    System.out.println("Пароль добавлен.");
                    break;
                case 2:
                    System.out.print("Введите имя пользователя: ");
                    String getUsername = scanner.nextLine();
                    String getPassword = passwordManager.getPassword(getUsername);
                    System.out.println("Пароль: " + getPassword);
                    break;
                case 3:
                    System.out.print("Введите имя пользователя для удаления пароля: ");
                    String deleteUsername = scanner.nextLine();
                    passwordManager.deletePassword(deleteUsername);
                    System.out.println("Пароль удален.");
                    break;
                case 4:
                    System.out.println("Выход из программы.");
                    System.exit(0);
                default:
                    System.out.println("Некорректный выбор.");
                    break;
            }
        }
    }
}