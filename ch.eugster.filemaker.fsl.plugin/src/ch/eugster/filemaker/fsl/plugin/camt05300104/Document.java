//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.19 um 03:10:03 PM CET 
//


package ch.eugster.filemaker.fsl.plugin.camt05300104;

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
 *         &lt;element name="BkToCstmrStmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.053.001.04}BankToCustomerStatementV04"/>
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
    "bkToCstmrStmt"
})
public class Document {

    @XmlElement(name = "BkToCstmrStmt", required = true)
    protected BankToCustomerStatementV04 bkToCstmrStmt;

    /**
     * Ruft den Wert der bkToCstmrStmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BankToCustomerStatementV04 }
     *     
     */
    public BankToCustomerStatementV04 getBkToCstmrStmt() {
        return bkToCstmrStmt;
    }

    /**
     * Legt den Wert der bkToCstmrStmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BankToCustomerStatementV04 }
     *     
     */
    public void setBkToCstmrStmt(BankToCustomerStatementV04 value) {
        this.bkToCstmrStmt = value;
    }

}
