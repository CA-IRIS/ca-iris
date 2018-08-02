package us.mn.state.dot.tms.server.comm.onvif.session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Random;

public class WSUsernameToken {

	private String username;
	private String nonce;
	private String password;

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private String date;

	public WSUsernameToken(String username, String password) {
		if (username == null || username.isEmpty()
			|| password == null || password.isEmpty())
			throw new IllegalArgumentException(
				"username and password may not be null");
		this.username = username;
		this.password = password;
	}

	/**
	 * Note: This will reset the date (timestamp) for this class, hence it
	 * should be called before using getUTCTime.
	 *
	 * @return B64Encode(SHA1 ( B64Decode ( Nonce) + Date + Password ) )
	 */
	String getPasswordDigest() throws NoSuchAlgorithmException {
		resetTime();
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			System.err.println(
				"ONVIF-required SHA-1 digest algorithm is " +
					"not" +
					" " +
					"implemented");
			throw e;
		}
		messageDigest.update((getNonce() + getUTCTime() + password)
			.getBytes());

		return Base64.getEncoder()
			.encodeToString(messageDigest.digest());
	}

	String getEncodedNonce() {
		if (nonce == null) {
			nonce = createNonce();
		}
		return Base64.getEncoder().encodeToString(nonce.getBytes());
	}

	protected String getUTCTime() {
		if (date == null) {
			date = Instant.now().toString();
		}
		return date;
	}

	private void resetTime() {
		date = null;
	}

	public String getUsername() {
		return username;
	}

	private String createNonce() {
		Random generator = new Random();
		return "" + generator.nextInt();
	}

	private String getNonce() {
		if (nonce == null) {
			nonce = createNonce();
		}
		return nonce;
	}
}
