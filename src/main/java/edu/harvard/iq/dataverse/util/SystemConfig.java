/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.util;

import edu.harvard.iq.dataverse.entities.DataFile;

/**
 *
 * @author Akio Sone, Univ, of North Carolina at Chapel Hill, H.W. Odum Inst.
 */
public class SystemConfig {

    /**
     * The default number of datafiles that we allow to be created through 
     * zip file upload.
     */
    private static final int defaultZipUploadFilesLimit = 1000; 

    public static final String FQDN = "dataverse.fqdn";
    
    
    
    
    public static final String SITE_URL = "dataverse.siteUrl";
    
    
    public static final String DOI_SERVER_PREFIX ="https://doi.org/";
    
    
    public Long getMaxFileUploadSize(){
        return null;
         //return settingsService.getValueForKeyAsLong(SettingsServiceBean.Key.MaxFileUploadSizeInBytes);
     }
    
    public int getZipUploadFilesLimit() {
        return defaultZipUploadFilesLimit;
    }
    
    public DataFile.ChecksumType getFileFixityChecksumAlgorithm() {
        DataFile.ChecksumType saneDefault = DataFile.ChecksumType.MD5;
        return saneDefault;
    }
}
