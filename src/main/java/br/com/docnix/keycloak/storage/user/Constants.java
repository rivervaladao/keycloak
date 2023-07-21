package br.com.docnix.keycloak.storage.user;

public interface Constants {
    String PERSISTENCE_UNIT_NAME = "docnix-user-store"; // Replace with your persistence unit name

    String CONFIG_KEY_JDBC_DRIVER = "jdbcDriver";
    String CONFIG_KEY_JDBC_URL = "jdbcUrl";
    String CONFIG_KEY_DB_USERNAME = "username";
    String CONFIG_KEY_DB_PASSWORD = "password";
    String CONFIG_KEY_VALIDATION_QUERY = "validationQuery";
}