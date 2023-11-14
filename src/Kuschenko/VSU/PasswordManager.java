package Kuschenko.VSU;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

class PasswordManager {
    private Map<String, String> passwordDatabase; //Ключ-значение (Ник-пароль)
    private String masterPasswordHash; // Хэшкод мастер-пароля

    public PasswordManager(String masterPassword) {
        passwordDatabase = new HashMap<>();
        initializeMasterPassword(masterPassword);
        loadPasswords();
    }

    private void initializeMasterPassword(String masterPassword) {
        try {
            // Переводим мастер-пароль в байтовое представление (256 бит)
            //https://kurl.ru/OKiSH
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            // Хэшируем представление и переводим в массив байт.
            byte[] keyBytes = sha.digest(masterPassword.getBytes());
            // Кодируем хэш по системе кодировки base64
            //https://kurl.ru/Kaklo
            masterPasswordHash = Base64.getEncoder().encodeToString(keyBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Проверяем мастер-пароль
    public boolean validateMasterPassword(String enteredMasterPassword) {
        try {
            // Как и в иницилизации, хэшируем и кодируем по base64
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] enteredKeyBytes = sha.digest(enteredMasterPassword.getBytes());
            String enteredPasswordHash = Base64.getEncoder().encodeToString(enteredKeyBytes);

            // Сверяем хэш введёного с хэшем сохранённого мастер-пароля
            return enteredPasswordHash.equals(masterPasswordHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Добавления логина и пароля в зашифрованном ввиде
    public void addPassword(String username, String password, String masterPassword) {
        if (validateMasterPassword(masterPassword)) {
            passwordDatabase.put(username, encrypt(password, masterPassword));
            savePasswords();
            System.out.println("Пароль добавлен.");
        } else {
            System.out.println("Неверный мастер-пароль. Невозможно добавить пароль.");
        }
    }

    // Получение логина и пароля
    public String getPassword(String username, String masterPassword) {
        if (validateMasterPassword(masterPassword)) {
            String encryptedPassword = passwordDatabase.get(username);
            if (encryptedPassword != null) {
                return decrypt(encryptedPassword, masterPassword);
            } else {
                return "Пароль не найден.";
            }
        } else {
            return "Неверный мастер-пароль.";
        }
    }

    public void deletePassword(String username, String masterPassword) {
        if (validateMasterPassword(masterPassword)) {
            passwordDatabase.remove(username);
            savePasswords();
            System.out.println("Пароль удален.");
        } else {
            System.out.println("Неверный мастер-пароль. Невозможно удалить пароль.");
        }
    }

    private void loadPasswords() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("encrypted_passwords.dat"))) {
            passwordDatabase = (Map<String, String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка при загрузке данных из файла.");
            e.printStackTrace();
        }
    }

    private void savePasswords() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("encrypted_passwords.dat"))) {
            oos.writeObject(passwordDatabase);
            System.out.println("Данные сохранены.");
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении данных в файл.");
            e.printStackTrace();
        }
    }

    // Шифрование самих логинов и паролей
    private String encrypt(String data, String password) {
        try {
            //Генерируем ключ
            SecretKey key = generateKey(password);
            // При помощи Java.crypto шифруем данные по стандарту AES (рейндал)
            //https://kurl.ru/DWYkK
            Cipher cipher = Cipher.getInstance("AES");
            // Инициализация шифрования с учётом ключа
            cipher.init(Cipher.ENCRYPT_MODE, key);
            // Шифрование в массив байтов
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            // Кодируем в base64
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String decrypt(String encryptedData, String password) {
        try {
            SecretKey key = generateKey(password);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Создаём ключ на основе мастер пароля
    private SecretKey generateKey(String password) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(password.getBytes());
        // Берём только первые 16 байт хэша
        return new SecretKeySpec(keyBytes, 0, 16, "AES");
    }
}