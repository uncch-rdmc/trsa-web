/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.web;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import us.cyberimpact.trsa.core.hostinfo.HostInfo;
import us.cyberimpact.trsa.core.hostinfo.HostInfoFacade;

/**
 *
 * @author asone
 */
@ManagedBean(name = "homePageView")
@SessionScoped
public class HomePageView implements Serializable {

    private static final Logger logger = Logger.getLogger(HomePageView.class.getName());

    @EJB
    private HostInfoFacade hostInfoFacade;

    List<HostInfo> hostInfoTable = new ArrayList<>();

    // default is ture, i.e., metadata-only-uploading 
    private boolean metadataOnly = true;

    public boolean isMetadataOnly() {
        return metadataOnly;
    }

    public void setMetadataOnly(boolean metadataOnly) {
        this.metadataOnly = metadataOnly;
    }


    
    
    
    
    
    /**
     * Creates a new instance of HomePageView
     */
    public HomePageView() {
    }
    
    
    
    
    
    @PostConstruct
    public void init() {
        
        
        hostInfoTable = hostInfoFacade.findAll();
        logger.log(Level.INFO, "homePageView:hostInfoTable={0}", hostInfoTable);
        if (hostInfoTable.isEmpty()){
            logger.log(Level.INFO, "homePageView:hostInfoTable is empty");
            addMessageEmptyHostInfo();
        } else {
            logger.log(Level.INFO, "homePageView:hostInfoTable exists and not empty");
            //addMessageHostInfoAvailable();
        }
        
    }
    
    

    
    public void addMessageHostInfoAvailable(){
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Dataverse data available", "Dataverse are already saved: if necessary, add new Dataverse data before uploading.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public void addMessageEmptyHostInfo(){
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Dataverse data are not saved!", "Add Dataverse data before uploading.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    
    // button actions 
    public String gotoTRSAprofilePage(){
        logger.log(Level.INFO, "got to TRSA Profile page");
        return "/trsaProfile/List.xhtml";
    }

    public String gotoUploadMetadataPage(){
        logger.log(Level.INFO, "got to UploadMetadataPage");
        return gotoFileUploadPage();
    }
    
    public String gotoDatasetCreationPage(){
        logger.log(Level.INFO, "go to DatasetCreation page");
        // modify the boolean value
        metadataOnly=false;
        return gotoFileUploadPage();
    }
    
    
    private String gotoFileUploadPage(){
        logger.log(Level.INFO, "metadataOnly={0}", metadataOnly);
        return "/fileupload.xhtml";
    }
    
    public String gotoHostinfoPage(){
        logger.log(Level.INFO, "got to host info page");
        return "/hostinfo/List.xhtml";
    }
}
