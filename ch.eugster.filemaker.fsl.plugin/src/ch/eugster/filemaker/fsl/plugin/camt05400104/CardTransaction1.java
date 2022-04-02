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
 * <p>Java-Klasse für CardTransaction1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CardTransaction1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Card" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.04}PaymentCard4" minOccurs="0"/>
 *         &lt;element name="POI" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.04}PointOfInteraction1" minOccurs="0"/>
 *         &lt;element name="Tx" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.04}CardTransaction1Choice" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CardTransaction1", propOrder = {
    "card",
    "poi",
    "tx"
})
public class CardTransaction1 {

    @XmlElement(name = "Card")
    protected PaymentCard4 card;
    @XmlElement(name = "POI")
    protected PointOfInteraction1 poi;
    @XmlElement(name = "Tx")
    protected CardTransaction1Choice tx;

    /**
     * Ruft den Wert der card-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PaymentCard4 }
     *     
     */
    public PaymentCard4 getCard() {
        return card;
    }

    /**
     * Legt den Wert der card-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentCard4 }
     *     
     */
    public void setCard(PaymentCard4 value) {
        this.card = value;
    }

    /**
     * Ruft den Wert der poi-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PointOfInteraction1 }
     *     
     */
    public PointOfInteraction1 getPOI() {
        return poi;
    }

    /**
     * Legt den Wert der poi-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PointOfInteraction1 }
     *     
     */
    public void setPOI(PointOfInteraction1 value) {
        this.poi = value;
    }

    /**
     * Ruft den Wert der tx-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CardTransaction1Choice }
     *     
     */
    public CardTransaction1Choice getTx() {
        return tx;
    }

    /**
     * Legt den Wert der tx-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CardTransaction1Choice }
     *     
     */
    public void setTx(CardTransaction1Choice value) {
        this.tx = value;
    }

}
