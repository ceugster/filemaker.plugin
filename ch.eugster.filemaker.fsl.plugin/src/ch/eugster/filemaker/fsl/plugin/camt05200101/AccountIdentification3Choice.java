//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.20 um 07:57:54 AM CET 
//


package ch.eugster.filemaker.fsl.plugin.camt05200101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für AccountIdentification3Choice complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AccountIdentification3Choice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="IBAN" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}IBANIdentifier"/>
 *           &lt;element name="BBAN" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}BBANIdentifier"/>
 *           &lt;element name="UPIC" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}UPICIdentifier"/>
 *           &lt;element name="PrtryAcct" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}SimpleIdentificationInformation2"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccountIdentification3Choice", propOrder = {
    "iban",
    "bban",
    "upic",
    "prtryAcct"
})
public class AccountIdentification3Choice {

    @XmlElement(name = "IBAN")
    protected String iban;
    @XmlElement(name = "BBAN")
    protected String bban;
    @XmlElement(name = "UPIC")
    protected String upic;
    @XmlElement(name = "PrtryAcct")
    protected SimpleIdentificationInformation2 prtryAcct;

    /**
     * Ruft den Wert der iban-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIBAN() {
        return iban;
    }

    /**
     * Legt den Wert der iban-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIBAN(String value) {
        this.iban = value;
    }

    /**
     * Ruft den Wert der bban-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBBAN() {
        return bban;
    }

    /**
     * Legt den Wert der bban-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBBAN(String value) {
        this.bban = value;
    }

    /**
     * Ruft den Wert der upic-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUPIC() {
        return upic;
    }

    /**
     * Legt den Wert der upic-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUPIC(String value) {
        this.upic = value;
    }

    /**
     * Ruft den Wert der prtryAcct-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SimpleIdentificationInformation2 }
     *     
     */
    public SimpleIdentificationInformation2 getPrtryAcct() {
        return prtryAcct;
    }

    /**
     * Legt den Wert der prtryAcct-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SimpleIdentificationInformation2 }
     *     
     */
    public void setPrtryAcct(SimpleIdentificationInformation2 value) {
        this.prtryAcct = value;
    }

}
