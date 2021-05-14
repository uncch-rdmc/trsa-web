/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.web;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import edu.unc.odum.dataverse.util.json.JsonResponseParser;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Faces;
import us.cyberimpact.trsa.entities.HostInfo;
import us.cyberimpact.trsa.entities.HostInfoFacade;


/**
 *
 * @author asone
 */
@Named(value = "dataverseConfirmationView")
@ViewScoped
public class DataverseConfirmationView implements Serializable{

    
    private static final Logger logger = 
            Logger.getLogger(DataverseConfirmationView.class.getName());
    /**
     * Creates a new instance of DataverseConfirmationView
     */
    public DataverseConfirmationView() {
    }
    
    
    @Inject
    private HostInfoFacade hostInfoFacade;
    
    private HostInfo hostInfo; 

    public HostInfo getHostInfo() {
        return hostInfo;
    }

    public void setHostInfo(HostInfo hostInfo) {
        this.hostInfo = hostInfo;
    }
    
    
    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "DataverseConfirmationView#init: start");
        hostInfo = new HostInfo();
        logger.log(Level.INFO, "DataverseConfirmationView#init: end");
    }
    
    public String confirmDataverseData(){
        // make a call to Dataverse and confirm entered data
        logger.log(Level.INFO, "========== DataverseConfirmationView#confirmDataverseData : start ==========");
        logger.log(Level.INFO, "hostInfor to be confirmed ={0}", 
                hostInfo);
        // calling the dataverse info
        // to build an api endpoint like below: 
        // http://localhost:8083/api/dataverses/dv-trsa-api
        
        String apiKey = hostInfo.getApitoken();
        String dataverseServer = hostInfo.getHosturl();
        String selectedDataverseId = hostInfo.getDataversealias();
        String apiEndpoint = dataverseServer
                + WebAppConstants.PATH_DATAVERSE_API
                + selectedDataverseId;
        String trsaRegNmbr = hostInfo.getTrsaRegNmbr().toString();
        logger.log(Level.INFO, "apiEndpoint={0}", apiEndpoint);

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Dataverse-key", apiKey);
        headers.put("X-TRSA-registrationId", trsaRegNmbr);

        HttpResponse<JsonNode> jsonResponse = null;

        try {
            jsonResponse = Unirest.get(apiEndpoint)
                    .headers(headers)
                    .queryString("identifier", selectedDataverseId).asJson();
        } catch (UnirestException ex) {
            logger.log(Level.SEVERE, "Unirest request error", ex);
            throw new WebApplicationException(ex, jsonResponse.getStatus());
        }
        int statusCode = jsonResponse.getStatus();
        logger.log(Level.INFO, "status code={0}", statusCode);
        
        FacesMessage msg;
        
        if (statusCode == 200){
            
            String responseString = jsonResponse.getBody().toString();
            logger.log(Level.INFO, "response body={0}", responseString);
            // JsonPath
            // /data/id
            // /data/alias 
            // /data/name 
            
            JsonResponseParser jsonParser = new JsonResponseParser();
            
            String dataverseAlias = 
                    jsonParser.ParseTargetStringField(responseString, "/data/alias");
            logger.log(Level.INFO, "dataverseAlias={0}", dataverseAlias);
            String dataverseTitle =
                    jsonParser.ParseTargetStringField(responseString, "/data/name");
                // save the title 
                hostInfo.setDataversetitle(dataverseTitle);
            logger.log(Level.INFO, "dataverseTitle={0}", dataverseTitle);
            String dataverseId = 
                    jsonParser.ParseTargetNumericField(responseString, "/data/id").toString();
            
            logger.log(Level.INFO, "dataverseId={0}", dataverseId);
            hostInfo.setDataverseid(Long.parseLong(dataverseId));
            // set the placeholder value, 0L
            hostInfo.setDatasetid(0L);
            logger.log(Level.INFO, "saving Dataverse data::{0}", hostInfo);
            if (dataverseAlias.equals(selectedDataverseId)){
                logger.log(Level.INFO, "dataverseAlias is confirmed");
                
                
                saveHostInfo(hostInfo);
//                if (hostInfoFacade.findAll().size() > 0){
//                    logger.log(Level.INFO, "host_info table has been updated");
//                    homePageView.setHostInfoSaved(true);
//                } else {
//                    logger.log(Level.INFO, "host_info table has not been updated");
//                }

                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Confirmation success", 
                    "The Dataverse Alias was confirmed");
                Faces.getContext().addMessage("topMessage", msg);
                mainButtonEnabled=true;
                //return "/index.xhtml?faces-redirect=true";
                return "";
            } else {
                logger.log(Level.INFO, "Confirmation failure");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "The Dataverse alias was not confirmed", 
                    "Confirmation of the Dataverse alias failed: ");
                Faces.getContext().addMessage("topMessage", msg);
                mainButtonEnabled=false;
                return "";
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Reqequest Failure",
                    "Confirmation failed; correct data and/or resubmit again ");
            Faces.getContext().addMessage("topMessage", msg);
            mainButtonEnabled=false;
            return "";
            
            
        }
//        logger.log(Level.INFO, "========== DataverseConfirmationView#confirmDataverseData : end ==========");
        

    }
    
    void saveHostInfo(HostInfo hostInfo){
        hostInfoFacade.save(hostInfo);
    }
    
    public String returnToMainMenu(){
        logger.log(Level.INFO, "back to the main menu");
        return "/index.xhtml";
    }
    
        boolean mainButtonEnabled = false;

    public boolean isMainButtonEnabled() {
        return mainButtonEnabled;
    }

    public void setMainButtonEnabled(boolean value) {
        this.mainButtonEnabled = value;
    }
}
