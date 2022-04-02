//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.20 um 07:57:07 AM CET 
//


package ch.eugster.filemaker.fsl.plugin.camt05200106;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für OriginalAndCurrentQuantities1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="OriginalAndCurrentQuantities1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FaceAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.06}ImpliedCurrencyAndAmount"/>
 *         &lt;element name="AmtsdVal" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.06}ImpliedCurrencyAndAmount"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OriginalAndCurrentQuantities1", propOrder = {
    "faceAmt",
    "amtsdVal"
})
public class OriginalAndCurrentQuantities1 {

    @XmlElement(name = "FaceAmt", required = true)
    protected BigDecimal faceAmt;
    @XmlElement(name = "AmtsdVal", required = true)
    protected BigDecimal amtsdVal;

    /**
     * Ruft den Wert der faceAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFaceAmt() {
        return faceAmt;
    }

    /**
     * Legt den Wert der faceAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFaceAmt(BigDecimal value) {
        this.faceAmt = value;
    }

    /**
     * Ruft den Wert der amtsdVal-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAmtsdVal() {
        return amtsdVal;
    }

    /**
     * Legt den Wert der amtsdVal-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAmtsdVal(BigDecimal value) {
        this.amtsdVal = value;
    }

}
