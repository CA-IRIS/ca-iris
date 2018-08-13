package us.mn.state.dot.tms.server.comm.onvif.session;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.Messenger;
import us.mn.state.dot.tms.server.comm.onvif.OnvifPoller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * This is a dummy class (later we may make use of the output stream). Currently
 * the if output is null, then the property is ignored!
 *
 * @author Wesley Skillern (Southwest Research Institue)
 */
public class HttpMessenger extends Messenger {
	private String uri;
	private HttpURLConnection httpURLConnection = null;
	private int timeout = 5000;

	public void setUri(String uri) {
		if (!this.uri.equals(uri)) {
			this.uri = uri;
			close();
		}
	}

	@Override
	public void open() throws IOException {
		if (uri == null || uri.isEmpty())
			throw new IOException("uri not set");
		URL url = new URL(uri);
		HttpURLConnection httpURLConnection =
			(HttpURLConnection) url.openConnection();
		httpURLConnection.setDoInput(true);
		httpURLConnection.setDoOutput(true);
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setRequestProperty("SOAPAction", uri);
		try {
			output = httpURLConnection.getOutputStream();
//            input = httpURLConnection.getInputStream();
		} catch (Exception e) {
			OnvifPoller.log(e.getMessage());
		}
	}

	@Override
	public void close() {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
		if (output != null) {
			try {
				output.close();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
		input = null;
		output = null;
		httpURLConnection = null;
	}

	/**
	 * If the uri has changed, the output stream is closed and opened
	 * again,
	 * so that the outputstream corresponds to the current uri Assumption:
	 * getOutputStream will always be called before getInputStream!
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		if (output == null)
			open();
		return output;
	}

	@Override
	public InputStream getInputStream(String path, ControllerImpl c)
		throws IOException
	{
		return input; // we won't be needing any response
	}

	@Override
	public void setTimeout(int t) throws IOException {
		timeout = t;
		if (httpURLConnection != null) {
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
