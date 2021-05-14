package us.cyberimpact.trsa.web;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import edu.harvard.iq.dataverse.entities.DataFile;
import edu.harvard.iq.dataverse.entities.DataFileFacade;
import edu.harvard.iq.dataverse.entities.DatasetVersion;
import edu.harvard.iq.dataverse.entities.DatasetVersionFacade;
import edu.harvard.iq.dataverse.ingest.IngestService;
import edu.harvard.iq.dataverse.util.SystemConfig;
import edu.unc.odum.dataverse.util.json.JsonPointerForDataset;
import edu.unc.odum.dataverse.util.json.JsonResponseParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonPatchBuilder;
import javax.json.JsonPointer;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.ws.rs.WebApplicationException;
import org.apache.commons.lang3.StringUtils;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Faces;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;
import us.cyberimpact.trsa.entities.HostInfo;
import us.cyberimpact.trsa.settings.AppConfig;

/**
 *
 * @author asone
 */
@Named(value = "submissionPageView")
@ViewScoped
public class SubmissionPageView implements Serializable {

    private static final Logger logger = Logger.getLogger(SubmissionPageView.class.getName());
    /**
     * Creates a new instance of SubmissionPageView
     */
    public SubmissionPageView() {
    }
    
    // injecting objects
    
//    
//    @Inject
//    private HomePageView homePageView;
    
//    private RequestType selectedRequestType=homePageView.getSelectedRequestType();
    private RequestType selectedRequestType;
    
//    @Inject
//    private DestinationSelectionView destSelectionView;
    
    private HostInfo selectedHostInfo;// =  destSelectionView.getSelectedHostInfo();
    
//    @Inject
//    private HostInfoFacade hostInfoFacade;

    public HostInfo getSelectedHostInfo() {
        return selectedHostInfo;
    }

    public void setSelectedHostInfo(HostInfo selectedHostInfo) {
        this.selectedHostInfo = selectedHostInfo;
    }
    
    
//    @Inject
//    private FileUploadView fileUploadView;
    
    @Inject
    private AppConfig appConfig; 
    
    @Inject
    private DataFileFacade dataFileFacade;
    
    
    @Inject
    DatasetVersionFacade datasetVersionFcd;
    
    @Inject
    IngestService ingestService;
//    private HostInfo hostInfo; 
//
//    public HostInfo getHostInfo() {
//        return hostInfo;
//    }
//
//    public void setHostInfo(HostInfo hostInfo) {
//        this.hostInfo = hostInfo;
//    }
    
    private String selectedDataverseId;

    public String getSelectedDataverseId() {
        return selectedDataverseId;
    }

    public void setSelectedDataverseId(String selectedDataverseId) {
        this.selectedDataverseId = selectedDataverseId;
    }

    
    
    
    private String localDatasetIdentifier;

    public String getLocalDatasetIdentifier() {
        return localDatasetIdentifier;
    }

    public void setLocalDatasetIdentifier(String localDatasetIdentifier) {
        this.localDatasetIdentifier = localDatasetIdentifier;
    }
    
    String selectedDatasetId;

    public String getSelectedDatasetId() {
        return selectedDatasetId;
    }

    public void setSelectedDatasetId(String selectedDatasetId) {
        this.selectedDatasetId = selectedDatasetId;
    }
    
    String assignedDatasetId;

    public String getAssignedDatasetId() {
        return assignedDatasetId;
    }

    public void setAssignedDatasetId(String assignedDatasetId) {
        this.assignedDatasetId = assignedDatasetId;
    }
    
    String doiServerPrefix; 
    private String trsaFilesPath="";
//    String targetDataverseId;
//
//    public String getTargetDataverseId() {
//        return targetDataverseId;
//    }
//
//    public void setTargetDataverseId(String targetDataverseId) {
//        this.targetDataverseId = targetDataverseId;
//    }
    
    
    private boolean notaryServiceBound;
    
    public boolean isNotaryServiceBound() {
        return notaryServiceBound;
    }
 
