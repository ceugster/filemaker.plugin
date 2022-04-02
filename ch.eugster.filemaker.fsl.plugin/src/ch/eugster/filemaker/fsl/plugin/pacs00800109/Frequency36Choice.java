//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.20 um 07:55:49 AM CET 
//


package ch.eugster.filemaker.fsl.plugin.pacs00800109;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Frequency36Choice complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Frequency36Choice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="Tp" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}Frequency6Code"/>
 *         &lt;element name="Prd" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}FrequencyPeriod1"/>
 *         &lt;element name="PtInTm" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}FrequencyAndMoment1"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Frequency36Choice", propOrder = {
    "tp",
    "prd",
    "ptInTm"
})
public class Frequency36Choice {

    @XmlElement(name = "Tp")
    @XmlSchemaType(name = "string")
    protected Frequency6Code tp;
    @XmlElement(name = "Prd")
    protected FrequencyPeriod1 prd;
    @XmlElement(name = "PtInTm")
    protected FrequencyAndMoment1 ptInTm;

    /**
     * Ruft den Wert der tp-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Frequency6Code }
     *     
     */
    public Frequency6Code getTp() {
        return tp;
    }

    /**
     * Legt den Wert der tp-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Frequency6Code }
     *     
     */
    public void setTp(Frequency6Code value) {
        this.tp = value;
    }

    /**
     * Ruft den Wert der prd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FrequencyPeriod1 }
     *     
     */
    public FrequencyPeriod1 getPrd() {
        return prd;
    }

    /**
     * Legt den Wert der prd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FrequencyPeriod1 }
     *     
     */
    public void setPrd(FrequencyPeriod1 value) {
        this.prd = value;
    }

    /**
     * Ruft den Wert der ptInTm-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FrequencyAndMoment1 }
     *     
     */
    public FrequencyAndMoment1 getPtInTm() {
        return ptInTm;
    }

    /**
     * Legt den Wert der ptInTm-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FrequencyAndMoment1 }
     *     
     */
    public void setPtInTm(FrequencyAndMoment1 value) {
        this.ptInTm = value;
    }

}
