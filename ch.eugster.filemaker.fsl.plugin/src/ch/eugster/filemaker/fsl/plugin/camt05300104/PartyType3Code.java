//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.19 um 03:10:03 PM CET 
//


package ch.eugster.filemaker.fsl.plugin.camt05300104;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PartyType3Code.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="PartyType3Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OPOI"/>
 *     &lt;enumeration value="MERC"/>
 *     &lt;enumeration value="ACCP"/>
 *     &lt;enumeration value="ITAG"/>
 *     &lt;enumeration value="ACQR"/>
 *     &lt;enumeration value="CISS"/>
 *     &lt;enumeration value="DLIS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PartyType3Code")
@XmlEnum
public enum PartyType3Code {

    OPOI,
    MERC,
    ACCP,
    ITAG,
    ACQR,
    CISS,
    DLIS;

    public String value() {
        return name();
    }

    public static PartyType3Code fromValue(String v) {
        return valueOf(v);
    }

}
