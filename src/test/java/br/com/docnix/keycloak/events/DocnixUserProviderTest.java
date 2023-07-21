package br.com.docnix.keycloak.events;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Slf4j
@Testcontainers
public class DocnixUserProviderTest {

	private static final String REALM_JSON = "/local-broker-realm-export.json";
	private static final String REALM = "local-broker";
	public static final String DOCNIX_USERNAME = "river.valadao@grupoitss.com.br";
	public static final String DOCNIX_PASSWORD = "123456";
	public static final String DOCNIX_EMAIL = "river.valadao@grupoitss.com.br";

	@Container
	private static final KeycloakContainer keycloak = new KeycloakContainer()
		.withRealmImportFile(REALM_JSON)
		.withProviderClassesFrom("target/classes");

	@ParameterizedTest
	@ValueSource(strings = {REALM})
	public void testRealms(String realm) {
		System.out.println("isHealth: "+ keycloak.isHealthy());
		String accountServiceUrl = given().when().get(keycloak.getAuthServerUrl() + "/realms/" + realm)
			.then().statusCode(200).body("realm", equalTo(realm))
			.extract().path("account-service");

		given().when().get(accountServiceUrl).then().statusCode(200);
	}

	@ParameterizedTest
	@CsvFileSource(resources = "/parameters.csv", delimiter = ';')
	public void testLoginAsUserAndCheckAccessToken(String userIdentifier, String userPassword, String userMail, String userGivenName) throws IOException {
		String accessTokenString = requestToken(userIdentifier, userPassword)
			.then().statusCode(200).extract().path("access_token");

		ObjectMapper mapper = new ObjectMapper();
		TypeReference<HashMap<String,Object>> typeRef = new TypeReference<>() {};

		byte[] tokenPayload = Base64.getDecoder().decode(accessTokenString.split("\\.")[1]);
		Map<String, Object> payload = mapper.readValue(tokenPayload, typeRef);

		assertThat(payload.get("email"), is(userMail));
		assertThat(payload.get("given_name"), is(userGivenName));
	}

	@Test
	public void testLoginAsUserWithInvalidPassword() {
		requestToken(DOCNIX_USERNAME, "invalid").then().statusCode(401);
	}

	private Response requestToken(String username, String password) {
		String tokenEndpoint = given().when().get(keycloak.getAuthServerUrl() + "/realms/" + REALM + "/.well-known/openid-configuration")
			.then().statusCode(200).extract().path("token_endpoint");
		return given()
			.contentType("application/x-www-form-urlencoded")
			.formParam("username", username)
			.formParam("password", password)
			.formParam("grant_type", "password")
			.formParam("client_id", KeycloakContainer.ADMIN_CLI_CLIENT)
			.formParam("scope", "openid")
			.when().post(tokenEndpoint);
	}

}
