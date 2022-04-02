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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ActiveOrHistoricCurrencyAndAmountRange2 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ActiveOrHistoricCurrencyAndAmountRange2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Amt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.08}ImpliedCurrencyAmountRange1Choice"/>
 *         &lt;element name="CdtDbtInd" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.08}CreditDebitCode" minOccurs="0"/>
 *         &lt;element name="Ccy" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.08}ActiveOrHistoricCurrencyCode"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActiveOrHistoricCurrencyAndAmountRange2", propOrder = {
    "amt",
    "cdtDbtInd",
    "ccy"
})
public class ActiveOrHistoricCurrencyAndAmountRange2 {

    @XmlElement(name = "Amt", required = true)
    protected ImpliedCurrencyAmountRange1Choice amt;
    @XmlElement(name = "CdtDbtInd")
    @XmlSchemaType(name = "string")
    protected CreditDebitCode cdtDbtInd;
    @XmlElement(name = "Ccy", required = true)
    protected String ccy;

    /**
     * Ruft den Wert der amt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ImpliedCurrencyAmountRange1Choice }
     *     
     */
    public ImpliedCurrencyAmountRange1Choice getAmt() {
        return amt;
    }

    /**
     * Legt den Wert der amt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ImpliedCurrencyAmountRange1Choice }
     *     
     */
    public void setAmt(ImpliedCurrencyAmountRange1Choice value) {
        this.amt = value;
    }

    /**
     * Ruft den Wert der cdtDbtInd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CreditDebitCode }
     *     
     */
    public CreditDebitCode getCdtDbtInd() {
        return cdtDbtInd;
    }

    /**
     * Legt den Wert der cdtDbtInd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditDebitCode }
     *     
     */
    public void setCdtDbtInd(CreditDebitCode value) {
        this.cdtDbtInd = value;
    }

    /**
     * Ruft den Wert der ccy-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * Legt den Wert der ccy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcy(String value) {
        this.ccy = value;
    }

}
