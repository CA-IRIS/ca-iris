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

public class SoapWrapper {

	private Object requestObject;
	private SOAPMessage soapRequest;

	public SoapWrapper(Object requestObject)
		throws SOAPException, JAXBException,
		ParserConfigurationException
	{
		this.requestObject = requestObject;
		soapRequest = createSoapRequest();
	}

	/**
	 * use this constructor if you want an auth header
	 */
	public SoapWrapper(Object requestObject, WSUsernameToken auth)
		throws SOAPException, JAXBException,
		ParserConfigurationException, NoSuchAlgorithmException
	{
		this(requestObject);
		addAuthHeader(auth);
	}

	public SOAPMessage callSoapWebService(String uri)
		throws IOException, SOAPException, JAXBException
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
	 * request and response objects for debugging purposes. Blocks until
	 * response!
	 */
	public Object callSoapWebService(String uri, Object responseObject)
		throws IOException, SOAPException, JAXBException
	{
		return convertToObject(callSoapWebService(uri),
			responseObject);
	}

	private SOAPMessage createSoapRequest()
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
	 */
	private void addAuthHeader(WSUsernameToken tok)
		throws SOAPException, NoSuchAlgorithmException
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
	 * @param xmlObject the returned object type to which the soapMessage
	 * will be converted
	 * @return
	 * @throws JAXBException
	 * @throws SOAPException
	 */
	private static Object convertToObject(
		SOAPMessage soapMessage, Object xmlObject)
		throws JAXBException, SOAPException
	{
		Unmarshaller unmarshaller =
			JAXBContext.newInstance(xmlObject.getClass())
				.createUnmarshaller();
		return unmarshaller.unmarshal(
			soapMessage.getSOAPBody().extractContentAsDocument());
	}
}
