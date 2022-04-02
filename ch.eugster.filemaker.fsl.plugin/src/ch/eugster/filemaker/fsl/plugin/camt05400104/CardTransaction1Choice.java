//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.19 um 03:09:45 PM CET 
//


package ch.eugster.filemaker.fsl.plugin.camt05400104;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für CardTransaction1Choice complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CardTransaction1Choice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="Aggtd" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.04}CardAggregated1"/>
 *         &lt;element name="Indv" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.04}CardIndividualTransaction1"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CardTransaction1Choice", propOrder = {
    "aggtd",
    "indv"
})
public class CardTransaction1Choice {

    @XmlElement(name = "Aggtd")
    protected CardAggregated1 aggtd;
    @XmlElement(name = "Indv")
    protected CardIndividualTransaction1 indv;

    /**
     * Ruft den Wert der aggtd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CardAggregated1 }
     *     
     */
    public CardAggregated1 getAggtd() {
        return aggtd;
    }

    /**
     * Legt den Wert der aggtd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CardAggregated1 }
     *     
     */
    public void setAggtd(CardAggregated1 value) {
        this.aggtd = value;
    }

    /**
     * Ruft den Wert der indv-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CardIndividualTransaction1 }
     *     
     */
    public CardIndividualTransaction1 getIndv() {
        return indv;
    }

    /**
     * Legt den Wert der indv-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CardIndividualTransaction1 }
     *     
     */
    public void setIndv(CardIndividualTransaction1 value) {
        this.indv = value;
    }

}
