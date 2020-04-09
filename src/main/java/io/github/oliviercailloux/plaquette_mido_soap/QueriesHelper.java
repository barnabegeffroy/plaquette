package io.github.oliviercailloux.plaquette_mido_soap;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class QueriesHelper {

	public static void setDefaultAuthenticator() {
		final Authenticator myAuth = getTokenAuthenticator();
		Authenticator.setDefault(myAuth);
	}

	public static Authenticator getTokenAuthenticator() {
		final PasswordAuthentication passwordAuthentication;
		try {
			passwordAuthentication = getAuthentication();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		final Authenticator myAuth = getConstantAuthenticator(passwordAuthentication);
		return myAuth;
	}

	private static PasswordAuthentication getAuthentication() throws IOException {
		final Authentication authentication = readAuthentication();
		final PasswordAuthentication passwordAuthentication;
		if (authentication.getUserName().isEmpty())
			throw new IllegalStateException("username is missing");
		if (authentication.getPassword().isEmpty())
			throw new IllegalStateException("password is missing for username " + authentication.getUserName());
		passwordAuthentication = new PasswordAuthentication(authentication.getUserName().get(),
				authentication.getPassword().get().toCharArray());
		return passwordAuthentication;
	}

	private static Authentication readAuthentication() throws IOException {

		{
			final String tokenUserName = System.getenv("API_username");
			final String tokenPassword = System.getenv("API_password");
			if (tokenPassword != null && tokenUserName != null) {
				return Authentication.given(tokenUserName, tokenPassword);
			}
		}
		{
			final String tokenUserName = System.getProperty("API_username");
			final String tokenPassword = System.getProperty("API_password");
			if (tokenPassword != null && tokenUserName != null) {
				return Authentication.given(tokenUserName, tokenPassword);
			}
		}
		final Path path = Paths.get("API_login.txt");
		if (!Files.exists(path)) {
			return Authentication.empty();
		}
		if (Files.readString(path).isEmpty()) {
			return Authentication.empty();
		}
		final List<String> lines = new ArrayList<String>(Files.readAllLines(path, StandardCharsets.UTF_8));
		{
			if (lines.size() == 1) {
				return Authentication.onlyUserName(lines.get(0).replaceAll("\n", ""));
			} else if (lines.size() == 2) {
				String userName = lines.get(0).replaceAll("\n", "");
				String password = lines.get(1).replaceAll("\n", "");
				return Authentication.given(userName, password);
			} else
				throw new IllegalStateException("File API_login.txt is not written correctly");
		}
	}

	private static Authenticator getConstantAuthenticator(PasswordAuthentication passwordAuthentication) {
		final Authenticator myAuth = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return passwordAuthentication;
			}
		};
		return myAuth;
	}

}
