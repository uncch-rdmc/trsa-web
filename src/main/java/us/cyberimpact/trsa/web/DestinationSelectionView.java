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
import javax.enterprise.context.SessionScoped;
import javax.faces.annotation.ManagedProperty;
import javax.faces.application.FacesMessage;


import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import us.cyberimpact.trsa.entities.HostInfo;
import us.cyberimpact.trsa.entities.HostInfoFacade;

/**
 *
 * @author asone
 */
@Named("destinationSelectionView")
@SessionScoped
public class DestinationSelectionView implements Serializable {

    private static final Logger logger = Logger.getLogger(DestinationSelectionView.class.getName());
    /**
     * Creates a new instance of DestinationSelectionView
     */
    public DestinationSelectionView() {
    }
    @Inject
    HostInfoFacade hostInfoFacade;
    
    List<HostInfo> hostInfoTable = new ArrayList<>();
    
    
    @Inject
    private HomePageView homePageView;
    
    public boolean isMetadataOnly() {
        return homePageView.isMetadataOnly();
    }

    public HomePageView getHomePageView() {
        return homePageView;
    }

    public void setHomePageView(HomePageView homePageView) {
        this.homePageView = homePageView;
    }
    
    private String selectedDatasetId;

    public String getSelectedDatasetId() {
        return selectedDatasetId;
    }

    public void setSelectedDatasetId(String selectedDatasetId) {
        this.selectedDatasetId = selectedDatasetId;
    }
    
    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "=========== DestinationSelectionView#init: start ===========");
        hostInfoTable = hostInfoFacade.findAll();
        logger.log(Level.INFO, "DestinationSelectionView:hostInfoTable:howMany={0}", hostInfoTable.size());
        if (hostInfoTable.isEmpty()){
            logger.log(Level.WARNING, "hostInfoTable is empty");
        } else {
            logger.log(Level.INFO, "DestinationSelectionView:hostInfoTable exists and not empty");
            selectedHostInfo=hostInfoTable.get(hostInfoTable.size()-1);
            logger.log(Level.INFO, "init:selectedHostInfo={0}", selectedHostInfo);
        }
        logger.log(Level.INFO, "=========== DestinationSelectionView#init: end ===========");
    }

    public List<HostInfo> getHostInfoTable() {
        return hostInfoTable;
    }

    public void setHostInfoTable(List<HostInfo> hostInfoTable) {
        this.hostInfoTable = hostInfoTable;
    }
     
    private HostInfo selectedHostInfo;

    public HostInfo getSelectedHostInfo() {
        return selectedHostInfo;
    }

    public void setSelectedHostInfo(HostInfo selectedHostInfo) {
        this.selectedHostInfo = selectedHostInfo;
    }
    
    public String selectDestination(HostInfo hostInfo){
        logger.log(Level.INFO, "=========== DestinationSelectionView#selectDestination: start ===========");
        logger.log(Level.INFO, "selectDestination: selectedHostInfo={0}", selectedHostInfo);
        logger.log(Level.INFO, "selectedHostInfo={0}", hostInfo);

        logger.log(Level.INFO, "selected datasetId={0}", selectedHostInfo.getDatasetid());
        // the dataset is extracted from a doi, it must not be empty
        if (StringUtils.isEmpty(selectedHostInfo.getDatasetDoi())){
            addMessageEmptyHostInfo();
            logger.log(Level.INFO, "go to host_info editor page");
            return "/hostinfo/List.xhtml";
        } else {
            selectedDatasetId= getDatasetId(selectedHostInfo.getDatasetDoi());
            logger.log(Level.INFO, "selectedDatasetId={0}", selectedDatasetId);
            logger.log(Level.INFO, "go to fileupload page");
        }
        logger.log(Level.INFO, "=========== DestinationSelectionView#selectDestination: end ===========");
        return "/fileupload.xhtml";
    }
    
    
    
    public void addMessageEmptyHostInfo(){
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Dataset's DOI datum is missing", "Add the DOI datum before uploading Metadata.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    
    public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage("host Selected", ((HostInfo) event.getObject()).getDataversetitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        logger.log(Level.INFO, "onRowSelect:selectedHostInfo={0}", selectedHostInfo);
    }
 
    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage("host Unselected", ((HostInfo) event.getObject()).getDataversetitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        logger.log(Level.INFO, "onRowUnselect:selectedHostInfo={0}", selectedHostInfo);
    }
    
    
    private String getDatasetId(String doi){
        // doi is given by such as doi:10.33563/FK2/NRIISK
        return doi.split("/")[2];
    }
    
    
}
