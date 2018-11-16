/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.core;

import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author asone
 */
@XmlRootElement
public class TrsaProfileDTO {
    
    private static final Logger logger = Logger.getLogger(TrsaProfileDTO.class.getName());
    

    public TrsaProfileDTO(String installation, String contactEmail, String apiKey, String dataStorageLocation, String dataAccessInfo, String notaryServiceURL, String SafeServiceURL) {
        this.installation = installation;
        this.contactEmail = contactEmail;
        this.apiKey = apiKey;
        this.dataStorageLocation = dataStorageLocation;
        this.dataAccessInfo = dataAccessInfo;
        this.notaryServiceURL = notaryServiceURL;
        this.SafeServiceURL = SafeServiceURL;
    }
    
    
    
    private String installation;

    /**
     * Get the value of installation
     *
     * @return the value of installation
     */
    public String getInstallation() {
        return installation;
    }

    /**
     * Set the value of installation
     *
     * @param installation new value of installation
     */
    public void setInstallation(String installation) {
        this.installation = installation;
    }

    private String contactEmail;

    /**
     * Get the value of contactEmail
     *
     * @return the value of contactEmail
     */
    public String getContactEmail() {
        return contactEmail;
    }

    /**
     * Set the value of contactEmail
     *
     * @param contactEmail new value of contactEmail
     */
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    
    
        private String apiKey;

    /**
     * Get the value of apiKey
     *
     * @return the value of apiKey
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Set the value of apiKey
     *
     * @param apiKey new value of apiKey
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

        private String dataStorageLocation;

    /**
     * Get the value of dataStorageLocation
     *
     * @return the value of dataStorageLocation
     */
    public String getDataStorageLocation() {
        return dataStorageLocation;
    }

    /**
     * Set the value of dataStorageLocation
     *
     * @param dataStorageLocation new value of dataStorageLocation
     */
    public void setDataStorageLocation(String dataStorageLocation) {
        this.dataStorageLocation = dataStorageLocation;
    }

    private String dataAccessInfo;

    /**
     * Get the value of dataAccessInfo
     *
     * @return the value of dataAccessInfo
     */
    public String getDataAccessInfo() {
        return dataAccessInfo;
    }

    /**
     * Set the value of dataAccessInfo
     *
     * @param dataAccessInfo new value of dataAccessInfo
     */
    public void setDataAccessInfo(String dataAccessInfo) {
        this.dataAccessInfo = dataAccessInfo;
    }

    private String notaryServiceURL;

    /**
     * Get the value of notaryServiceURL
     *
     * @return the value of notaryServiceURL
     */
    public String getNotaryServiceURL() {
        return notaryServiceURL;
    }

    /**
     * Set the value of notaryServiceURL
     *
     * @param notaryServiceURL new value of notaryServiceURL
     */
    public void setNotaryServiceURL(String notaryServiceURL) {
        this.notaryServiceURL = notaryServiceURL;
    }

    private String SafeServiceURL;

    /**
     * Get the value of SafeServiceURL
     *
     * @return the value of SafeServiceURL
     */
    public String getSafeServiceURL() {
        return SafeServiceURL;
    }

    /**
     * Set the value of SafeServiceURL
     *
     * @param SafeServiceURL new value of SafeServiceURL
     */
    public void setSafeServiceURL(String SafeServiceURL) {
        this.SafeServiceURL = SafeServiceURL;
    }

}
