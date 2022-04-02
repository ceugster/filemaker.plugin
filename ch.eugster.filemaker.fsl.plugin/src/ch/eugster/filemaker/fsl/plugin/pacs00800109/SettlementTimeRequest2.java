//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.20 um 07:55:49 AM CET 
//


package ch.eugster.filemaker.fsl.plugin.pacs00800109;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java-Klasse für SettlementTimeRequest2 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="SettlementTimeRequest2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CLSTm" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}ISOTime" minOccurs="0"/>
 *         &lt;element name="TillTm" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}ISOTime" minOccurs="0"/>
 *         &lt;element name="FrTm" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}ISOTime" minOccurs="0"/>
 *         &lt;element name="RjctTm" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}ISOTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SettlementTimeRequest2", propOrder = {
    "clsTm",
    "tillTm",
    "frTm",
    "rjctTm"
})
public class SettlementTimeRequest2 {

    @XmlElement(name = "CLSTm")
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar clsTm;
    @XmlElement(name = "TillTm")
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar tillTm;
    @XmlElement(name = "FrTm")
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar frTm;
    @XmlElement(name = "RjctTm")
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar rjctTm;

    /**
     * Ruft den Wert der clsTm-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCLSTm() {
        return clsTm;
    }

    /**
     * Legt den Wert der clsTm-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCLSTm(XMLGregorianCalendar value) {
        this.clsTm = value;
    }

    /**
     * Ruft den Wert der tillTm-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTillTm() {
        return tillTm;
    }

    /**
     * Legt den Wert der tillTm-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTillTm(XMLGregorianCalendar value) {
        this.tillTm = value;
    }

    /**
     * Ruft den Wert der frTm-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFrTm() {
        return frTm;
    }

    /**
     * Legt den Wert der frTm-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFrTm(XMLGregorianCalendar value) {
        this.frTm = value;
    }

    /**
     * Ruft den Wert der rjctTm-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRjctTm() {
        return rjctTm;
    }

    /**
     * Legt den Wert der rjctTm-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRjctTm(XMLGregorianCalendar value) {
        this.rjctTm = value;
    }

}