    public void setNotaryServiceBound(boolean nsBound) {
        this.notaryServiceBound = nsBound;
    }
    
    private boolean notaryServiceEnabled=false;
    
    private String ingestedFilename;
    
    
    
    
    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "========== SubmissionPageView#init() start ==========");
        //selectedRequestType=homePageView.getSelectedRequestType();
        selectedRequestType=Faces.getSessionAttribute("selectedRequestType");
        //selectedHostInfo =  destSelectionView.getSelectedHostInfo();
        selectedHostInfo = Faces.getSessionAttribute("selectedHostInfo");
        logger.log(Level.INFO, "#init: selectedRequestType={0}", selectedRequestType);

        
        logger.log(Level.INFO, "SubmissionPageView#init: selectedHostInfo={0}", selectedHostInfo);
        if (selectedHostInfo == null){
            logger.log(Level.INFO, "selectedHostInfo is null");
            // TODO
            // error message here and maybe to be forwarded to the host_info page
        } else {
            logger.log(Level.INFO, "SubmissionPageView#init(): selectedHostInfo={0}", selectedHostInfo);
            
            //publishButtonEnabled=true;
            logger.log(Level.INFO, "SubmissionPageView#publishButtonEnabled={0}", publishButtonEnabled);
            
        }
        // here setup common data 
        apiKey = selectedHostInfo.getApitoken();//apiKeyHCValue ;
        logger.log(Level.INFO, "SubmissionPageView:init():apiKey={0}", apiKey);
        
        // target dataverse server
        dataverseServer = selectedHostInfo.getHosturl();//dataverseServerHCValue;
        logger.log(Level.INFO, "dataverseServer={0}", dataverseServer);
        
        String datasetIdFromFileUploadView = Faces.getSessionAttribute("datasetIdentifier");
        logger.log(Level.INFO, "datasetIdFromFileUploadView={0}", datasetIdFromFileUploadView);
        ingestedFilename = Faces.getSessionAttribute("fileName");
        logger.log(Level.INFO, "ingestedFilename received={0}", ingestedFilename);
        filenameonly = Faces.getSessionAttribute("fileNameOnly");
        logger.log(Level.INFO, "filenameonly received={0}", filenameonly);
        
        
        trsaRegNmbr = selectedHostInfo.getTrsaRegNmbr();
        logger.log(Level.INFO, "trsaRegNmbr={0}", trsaRegNmbr);
        
        
        // here setup data for each submission request type
        switch (selectedRequestType) {
            case EMPTY_DATASET:
                logger.log(Level.INFO, "EMPTY_DATASET case");
                // DataverseId is required 
                selectedDataverseId = Long.toString(selectedHostInfo.getDataverseid());
                logger.log(Level.INFO, "SubmissionPageView#init(): selectedDataverseId={0}", selectedDataverseId);
                break;
            case FULL_DATASET:
                logger.log(Level.INFO, "FULL_DATASET case");
                // localDatasetID must be saved
                localDatasetIdentifier =  datasetIdFromFileUploadView;//fileUploadView.getDatasetIdentifier();
                logger.log(Level.INFO, "SubmissionPageView#init():localDatasetIdentifier passed={0}", localDatasetIdentifier);
                // DataverseId is required 
                selectedDataverseId = Long.toString(selectedHostInfo.getDataverseid());
                logger.log(Level.INFO, "SubmissionPageView#init():selectedDataverseId={0}", selectedDataverseId);
                break;
            case METADATA_ONLY:
                logger.log(Level.INFO, "METADATA_ONLY case");
                // localDatasetID must be saved
                localDatasetIdentifier = datasetIdFromFileUploadView;//fileUploadView.getDatasetIdentifier();
                logger.log(Level.INFO, "SubmissionPageView:init():localDatasetIdentifier passed={0}", localDatasetIdentifier);
                // DatasetId is required
                selectedDatasetId =  Long.toString(selectedHostInfo.getDatasetid());
                logger.log(Level.INFO, "selectedDatasetId={0}", selectedDatasetId);
                break;
            default:
                logger.log(Level.SEVERE, "Some uxexpected request was chosen");
                throw new IllegalArgumentException();
        }
        logger.log(Level.INFO, "end of the switch statement within init()");
        
        
        doiServerPrefix = SystemConfig.DOI_SERVER_PREFIX;
        logger.log(Level.INFO, "doiServerPrefix={0}", doiServerPrefix);
        
        trsaFilesPath= appConfig.getTrsaFilesPath();
        logger.log(Level.INFO, "appConfig.getTrsaFilesPath={0}", trsaFilesPath);
        
        if (appConfig.isNotaryServiceEnabled()){
            notaryServiceEnabled=true;
        }
        notaryServiceBound=notaryServiceEnabled;
        logger.log(Level.INFO, "notaryServiceBound={0}", notaryServiceBound);
        
        
