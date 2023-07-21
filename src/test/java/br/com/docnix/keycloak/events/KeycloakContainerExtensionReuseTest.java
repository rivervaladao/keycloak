package br.com.docnix.keycloak.events;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.keycloak.TokenVerifier;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.info.ServerInfoRepresentation;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.Instant;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests reusable containers support for {@link KeycloakContainer}.
 */
@Testcontainers
public class KeycloakContainerExtensionReuseTest {

    private static final String TEST_REALM_JSON = "local-broker-realm-export.json";
    private static final String TEST_REALM = "local-broker";

    @Test
    public void shouldStartKeycloak() {
        try (KeycloakContainer keycloak = new KeycloakContainer()) {
            keycloak.start();
        }
    }

    @Test
    public void shouldConsiderConfiguredStartupTimeout() {
        final int MAX_TIMEOUT = 5;
        Instant start = Instant.now();
        try {
            Duration duration = Duration.ofSeconds(MAX_TIMEOUT);
            try (KeycloakContainer keycloak = new KeycloakContainer().withStartupTimeout(duration)) {
                keycloak.start();
            }
        } catch(ContainerLaunchException ex) {
            Duration observedDuration = Duration.between(start, Instant.now());
            assertTrue(observedDuration.toSeconds() >= MAX_TIMEOUT && observedDuration.toSeconds() < 30,
                    String.format("Startup time should consider configured limit of %d seconds, but took %d seconds",
                            MAX_TIMEOUT, observedDuration.toSeconds()));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {TEST_REALM_JSON})
    public void shouldImportRealm(final String realmLocation) {
        try (KeycloakContainer keycloak = new KeycloakContainer().withRealmImportFile(realmLocation)) {
            keycloak.start();

            String accountService = given().when().get(keycloak.getAuthServerUrl() + "/realms/"+TEST_REALM)
                    .then().statusCode(200).body("realm", equalTo(TEST_REALM))
                    .extract().path("account-service");

            given().when().get(accountService).then().statusCode(200);
        }
    }

    @Test
    public void shouldReturnServerInfo() {
        try (KeycloakContainer keycloak = new KeycloakContainer()) {
            keycloak.start();

            checkKeycloakContainerInternals(keycloak);
        }
    }

    @Test
    public void shouldUseDifferentAdminCredentials() {
        try (KeycloakContainer keycloak = new KeycloakContainer()
                .withAdminUsername("admin")
                .withAdminPassword("admin")) {
            keycloak.start();

            checkKeycloakContainerInternals(keycloak);
        }
    }

    @Test
    public void shouldRunOnDifferentContextPath() {
        String contextPath = "/auth/";
        try (KeycloakContainer keycloak = new KeycloakContainer().withContextPath(contextPath)) {
            keycloak.start();

            String authServerUrl = keycloak.getAuthServerUrl();
            assertThat(authServerUrl, endsWith(contextPath));

            given().when().get(authServerUrl + "/realms/master/.well-known/openid-configuration")
                    .then().statusCode(200);

            checkKeycloakContainerInternals(keycloak);
        }
    }

    @Test
    public void shouldCacheStaticContentPerDefault() {
        try (KeycloakContainer keycloak = new KeycloakContainer()) {
            keycloak.start();

            String authServerUrl = keycloak.getAuthServerUrl();
            given().when().get(getProjectLogoUrl(authServerUrl))
                    .then().statusCode(200).header("Cache-Control", containsString("max-age=2592000"));
        }
    }

    @Test
    public void shouldNotCacheStaticContentWithDisabledCaching() {
        try (KeycloakContainer keycloak = new KeycloakContainer().withDisabledCaching()) {
            keycloak.start();

            String authServerUrl = keycloak.getAuthServerUrl();
            given().when().get(getProjectLogoUrl(authServerUrl))
                    .then().statusCode(200).header("Cache-Control", "no-cache");
        }
    }

    @Test
    public void shouldNotExposeMetricsPerDefault() {
        try (KeycloakContainer keycloak = new KeycloakContainer()) {
            keycloak.start();

            String authServerUrl = keycloak.getAuthServerUrl();
            given().when().get(getMetricsUrl(authServerUrl))
                    .then().statusCode(404);
        }
    }

    @Test
    public void shouldExposeMetricsWithEnabledMetrics() {
        try (KeycloakContainer keycloak = new KeycloakContainer().withEnabledMetrics()) {
            keycloak.start();

            String authServerUrl = keycloak.getAuthServerUrl();
            given().when().get(getMetricsUrl(authServerUrl))
                    .then().statusCode(200);
        }
    }

    private void checkKeycloakContainerInternals(KeycloakContainer keycloak) {
        Keycloak keycloakAdminClient = keycloak.getKeycloakAdminClient();
        ServerInfoRepresentation serverInfo = keycloakAdminClient.serverInfo().getInfo();
        assertThat(serverInfo, notNullValue());
        assertThat(serverInfo.getSystemInfo().getVersion(), startsWith(keycloak.getKeycloakDefaultVersion()));
    }

    private String getProjectLogoUrl(String authServerUrl) {
        return authServerUrl + "/welcome-content/keycloak-project.png";
    }

    private String getMetricsUrl(String authServerUrl) {
        return authServerUrl + "/metrics";
    }

}