//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.19 um 03:09:45 PM CET 
//


package ch.eugster.filemaker.fsl.plugin.camt05400104;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PriceRateOrAmountChoice complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PriceRateOrAmountChoice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="Rate" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.04}PercentageRate"/>
 *         &lt;element name="Amt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.04}ActiveOrHistoricCurrencyAnd13DecimalAmount"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PriceRateOrAmountChoice", propOrder = {
    "rate",
    "amt"
})
public class PriceRateOrAmountChoice {

    @XmlElement(name = "Rate")
    protected BigDecimal rate;
    @XmlElement(name = "Amt")
    protected ActiveOrHistoricCurrencyAnd13DecimalAmount amt;

    /**
     * Ruft den Wert der rate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * Legt den Wert der rate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setRate(BigDecimal value) {
        this.rate = value;
    }

    /**
     * Ruft den Wert der amt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ActiveOrHistoricCurrencyAnd13DecimalAmount }
     *     
     */
    public ActiveOrHistoricCurrencyAnd13DecimalAmount getAmt() {
        return amt;
    }

    /**
     * Legt den Wert der amt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ActiveOrHistoricCurrencyAnd13DecimalAmount }
     *     
     */
    public void setAmt(ActiveOrHistoricCurrencyAnd13DecimalAmount value) {
        this.amt = value;
    }

}