//        fileIdList= fileUploadView.getFileIdList();
//        logger.log(Level.INFO, "fileIdList={0}", fileIdList);
//        if (fileIdList.isEmpty()){
//            logger.log(Level.WARNING, "datafile id is missing");
//        } else {
//            logger.log(Level.INFO, "the number of datafiles to be uploaded is {0}", fileIdList.size());
//        }
        
        ingestedDataFileList = Faces.getSessionAttribute("ingestedDataFileList");//fileUploadView.getIngestedDataFileList();
        if (ingestedDataFileList.isEmpty()){
            logger.log(Level.WARNING, "datafile is empty");
        } else {
            logger.log(Level.INFO, "the number of datafiles to be uploaded is {0}", ingestedDataFileList.size());
            logger.log(Level.INFO, "ingestedDataFileList={0}", ingestedDataFileList.get(0));
        }
        datasetDoi = selectedHostInfo.getDatasetDoi();
        logger.log(Level.INFO, "datasetDoi={0}", datasetDoi);
        
        logger.log(Level.INFO, "========== SubmissionPageView#init() end ==========");
    }
    
    
    // fields for request parameters
    
    String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    
    String dataverseServer;

    public String getDataverseServer() {
        return dataverseServer;
    }

    public void setDataverseServer(String dataverseServer) {
        this.dataverseServer = dataverseServer;
    }
    
    
    private String ingestedFile;

    public String getIngestedFile() {
//        return  (fileUploadView != null) && 
//                (fileUploadView.getFileName() != null) ?
//                fileUploadView.getFileName() : "N/A";
        
        return ingestedFilename!= null ? ingestedFilename : "N/A";
    }
    
    public void setIngestedFile(String ingestedFile) {
        this.ingestedFile = ingestedFile;
    }
    
    
    private Long trsaRegNmbr;

    public Long getTrsaRegNmbr() {
        return trsaRegNmbr;
    }

    public void setTrsaRegNmbr(Long trsaRegNmbr) {
        this.trsaRegNmbr = trsaRegNmbr;
    }
    
    private String filenameonly;

    public String getFilenameonly() {
        return filenameonly;
    }

    public void setFilenameonly(String filenameonly) {
        this.filenameonly = filenameonly;
    }
    
    private String datasetDoi;

    public String getDatasetDoi() {
        return datasetDoi;
    }

    public void setDatasetDoi(String datasetDoi) {
        this.datasetDoi = datasetDoi;
    }
    
    
    
    // Facade for all three cases
    
    public void submissionFacade(){
        switch(selectedRequestType){
            case EMPTY_DATASET:
                createEmptyDataset();
                break;
            case FULL_DATASET:
                createNewDataset();
                break;
            case METADATA_ONLY:
                uploadMetadataOnly();
                break;
            default:
                logger.log(Level.SEVERE, "Some uxexpected request was chosen");
                throw new IllegalArgumentException();
                
        }
        
    }
    
    // EMPTY_DATASET
    void createEmptyDataset(){
        logger.log(Level.INFO, "SubmissionPageView#createEmptyDataset");
        // strategy
        // read a template for a minimumDataset as JSON
        // pointing to the payload file
        
        
        String templateDirectory = System.getProperty(WebAppConstants.TRSA_TEMPLATE_DIRECTORY);
        // -Dtrsa.template.directory
        // = /home/asone/myopt/payara5/glassfish/domains/domain1/config/trsa/template
        // <jvm-options>-Dtrsa.template.directory=${com.sun.aas.instanceRoot}/config/trsa/template</jvm-options>
        String templateFileName=System.getProperty(WebAppConstants.TRSA_TEMPLATE_FILE_NAME);
        String fileLocation = templateDirectory +"/"
                + templateFileName ;
        String payloadFileName = trsaFilesPath
                + localDatasetIdentifier + "/" + WebAppConstants.FILTERED_PAYLOAD_FILENAME;
        
        
        // 1. read back the saved template
        // 2. path customizable parts with saved DB data
        
        try (InputStream rawIs = new FileInputStream(new File(fileLocation));
                JsonReader jsonReader = Json.createReader(rawIs);
                PrintWriter printWriter = new PrintWriter(new File(payloadFileName), "UTF-8");
                JsonWriter jsonWriter = Json.createWriter(printWriter)) {
            
            JsonObject rawJsonObject = jsonReader.readObject();
            logger.log(Level.INFO, "rawJsonObject={0}", rawJsonObject);
            // Dataset title 
            // 
        } catch (IOException ie){
            // TODO 
            // create the error message
            // call the sweeping method
            // return
        }
    }
    
    // FULL_DATASET
    void createNewDataset(){
        logger.log(Level.INFO, "SubmissionPageView#createNewDataset");
        // pointing to the payload file
        String fileLocation = trsaFilesPath
                + localDatasetIdentifier + "/" + WebAppConstants.EXPORT_FILE_NAME_JSON;
        String payloadFileName = trsaFilesPath
                + localDatasetIdentifier + "/" + WebAppConstants.FILTERED_PAYLOAD_FILENAME;
        
        String jsonBody="";
        try (InputStream rawIs = new FileInputStream(new File(fileLocation));
                JsonReader jsonReader = Json.createReader(rawIs);
                PrintWriter printWriter = new PrintWriter(new File(payloadFileName), "UTF-8");
                JsonWriter jsonWriter = Json.createWriter(printWriter)) {
            
            JsonObject rawJsonObject = jsonReader.readObject();
            logger.log(Level.INFO, "rawJsonObject={0}", rawJsonObject);

            // create a new Json object to store two JsonPointers
            JsonObject object = Json.createObjectBuilder().build();
            // Warning: The following lines work with Java-ee-api-8 package
            // i.e., not 7 that does not include JsonPointer API
            // create the two JsonPointer instances 
            
            // pointerto the metadataBlock segment from the raw JSON object
            JsonPointer metadataBlock
                    = Json.createPointer(JsonPointerForDataset.POINTER_METADATABLOCKS);
            // pointer to files segment, too
            JsonPointer files = Json.createPointer(JsonPointerForDataset.POINTER_FILES);
            
            // get the value for each of the above JsonPointer instances
            JsonValue metadataBlockValue = metadataBlock.getValue(rawJsonObject);
            JsonValue filesValue = files.getValue(rawJsonObject);
            // create a JsonPatchBuilder object and add JsonValue instances
            JsonPatchBuilder builder = Json.createPatchBuilder();
            
            // create the following simpler structure from the raw JSON file
            // {"datasetVersion": {"metadataBlocks": {}, "files": []}}
            
            JsonObject payloadObject = Json.createObjectBuilder().add(
                    "datasetVersion", builder
                            .add(JsonPointerForDataset.POINTER_METADATABLOCKS_FILTERED, metadataBlockValue)
                            .add(JsonPointerForDataset.POINTER_FILES_FILTERED, filesValue)
                            .build()
                            .apply(object)
                    ).build();

            logger.log(Level.INFO, "finalized payload JSON object={0}", payloadObject);
            jsonWriter.writeObject(payloadObject);
            jsonBody = payloadObject.toString(); 

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IOException was thrown during io operations", ex);
            // TODO 
            // create the error message
            // call the sweeping method
            // return
        }
        
        logger.log(Level.FINE, "jsonbody={0}", jsonBody);
        logger.log(Level.INFO, "create a new Dataset case");
        String apiEndpoint = dataverseServer 
                    + WebAppConstants.PATH_DATAVERSE_API
                    //+ "/api/dataverses/"
                    + selectedDataverseId
                    +"/datasets";
        logger.log(Level.INFO, "apiEndpoint={0}", apiEndpoint);
            
        HttpResponse<JsonNode> jsonResponse=null;
        try {
            jsonResponse = Unirest.post(apiEndpoint)
                    .header("X-Dataverse-key", apiKey)
                    .queryString("identifier", selectedDataverseId)
                    .body(jsonBody)
                    .asJson();
        } catch (UnirestException ex) {
            logger.log(Level.SEVERE, "Unirest request error", ex);
            throw new WebApplicationException(ex, jsonResponse.getStatus());
        }
        responseBody = jsonResponse.getBody().toString();
        commonPostRequestStep(jsonResponse);
        parseResponseBodyFulldataset();
        //clearSession();
    }
    
    // METADATA_ONLY
    void uploadMetadataOnly(){
        logger.log(Level.INFO, "========== SubmissionPageView#uploadMetadataOnly: start ==========");
        logger.log(Level.INFO, "uploadMetadataOnly case");
        
        // sanity check one more tiem before submission
        try {
            if (StringUtils.isBlank(apiKey) || 
                    StringUtils.isBlank(dataverseServer) ||
            StringUtils.isBlank(selectedDatasetId) || 
                    StringUtils.isBlank(trsaRegNmbr.toString())){
                String message="API key, dataverse server name, and trsa-registration number are incomplete ";
                throw new IllegalArgumentException(message);
            }
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Data to construct the API endpoint is incomplete");
            String message ="Data to construct the API endpoint is incomplete; complete data before submission";
            Faces.getContext().addMessage("topMessage", 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
             "Incomplete destination data", message));
            publishButtonEnabled=false;
            return;
        }
        
        String fileLocation = trsaFilesPath
                + localDatasetIdentifier
                + "/" + WebAppConstants.EXPORT_FILE_NAME_JSON;
        
        logger.log(Level.FINE, "fileLocation={0}", fileLocation);
        String payloadFileName = trsaFilesPath
                + localDatasetIdentifier
                + "/" + WebAppConstants.FILTERED_PAYLOAD_FILENAME;
        logger.log(Level.FINE, "payloadFileName={0}", payloadFileName);
        
        
        
        try (InputStream rawIs = new FileInputStream(new File(fileLocation));
                JsonReader jsonReader = Json.createReader(rawIs);
                PrintWriter printWriter = new PrintWriter(new File(payloadFileName), "UTF-8");
                JsonWriter jsonWriter = Json.createWriter(printWriter)) {

            JsonObject rawJsonObject = jsonReader.readObject();
            logger.log(Level.INFO, "rawJsonObject={0}", rawJsonObject);

            // create a new Json object to store two JsonPointers
            JsonObject object = Json.createObjectBuilder().build();
            // Warning: The following lines work with Java-ee-api-8 package
            // i.e., not 7 that does not include JsonPointer API
            // create the two JsonPointer instances 
            // point to the metadataBlock segment from the raw JSON object
            JsonPointer metadataBlock
                    = Json.createPointer(JsonPointerForDataset.POINTER_METADATABLOCKS);
            // point to files segment, too
            JsonPointer files = Json.createPointer(JsonPointerForDataset.POINTER_FILES);

            // get the value for each of the above JsonPointer instances
            JsonValue metadataBlockValue = metadataBlock.getValue(rawJsonObject);
            JsonValue filesValue = files.getValue(rawJsonObject);

            // create a JsonPatchBuilder object and add JsonValue instances
            JsonPatchBuilder builder = Json.createPatchBuilder();

                    
            JsonObject payloadObject = builder
                            .add(JsonPointerForDataset.POINTER_METADATABLOCKS_FILTERED, metadataBlockValue)
                            .add(JsonPointerForDataset.POINTER_FILES_FILTERED, filesValue)
                            .build()
                            .apply(object);

            logger.log(Level.INFO, "payload JSON object={0}", payloadObject);
            jsonWriter.writeObject(payloadObject);
            jsonBody = payloadObject.toString(); 
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IOException was thrown during io operations");
            // TODO
            // call the sweeping method
            String message ="Payload creation failed with IOException: check server.log";
                Faces.getContext().addMessage("topMessage", 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                                "Payload-creation Failure before submission", message));
            publishButtonEnabled=false;
            return;
        }

        logger.log(Level.INFO, "payload: jsonbody={0}", jsonBody);

        logger.log(Level.INFO, "submit-metadata-only API case");
        String apiEndpoint = dataverseServer + "/api/datasets/" + selectedDatasetId
                + "/" + WebAppConstants.PATH_ADD_METADATA;
        logger.log(Level.INFO, "apiEndpoint={0}", apiEndpoint);
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Dataverse-key", apiKey);
        headers.put("X-TRSA-registrationId", trsaRegNmbr.toString());

        HttpResponse<JsonNode> jsonResponse=null;
        try {
            jsonResponse = Unirest.post(apiEndpoint)
                    .headers(headers)
                    .body(jsonBody)
                    .asJson();
        } catch (UnirestException ex) {
            logger.log(Level.SEVERE, "Submission failed with UnirestException");
            // TODO 
            // call the sweeping method
            String message ="Submission preparation failed with UnirestException";
                Faces.getContext().addMessage("topMessage", 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                                "Submission Failure", message));
            publishButtonEnabled=false;
            return;
        }
        logger.log(Level.INFO, "Submission ended anyway; check the return code");
        responseBody = jsonResponse.getBody().toString();
        commonPostRequestStep(jsonResponse);
        parseResponseBodyMetadataOnly();
        viewDetailButtonEnabled=true;
        logger.log(Level.INFO, "========== SubmissionPageView#uploadMetadataOnly: end ==========");
    }
    
    private void commonPostRequestStep(HttpResponse<JsonNode> jsonResponse){
        logger.log(Level.INFO, "#commonPostRequestStep(): start");
        
        
        if (jsonResponse !=null){
            int statusCode = jsonResponse.getStatus();
            logger.log(Level.INFO, "status code={0}", statusCode);

            if (statusCode== 200 || statusCode==201){
                logger.log(Level.INFO, "status code was OK:{0}", statusCode);
                String message ="Metadata were successfully submitted";
                Faces.getContext().addMessage("topMessage", 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, 
                                "Submission Success", message));
                
                publishButtonEnabled=false;
            } else {
                // returned code is not 200 nor 201
                logger.log(Level.WARNING, "Status code is not 200:{0}",
                        statusCode);
                String message ="Submission failed with the status code="+statusCode;
                Faces.getContext().addMessage("topMessage", 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                                "Submission Failure", message));
                publishButtonEnabled=false;
                
            }
        } else {
            logger.log(Level.WARNING, "Response is null");
            // failed locally or no response
            String message ="Failed to get a response from the Dataverse server";
                Faces.getContext().addMessage("topMessage", 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
             "Submission Failure: Null response", message));
            publishButtonEnabled=false;
            
        }
        logger.log(Level.INFO, "#commonPostRequestStep(): end");
        

    }
    
    private void parseResponseBodyFulldataset(){
        
        logger.log(Level.FINE, "response body={0}", responseBody);
        JsonResponseParser jsonParser = new JsonResponseParser();

        assignedDatasetId = jsonParser.parseDatasetIdFromCreationResponse(responseBody);
        
        logger.log(Level.INFO, "assignedDatasetId={0}", assignedDatasetId);

        returnedDatasetDoi = jsonParser.parseDatasetDoiFromDsCreationResponse(responseBody);
        logger.log(Level.INFO, "returnedDatasetDoi={0}", returnedDatasetDoi);
        
        datasetDoiUrl= doiServerPrefix+ returnedDatasetDoi.split(":")[1];
        logger.log(Level.INFO, "datasetDoiUrl={0}", datasetDoiUrl);
    }
    
    private void parseResponseBodyMetadataOnly(){
        
        //logger.log(Level.FINE, "response body={0}", responseBody);
        // TODO
        // get key data from the response body
//        JsonResponseParser jsonParser = new JsonResponseParser();

//        assignedDatasetId = jsonParser.parseDatasetIdFromCreationResponse(responseString);
//        
//        logger.log(Level.INFO, "assignedDatasetId={0}", assignedDatasetId);
//
//        returnedDatasetDoi = jsonParser.parseDatasetDoiFromDsCreationResponse(responseString);
//        logger.log(Level.INFO, "returnedDatasetDoi={0}", returnedDatasetDoi);
//        
//        datasetDoiUrl= doiServerPrefix+ returnedDatasetDoi.split(":")[1];
//        logger.log(Level.INFO, "datasetDoiUrl={0}", datasetDoiUrl);
    }
    
    
    private void clearSession(){
        logger.log(Level.INFO, "sessionscoped data are reset");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }
    
    
    public boolean isEmptyDatasetCreation(){
        return selectedRequestType == RequestType.EMPTY_DATASET;
    }    
    
    
    boolean publishButtonEnabled=false;

    public boolean isPublishButtonEnabled() {
        return publishButtonEnabled;
    }

    public void setPublishButtonEnabled(boolean publishButtonEnabled) {
        this.publishButtonEnabled = publishButtonEnabled;
    }
    
    public void goToDataverseSite() {
        // atach "/dataverse/${dataverse-aias}"
        String dataversePath="/dataverse/"+ selectedHostInfo.getDataversealias();
        try {
            goToExternalSite(dataverseServer+dataversePath);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IOException was thrown before tried to jump to the site", ex);
        }
    }

    public void goToExternalSite(String url) throws IOException {
        logger.log(Level.INFO, "external url={0}", url);
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();

        externalContext.redirect(url);
        //clearSession();
    }
    
    
    boolean gotoDataverseButtonEnabled=false;

    public boolean isGotoDataverseButtonEnabled() {
        return gotoDataverseButtonEnabled;
    }

    public void setGotoDataverseButtonEnabled(boolean value) {
        this.gotoDataverseButtonEnabled = value;
    }
    
    public String goToMainMenu(){
        //clearSession();
        logger.log(Level.INFO, "return to the Main menu");
        return "/index.xhtml?faces-redirect=true";
    }
    
    String returnedDatasetDoi;

    public String getReturnedDatasetDoi() {
        return returnedDatasetDoi;
    }

    public void setReturnedDatasetDoi(String returnedDatasetDoi) {
        this.returnedDatasetDoi = returnedDatasetDoi;
    }
    
    String datasetDoiUrl;

    public String getDatasetDoiUrl() {
        return datasetDoiUrl;
    }

    public void setDatasetDoiUrl(String datasetDoiUrl) {
        this.datasetDoiUrl = datasetDoiUrl;
    }
    
    public void addMessage() {
        String summary = notaryServiceBound ? "Checked" : "Unchecked";
        FacesContext.getCurrentInstance().addMessage("topMessage", new FacesMessage(summary));
    }
    
    
    private List<Long> fileIdList= new ArrayList<>();

    public List<Long> getFileIdList() {
        return fileIdList;
    }

    public void setFileIdList(List<Long> fileIdList) {
        this.fileIdList = fileIdList;
    }
    
    private List<DataFile> ingestedDataFileList = new ArrayList<>();

    public List<DataFile> getIngestedDataFileList() {
        return ingestedDataFileList;
    }

    public void setIngestedDataFileList(List<DataFile> ingestedDataFileList) {
        this.ingestedDataFileList = ingestedDataFileList;
    }
    
    public void saveNSchanges(){
        int modifiedCounter = 0;
        for (DataFile dataFile: ingestedDataFileList){
            if (dataFile.isNotaryServiceBound()){
                modifiedCounter++;
            }
            logger.log(Level.INFO, "id:{0}:isNotaryServiceBound={1}", 
                    new Object[]{dataFile.getId(), dataFile.isNotaryServiceBound()});
            dataFileFacade.edit(dataFile);
        }
        logger.log(Level.INFO, "how many DataFiles were updated={0}", modifiedCounter);
        if (modifiedCounter >0){
            logger.log(Level.INFO, "re-exporting metadata files are necessary");
            
            try {
                reExportMetadataFiles();
            } catch (RuntimeException e){
                String message ="Failed to get a DatasetVersion; an ingest failure is suspected; check server.log";
                Faces.getContext().addMessage("topMessage", 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, 
             "Dataset Version is not available", message));
                publishButtonEnabled=false;
                return;
            }
            
            
        } else {
            logger.log(Level.INFO, "re-exporting metadata files are NOT necessary");
        }
        
        publishButtonEnabled=true;
        String message ="New Notary-Service Settings were successfully saved";
        Faces.getContext().addMessage("topMessage", 
            new FacesMessage(FacesMessage.SEVERITY_INFO, 
     "Change was successfully saved", message));
    }
    
    
    public void reExportMetadataFiles() throws RuntimeException{
        logger.log(Level.INFO, "========== SubmissionPageView#reExportMetadataFiles: start ==========");
            List<DatasetVersion> versions = datasetVersionFcd.findAll();
            if (versions != null && !versions.isEmpty()) {
                logger.log(Level.INFO, "how many dataset versions={0}", versions.size());
                DatasetVersion latestDatasetVerion= versions.get(versions.size()-1);
                ingestService.exportDataset(latestDatasetVerion);
            } else {
                logger.log(Level.WARNING, "DatasetVersion is null|empty");
                throw new RuntimeException("DatasetVersion is null|empty");
                
                // TODO
                // create the error message 
                // call the sweeping method
                // return
            }
            
        
        logger.log(Level.INFO, "========== SubmissionPageView#reExportMetadataFiles: end ==========");
    }
    
    
    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        
        if(newValue != null && !newValue.equals(oldValue)) {
            
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, 
                    "Selection Changed", "Old: " + oldValue + ", New:" + newValue);
            Faces.getContext().addMessage("topGrowl", msg);
        }
    }
    
    public void onRowEdit(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Selection Edited", 
                ((DataFile) event.getObject()).getId().toString());
        Faces.getContext().addMessage("topGrowl", msg);
    }
     
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((DataFile) event.getObject()).getId().toString());
        Faces.getContext().addMessage("topGrowl", msg);
    }
    
    public String returnToMainMenu(){
        logger.log(Level.INFO, "SubmissionPageView#returnToMainMenu: sessionscoped data are reset");
        Faces.getExternalContext().invalidateSession();
        
        logger.log(Level.INFO, "return to the main menu");
        return "/index.xhtml?faces-redirect=true";
    }
    
    public void showResponseBody(){
        logger.log(Level.FINE, "responseBody={0}", responseBody);
        
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, 
                "Response Body", responseBody);
        PrimeFaces.current().dialog().showMessageDynamic(message);
    }
    
    private String responseBody;

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
    
    
    private String jsonBody="";

    public String getJsonBody() {
        return jsonBody;
    }

    public void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }
    
    
    
    public void showPayload(){
        logger.log(Level.FINE, "jsonBody={0}", jsonBody);
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, 
                "Metadata to be submitted", jsonBody);
        PrimeFaces.current().dialog().showMessageDynamic(message);
    }
    
    
    private boolean viewDetailButtonEnabled=false;

    public boolean isViewDetailButtonEnabled() {
        return viewDetailButtonEnabled;
    }

    public void setViewDetailButtonEnabled(boolean viewDetailButtonEnabled) {
        this.viewDetailButtonEnabled = viewDetailButtonEnabled;
    }
    
    
}
