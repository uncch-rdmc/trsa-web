package us.cyberimpact.trsa.web;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import edu.unc.odum.dataverse.util.json.JsonFilter;
import edu.unc.odum.dataverse.util.json.JsonPointerForDataset;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
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
import us.cyberimpact.trsa.entities.HostInfo;
import us.cyberimpact.trsa.entities.HostInfoFacade;

/**
 *
 * @author asone
 */
@Named(value = "submissionPageView")
@SessionScoped
public class SubmissionPageView implements Serializable {

    private static final Logger logger = Logger.getLogger(SubmissionPageView.class.getName());
    /**
     * Creates a new instance of SubmissionPageView
     */
    public SubmissionPageView() {
    }
    
    // injecting objects
    
    
    @Inject
    private HomePageView homePageView;
    
    private RequestType selectedRequestType=homePageView.getSelectedRequestType();
    
    
    @Inject
    private DestinationSelectionView destSelectionView;
    
    private HostInfo selectedHostInfo =  destSelectionView.getSelectedHostInfo();
    
    @Inject
    private HostInfoFacade hostInfoFacade;

    public HostInfo getSelectedHostInfo() {
        return selectedHostInfo;
    }

    public void setSelectedHostInfo(HostInfo selectedHostInfo) {
        this.selectedHostInfo = selectedHostInfo;
    }
    
    
    @Inject
    private FileUploadView fileUploadView;
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
    
    
    
//    String targetDataverseId;
//
//    public String getTargetDataverseId() {
//        return targetDataverseId;
//    }
//
//    public void setTargetDataverseId(String targetDataverseId) {
//        this.targetDataverseId = targetDataverseId;
//    }
    
    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "selectedRequestType={0}", selectedRequestType);

        
        logger.log(Level.INFO, "selectedHostInfo={0}", selectedHostInfo);
        if (selectedHostInfo == null){
            logger.log(Level.INFO, "selectedHostInfo is null");
            // TODO
            // error message here and maybe to be forwarded to the host_info page
        } else {
            logger.log(Level.INFO, "SubmissionPageView:init(): selectedHostInfo={0}", selectedHostInfo);
        }
        // here setup common data 
        // api-key from TRSA-Profile table
        apiKey = selectedHostInfo.getApitoken();//apiKeyHCValue ;
        logger.log(Level.INFO, "SubmissionPageView:init():apiKey={0}", apiKey);
        
        // target dataverse server
        dataverseServer = selectedHostInfo.getHosturl();//dataverseServerHCValue;
        logger.log(Level.INFO, "dataverseServer={0}", dataverseServer);
        
        // here setup data for each submission request type
        switch (selectedRequestType) {
            case EMPTY_DATASET:
                logger.log(Level.INFO, "EMPTY_DATASET case");
                // DataverseId is required 
                selectedDataverseId = Long.toString(selectedHostInfo.getDataverseid());
                logger.log(Level.INFO, "selectedDataverseId={0}", selectedDataverseId);
                break;
            case FULL_DATASET:
                logger.log(Level.INFO, "FULL_DATASET case");
                // localDatasetID must be saved
                localDatasetIdentifier = fileUploadView.getDatasetIdentifier();
                logger.log(Level.INFO, "IngestPageView:init():localDatasetIdentifier passed={0}", localDatasetIdentifier);
                // DataverseId is required 
                selectedDataverseId = Long.toString(selectedHostInfo.getDataverseid());
                logger.log(Level.INFO, "selectedDataverseId={0}", selectedDataverseId);
                break;
            case METADATA_ONLY:
                logger.log(Level.INFO, "METADATA_ONLY case");
                // localDatasetID must be saved
                localDatasetIdentifier = fileUploadView.getDatasetIdentifier();
                logger.log(Level.INFO, "IngestPageView:init():localDatasetIdentifier passed={0}", localDatasetIdentifier);
                // DatasetId is required
                selectedDatasetId =  Long.toString(selectedHostInfo.getDatasetid());
                logger.log(Level.INFO, "selectedDatasetId={0}", selectedDatasetId);
                break;
            default:
                logger.log(Level.SEVERE, "Some uxexpected request was chosen");
                throw new IllegalArgumentException();
        }
        logger.log(Level.INFO, "end of the switch statement within init()");

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
        return  (fileUploadView != null) && 
                (fileUploadView.getFileName() != null) ?
                fileUploadView.getFileName() : "N/A";
    }
    
    public void setIngestedFile(String ingestedFile) {
        this.ingestedFile = ingestedFile;
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
        
        
        String templateDirectory = System.getProperty(Settings.TRSA_TEMPLATE_DIRECTORY);
        // -Dtrsa.template.directory
        // = /home/asone/myopt/payara5/glassfish/domains/domain1/config/trsa/template
        // <jvm-options>-Dtrsa.template.directory=${com.sun.aas.instanceRoot}/config/trsa/template</jvm-options>
        String templateFileName=System.getProperty(Settings.TRSA_TEMPLATE_FILE_NAME);
        String fileLocation = templateDirectory +"/"
                + templateFileName ;
        String payloadFileName = Settings.STORAGE_LOCATION_PREFIX
                + localDatasetIdentifier + "/" + Settings.FILTERED_PAYLOAD_FILENAME;
        
        
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
            
        }
    }
    
    // FULL_DATASET
    void createNewDataset(){
        logger.log(Level.INFO, "SubmissionPageView#createNewDataset");
        // pointing to the payload file
        String fileLocation = Settings.STORAGE_LOCATION_PREFIX
                + localDatasetIdentifier + "/" + Settings.EXPORT_FILE_NAME_JSON;
        String payloadFileName = Settings.STORAGE_LOCATION_PREFIX
                + localDatasetIdentifier + "/" + Settings.FILTERED_PAYLOAD_FILENAME;
        
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
            throw new WebApplicationException(ex);
        }
        
        logger.log(Level.FINE, "jsonbody={0}", jsonBody);
        logger.log(Level.INFO, "create a new Dataset case");
        String apiEndpoint = dataverseServer 
                    + Settings.PATH_DATAVERSE_API
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

        logger.log(Level.INFO, "status code={0}", jsonResponse.getStatus());

        String responseString = jsonResponse.getBody().toString();
        logger.log(Level.INFO, "response body={0}", responseString);


        JsonFilter jsonFilter = new JsonFilter();

        String datasetIdString = jsonFilter.parseDatasetCreationResponse(responseString);
        logger.log(Level.INFO, "datasetIdString={0}", datasetIdString);

        clearSession();
    }
    
    // METADATA_ONLY
    void uploadMetadataOnly(){
        logger.log(Level.INFO, "uploadMetadataOnly case");
        String fileLocation = Settings.STORAGE_LOCATION_PREFIX + localDatasetIdentifier
                + "/" + Settings.EXPORT_FILE_NAME_JSON;
        String payloadFileName = Settings.STORAGE_LOCATION_PREFIX + localDatasetIdentifier
                + "/" + Settings.FILTERED_PAYLOAD_FILENAME;

        String jsonBody = "";
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
            logger.log(Level.SEVERE, "IOException was thrown during io operations", ex);
        }

        logger.log(Level.FINE, "jsonbody={0}", jsonBody);

        logger.log(Level.INFO, "publish Metadata-only API case");
        String apiEndpoint = dataverseServer + "/api/datasets/" + selectedDatasetId
                + "/" + Settings.PATH_ADD_METADATA;
        logger.log(Level.INFO, "apiEndpoint={0}", apiEndpoint);


        HttpResponse<JsonNode> jsonResponse=null;
        try {
            jsonResponse = Unirest.post(apiEndpoint)
                    .header("X-Dataverse-key", apiKey)
                    .body(jsonBody)
                    .asJson();
        } catch (UnirestException ex) {
            logger.log(Level.SEVERE, "UnirestException was thrown: a request of uploading metadata failed", ex);
        }

        logger.log(Level.INFO, "status code={0}", jsonResponse.getStatus());
        logger.log(Level.INFO, "response body={0}", jsonResponse.getBody().toString());


        clearSession();
    }
    
    private void clearSession(){
        logger.log(Level.INFO, "sessionscoped data are reset");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }
    
}
