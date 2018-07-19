package us.mn.state.dot.tms.server.comm.onvif.soap;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;

public abstract class WSUsernameToken {

    private String username;
    private String nonce;
    private String password;
    private String date;

    public WSUsernameToken(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * @return B64Encode( SHA1( B64Decode( Nonce ) + Date + Password ) )
     */
    public String getPasswordDigest() throws NoSuchAlgorithmException {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("ONVIF-required SHA-1 digest algorithm is not implemented");
            throw e;
        }
        messageDigest.update((getNonce() + getUTCTime() + password).getBytes());
        return Base64.getEncoder().encodeToString(messageDigest.digest());
    }

    public String getEncodedNonce() {
        if (nonce == null) {
            nonce = createNonce();
        }
        return Base64.getEncoder().encodeToString(nonce.getBytes());
    }

    public String getUTCTime() {
        if (date == null) {
            date = Instant.now().toString();
        }
        return date;
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
