package us.mn.state.dot.tms.server.comm.onvif.session;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import junit.framework.TestCase;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver10.device.wsdl.GetSystemDateAndTime;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public class SoapWrapperTest extends TestCase {

	private GetSystemDateAndTime obj = new GetSystemDateAndTime();

	private String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
		"<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\">" +
		"<env:Header/>" +
		"<env:Body>" +
		"<GetSystemDateAndTime xmlns=\"http://www.onvif.org/ver10/device/wsdl\"/>" +
		"</env:Body>" +
		"</env:Envelope>";

	public void testConvertToXml() throws Exception {
		SOAPMessage m = SoapWrapper.newMessage(obj);
		String actual = soap2Str(m);
		assertEquals(xml, actual);
	}

	public void testConvertToObject() throws Exception {
		SOAPMessage soapMessage = str2Soap(xml);
		Object o = SoapWrapper.convertToObject(soapMessage, obj.getClass());
		GetSystemDateAndTime actual = (GetSystemDateAndTime) o;
		assertEquals(obj.getClass(), actual.getClass());
	}

	private String soap2Str(SOAPMessage m) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		m.writeTo(out);
		return new String(out.toByteArray());
	}

	private SOAPMessage str2Soap(String xml) throws Exception {
		InputStream is = new ByteInputStream(
			xml.getBytes(Charset.forName("UTF-8")), xml.length());
		return MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL)
			.createMessage(new MimeHeaders(), is);
	}
}