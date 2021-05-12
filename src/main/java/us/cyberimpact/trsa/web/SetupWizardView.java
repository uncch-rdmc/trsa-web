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
import edu.unc.odum.dataverse.util.json.JsonPointerForDataset;
import edu.unc.odum.dataverse.util.json.JsonResponseParser;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonPatch;
import javax.json.JsonPatchBuilder;
import javax.ws.rs.WebApplicationException;
import org.apache.commons.lang3.StringUtils;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Faces;
import org.primefaces.event.FlowEvent;
import us.cyberimpact.trsa.entities.HostInfo;
import us.cyberimpact.trsa.entities.HostInfoFacade;

/**
 *
 * @author asone
 */
@Named(value = "setupWizardView")
@ViewScoped
public class SetupWizardView implements Serializable {

    private static final Logger logger = Logger.getLogger(SetupWizardView.class.getName());

    private List<String> subjectList = new ArrayList<>();
    
//    private String selectedSubject;
    
    private HostInfo hostInfo; 
    
    private SkeletalDataset skeletalDataset;
    
    
    @Inject
    private HostInfoFacade hostInfoFacade;
    
//    @Inject
//    private HomePageView homePageView;
    
    
//    @Inject 
//    private DestinationSelectionView destinationSelectionView;
//    
    private boolean datasetCreated=false;

    public boolean isDatasetCreated() {
        return datasetCreated;
    }

    public void setDatasetCreated(boolean datasetCreated) {
        this.datasetCreated = datasetCreated;
    }

    
    
    
    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "========== SetupWizardView#init : start ==========");
        
//        if (homePageView.isHostInfoSaved()){
////        if (StringUtils.isNotBlank(destinationSelectionView.getSelectedHostInfo().getDataversealias())){
//            logger.log(Level.INFO, "Target Dataverse is not null:{0}", 
//                    destinationSelectionView.getSelectedHostInfo());
//            hostInfo = destinationSelectionView.getSelectedHostInfo();
//        } else {
//            logger.log(Level.INFO, "information about the selected dataverse is missing");
            hostInfo = new HostInfo();
//        }
        subjectList.add("Agricultural Sciences");
        subjectList.add("Arts and Humanities");
        subjectList.add("Astronomy and Astrophysics");
        subjectList.add("Business and Management");
        subjectList.add("Chemistry");
        subjectList.add("Computer and Information Science");
        subjectList.add("Earth and Environmental Sciences");
        subjectList.add("Engineering");
        subjectList.add("Law");
        subjectList.add("Mathematical Sciences");
        subjectList.add("Medicine, Health and Life Sciences");
        subjectList.add("Physics");
        subjectList.add("Social Sciences");
        subjectList.add("Other");
        logger.log(Level.INFO, "subjectList={0}", subjectList);
        
        skeletalDataset = new SkeletalDataset();
        logger.log(Level.INFO, "skeletalDataset={0}", skeletalDataset);
//        logger.log(Level.INFO, "selectedSubject={0}", selectedSubject);
        logger.log(Level.INFO, "========== SetupWizardView#init : end ==========");
    }

    /**
     * Creates a new instance of SetupWizardView
     */
    public SetupWizardView() {
    }

    public List<String> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(List<String> subjectList) {
        this.subjectList = subjectList;
    }

