//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.20 um 07:57:54 AM CET 
//


package ch.eugster.filemaker.fsl.plugin.camt05200101;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für BalanceType8Code.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="BalanceType8Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OPBD"/>
 *     &lt;enumeration value="ITBD"/>
 *     &lt;enumeration value="CLBD"/>
 *     &lt;enumeration value="XPCD"/>
 *     &lt;enumeration value="OPAV"/>
 *     &lt;enumeration value="ITAV"/>
 *     &lt;enumeration value="CLAV"/>
 *     &lt;enumeration value="FWAV"/>
 *     &lt;enumeration value="PRCD"/>
 *     &lt;enumeration value="IOPA"/>
 *     &lt;enumeration value="IITA"/>
 *     &lt;enumeration value="ICLA"/>
 *     &lt;enumeration value="IFWA"/>
 *     &lt;enumeration value="ICLB"/>
 *     &lt;enumeration value="IITB"/>
 *     &lt;enumeration value="IOPB"/>
 *     &lt;enumeration value="IXPC"/>
 *     &lt;enumeration value="DOPA"/>
 *     &lt;enumeration value="DITA"/>
 *     &lt;enumeration value="DCLA"/>
 *     &lt;enumeration value="DFWA"/>
 *     &lt;enumeration value="DCLB"/>
 *     &lt;enumeration value="DITB"/>
 *     &lt;enumeration value="DOPB"/>
 *     &lt;enumeration value="DXPC"/>
 *     &lt;enumeration value="COPA"/>
 *     &lt;enumeration value="CITA"/>
 *     &lt;enumeration value="CCLA"/>
 *     &lt;enumeration value="CFWA"/>
 *     &lt;enumeration value="CCLB"/>
 *     &lt;enumeration value="CITB"/>
 *     &lt;enumeration value="COPB"/>
 *     &lt;enumeration value="CXPC"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BalanceType8Code")
@XmlEnum
public enum BalanceType8Code {

    OPBD,
    ITBD,
    CLBD,
    XPCD,
    OPAV,
    ITAV,
    CLAV,
    FWAV,
    PRCD,
    IOPA,
    IITA,
    ICLA,
    IFWA,
    ICLB,
    IITB,
    IOPB,
    IXPC,
    DOPA,
    DITA,
    DCLA,
    DFWA,
    DCLB,
    DITB,
    DOPB,
    DXPC,
    COPA,
    CITA,
    CCLA,
    CFWA,
    CCLB,
    CITB,
    COPB,
    CXPC;

    public String value() {
        return name();
    }

    public static BalanceType8Code fromValue(String v) {
        return valueOf(v);
    }

}
