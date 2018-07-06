/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.ingest;

import java.util.logging.Logger;

/**
 *
 * @author Akio Sone, Univ, of North Carolina at Chapel Hill, H.W. Odum Inst.
 */
public class DataFileInfo {
    private static final Logger logger = Logger.getLogger(DataFileInfo.class.getName());

    public DataFileInfo() {
    }
    
//     @Column(nullable=true)
    private Long filesize;
    
    
    /**
     * Get property filesize, number of bytes
     * @return value of property filesize.
     */
    public long getFilesize() {
        if (this.filesize == null) {
            // -1 means "unknown"
            return -1;
        } 
        return this.filesize;
    }

    /**
     * Set property filesize in bytes
     * 
     * Allow nulls, but not negative numbers.
     * 
     * @param filesize new value of property filesize.
     */
    public void setFilesize(long filesize) {
        if (filesize < 0){
            return;
        }
       this.filesize = filesize;
    }
    
//    @NotBlank
//    @Column( nullable = false )
//    @Pattern(regexp = "^.*/.*$", message = "Content-Type must contain a slash")
    private String contentType;
    
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
        
        
}
