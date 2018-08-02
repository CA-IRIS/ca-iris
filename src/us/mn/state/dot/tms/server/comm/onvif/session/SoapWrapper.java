package us.mn.state.dot.tms.server.comm.onvif.session;

import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * an encapsulation of the soap formatting and transmission logic for soap
 * requests
 *
 * @author Wesley Skillern (Southwest Research Institue)
 */
public class SoapWrapper {

	private SOAPMessage soapRequest;

	/**
	 * Create a soap request
	 *
	 * @param requestObject must be initialized with fields populated
	 * @throws SOAPException
	 * @throws JAXBException
	 * @throws ParserConfigurationException
	 */
	public SoapWrapper(Object requestObject)
		throws SOAPException, JAXBException,
		ParserConfigurationException
	{
		soapRequest = createSoapRequest(requestObject);
	}

	/**
	 * Used for getting a response from a session web service using this
	 * request and a response object. Blocks until response! Does not
	 * format
	 * response.
	 *
	 * @param uri this soap message will be sent to the uri
	 * @return the response initialized object
	 * @throws IOException error during soap transmission
	 * @throws SOAPException error during response formatting
	 */
	public SOAPMessage callSoapWebService(String uri)
		throws IOException, SOAPException
	{
		// create connection
		SOAPConnectionFactory soapConnectionFactory =
			SOAPConnectionFactory.newInstance();
		SOAPConnection soapConnection =
			soapConnectionFactory.createConnection();
		// send session msg

		soapRequest.saveChanges();

		// todo remove debug
		System.out.println("Request SOAP message: ");
		soapRequest.writeTo(System.out);
		System.out.println();

		System.out.println("To uri: " + uri);

		SOAPMessage soapResponse =
			soapConnection.call(soapRequest, uri);

		System.out.println("Response SOAP message: ");
		soapResponse.writeTo(System.out);
		System.out.println();

		return soapResponse;
	}

	/**
	 * Used for getting a response from a session web service using this
	 * request and a response object. Blocks until response!
	 *
	 * @param uri this soap message will be sent to the uri
	 * @param targetClass for formatting purposes
	 * @return the response initialized object
	 * @throws IOException error during soap transmission
	 * @throws SOAPException error during response formatting
	 * @throws JAXBException error during response formatting
	 * @throws NoSuchAlgorithmException trouble adding auth header
	 */
	public Object callSoapWebService(
		String uri, Class<?> targetClass,
		WSUsernameToken auth)
		throws IOException, SOAPException, JAXBException,
		NoSuchAlgorithmException
	{
		addAuthHeader(auth);
		return convertToObject(callSoapWebService(uri),
			targetClass);
	}

	/**
	 * @param requestObject
	 * @return a new instance of a soap request
	 * @throws SOAPException malformed soap
	 * @throws JAXBException cannot make document
	 * @throws ParserConfigurationException cannot convert soapMessage into
	 * 	document
	 */
	private SOAPMessage createSoapRequest(Object requestObject)
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
	private void createSoapBody(SOAPMessage soapMessage, Document document)
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
	private void addAuthHeader(WSUsernameToken tok)
		throws NoSuchAlgorithmException, SOAPException
	{
		SOAPPart soapPart = soapRequest.getSOAPPart();
		SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
		SOAPHeader soapHeader = soapRequest.getSOAPHeader();
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
	 * @return
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
}
