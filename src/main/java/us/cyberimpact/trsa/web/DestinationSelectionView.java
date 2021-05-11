package us.cyberimpact.trsa.web;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;


import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Faces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import us.cyberimpact.trsa.entities.HostInfo;
import us.cyberimpact.trsa.entities.HostInfoFacade;

/**
 *
 * @author asone
 */
@Named("destinationSelectionView")
@ViewScoped
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
    
    
//    @Inject
//    private HomePageView homePageView;
    
//    public boolean isMetadataOnly() {
//        return homePageView.isMetadataOnly();
//    }

//    public HomePageView getHomePageView() {
//        return homePageView;
//    }
//
//    public void setHomePageView(HomePageView homePageView) {
//        this.homePageView = homePageView;
//    }
    
    
    private RequestType selectedRequestType;

    public RequestType getSelectedRequestType() {
        return selectedRequestType;
    }

    public void setSelectedRequestType(RequestType selectedRequestType) {
        this.selectedRequestType = selectedRequestType;
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
        
        selectedRequestType= Faces.getSessionAttribute("selectedRequestType");
        logger.log(Level.INFO, "selectedRequestType={0}", selectedRequestType);
        logger.log(Level.INFO, "=========== DestinationSelectionView#init: end ===========");
    }

    public List<HostInfo> getHostInfoTable() {
        logger.log(Level.INFO, "getHostInfoTable is called");
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
        Faces.setSessionAttribute("selectedHostInfo", selectedHostInfo);

        logger.log(Level.INFO, "selected datasetId={0}", selectedHostInfo.getDatasetid());
        // the dataset is extracted from a doi, it must not be empty
        if (StringUtils.isEmpty(selectedHostInfo.getDatasetDoi())){
            addMessageEmptyHostInfo();
            logger.log(Level.INFO, "doi is empty: dataset Id cannot be extracted");
            logger.log(Level.INFO, "go to host_info editor page");
            return "";
        } else {
            selectedDatasetId= getDatasetId(selectedHostInfo.getDatasetDoi());
            logger.log(Level.INFO, "selectedDatasetId={0}", selectedDatasetId);
            logger.log(Level.INFO, "go to fileupload page");
        }
        logger.log(Level.INFO, "selectedRequestType:current value={0}", selectedRequestType);
        Faces.setSessionAttribute("selectedDatasetId", selectedDatasetId);
        logger.log(Level.INFO, "=========== DestinationSelectionView#selectDestination: end ===========");
        return "/fileupload.xhtml";
    }
    
    
    public String selectDataverse(HostInfo hostInfo){
        logger.log(Level.INFO, "=========== DestinationSelectionView#selectDataverse: start ===========");
        logger.log(Level.INFO, "selectDataverse: selectedHostInfo={0}", selectedHostInfo);
        logger.log(Level.INFO, "selectedHostInfo={0}", hostInfo);
        selectedHostInfo=hostInfo;
        
        logger.log(Level.INFO, "=========== DestinationSelectionView#selectDataverse: end ===========");
        return "/setupWizard.xhtml";
    }
    
    
    
    
    
    public void addMessageEmptyHostInfo(){
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Dataset's DOI is missing", "Add the DOI to the host info before uploading Metadata.");
        Faces.getContext().addMessage("topMessage", message);
    }
    
    
    public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage("host Selected", ((HostInfo) event.getObject()).getDataversetitle());
        Faces.getContext().addMessage(null, msg);
        logger.log(Level.INFO, "onRowSelect:selectedHostInfo={0}", selectedHostInfo);
    }
 
    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage("host Unselected", ((HostInfo) event.getObject()).getDataversetitle());
        Faces.getContext().addMessage(null, msg);
        logger.log(Level.INFO, "onRowUnselect:selectedHostInfo={0}", selectedHostInfo);
    }
    
    
    private String getDatasetId(String doi){
        // doi is given by such as doi:10.33563/FK2/NRIISK
        String [] parts = doi.split("/");
        return parts[parts.length-1];
    }
    
    
}
