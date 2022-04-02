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
 * <p>Java-Klasse für AmountAndCurrencyExchangeDetails1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AmountAndCurrencyExchangeDetails1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Amt" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CurrencyAndAmount"/>
 *         &lt;element name="CcyXchg" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CurrencyExchange3" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AmountAndCurrencyExchangeDetails1", propOrder = {
    "amt",
    "ccyXchg"
})
public class AmountAndCurrencyExchangeDetails1 {

    @XmlElement(name = "Amt", required = true)
    protected CurrencyAndAmount amt;
    @XmlElement(name = "CcyXchg")
    protected CurrencyExchange3 ccyXchg;

    /**
     * Ruft den Wert der amt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public CurrencyAndAmount getAmt() {
        return amt;
    }

    /**
     * Legt den Wert der amt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public void setAmt(CurrencyAndAmount value) {
        this.amt = value;
    }

    /**
     * Ruft den Wert der ccyXchg-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyExchange3 }
     *     
     */
    public CurrencyExchange3 getCcyXchg() {
        return ccyXchg;
    }

    /**
     * Legt den Wert der ccyXchg-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyExchange3 }
     *     
     */
    public void setCcyXchg(CurrencyExchange3 value) {
        this.ccyXchg = value;
    }

}
