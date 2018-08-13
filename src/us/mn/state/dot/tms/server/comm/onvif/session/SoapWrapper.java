package us.mn.state.dot.tms.server.comm.onvif.session;

import org.w3c.dom.Document;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.SoapWrapperException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * A static encapsulation of the soap formatting and transmission logic for soap
 * requests
 *
 * @author Wesley Skillern (Southwest Research Institue)
 */
public class SoapWrapper {

	private static final DebugLog SOAP_LOG = new DebugLog("soap");

	/**
	 * Used for getting a response from a session web service using this
	 * request and a response object. Blocks until response!
	 *
	 * @param uri this soap message will be sent to the uri
	 * @param targetClass for formatting purposes
	 * @return the response initialized object
	 * @throws IOException error during soap transmission
	 */
	public static Object callWebService(
		Object requestObject, String uri, Class<?> targetClass,
		WSUsernameToken auth) throws SoapWrapperException
	{
		try {
			SOAPMessage request =
				createSoapRequest(requestObject);
			addAuthHeader(request, auth);
			SOAPMessage m = callWebService(request, uri);

			if (m.getSOAPBody().hasFault()) {

				log("SOAP Fault", m);
				throw new IOException(
					m.getSOAPBody().getFault()
						.getFaultString());
			}
			return convertToObject(m, targetClass);
		} catch (NoSuchAlgorithmException
			| JAXBException
			| ParserConfigurationException
			| IOException e) {
			throw new SoapWrapperException(e);
		} catch (SOAPException e) {
			SoapWrapperException wrapped =
				new SoapWrapperException(e);
			int httpStatus = parseHttpErrStatus(e);
			wrapped.setHttpErr(httpStatus);
			throw wrapped;
		}
	}

	/**
	 * Used for getting a response from a session web service using this
	 * request and a response object. Blocks until response! Does not
	 * format
	 * response.
	 *
	 * @param uri this soap message will be sent to the uri
	 * @return the response initialized object
	 * @throws SOAPException soap error (possibly client request problem)
	 */
	private static SOAPMessage callWebService(
		SOAPMessage request,
		String uri)
		throws SOAPException
	{
		// create connection
		SOAPConnectionFactory soapConnectionFactory =
			SOAPConnectionFactory.newInstance();
		SOAPConnection soapConnection =
			soapConnectionFactory.createConnection();
		// send session msg

		request.saveChanges();

		log("Request SOAP message to " + uri, request);

		SOAPMessage soapResponse =
			soapConnection.call(request, uri);

		log("Response SOAP message from " + uri, soapResponse);

		return soapResponse;
	}

	/**
	 * @param requestObject the object from which the message format will
	 * 	be derived
	 * @return a new instance of a soap request document
	 */
	private static SOAPMessage createSoapRequest(Object requestObject)
		throws SOAPException, JAXBException,
		ParserConfigurationException
	{
		MessageFactory messageFactory =
			MessageFactory
				.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
		SOAPMessage soapMessage = messageFactory.createMessage();
		soapMessage
			.setProperty(SOAPMessage.WRITE_XML_DECLARATION,
				"true");
		Document document = convertToXml(requestObject);
		createSoapBody(soapMessage, document);

		return soapMessage;
	}

	/**
	 * @param soapMessage the soap object to which the body will be added
	 * @param document the body content to add
	 * @throws SOAPException malformed soap
	 */
	private static void createSoapBody(
		SOAPMessage soapMessage,
		Document document)
		throws SOAPException
	{
		SOAPPart soapPart = soapMessage.getSOAPPart();
		SOAPEnvelope envelope = soapPart.getEnvelope();
		SOAPBody soapBody = envelope.getBody();
		soapBody.addDocument(document);
	}

