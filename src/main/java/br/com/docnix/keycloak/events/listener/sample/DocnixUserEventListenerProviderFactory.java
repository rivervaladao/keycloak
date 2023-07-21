package br.com.docnix.keycloak.events.listener.sample;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.AbstractKeycloakTransaction;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public abstract class DocnixUserEventListenerProviderFactory extends AbstractEventListenerProviderFactory {
    private static final Logger log = Logger.getLogger(DocnixUserEventListenerProviderFactory.class);

    public DocnixUserEventListenerProviderFactory() {
    }

    public EventListenerProvider create(final KeycloakSession session) {
        return new AbstractEventListenerProvider() {
            public void onEvent(Event event) {
                if (EventType.REGISTER.equals(event.getType())) {
                    this.userAdded(event.getRealmId(), event.getUserId());
                }

            }

            public void onEvent(AdminEvent adminEvent, boolean b) {
                if (ResourceType.USER.equals(adminEvent.getResourceType()) && OperationType.CREATE.equals(adminEvent.getOperationType())) {
                    String resourcePath = adminEvent.getResourcePath();
                    if (resourcePath.startsWith("users/")) {
                        this.userAdded(adminEvent.getRealmId(), resourcePath.substring("users/".length()));
                    } else {
                        DocnixUserEventListenerProviderFactory.log.warnf("AdminEvent was CREATE:USER without appropriate resourcePath=%s", resourcePath);
                    }
                }

            }

            void userAdded(final String realmId, final String userId) {
                session.getTransactionManager().enlistAfterCompletion(new AbstractKeycloakTransaction() {
                    protected void commitImpl() {
                        RealmModel realm = session.realms().getRealm(realmId);
                        UserModel user = session.users().getUserById(realm, userId);
                        DocnixUserEventListenerProviderFactory.this.getUserChangedHandler().onUserAdded(session, realm, user);
                    }

                    protected void rollbackImpl() {
                    }
                });
            }
        };
    }

    abstract UserChangedHandler getUserChangedHandler();

    public void postInit(KeycloakSessionFactory factory) {
        factory.register((event) -> {
            if (event instanceof UserModel.UserRemovedEvent) {
                UserModel.UserRemovedEvent removal = (UserModel.UserRemovedEvent)event;
                this.getUserChangedHandler().onUserRemoved(removal.getKeycloakSession(), removal.getRealm(), removal.getUser());
            }

        });
    }

    abstract class UserChangedHandler {
        UserChangedHandler() {
        }

        abstract void onUserAdded(KeycloakSession var1, RealmModel var2, UserModel var3);

        abstract void onUserRemoved(KeycloakSession var1, RealmModel var2, UserModel var3);
    }
}
