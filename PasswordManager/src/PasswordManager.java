import java.util.HashMap;
import java.util.Map;

class PasswordManager {
    private Map<String, Password> passwordDatabase;

    public PasswordManager() {
        passwordDatabase = new HashMap<>();
    }

    public void addPassword(String username, String password) {
        Password newPassword = new Password(username, password);
        passwordDatabase.put(username, newPassword);
    }

    public String getPassword(String username) {
        Password storedPassword = passwordDatabase.get(username);
        if (storedPassword != null) {
            return storedPassword.getPassword();
        }
        return "Пароль не найден.";
    }

    public void deletePassword(String username) {
        passwordDatabase.remove(username);
    }
}