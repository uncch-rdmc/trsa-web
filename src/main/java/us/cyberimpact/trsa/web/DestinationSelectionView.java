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
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import us.cyberimpact.trsa.core.hostinfo.HostInfo;
import us.cyberimpact.trsa.core.hostinfo.HostInfoFacade;

/**
 *
 * @author asone
 */
@ManagedBean(name = "destinationSelectionView")
@SessionScoped
public class DestinationSelectionView implements Serializable {

    private static final Logger logger = Logger.getLogger(DestinationSelectionView.class.getName());
    /**
     * Creates a new instance of DestinationSelectionView
     */
    public DestinationSelectionView() {
    }
    @EJB
    HostInfoFacade hostInfoFacade;
    
    List<HostInfo> hostInfoTable = new ArrayList<>();
    
    
    
    @ManagedProperty("#{homePageView}")
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
    
    
    
    @PostConstruct
    public void init() {
        
        
//        trsaProfileTable= trsaProfileFacade.findAll();
//        logger.log(Level.INFO, "FileUploadView:TrsaProfileTable={0}", trsaProfileTable);
//        if (trsaProfileTable.isEmpty()){
//            logger.log(Level.INFO, "trsa profile is empty");
//            // turn off the publish button 
//           isTrsaProfileReady=false;
//        } else {
//            // turn on the publish button
//            isTrsaProfileReady=true;
//            logger.log(Level.INFO, "FileUploadView:trsa profile exists");
//            
//            logger.log(Level.INFO, "url={0}",trsaProfileTable.get(0).getDataverseurl());            
//            logger.log(Level.INFO, "api-token={0}",trsaProfileTable.get(0).getApitoken());
//            
//            
//        }
        hostInfoTable = hostInfoFacade.findAll();
        logger.log(Level.INFO, "FileUploadView:hostInfoTable={0}", hostInfoTable);
        if (hostInfoTable.isEmpty()){
            logger.log(Level.INFO, "hostInfoTable is empty");
        } else {
            logger.log(Level.INFO, "FileUploadView:hostInfoTable exists and not empty");
            selectedHostInfo=hostInfoTable.get(hostInfoTable.size()-1);
            logger.log(Level.INFO, "init:selectedHostInfo={0}", selectedHostInfo);
        }
        
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
        logger.log(Level.INFO, "selectDestination: selectedHostInfo={0}", selectedHostInfo);
        logger.log(Level.INFO, "selectedHostInfo={0}", hostInfo);

        logger.log(Level.INFO, "datasetId={0}", selectedHostInfo.getDatasetid());
        logger.log(Level.INFO, "go to publish page");
        return "/ingest.xhtml";
    }
//    public String selectDestination(){
//        logger.log(Level.INFO, "selectDestination");
//        logger.log(Level.INFO, "selectedHostInfo={0}", hostInfo);
//        this.selectedHostInfo= hostInfo;
//        logger.log(Level.INFO, "datasetId={0}", selectedHostInfo.getDatasetid());
//        logger.log(Level.INFO, "go to publish page");
//        return "/ingest.xhtml";
//    }
    
    
    public String selectDest(){
        logger.log(Level.INFO, "selectDestination: selectedHostInfo={0}", selectedHostInfo);


        logger.log(Level.INFO, "datasetId={0}", selectedHostInfo.getDatasetid());
        logger.log(Level.INFO, "go to publish page");
        return "/ingest.xhtml";
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
    
    
}
