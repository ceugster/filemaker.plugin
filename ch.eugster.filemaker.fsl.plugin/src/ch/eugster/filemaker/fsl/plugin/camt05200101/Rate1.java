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
 * <p>Java-Klasse für Rate1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Rate1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Rate" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}RateTypeChoice"/>
 *         &lt;element name="VldtyRg" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CurrencyAndAmountRange" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Rate1", propOrder = {
    "rate",
    "vldtyRg"
})
public class Rate1 {

    @XmlElement(name = "Rate", required = true)
    protected RateTypeChoice rate;
    @XmlElement(name = "VldtyRg")
    protected CurrencyAndAmountRange vldtyRg;

    /**
     * Ruft den Wert der rate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RateTypeChoice }
     *     
     */
    public RateTypeChoice getRate() {
        return rate;
    }

    /**
     * Legt den Wert der rate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RateTypeChoice }
     *     
     */
    public void setRate(RateTypeChoice value) {
        this.rate = value;
    }

    /**
     * Ruft den Wert der vldtyRg-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmountRange }
     *     
     */
    public CurrencyAndAmountRange getVldtyRg() {
        return vldtyRg;
    }

    /**
     * Legt den Wert der vldtyRg-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmountRange }
     *     
     */
    public void setVldtyRg(CurrencyAndAmountRange value) {
        this.vldtyRg = value;
    }

}
