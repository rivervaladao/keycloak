//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package br.com.docnix.keycloak.events.listener.sample;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

public abstract class AbstractEventListenerProvider implements EventListenerProvider {
    private static final Logger log = Logger.getLogger(AbstractEventListenerProvider.class);

    public AbstractEventListenerProvider() {
    }

    public void onEvent(Event event) {
        log.debugf("%s", Events.toString(event));
    }

    public void onEvent(AdminEvent adminEvent, boolean b) {
        log.debugf("%s", Events.toString(adminEvent));
    }

    public void close() {
    }
}
