//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.20 um 07:57:54 AM CET 
//


package ch.eugster.filemaker.fsl.plugin.camt05200101;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für TransactionParty1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TransactionParty1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InitgPty" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}PartyIdentification8" minOccurs="0"/>
 *         &lt;element name="Dbtr" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}PartyIdentification8" minOccurs="0"/>
 *         &lt;element name="DbtrAcct" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CashAccount7" minOccurs="0"/>
 *         &lt;element name="UltmtDbtr" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}PartyIdentification8" minOccurs="0"/>
 *         &lt;element name="Cdtr" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}PartyIdentification8" minOccurs="0"/>
 *         &lt;element name="CdtrAcct" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CashAccount7" minOccurs="0"/>
 *         &lt;element name="UltmtCdtr" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}PartyIdentification8" minOccurs="0"/>
 *         &lt;element name="TradgPty" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}PartyIdentification8" minOccurs="0"/>
 *         &lt;element name="Prtry" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}ProprietaryParty1" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionParty1", propOrder = {
    "initgPty",
    "dbtr",
    "dbtrAcct",
    "ultmtDbtr",
    "cdtr",
    "cdtrAcct",
    "ultmtCdtr",
    "tradgPty",
    "prtry"
})
public class TransactionParty1 {

    @XmlElement(name = "InitgPty")
    protected PartyIdentification8 initgPty;
    @XmlElement(name = "Dbtr")
    protected PartyIdentification8 dbtr;
    @XmlElement(name = "DbtrAcct")
    protected CashAccount7 dbtrAcct;
    @XmlElement(name = "UltmtDbtr")
    protected PartyIdentification8 ultmtDbtr;
    @XmlElement(name = "Cdtr")
    protected PartyIdentification8 cdtr;
    @XmlElement(name = "CdtrAcct")
    protected CashAccount7 cdtrAcct;
    @XmlElement(name = "UltmtCdtr")
    protected PartyIdentification8 ultmtCdtr;
    @XmlElement(name = "TradgPty")
    protected PartyIdentification8 tradgPty;
    @XmlElement(name = "Prtry")
    protected List<ProprietaryParty1> prtry;

    /**
     * Ruft den Wert der initgPty-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentification8 }
     *     
     */
    public PartyIdentification8 getInitgPty() {
        return initgPty;
    }

    /**
     * Legt den Wert der initgPty-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentification8 }
     *     
     */
    public void setInitgPty(PartyIdentification8 value) {
        this.initgPty = value;
    }

    /**
     * Ruft den Wert der dbtr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentification8 }
     *     
     */
    public PartyIdentification8 getDbtr() {
        return dbtr;
    }

    /**
     * Legt den Wert der dbtr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentification8 }
     *     
     */
    public void setDbtr(PartyIdentification8 value) {
        this.dbtr = value;
    }

    /**
     * Ruft den Wert der dbtrAcct-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CashAccount7 }
     *     
     */
    public CashAccount7 getDbtrAcct() {
        return dbtrAcct;
    }

    /**
     * Legt den Wert der dbtrAcct-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccount7 }
     *     
     */
    public void setDbtrAcct(CashAccount7 value) {
        this.dbtrAcct = value;
    }

    /**
     * Ruft den Wert der ultmtDbtr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentification8 }
     *     
     */
    public PartyIdentification8 getUltmtDbtr() {
        return ultmtDbtr;
    }

    /**
     * Legt den Wert der ultmtDbtr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentification8 }
     *     
     */
    public void setUltmtDbtr(PartyIdentification8 value) {
        this.ultmtDbtr = value;
    }

    /**
     * Ruft den Wert der cdtr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentification8 }
     *     
     */
    public PartyIdentification8 getCdtr() {
        return cdtr;
    }

    /**
     * Legt den Wert der cdtr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentification8 }
     *     
     */
    public void setCdtr(PartyIdentification8 value) {
        this.cdtr = value;
    }

    /**
     * Ruft den Wert der cdtrAcct-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CashAccount7 }
     *     
     */
    public CashAccount7 getCdtrAcct() {
        return cdtrAcct;
    }

    /**
     * Legt den Wert der cdtrAcct-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccount7 }
     *     
     */
    public void setCdtrAcct(CashAccount7 value) {
        this.cdtrAcct = value;
    }

    /**
     * Ruft den Wert der ultmtCdtr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentification8 }
     *     
     */
    public PartyIdentification8 getUltmtCdtr() {
        return ultmtCdtr;
    }

    /**
     * Legt den Wert der ultmtCdtr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentification8 }
     *     
     */
    public void setUltmtCdtr(PartyIdentification8 value) {
        this.ultmtCdtr = value;
    }

    /**
     * Ruft den Wert der tradgPty-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentification8 }
     *     
     */
    public PartyIdentification8 getTradgPty() {
        return tradgPty;
    }

    /**
     * Legt den Wert der tradgPty-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentification8 }
     *     
     */
    public void setTradgPty(PartyIdentification8 value) {
        this.tradgPty = value;
    }

    /**
     * Gets the value of the prtry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the prtry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrtry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProprietaryParty1 }
     * 
     * 
     */
    public List<ProprietaryParty1> getPrtry() {
        if (prtry == null) {
            prtry = new ArrayList<ProprietaryParty1>();
        }
        return this.prtry;
    }

}
