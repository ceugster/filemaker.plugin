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
 * <p>Java-Klasse für ReferredDocumentInformation1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ReferredDocumentInformation1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RfrdDocTp" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}ReferredDocumentType1" minOccurs="0"/>
 *         &lt;element name="RfrdDocNb" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}Max35Text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferredDocumentInformation1", propOrder = {
    "rfrdDocTp",
    "rfrdDocNb"
})
public class ReferredDocumentInformation1 {

    @XmlElement(name = "RfrdDocTp")
    protected ReferredDocumentType1 rfrdDocTp;
    @XmlElement(name = "RfrdDocNb")
    protected String rfrdDocNb;

    /**
     * Ruft den Wert der rfrdDocTp-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ReferredDocumentType1 }
     *     
     */
    public ReferredDocumentType1 getRfrdDocTp() {
        return rfrdDocTp;
    }

    /**
     * Legt den Wert der rfrdDocTp-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferredDocumentType1 }
     *     
     */
    public void setRfrdDocTp(ReferredDocumentType1 value) {
        this.rfrdDocTp = value;
    }

    /**
     * Ruft den Wert der rfrdDocNb-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRfrdDocNb() {
        return rfrdDocNb;
    }

    /**
     * Legt den Wert der rfrdDocNb-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRfrdDocNb(String value) {
        this.rfrdDocNb = value;
    }

}
