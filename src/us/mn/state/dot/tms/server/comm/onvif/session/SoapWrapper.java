package us.mn.state.dot.tms.server.comm.onvif.session;

import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import java.security.NoSuchAlgorithmException;

/**
 * A static encapsulation of the soap formatting and transmission logic for soap
 * requests
 *
 * @author Wesley Skillern (Southwest Research Institue)
 */
public class SoapWrapper {
	public static SOAPMessage newMessage(Object o)
		throws SOAPException, JAXBException,
		ParserConfigurationException
	{
		MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
		SOAPMessage soapMessage = messageFactory.createMessage();
		soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
		Document document = convertToXml(o);
		createSoapBody(document, soapMessage);
		return soapMessage;
	}

	/**
	 * @param document the body content to add
	 * @throws SOAPException malformed soap
	 */
	private static void createSoapBody(Document document, SOAPMessage msg) throws SOAPException {
		SOAPPart soapPart = msg.getSOAPPart();
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
	public static void addAuthHeader(SOAPMessage msg,
		WSUsernameToken tok)
		throws NoSuchAlgorithmException, SOAPException
	{
		SOAPPart soapPart = msg.getSOAPPart();
		SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
		SOAPHeader soapHeader = msg.getSOAPHeader();
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
	public static Document convertToXml(Object xmlObject)
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
	 * @param source the message to convert
	 * @param targetClass the returned object type to which the soapMessage
	 * 	will be converted
	 * @return the object form of soapMessage
	 * @throws JAXBException an unmarshaller instance could not be created
	 * 	for the obect
	 * @throws SOAPException if the SOAP Body does not exist or cannot be
	 * 	retrieved
	 */
	public static Object convertToObject(
		SOAPMessage source, Class<?> targetClass)
		throws JAXBException, SOAPException
	{
		Unmarshaller unmarshaller =
			JAXBContext.newInstance(targetClass)
				.createUnmarshaller();
		return unmarshaller.unmarshal(
			source.getSOAPBody().extractContentAsDocument());
	}
}
