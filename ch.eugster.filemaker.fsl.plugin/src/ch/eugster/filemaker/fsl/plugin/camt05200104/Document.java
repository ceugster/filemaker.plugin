//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.20 um 07:57:24 AM CET 
//


package ch.eugster.filemaker.fsl.plugin.camt05200104;

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
 *         &lt;element name="BkToCstmrAcctRpt" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.04}BankToCustomerAccountReportV04"/>
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
    "bkToCstmrAcctRpt"
})
public class Document {

    @XmlElement(name = "BkToCstmrAcctRpt", required = true)
    protected BankToCustomerAccountReportV04 bkToCstmrAcctRpt;

    /**
     * Ruft den Wert der bkToCstmrAcctRpt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BankToCustomerAccountReportV04 }
     *     
     */
    public BankToCustomerAccountReportV04 getBkToCstmrAcctRpt() {
        return bkToCstmrAcctRpt;
    }

    /**
     * Legt den Wert der bkToCstmrAcctRpt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BankToCustomerAccountReportV04 }
     *     
     */
    public void setBkToCstmrAcctRpt(BankToCustomerAccountReportV04 value) {
        this.bkToCstmrAcctRpt = value;
    }

}