	/**
	 * A WSUsername specific implementation of an authentication header
	 *
	 * @param tok the token for the session
	 * @throws SOAPException malformed soap
	 * @throws NoSuchAlgorithmException cannot generate password digest
	 */
	private static void addAuthHeader(
		SOAPMessage request,
		WSUsernameToken tok)
		throws NoSuchAlgorithmException, SOAPException
	{
		SOAPPart soapPart = request.getSOAPPart();
		SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
		SOAPHeader soapHeader = request.getSOAPHeader();
		soapEnvelope.addNamespaceDeclaration("wsse",
			"http://docs.oasis-open" +
				".org/wss/2004/01/oasis-200401-wss" +
				"-wssecurity" +
				"-secext-1.0.xsd");
		soapEnvelope.addNamespaceDeclaration("wsu",
			"http://docs.oasis-open" +
				".org/wss/2004/01/oasis-200401-wss" +
				"-wssecurity" +
				"-utility-1.0.xsd");

		SOAPElement securityElement =
			soapHeader.addChildElement("Security", "wsse");

		SOAPElement usernameTokenElement = securityElement
			.addChildElement("UsernameToken", "wsse");
		SOAPElement usernameElement = usernameTokenElement
			.addChildElement("Username", "wsse");
		usernameElement.setTextContent(tok.getUsername());
		SOAPElement passwordElement = usernameTokenElement
			.addChildElement("Password", "wsse");
		passwordElement.setAttribute("Type",
			"http://docs.oasis-open" +
				".org/wss/2004/01/oasis-200401-wss-username" +
				"-token-profile-1.0#PasswordDigest");
		passwordElement.setTextContent(tok.getPasswordDigest());
		SOAPElement nonceElement =
			usernameTokenElement.addChildElement("Nonce", "wsse");
		nonceElement.setTextContent(tok.getEncodedNonce());
		SOAPElement createdElement =
			usernameTokenElement.addChildElement("Created", "wsu");
		createdElement.setTextContent(tok.getUTCTime());
	}

	/**
	 * @param xmlObject the object to convert to xml
	 * @return an xml representation of the instance of Object
	 * @throws JAXBException a marshaller could not be created for the
	 * 	object
	 * @throws ParserConfigurationException an xml document could not be
	 * 	created for the object
	 */
	private static Document convertToXml(Object xmlObject)
		throws JAXBException, ParserConfigurationException
	{
		JAXBContext jaxbContext =
			JAXBContext.newInstance(xmlObject.getClass());
		Marshaller marshaller = jaxbContext.createMarshaller();
		Document document = DocumentBuilderFactory.newInstance()
			.newDocumentBuilder().newDocument();
		marshaller.marshal(xmlObject, document);
		return document;
	}

	/**
	 * @param soapMessage the message to convert
	 * @param targetClass the returned object type to which the soapMessage
	 * 	will be converted
	 * @return the object form of soapMessage
	 * @throws JAXBException an unmarshaller instance could not be created
	 * 	for the obect
	 * @throws SOAPException if the SOAP Body does not exist or cannot be
	 * 	retrieved
	 */
	private static Object convertToObject(
		SOAPMessage soapMessage, Class<?> targetClass)
		throws JAXBException, SOAPException
	{
		Unmarshaller unmarshaller =
			JAXBContext.newInstance(targetClass)
				.createUnmarshaller();
		return unmarshaller.unmarshal(
			soapMessage.getSOAPBody().extractContentAsDocument());
	}


	private static int parseHttpErrStatus(SOAPException e)
		throws SoapWrapperException
	{
		if (e.getCause() == null
			|| e.getCause().getMessage() == null)
			throw new SoapWrapperException(e);
		String msg = e.getCause().getMessage();
		String strB4Stats = "Bad response: (";
		if (!msg.contains(strB4Stats))
			throw new SoapWrapperException(e);
		int startI = msg.indexOf(strB4Stats);
		if (msg.length() < strB4Stats.length() + 3)
			throw new SoapWrapperException(e);
		String statusStr = msg.substring(startI + strB4Stats.length(),
			startI + strB4Stats.length() + 3);
		int status;
		try {
			status = Integer.parseInt(statusStr);
		} catch (NumberFormatException e1) {
			throw new SoapWrapperException(e);
		}
		return status;
	}

	/**
	 * Formats context information and msg and writes to soap log file.
	 */
	private static void log(String context, SOAPMessage msg) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			msg.writeTo(out);
		} catch (Exception e) {
			SOAP_LOG.log(
				"Could not convert SOAP message to string for "
					+ context + "\n" + e);
		}
		SOAP_LOG.log(new String(out.toByteArray()));
	}
}
