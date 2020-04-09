package io.github.oliviercailloux.plaquette_mido_soap;


import io.github.oliviercailloux.plaquette_mido_soap.QueriesHelper;
import static org.junit.jupiter.api.Assertions.*;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import org.junit.Rule;
import org.junit.contrib.java.lang.system.*;
import org.junit.jupiter.api.Test;

class AuthenticationTest {

	@Rule
	public final EnvironmentVariables API_UserNameEnv = new EnvironmentVariables().set("API_username", "a username");

	@Rule
	public final ProvideSystemProperty API_UserNameProp = new ProvideSystemProperty("API_username", "env username");

	@Rule
	public final ProvideSystemProperty API_UserPasswordProp = new ProvideSystemProperty("API_password", "env password");

	@Test
	public void test() {
		assertEquals("env username", System.getProperty("API_username"));
	}

}
