/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.web.gui;

import javax.inject.Named;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author akios
 */
@ManagedBean(name = "ingestPageView")
@SessionScoped
public class IngestPageView implements Serializable {
    private static final Logger logger = Logger.getLogger(IngestPageView.class.getName());
    @ManagedProperty("#{fileUploadView}")
    private FileUploadView fileUploadView;
    /**
     * Creates a new instance of IngestPageView
     */
    public IngestPageView() {
    }
    
    public String ingestedFile;

    public String getIngestedFile() {
        return fileUploadView.getFileName();
    }

    public void setIngestedFile(String ingestedFile) {
        this.ingestedFile = ingestedFile;
    }



    public void setFileUploadView(FileUploadView fileUploadView) {
        this.fileUploadView = fileUploadView;
    }
    
    
}
