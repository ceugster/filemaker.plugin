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
 * <p>Java-Klasse für OnLineCapability1Code.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="OnLineCapability1Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OFLN"/>
 *     &lt;enumeration value="ONLN"/>
 *     &lt;enumeration value="SMON"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "OnLineCapability1Code")
@XmlEnum
public enum OnLineCapability1Code {

    OFLN,
    ONLN,
    SMON;

    public String value() {
        return name();
    }

    public static OnLineCapability1Code fromValue(String v) {
        return valueOf(v);
    }

}
