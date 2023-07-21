package br.com.docnix.keycloak.events.listener.sample;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

@JBossLog
public class DocnixUserAddRemoveListener extends DocnixUserEventListenerProviderFactory {

    @Override
    public String getId() {
        return "docnix-ext-event-user-listener";
    }

    @Override
    UserChangedHandler getUserChangedHandler() {
        return new UserChangedHandler() {
            @Override
            void onUserAdded(KeycloakSession session, RealmModel realm, UserModel user) {
                log.infof("User %s added to Realm %s", user.getUsername(), realm.getName());
            }

            @Override
            void onUserRemoved(KeycloakSession session, RealmModel realm, UserModel user) {
                log.infof("User %s removed from Realm %s", user.getUsername(), realm.getName());
            }
        };
    }

}