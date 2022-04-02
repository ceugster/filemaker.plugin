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
 * <p>Java-Klasse für CardTransaction3Choice complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CardTransaction3Choice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="Aggtd" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.08}CardAggregated2"/>
 *         &lt;element name="Indv" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.08}CardIndividualTransaction2"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CardTransaction3Choice", propOrder = {
    "aggtd",
    "indv"
})
public class CardTransaction3Choice {

    @XmlElement(name = "Aggtd")
    protected CardAggregated2 aggtd;
    @XmlElement(name = "Indv")
    protected CardIndividualTransaction2 indv;

    /**
     * Ruft den Wert der aggtd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CardAggregated2 }
     *     
     */
    public CardAggregated2 getAggtd() {
        return aggtd;
    }

    /**
     * Legt den Wert der aggtd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CardAggregated2 }
     *     
     */
    public void setAggtd(CardAggregated2 value) {
        this.aggtd = value;
    }

    /**
     * Ruft den Wert der indv-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CardIndividualTransaction2 }
     *     
     */
    public CardIndividualTransaction2 getIndv() {
        return indv;
    }

    /**
     * Legt den Wert der indv-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CardIndividualTransaction2 }
     *     
     */
    public void setIndv(CardIndividualTransaction2 value) {
        this.indv = value;
    }

}
