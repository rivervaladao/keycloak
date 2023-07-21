package br.com.docnix.keycloak.events.listener.sample;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSessionFactory;

public abstract class AbstractEventListenerProviderFactory implements EventListenerProviderFactory {
    public AbstractEventListenerProviderFactory() {
    }

    public void init(Config.Scope scope) {
    }

    public void postInit(KeycloakSessionFactory factory) {
    }

    public void close() {
    }
}
