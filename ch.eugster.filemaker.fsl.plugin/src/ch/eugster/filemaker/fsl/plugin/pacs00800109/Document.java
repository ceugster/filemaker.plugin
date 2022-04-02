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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Document complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Document">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FIToFICstmrCdtTrf" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}FIToFICustomerCreditTransferV09"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Document", propOrder = {
    "fiToFICstmrCdtTrf"
})
public class Document {

    @XmlElement(name = "FIToFICstmrCdtTrf", required = true)
    protected FIToFICustomerCreditTransferV09 fiToFICstmrCdtTrf;

    /**
     * Ruft den Wert der fiToFICstmrCdtTrf-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FIToFICustomerCreditTransferV09 }
     *     
     */
    public FIToFICustomerCreditTransferV09 getFIToFICstmrCdtTrf() {
        return fiToFICstmrCdtTrf;
    }

    /**
     * Legt den Wert der fiToFICstmrCdtTrf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FIToFICustomerCreditTransferV09 }
     *     
     */
    public void setFIToFICstmrCdtTrf(FIToFICustomerCreditTransferV09 value) {
        this.fiToFICstmrCdtTrf = value;
    }

}
