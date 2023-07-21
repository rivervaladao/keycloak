/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.docnix.keycloak.storage.user.jpa;

import br.com.docnix.keycloak.storage.user.jpa.DocnixUserStorageProvider;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.List;

import static br.com.docnix.keycloak.storage.user.Constants.*;

@Slf4j
public class DocnixUserStorageProviderFactory implements UserStorageProviderFactory<DocnixUserStorageProvider> {
    public static final String PROVIDER_ID = "docnix-user-storage-jpa";

    @Override
    public DocnixUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        return new DocnixUserStorageProvider(session, model);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "Docnix Federation User Storage Provider";
    }

    @Override
    public void close() {
        log.info("<<<<<< Closing factory");
    }

    @Override
    public void init(Config.Scope config) {
        log.info("Creating Docnix Federation Database Provider Factory");
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        log.info("Finish Docnix Federation Database Provider Factory");
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return  ProviderConfigurationBuilder.create()
/*                .property()
                    .name(CONFIG_KEY_JDBC_DRIVER)
                    .label("JDBC Driver Class")
                    .type(ProviderConfigProperty.STRING_TYPE)
                    .defaultValue("org.h2.Driver")
                    .helpText("Fully qualified class name of the JDBC driver")
                    .add()*/
                .property()
                    .name(CONFIG_KEY_JDBC_URL)
                    .label("JDBC URL")
                    .type(ProviderConfigProperty.STRING_TYPE)
                    .defaultValue("jdbc:h2:mem:customdb")
                    .helpText("JDBC URL used to connect to the user database")
                    .add()
                .property()
                    .name(CONFIG_KEY_DB_USERNAME)
                    .label("Database User")
                    .type(ProviderConfigProperty.STRING_TYPE)
                    .helpText("Username used to connect to the database")
                    .add()
                .property()
                    .name(CONFIG_KEY_DB_PASSWORD)
                    .label("Database Password")
                    .type(ProviderConfigProperty.STRING_TYPE)
                    .helpText("Password used to connect to the database")
                    .secret(true)
                    .add()
/*                .property()
                    .name(CONFIG_KEY_VALIDATION_QUERY)
                    .label("SQL Validation Query")
                    .type(ProviderConfigProperty.STRING_TYPE)
                    .helpText("SQL query used to validate a connection")
                    .defaultValue("select 1")
                    .add()*/
                .build();
    }
}
