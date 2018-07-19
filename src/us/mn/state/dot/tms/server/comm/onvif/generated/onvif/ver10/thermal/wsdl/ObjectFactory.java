
package us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.thermal.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.thermal.wsdl package.
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Capabilities_QNAME = new QName("http://www.onvif.org/ver10/thermal/wsdl", "Capabilities");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: us.mn.state.dot.tms.server.comm.onvif.generated.onvif.ver10.thermal.wsdl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetServiceCapabilities }
     * 
     */
    public GetServiceCapabilities createGetServiceCapabilities() {
        return new GetServiceCapabilities();
    }

    /**
     * Create an instance of {@link GetServiceCapabilitiesResponse }
     * 
     */
    public GetServiceCapabilitiesResponse createGetServiceCapabilitiesResponse() {
        return new GetServiceCapabilitiesResponse();
    }

    /**
     * Create an instance of {@link Capabilities }
     * 
     */
    public Capabilities createCapabilities() {
        return new Capabilities();
    }

    /**
     * Create an instance of {@link GetConfigurationOptions }
     * 
     */
    public GetConfigurationOptions createGetConfigurationOptions() {
        return new GetConfigurationOptions();
    }

    /**
     * Create an instance of {@link GetConfigurationOptionsResponse }
     * 
     */
    public GetConfigurationOptionsResponse createGetConfigurationOptionsResponse() {
        return new GetConfigurationOptionsResponse();
    }

    /**
     * Create an instance of {@link ConfigurationOptions }
     * 
     */
    public ConfigurationOptions createConfigurationOptions() {
        return new ConfigurationOptions();
    }

    /**
     * Create an instance of {@link GetConfiguration }
     * 
     */
    public GetConfiguration createGetConfiguration() {
        return new GetConfiguration();
    }

    /**
     * Create an instance of {@link GetConfigurationResponse }
     * 
     */
    public GetConfigurationResponse createGetConfigurationResponse() {
        return new GetConfigurationResponse();
    }

    /**
     * Create an instance of {@link Configuration }
     * 
     */
    public Configuration createConfiguration() {
        return new Configuration();
    }

    /**
     * Create an instance of {@link GetConfigurations }
     * 
     */
    public GetConfigurations createGetConfigurations() {
        return new GetConfigurations();
    }

    /**
     * Create an instance of {@link GetConfigurationsResponse }
     * 
     */
    public GetConfigurationsResponse createGetConfigurationsResponse() {
        return new GetConfigurationsResponse();
    }

    /**
     * Create an instance of {@link Configurations }
     * 
     */
    public Configurations createConfigurations() {
        return new Configurations();
    }

    /**
     * Create an instance of {@link SetConfiguration }
     * 
     */
    public SetConfiguration createSetConfiguration() {
        return new SetConfiguration();
    }

    /**
     * Create an instance of {@link SetConfigurationResponse }
     * 
     */
    public SetConfigurationResponse createSetConfigurationResponse() {
        return new SetConfigurationResponse();
    }

    /**
     * Create an instance of {@link GetRadiometryConfigurationOptions }
     * 
     */
    public GetRadiometryConfigurationOptions createGetRadiometryConfigurationOptions() {
        return new GetRadiometryConfigurationOptions();
    }

    /**
     * Create an instance of {@link GetRadiometryConfigurationOptionsResponse }
     * 
     */
    public GetRadiometryConfigurationOptionsResponse createGetRadiometryConfigurationOptionsResponse() {
        return new GetRadiometryConfigurationOptionsResponse();
    }

    /**
     * Create an instance of {@link RadiometryConfigurationOptions }
     * 
     */
    public RadiometryConfigurationOptions createRadiometryConfigurationOptions() {
        return new RadiometryConfigurationOptions();
    }

    /**
     * Create an instance of {@link GetRadiometryConfiguration }
     * 
     */
    public GetRadiometryConfiguration createGetRadiometryConfiguration() {
        return new GetRadiometryConfiguration();
    }

    /**
     * Create an instance of {@link GetRadiometryConfigurationResponse }
     * 
     */
    public GetRadiometryConfigurationResponse createGetRadiometryConfigurationResponse() {
        return new GetRadiometryConfigurationResponse();
    }

    /**
     * Create an instance of {@link RadiometryConfiguration }
     * 
     */
    public RadiometryConfiguration createRadiometryConfiguration() {
        return new RadiometryConfiguration();
    }

    /**
     * Create an instance of {@link SetRadiometryConfiguration }
     * 
     */
    public SetRadiometryConfiguration createSetRadiometryConfiguration() {
        return new SetRadiometryConfiguration();
    }

    /**
     * Create an instance of {@link SetRadiometryConfigurationResponse }
     * 
     */
    public SetRadiometryConfigurationResponse createSetRadiometryConfigurationResponse() {
        return new SetRadiometryConfigurationResponse();
    }

    /**
     * Create an instance of {@link ColorPalette }
     * 
     */
    public ColorPalette createColorPalette() {
        return new ColorPalette();
    }

    /**
     * Create an instance of {@link NUCTable }
     * 
     */
    public NUCTable createNUCTable() {
        return new NUCTable();
    }

    /**
     * Create an instance of {@link Cooler }
     * 
     */
    public Cooler createCooler() {
        return new Cooler();
    }

    /**
     * Create an instance of {@link CoolerOptions }
     * 
     */
    public CoolerOptions createCoolerOptions() {
        return new CoolerOptions();
    }

    /**
     * Create an instance of {@link RadiometryGlobalParameters }
     * 
     */
    public RadiometryGlobalParameters createRadiometryGlobalParameters() {
        return new RadiometryGlobalParameters();
    }

    /**
     * Create an instance of {@link RadiometryGlobalParameterOptions }
     * 
     */
    public RadiometryGlobalParameterOptions createRadiometryGlobalParameterOptions() {
        return new RadiometryGlobalParameterOptions();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Capabilities }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.onvif.org/ver10/thermal/wsdl", name = "Capabilities")
    public JAXBElement<Capabilities> createCapabilities(Capabilities value) {
        return new JAXBElement<Capabilities>(_Capabilities_QNAME, Capabilities.class, null, value);
    }

}
