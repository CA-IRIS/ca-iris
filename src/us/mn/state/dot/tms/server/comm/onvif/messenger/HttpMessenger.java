package us.mn.state.dot.tms.server.comm.onvif.messenger;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.Messenger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This Messenger implementation is responsible for http connection
 * operations
 */
public class HttpMessenger extends Messenger {
    private String uri;
    private HttpURLConnection httpURLConnection = null;
    private int timeout = 5000;
    private boolean uriChanged = true;

    public HttpMessenger(String uri) {
        input = null;
        output = null;
        this.uri = uri;
    }

    public void setUri(String uri) {
        if (!this.uri.matches(uri)) {
            this.uri = uri;
            uriChanged = true;
        }
    }

    @Override
    public void open() throws IOException {
        if (uri == null || uri.isEmpty())
            throw new IOException("uri not set");
        URL url = new URL(uri);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("SOAPAction", uri);
        try {
            output = httpURLConnection.getOutputStream();
//            input = httpURLConnection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (input != null) {
            try {
                input.close();
            }
            catch (IOException e) {
                // Ignore
            }
        }
        if (output != null) {
            try {
                output.close();
            }
            catch (IOException e) {
                // Ignore
            }
        }
        input = null;
        output = null;
        httpURLConnection = null;
    }

    /**
     * If the uri has changed, the output stream is closed and opened again, so
     * that the outputstream corresponds to the current uri
     * Assumption: getOutputStream will always be called before getInputStream!
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (uriChanged) {
            close();
            open();
            uriChanged = false;
        }
        return output;
    }

    @Override
    public InputStream getInputStream(String path, ControllerImpl c)
            throws IOException {
        return input; // we won't be needing any response
    }

    @Override
    public void setTimeout(int t) throws IOException {
        timeout = t;
        if(httpURLConnection != null) {
            httpURLConnection.setConnectTimeout(timeout);
            httpURLConnection.setReadTimeout(timeout);
        }
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    public String getUri() {
        return uri;
    }
}
