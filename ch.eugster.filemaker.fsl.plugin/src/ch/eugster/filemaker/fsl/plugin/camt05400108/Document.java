//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.20 um 07:56:34 AM CET 
//


package ch.eugster.filemaker.fsl.plugin.camt05400108;

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
 *         &lt;element name="BkToCstmrDbtCdtNtfctn" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.08}BankToCustomerDebitCreditNotificationV08"/>
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
    "bkToCstmrDbtCdtNtfctn"
})
public class Document {

    @XmlElement(name = "BkToCstmrDbtCdtNtfctn", required = true)
    protected BankToCustomerDebitCreditNotificationV08 bkToCstmrDbtCdtNtfctn;

    /**
     * Ruft den Wert der bkToCstmrDbtCdtNtfctn-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BankToCustomerDebitCreditNotificationV08 }
     *     
     */
    public BankToCustomerDebitCreditNotificationV08 getBkToCstmrDbtCdtNtfctn() {
        return bkToCstmrDbtCdtNtfctn;
    }

    /**
     * Legt den Wert der bkToCstmrDbtCdtNtfctn-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BankToCustomerDebitCreditNotificationV08 }
     *     
     */
    public void setBkToCstmrDbtCdtNtfctn(BankToCustomerDebitCreditNotificationV08 value) {
        this.bkToCstmrDbtCdtNtfctn = value;
    }

}
