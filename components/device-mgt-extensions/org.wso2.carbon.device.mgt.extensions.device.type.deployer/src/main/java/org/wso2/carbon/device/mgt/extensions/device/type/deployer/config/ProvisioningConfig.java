
package org.wso2.carbon.device.mgt.extensions.device.type.deployer.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProvisioningConfig complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProvisioningConfig">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SharedWithAllTenants" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvisioningConfig", propOrder = {
    "sharedWithAllTenants"
})
public class ProvisioningConfig {

    @XmlElement(name = "SharedWithAllTenants", required = true)
    protected boolean sharedWithAllTenants;

    /**
     * Gets the value of the sharedWithAllTenants property.
     * 
     * @return
     *     possible object is
     *     {@link boolean }
     *     
     */
    public boolean isSharedWithAllTenants() {
        return sharedWithAllTenants;
    }

    /**
     * Sets the value of the sharedWithAllTenants property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSharedWithAllTenants(boolean value) {
        this.sharedWithAllTenants = value;
    }

}