//    public String getSelectedSubject() {
//        return selectedSubject;
//    }
//
//    public void setSelectedSubject(String selectedSubject) {
//        this.selectedSubject = selectedSubject;
//    }

    public HostInfo getHostInfo() {
        return hostInfo;
    }

    public void setHostInfo(HostInfo hostInfo) {
        this.hostInfo = hostInfo;
    }

    public SkeletalDataset getSkeletalDataset() {
        return skeletalDataset;
    }

    public void setSkeletalDataset(SkeletalDataset skeletalDataset) {
        this.skeletalDataset = skeletalDataset;
    }

    
    
    private String assignedDatasetId;

    public String getAssignedDatasetId() {
        return assignedDatasetId;
    }

    public void setAssignedDatasetId(String assignedDatasetId) {
        this.assignedDatasetId = assignedDatasetId;
    }
    
    private String assignedDatasetDoi;

    public String getAssignedDatasetDoi() {
        return assignedDatasetDoi;
    }

    public void setAssignedDatasetDoi(String assignedDatasetDoi) {
        this.assignedDatasetDoi = assignedDatasetDoi;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    private boolean skip;

    public void save() {
        logger.log(Level.INFO, "========== SetupWizardView#save : start ==========");



        logger.log(Level.INFO, "skeletalDataset={0}", skeletalDataset);
//        logger.log(Level.INFO, "selectedSubject={0}", selectedSubject);
        logger.log(Level.INFO, "hostInfor={0}", hostInfo);
        logger.log(Level.INFO, "data are going to be persisted");
        
        // step 1: create a new, empty dataset and get IDs
        
        createEmptyDataset();
        
        logger.log(Level.INFO, "========== SetupWizardView#save : end ==========");

        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
        "Creating an empty Dataset was Successful", 
                "A new Dataset has been created: ID="+ assignedDatasetDoi);
         Faces.getContext().addMessage("topGrowl", msg);
    }
    
    // EMPTY_DATASET
    public void createEmptyDataset() {
        logger.log(Level.INFO, "========== SetupWizardView#createEmptyDataset: start ==========");
        logger.log(Level.INFO, "skeletalDataset={0}", skeletalDataset);
        logger.log(Level.INFO, "hostInfor={0}", hostInfo);

        
        
            // The following line intends to save some of the returned data of
            // a newly created Dataset in a row of host_info table
            // for the very first time a host_info row is updated 
            // after a Dataverse is newly registered 
            // this means check how many rows for each Dataverse and 
            // if it is just one, then it is update otherwise creation
            boolean isUpdateCase=isUpdateCase(hostInfo);
            logger.log(Level.INFO, "isUpdateCase={0}", isUpdateCase);
//            List<HostInfo> dvResultList = 
//                hostInfoFacade.findByDataverseId(hostInfo.getDataverseid());
//            
//            if (dvResultList.size() == 1){
//                logger.log(Level.INFO, "hostInfo row ={0}", dvResultList);
//                // there is a row: there are two cases:
//                // create a new row (Dataset) or update the current row 
//                if (dvResultList.get(0).getDatasetDoi() == null){
//                    logger.log(Level.INFO, "hostInfo: doi is null");
//                    // this is a case of updating the exisitng row
//                    isUpdateCase=true;
//                } else {
//                    logger.log(Level.INFO, "hostInfo: doi is not null:{0}" , 
//                            dvResultList.get(0).getDatasetDoi());
//                    // this is a case of adding a new row (Dataset)
//                }
//            } else if (dvResultList.size() > 1) {
//               // do nothing: adding a new row to a host_info
//            }
            

        
        
        
        
        
        
        // strategy
        // 1. read back the saved template
        // 2. path customizable parts with saved DB data

        String skeletalDatasetTemplate = "/json/min-data-to-create-dataset.json";
        
        try (InputStream intemp
            = SetupWizardView.class.getResourceAsStream(skeletalDatasetTemplate)) {
            
            JsonObject dsTemplate = Json.createReader(intemp).readObject();
            
            // replace boiler-plates with web-GUI-captured ones
            JsonObject dsPayload = prepareJsonPatch(skeletalDataset).apply(dsTemplate);
            
            logger.log(Level.INFO, "payload to create a new Dataset={0}", dsPayload);
            
            String jsonBody = dsPayload.toString();

            String apiKey = hostInfo.getApitoken();
            String dataverseServer = hostInfo.getHosturl();
            String selectedDataverseId = hostInfo.getDataversealias();
            String apiEndpoint = dataverseServer
                    + WebAppConstants.PATH_DATAVERSE_API
                    + selectedDataverseId
                    + "/datasets";

            logger.log(Level.INFO, "apiEndpoint={0}", apiEndpoint);

            Map<String, String> headers = new HashMap<>();
            headers.put("X-Dataverse-key", apiKey);
            headers.put("X-TRSA-registrationId", getRegisteredTrsaId());

            HttpResponse<JsonNode> jsonResponse = null;
            
            try {
                jsonResponse = Unirest.post(apiEndpoint)
                        .headers(headers)
                        .queryString("identifier", selectedDataverseId)
                        .body(jsonBody)
                        .asJson();
            } catch (UnirestException ex) {
                logger.log(Level.SEVERE, "Unirest request error", ex);
                throw new WebApplicationException(ex, jsonResponse.getStatus());
            }

            logger.log(Level.INFO, "status code={0}", jsonResponse.getStatus());

            String responseString = jsonResponse.getBody().toString();
            logger.log(Level.INFO, "response body={0}", responseString);

            JsonResponseParser jsonParser = new JsonResponseParser();

            assignedDatasetId = 
                jsonParser.parseDatasetIdFromCreationResponse(responseString);

            logger.log(Level.INFO, "assignedDatasetId={0}", assignedDatasetId);

            assignedDatasetDoi = 
                jsonParser.parseDatasetDoiFromDsCreationResponse(responseString);
            
            logger.log(Level.INFO, "assignedDatasetDoi={0}", assignedDatasetDoi);
            
            hostInfo.setDatasetid(Long.parseLong(assignedDatasetId));
            hostInfo.setDatasetDoi(assignedDatasetDoi);
            hostInfo.setDatasetTitle(skeletalDataset.getDatasetTitle());
            // missing data
            /// dataset-title
            // dataverse title
            // dataverse id
            if (isUpdateCase){
                logger.log(Level.INFO, "updating the row");
                hostInfoFacade.edit(hostInfo);
            } else {
                logger.log(Level.INFO, "saving this dataset");
                hostInfoFacade.save(hostInfo);
            }
            // disable the button "create a dataset"
            datasetCreated=true;
        
        

        } catch (IOException ie) {
            logger.log(Level.INFO, "IOException was thrown", ie);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Creating an Empty dataset failed",
                    "Creating a new empty Dataset failed due to IOException");
            Faces.getContext().addMessage("topMessage", msg);
            return;
        }
        logger.log(Level.INFO, "========== SetupWizardView#createEmptyDataset: end ==========");
    }
    
    
    private boolean isUpdateCase(HostInfo hostInfo){
        logger.log(Level.INFO, "========== SetupWizardView#isUpdateCase: start ==========");
            // The following line intends to save some of the returned data of
            // a newly created Dataset in a row of host_info table
            // for the very first time a host_info row is updated 
            // after a Dataverse is newly registered 
            // this means check how many rows for each Dataverse and 
            // if it is just one, then it is update otherwise creation
            
            // default is set to "not update, create a new row"
            boolean isUpdateCase=false;
            
            List<HostInfo> dvResultList = 
                hostInfoFacade.findByDataverseId(hostInfo.getDataverseid());
            
            if (dvResultList.size() == 1){
                logger.log(Level.INFO, "hostInfo row ={0}", dvResultList);
                // there is a row: there are two cases:
                // create a new row (Dataset) or update the current row 
                
                // warning: datasetdoi is a string and it would be empty, "", not null
                if (StringUtils.isBlank(dvResultList.get(0).getDatasetDoi())){
                    logger.log(Level.INFO, "hostInfo: doi is null or empty");
                    // this is a case of updating the exisitng row
                    isUpdateCase=true;
                } else {
                    logger.log(Level.INFO, "hostInfo: doi is not null:{0}" , 
                            dvResultList.get(0).getDatasetDoi());
                    // this is a case of adding a new row (Dataset)
                }
            } else if (dvResultList.size() > 1) {
               // do nothing: adding a new row to a host_info
            }
        logger.log(Level.INFO, "isUpdateCase={0}", isUpdateCase);
        logger.log(Level.INFO, "========== SetupWizardView#isUpdateCase: end ==========");
        return isUpdateCase;
    }
    
    
    private String getRegisteredTrsaId(){
        logger.log(Level.INFO, "hostInfo={0}", hostInfo);
        if (hostInfo.getTrsaRegNmbr()!= null){
            return hostInfo.getTrsaRegNmbr().toString();
        } else {
            return "1";
        }
    }
    
    
    
    public JsonPatch prepareJsonPatch(SkeletalDataset skeletalDataset){
        logger.log(Level.INFO, "========== SetupWizardView#prepareJsonPatch : start ==========");
        JsonArray subject = skeletalDataset.getSubject() != null ? Json.createArrayBuilder().
                add(skeletalDataset.getSubject()).build() : null;
            JsonPatchBuilder jPBuilder = Json.createPatchBuilder();
            JsonPatch jsonPatch = jPBuilder
                .replace(JsonPointerForDataset.POINTER_TO_DATASET_TITLE, skeletalDataset.getDatasetTitle())
                .replace(JsonPointerForDataset.POINTER_TO_DATASET_AUTHOR_AFFILIATION, skeletalDataset.getAuthorAffiliation())
                .replace(JsonPointerForDataset.POINTER_TO_AUTHOR_NAME, skeletalDataset.getAuthorName())
                .replace(JsonPointerForDataset.POINTER_TO_EMAIL, skeletalDataset.getDatasetContactEmail())
                .replace(JsonPointerForDataset.POINTER_TO_DATASET_DESCRIPTION, skeletalDataset.getDsDescriptionValue())
                .replace(JsonPointerForDataset.POINTER_TO_SUBJECTS, subject)
                    .build();
            logger.log(Level.INFO, "JsonPatchForDatasetTemplate#prepareJsonPatch:jsonPatch=\n{0}", jsonPatch);
        logger.log(Level.INFO, "========== SetupWizardView#prepareJsonPatch : end ==========");
        return jsonPatch;
    }
    
    
    public String confirmDataverseData(){
        // make a call to Dataverse and confirm entered data
        logger.log(Level.INFO, "========== SetupWizardView#confirmDataverseData : start ==========");
        logger.log(Level.INFO, "hostInfor to be confirmed ={0}", hostInfo);
        // calling the dataverse info
        // to build an api endpoint like below: 
        // http://localhost:8083/api/dataverses/dv-trsa-api
        
        String apiKey = hostInfo.getApitoken();
        String dataverseServer = hostInfo.getHosturl();
        String selectedDataverseId = hostInfo.getDataversealias();
        String apiEndpoint = dataverseServer
                + WebAppConstants.PATH_DATAVERSE_API
                + selectedDataverseId;

        logger.log(Level.INFO, "apiEndpoint={0}", apiEndpoint);

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Dataverse-key", apiKey);
        headers.put("X-TRSA-registrationId", getRegisteredTrsaId());

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
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return "/index.xhtml?faces-redirect=true";
            } else {
                logger.log(Level.INFO, "Confirmation failure");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "The Dataverse alias was not confirmed", 
                    "Confirmation of the Dataverse alias failed: ");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return "";
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Reqequest Failure",
                    "Confirmation failed; correct data and/or resubmit again ");
            Faces.getContext().addMessage("messages", msg);
            return "";
            
            
        }
//        logger.log(Level.INFO, "========== SetupWizardView#confirmDataverseData : end ==========");
        

    }
    
    
    void saveHostInfo(HostInfo hostInfo){
        hostInfoFacade.save(hostInfo);
    }
    
    
    public String gotoSelectDataverse(){
        return "/selectDataverse.xhtml";
    }
    
    
    
    // ========================================================================
    
    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public String onFlowProcess(FlowEvent event) {
        if (skip) {
            skip = false;
            return "confirm";
        } else {
            return event.getNewStep();
        }
    }

}
