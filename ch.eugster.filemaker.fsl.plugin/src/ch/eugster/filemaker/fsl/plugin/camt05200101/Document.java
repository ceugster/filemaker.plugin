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
 * <p>Java-Klasse für Document complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Document">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BkToCstmrAcctRptV01" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}BankToCustomerAccountReportV01"/>
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
    "bkToCstmrAcctRptV01"
})
public class Document {

    @XmlElement(name = "BkToCstmrAcctRptV01", required = true)
    protected BankToCustomerAccountReportV01 bkToCstmrAcctRptV01;

    /**
     * Ruft den Wert der bkToCstmrAcctRptV01-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BankToCustomerAccountReportV01 }
     *     
     */
    public BankToCustomerAccountReportV01 getBkToCstmrAcctRptV01() {
        return bkToCstmrAcctRptV01;
    }

    /**
     * Legt den Wert der bkToCstmrAcctRptV01-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BankToCustomerAccountReportV01 }
     *     
     */
    public void setBkToCstmrAcctRptV01(BankToCustomerAccountReportV01 value) {
        this.bkToCstmrAcctRptV01 = value;
    }

}
