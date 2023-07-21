package br.com.docnix.keycloak.storage.support;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;

public class CustomDialectResolver implements DialectResolver {
    public CustomDialectResolver() {
    }

    public Dialect resolveDialect(DialectResolutionInfo info) {
       String databaseName = info.getDatabaseName();
        return switch (databaseName) {
            case "MySQL" -> new MySQLDialect();
            case "PostgreSQL" -> new PostgreSQLDialect();
            default -> new H2Dialect();
        };
    }
}