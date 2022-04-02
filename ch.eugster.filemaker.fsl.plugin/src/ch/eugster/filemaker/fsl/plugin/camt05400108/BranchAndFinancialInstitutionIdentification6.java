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
 * <p>Java-Klasse für BranchAndFinancialInstitutionIdentification6 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="BranchAndFinancialInstitutionIdentification6">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FinInstnId" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.08}FinancialInstitutionIdentification18"/>
 *         &lt;element name="BrnchId" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.08}BranchData3" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BranchAndFinancialInstitutionIdentification6", propOrder = {
    "finInstnId",
    "brnchId"
})
public class BranchAndFinancialInstitutionIdentification6 {

    @XmlElement(name = "FinInstnId", required = true)
    protected FinancialInstitutionIdentification18 finInstnId;
    @XmlElement(name = "BrnchId")
    protected BranchData3 brnchId;

    /**
     * Ruft den Wert der finInstnId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FinancialInstitutionIdentification18 }
     *     
     */
    public FinancialInstitutionIdentification18 getFinInstnId() {
        return finInstnId;
    }

    /**
     * Legt den Wert der finInstnId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialInstitutionIdentification18 }
     *     
     */
    public void setFinInstnId(FinancialInstitutionIdentification18 value) {
        this.finInstnId = value;
    }

    /**
     * Ruft den Wert der brnchId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BranchData3 }
     *     
     */
    public BranchData3 getBrnchId() {
        return brnchId;
    }

    /**
     * Legt den Wert der brnchId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchData3 }
     *     
     */
    public void setBrnchId(BranchData3 value) {
        this.brnchId = value;
    }

}
