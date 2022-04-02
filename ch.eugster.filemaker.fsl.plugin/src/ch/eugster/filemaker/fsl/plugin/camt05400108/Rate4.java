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
 * <p>Java-Klasse für Rate4 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Rate4">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Tp" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.08}RateType4Choice"/>
 *         &lt;element name="VldtyRg" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.08}ActiveOrHistoricCurrencyAndAmountRange2" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Rate4", propOrder = {
    "tp",
    "vldtyRg"
})
public class Rate4 {

    @XmlElement(name = "Tp", required = true)
    protected RateType4Choice tp;
    @XmlElement(name = "VldtyRg")
    protected ActiveOrHistoricCurrencyAndAmountRange2 vldtyRg;

    /**
     * Ruft den Wert der tp-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RateType4Choice }
     *     
     */
    public RateType4Choice getTp() {
        return tp;
    }

    /**
     * Legt den Wert der tp-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RateType4Choice }
     *     
     */
    public void setTp(RateType4Choice value) {
        this.tp = value;
    }

    /**
     * Ruft den Wert der vldtyRg-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ActiveOrHistoricCurrencyAndAmountRange2 }
     *     
     */
    public ActiveOrHistoricCurrencyAndAmountRange2 getVldtyRg() {
        return vldtyRg;
    }

    /**
     * Legt den Wert der vldtyRg-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ActiveOrHistoricCurrencyAndAmountRange2 }
     *     
     */
    public void setVldtyRg(ActiveOrHistoricCurrencyAndAmountRange2 value) {
        this.vldtyRg = value;
    }

}
