//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.05.03 at 03:55:56 PM CEST 
//


package org.fiteagle.north.sfa.aaa.jaxbClasses;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="fd_name" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="fd_weight" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="violatable">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="true"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="global_operator">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="OnceOnly"/>
 *             &lt;enumeration value="FirstFree"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="local_operator">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="+"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "fd")
public class Fd {

    @XmlAttribute(name = "fd_name", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String fdName;
    @XmlAttribute(name = "fd_weight", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String fdWeight;
    @XmlAttribute(name = "violatable")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String violatable;
    @XmlAttribute(name = "global_operator")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String globalOperator;
    @XmlAttribute(name = "local_operator")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String localOperator;

    /**
     * Gets the value of the fdName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFdName() {
        return fdName;
    }

    /**
     * Sets the value of the fdName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFdName(String value) {
        this.fdName = value;
    }

    /**
     * Gets the value of the fdWeight property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFdWeight() {
        return fdWeight;
    }

    /**
     * Sets the value of the fdWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFdWeight(String value) {
        this.fdWeight = value;
    }

    /**
     * Gets the value of the violatable property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getViolatable() {
        return violatable;
    }

    /**
     * Sets the value of the violatable property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setViolatable(String value) {
        this.violatable = value;
    }

    /**
     * Gets the value of the globalOperator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGlobalOperator() {
        return globalOperator;
    }

    /**
     * Sets the value of the globalOperator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGlobalOperator(String value) {
        this.globalOperator = value;
    }

    /**
     * Gets the value of the localOperator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocalOperator() {
        return localOperator;
    }

    /**
     * Sets the value of the localOperator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocalOperator(String value) {
        this.localOperator = value;
    }

}
