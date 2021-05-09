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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import us.cyberimpact.trsa.entities.HostInfo;
import us.cyberimpact.trsa.entities.HostInfoFacade;

/**
 *
 * @author asone
 */
@Named("homePageView")
@ViewScoped
public class HomePageView implements Serializable {

    private static final Logger logger = Logger.getLogger(HomePageView.class.getName());

    @Inject
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


    private boolean emptyDatasetCreation = false;
    
    public boolean isEmptyDatasetCreation(){
        return emptyDatasetCreation;
    }

    public void setEmptyDatasetCreation(boolean emptyDatasetCreation) {
        this.emptyDatasetCreation = emptyDatasetCreation;
    }
    
    
    
    private RequestType selectedRequestType;

    public RequestType getSelectedRequestType() {
        return selectedRequestType;
    }

    public void setSelectedRequestType(RequestType selectedRequestType) {
        this.selectedRequestType = selectedRequestType;
    }
    
    
    private boolean hostInfoSaved=false;

    public boolean isHostInfoSaved() {
        return hostInfoSaved;
    }

    public void setHostInfoSaved(boolean hostInfoSaved) {
        this.hostInfoSaved = hostInfoSaved;
    }


    /**
     * Creates a new instance of HomePageView
     */
    public HomePageView() {
    }
    
    
    
    
    
    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "========== HomePageView#init : start ==========");
        
        clearSession();
        
        hostInfoTable = hostInfoFacade.findAll();
        logger.log(Level.INFO, "homePageView:hostInfoTable:howManyRows={0}", hostInfoTable.size());
        logger.log(Level.FINE, "homePageView:hostInfoTable={0}", hostInfoTable);
        if (hostInfoTable.isEmpty()){
            logger.log(Level.INFO, "homePageView:hostInfoTable is empty");
            addMessageEmptyHostInfo();
            
        } else {
            logger.log(Level.INFO, "homePageView:hostInfoTable exists and not empty:{0}", hostInfoTable);
            hostInfoSaved=true;
            //addMessageHostInfoAvailable();
        }
        logger.log(Level.INFO, "========== HomePageView#init : end ==========");
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
        logger.log(Level.INFO, "HomePageView: got to TRSA Profile page");
        return "/trsaProfile/List.xhtml";
    }

    public String gotoUploadMetadataPage(){
        logger.log(Level.INFO, "HomePageView: got to UploadMetadataPage");
        selectedRequestType=RequestType.METADATA_ONLY;
        return gotoDestinationSelectionPage();
    }
    
    public String gotoDatasetCreationPage(){
        logger.log(Level.INFO, "HomePageView: go to DatasetCreation page");
        // modify the boolean value
        metadataOnly=false;
        selectedRequestType=RequestType.FULL_DATASET;
        return gotoFileUploadPage();
    }
    
    
    private String gotoFileUploadPage(){
        logger.log(Level.INFO, "HomePageView:gotoFileUploadPage:metadataOnly={0}", metadataOnly);
        return "/fileupload.xhtml";
    }
    
    private String gotoDestinationSelectionPage(){
        logger.log(Level.INFO, "HomePageView:gotoDestinationSelectionPage: metadataOnly={0}", metadataOnly);
        return "/destination.xhtml" ;
    }
    
    
    
    public String gotoHostinfoPage(){
        logger.log(Level.INFO, "HomePageView: got to host info page");
        return "/hostinfo/List.xhtml";
    }
    
    public String gotoDsTemplateDataPage(){
        logger.log(Level.INFO, "HomePageView: got to Dataset Template page");
        return "/dsTemplateData/List.xhtml";
    }
    
    public String gotoEmptyDatasetCreationPage(){
        logger.log(Level.INFO, "HomePageView: got to Empty-Dataset-Creation page");
        selectedRequestType = RequestType.EMPTY_DATASET;
        setEmptyDatasetCreation(true);
        logger.log(Level.INFO, "isEmptyDatasetCreation set to ={0}", isEmptyDatasetCreation());
        // return "/dsTemplateSelection.xhtml";
        
        hostInfoTable = hostInfoFacade.findAll();
        logger.log(Level.INFO, "homePageView:hostInfoTable:howManyRows={0}", hostInfoTable.size());
        
        return "/selectDataverse.xhtml";
    }
    
    
    public String gotoPairingConfirmationPage(){
        logger.log(Level.INFO, "HomePageView: got to the pairing confirmation page");
        selectedRequestType = RequestType.PAIRING_CONFIRMATION;
        // setEmptyDatasetCreation(true);
        //logger.log(Level.INFO, "isEmptyDatasetCreation set to ={0}", isEmptyDatasetCreation());
        // return "/dsTemplateSelection.xhtml";
        return "/pairingConfirmation.xhtml";
    }
    
    private void clearSession(){
        logger.log(Level.INFO, "HomePageView#clearSession: sessionscoped data are reset");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }
    
    
}
