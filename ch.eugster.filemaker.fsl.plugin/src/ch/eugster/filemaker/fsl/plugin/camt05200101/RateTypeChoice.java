//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.20 um 07:57:54 AM CET 
//


package ch.eugster.filemaker.fsl.plugin.camt05200101;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für RateTypeChoice complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="RateTypeChoice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="PctgRate" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}PercentageRate"/>
 *           &lt;element name="TxtlRate" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}Max35Text"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RateTypeChoice", propOrder = {
    "pctgRate",
    "txtlRate"
})
public class RateTypeChoice {

    @XmlElement(name = "PctgRate")
    protected BigDecimal pctgRate;
    @XmlElement(name = "TxtlRate")
    protected String txtlRate;

    /**
     * Ruft den Wert der pctgRate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPctgRate() {
        return pctgRate;
    }

    /**
     * Legt den Wert der pctgRate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPctgRate(BigDecimal value) {
        this.pctgRate = value;
    }

    /**
     * Ruft den Wert der txtlRate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTxtlRate() {
        return txtlRate;
    }

    /**
     * Legt den Wert der txtlRate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTxtlRate(String value) {
        this.txtlRate = value;
    }

}
