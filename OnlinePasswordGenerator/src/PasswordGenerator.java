import java.security.SecureRandom;
import java.sql.*;


public class PasswordGenerator {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/password_mngr";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static Connection connection;
    private static final int PASSWORD_LENGTH = 10;


    public static void main(String[] args) throws SQLException {

        try {
            openConnection();
            String generatedPassword = generatePassword();
            storePassword(generatedPassword);
            String retrievedPassword = retrievePassword();
            System.out.println("Retrieved Password is: " +retrievedPassword);

        } catch (SQLException e) {
            e.printStackTrace();
        }  finally {
            closeConnection();
        }
    }
    private static void openConnection() throws SQLException {
        connection = DriverManager.getConnection(JDBC_URL,DB_USER,DB_PASSWORD);
    }
    private static void closeConnection() throws SQLException {
        if(connection != null && !connection.isClosed()){
            connection.close();
        }
    }

    private static String generatePassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i=0; i<PASSWORD_LENGTH; i++){
            int randomIndex = random.nextInt(characters.length());
            password.append(characters.charAt(randomIndex));
        }
        return password.toString();
    }

    private static void storePassword(String generatedPassword) throws SQLException {

        String sql = "INSERT INTO generated_password (password) VALUES(?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1,generatedPassword);
            preparedStatement.executeUpdate();
        }
    }

    public static String retrievePassword() throws SQLException {

        String sql = "SELECT password from generated_password ORDER BY id DESC LIMIT 1";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getString("password");
            } else {
                return "NO Password found in the database";
            }
        }
    }
}