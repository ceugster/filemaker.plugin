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
 * <p>Java-Klasse für CashAccount13 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CashAccount13">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}AccountIdentification3Choice"/>
 *         &lt;element name="Tp" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CashAccountType2" minOccurs="0"/>
 *         &lt;element name="Ccy" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CurrencyCode" minOccurs="0"/>
 *         &lt;element name="Nm" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}Max70Text" minOccurs="0"/>
 *         &lt;element name="Ownr" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}PartyIdentification8" minOccurs="0"/>
 *         &lt;element name="Svcr" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}BranchAndFinancialInstitutionIdentification3" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CashAccount13", propOrder = {
    "id",
    "tp",
    "ccy",
    "nm",
    "ownr",
    "svcr"
})
public class CashAccount13 {

    @XmlElement(name = "Id", required = true)
    protected AccountIdentification3Choice id;
    @XmlElement(name = "Tp")
    protected CashAccountType2 tp;
    @XmlElement(name = "Ccy")
    protected String ccy;
    @XmlElement(name = "Nm")
    protected String nm;
    @XmlElement(name = "Ownr")
    protected PartyIdentification8 ownr;
    @XmlElement(name = "Svcr")
    protected BranchAndFinancialInstitutionIdentification3 svcr;

    /**
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AccountIdentification3Choice }
     *     
     */
    public AccountIdentification3Choice getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountIdentification3Choice }
     *     
     */
    public void setId(AccountIdentification3Choice value) {
        this.id = value;
    }

    /**
     * Ruft den Wert der tp-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CashAccountType2 }
     *     
     */
    public CashAccountType2 getTp() {
        return tp;
    }

    /**
     * Legt den Wert der tp-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccountType2 }
     *     
     */
    public void setTp(CashAccountType2 value) {
        this.tp = value;
    }

    /**
     * Ruft den Wert der ccy-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * Legt den Wert der ccy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcy(String value) {
        this.ccy = value;
    }

    /**
     * Ruft den Wert der nm-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNm() {
        return nm;
    }

    /**
     * Legt den Wert der nm-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNm(String value) {
        this.nm = value;
    }

    /**
     * Ruft den Wert der ownr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentification8 }
     *     
     */
    public PartyIdentification8 getOwnr() {
        return ownr;
    }

    /**
     * Legt den Wert der ownr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentification8 }
     *     
     */
    public void setOwnr(PartyIdentification8 value) {
        this.ownr = value;
    }

    /**
     * Ruft den Wert der svcr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BranchAndFinancialInstitutionIdentification3 }
     *     
     */
    public BranchAndFinancialInstitutionIdentification3 getSvcr() {
        return svcr;
    }

    /**
     * Legt den Wert der svcr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchAndFinancialInstitutionIdentification3 }
     *     
     */
    public void setSvcr(BranchAndFinancialInstitutionIdentification3 value) {
        this.svcr = value;
    }

}
