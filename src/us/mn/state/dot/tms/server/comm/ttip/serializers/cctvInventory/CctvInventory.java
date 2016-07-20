/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2016  Minnesota Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */


package us.mn.state.dot.tms.server.comm.ttip.serializers.cctvInventory;

import us.mn.state.dot.tms.server.comm.ttip.serializers.common.GeoLocationPoint;
import us.mn.state.dot.tms.server.comm.ttip.serializers.common.TimeStamp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}organization-information"/>
 *         &lt;element ref="{}device-id"/>
 *         &lt;element ref="{}device-name"/>
 *         &lt;element ref="{}location"/>
 *         &lt;element ref="{}cctv-image"/>
 *         &lt;element ref="{}cctv-url"/>
 *         &lt;element ref="{}cctv-other"/>
 *         &lt;element ref="{}node-id"/>
 *         &lt;element ref="{}route-designator"/>
 *         &lt;element ref="{}last-update-time"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Dan Rossiter
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "organizationInformation",
    "deviceId",
    "deviceName",
    "location",
    "cctvImage",
    "cctvUrl",
    "cctvOther",
    "nodeId",
    "routeDesignator",
    "lastUpdateTime"
})
@XmlRootElement(name = "cCTVInventory")
public class CctvInventory {

    @XmlElement(name = "organization-information", required = true)
    protected OrganizationInformation organizationInformation;
    @XmlElement(name = "device-id")
    protected short deviceId;
    @XmlElement(name = "device-name", required = true)
    protected String deviceName;
    @XmlElement(name = "location", required = true)
    protected GeoLocationPoint location;
    @XmlElement(name = "cctv-image", required = true)
    protected String cctvImage;
    @XmlElement(name = "cctv-url", required = true)
    protected String cctvUrl;
    @XmlElement(name = "cctv-other", required = true)
    protected String cctvOther;
    @XmlElement(name = "node-id", required = true)
    protected String nodeId;
    @XmlElement(name = "route-designator", required = true)
    protected RouteDesignator routeDesignator;
    @XmlElement(name = "last-update-time", required = true)
    protected TimeStamp lastUpdateTime;

    /**
     * Gets the value of the organizationInformation property.
     * 
     * @return
     *     possible object is
     *     {@link OrganizationInformation }
     *     
     */
    public OrganizationInformation getOrganizationInformation() {
        return organizationInformation;
    }

    /**
     * Sets the value of the organizationInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganizationInformation }
     *     
     */
    public void setOrganizationInformation(OrganizationInformation value) {
        this.organizationInformation = value;
    }

    /**
     * Gets the value of the deviceId property.
     * 
     */
    public short getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the value of the deviceId property.
     * 
     */
    public void setDeviceId(short value) {
        this.deviceId = value;
    }

    /**
     * Gets the value of the deviceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Sets the value of the deviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeviceName(String value) {
        this.deviceName = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link GeoLocationPoint }
     *     
     */
    public GeoLocationPoint getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeoLocationPoint }
     *     
     */
    public void setLocation(GeoLocationPoint value) {
        this.location = value;
    }

    /**
     * Gets the value of the cctvImage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCctvImage() {
        return cctvImage;
    }

    /**
     * Sets the value of the cctvImage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCctvImage(String value) {
        this.cctvImage = value;
    }

    /**
     * Gets the value of the cctvUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCctvUrl() {
        return cctvUrl;
    }

    /**
     * Sets the value of the cctvUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCctvUrl(String value) {
        this.cctvUrl = value;
    }

    /**
     * Gets the value of the cctvOther property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCctvOther() {
        return cctvOther;
    }

    /**
     * Sets the value of the cctvOther property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCctvOther(String value) {
        this.cctvOther = value;
    }

    /**
     * Gets the value of the nodeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * Sets the value of the nodeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNodeId(String value) {
        this.nodeId = value;
    }

    /**
     * Gets the value of the routeDesignator property.
     * 
     * @return
     *     possible object is
     *     {@link RouteDesignator }
     *     
     */
    public RouteDesignator getRouteDesignator() {
        return routeDesignator;
    }

    /**
     * Sets the value of the routeDesignator property.
     * 
     * @param value
     *     allowed object is
     *     {@link RouteDesignator }
     *     
     */
    public void setRouteDesignator(RouteDesignator value) {
        this.routeDesignator = value;
    }

    /**
     * Gets the value of the lastUpdateTime property.
     * 
     * @return
     *     possible object is
     *     {@link TimeStamp }
     *     
     */
    public TimeStamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * Sets the value of the lastUpdateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeStamp }
     *     
     */
    public void setLastUpdateTime(TimeStamp value) {
        this.lastUpdateTime = value;
    }

}
