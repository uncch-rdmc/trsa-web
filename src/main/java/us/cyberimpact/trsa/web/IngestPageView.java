package us.cyberimpact.trsa.web;

import us.cyberimpact.trsa.entities.TrsaProfileFacade;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import edu.unc.odum.dataverse.util.json.JsonFilter;
import edu.unc.odum.dataverse.util.json.JsonPointerForDataset;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonPointer;
import javax.json.JsonValue;
import javax.json.JsonPatchBuilder;
import us.cyberimpact.trsa.entities.TrsaProfile;
import us.cyberimpact.trsa.entities.HostInfo;
import us.cyberimpact.trsa.entities.HostInfoFacade;

/**
 *
 * @author akios
 */
@ManagedBean(name = "ingestPageView")
@SessionScoped
public class IngestPageView implements Serializable {
    
    private static final Logger logger = Logger.getLogger(IngestPageView.class.getName());
    
    @EJB
    private TrsaProfileFacade trsaProfileFacade;
    
    List<TrsaProfile> trsaProfileTable = new ArrayList<>();
    
    boolean isTrsaProfileReady = false;
    
    
    @EJB
    private HostInfoFacade hostInfoFacade;
    
    
     List<HostInfo> hostInfoTable = new ArrayList<>();
    
    @ManagedProperty("#{fileUploadView}")
    private FileUploadView fileUploadView;
    
    
    @ManagedProperty("#{destinationSelectionView}")
    private DestinationSelectionView destSelectionView;

    public void setDestSelectionView(DestinationSelectionView destSelectionView) {
        this.destSelectionView = destSelectionView;
    }

    
    private HostInfo hostInfo; 

    public HostInfo getHostInfo() {
        return hostInfo;
    }

    public void setHostInfo(HostInfo hostInfo) {
        this.hostInfo = hostInfo;
    }
    
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
    
    
    
    
    private String localDatasetIdentifier;

    public String getLocalDatasetIdentifier() {
        return localDatasetIdentifier;
    }

    public void setLocalDatasetIdentifier(String localDatasetIdentifier) {
        this.localDatasetIdentifier = localDatasetIdentifier;
    }
    
    String targetDatasetId;

    public String getTargetDatasetId() {
        return targetDatasetId;
    }

    public void setTargetDatasetId(String targetDatasetId) {
        this.targetDatasetId = targetDatasetId;
    }
    
    
    

    String targetDataverseId;

    public String getTargetDataverseId() {
        return targetDataverseId;
    }

    public void setTargetDataverseId(String targetDataverseId) {
        this.targetDataverseId = targetDataverseId;
    }

    String progressTest;

    public String getProgressTest() {
        return progressTest;
    }

    public void setProgressTest(String progressTest) {
        this.progressTest = progressTest;
    }

    /**
     * Creates a new instance of IngestPageView
     */
    public IngestPageView() {
    }
    
    
    private String apiKeyHCValue ="6bf35b95-4746-4bec-9547-3f19fe582727";
    private String dataverseServerHCValue="http://localhost:8083";
    private String dataverseIdHCValue="17";

    @PostConstruct
    public void init() {
        
        logger.log(Level.INFO, "Metadata-only upload={0}", isMetadataOnly());
        trsaProfileTable= trsaProfileFacade.findAll();
        
        logger.log(Level.INFO, "IngestPageView:init():TrsaProfileTable={0}", trsaProfileTable);
        if (trsaProfileTable.isEmpty()){
            logger.log(Level.INFO, "IngestPageView:trsaProfileTableis empty");
        } else {
            logger.log(Level.INFO, "trsaProfileTable is available: ={0}", trsaProfileTable);
            // update fields
//
//            logger.log(Level.INFO, "IngestPageView:init():trsa profile exists");
//
//            logger.log(Level.INFO, "IngestPageView:init():url={0}",trsaProfileTable.get(0).getDataverseurl());
//            dataverseServerHCValue = trsaProfileTable.get(0).getDataverseurl();
//            
//            logger.log(Level.INFO, "IngestPageView:init():api-token={0}",trsaProfileTable.get(0).getApitoken());
//            apiKeyHCValue=trsaProfileTable.get(0).getApitoken();
            
            // turn on the switch
            logger.log(Level.INFO, "IngestPageView:init():before turned on: state of isTrsaProfileReady={0}", isTrsaProfileReady);
            isTrsaProfileReady=true;
            logger.log(Level.INFO, "IngestPageView:init():after update: isTrsaProfileReady={0}", isTrsaProfileReady);
        }
        
        
        hostInfoTable = hostInfoFacade.findAll();
        logger.log(Level.INFO, "FileUploadView:hostInfoTable:howManyRows={0}", hostInfoTable.size());
        if (hostInfoTable.isEmpty()){
            logger.log(Level.WARNING, "hostInfoTable is empty");
        } else {
            publishButtonEnabled=true;
            logger.log(Level.INFO, "FileUploadView:hostInfoTable exists and not empty");
        }
        
        
        
        
/*
        
        // localDatasetID
        localDatasetIdentifier = fileUploadView.getDatasetIdentifier();
        logger.log(Level.INFO, "IngestPageView:init():datasetIdentifier passed={0}", localDatasetIdentifier);
        
        // target dataverse (currently hard-coded)
        targetDataverseId = dataverseIdHCValue; //"6";
        logger.log(Level.INFO, "IngestPageView:init():targetDataverseId={0}", targetDataverseId);
        
        // api-key from TRSA-Profile table
        apiKey = apiKeyHCValue ;
        logger.log(Level.INFO, "IngestPageView:init():apiKey={0}", apiKey);
        
        // target dataverse server
        dataverseServer = dataverseServerHCValue;
        logger.log(Level.INFO, "dataverseServer={0}", dataverseServer);
        
*/
        
        // new approach 
        logger.log(Level.INFO, "destSelectionView:SelectedHostInfo ={0}", destSelectionView.getSelectedHostInfo());
        
        hostInfo = destSelectionView.getSelectedHostInfo();
        
        logger.log(Level.INFO, "IngestPageView:init:hostInfo={0}", hostInfo);
        // localDatasetID
        localDatasetIdentifier = fileUploadView.getDatasetIdentifier();
        logger.log(Level.INFO, "IngestPageView:init():localDatasetIdentifier passed={0}", localDatasetIdentifier);
        
        // target dataverse
        targetDataverseId = Long.toString(hostInfo.getDataverseid());
        logger.log(Level.INFO, "IngestPageView:init():targetDataverseId={0}", targetDataverseId);
        
        
        targetDatasetId = Long.toString(hostInfo.getDatasetid());
        logger.log(Level.INFO, "targetDatasetId={0}", targetDatasetId);
        
        // api-key from TRSA-Profile table
        apiKey = hostInfo.getApitoken();//apiKeyHCValue ;
        logger.log(Level.INFO, "IngestPageView:init():apiKey={0}", apiKey);
        
        // target dataverse server
        dataverseServer = hostInfo.getHosturl();//dataverseServerHCValue;
        logger.log(Level.INFO, "dataverseServer={0}", dataverseServer);
        
        
        
        
    }

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
    
    public static String STORAGE_LOCATION_PREFIX= "/tmp/files/10.5072/FK2/" ;
    public static String PATH_ADD_METADATA="addFileMetadata";

    public void getInfo(ActionEvent actionEvent) {
        try {
            progressTest = "starting getInfo request";
            logger.log(Level.INFO, "testing the version of the target Dataverse");
            
            String serverInfo=dataverseServerHCValue+ "/api/info/version";
            HttpResponse<JsonNode> jsonResponse
                    = Unirest.get(serverInfo).header("X-Dataverse-key",
                            apiKey).asJson();

            logger.log(Level.INFO, "status code={0}", jsonResponse.getStatus());
            logger.log(Level.INFO, "body={0}", jsonResponse.getBody().toString());

            addMessage(jsonResponse.getBody().toString());
            progressTest = "finishing request";
        } catch (UnirestException ex) {
            logger.log(Level.SEVERE, "UnirestException", ex);
        }
    }

    
    public void publishFacade(){
        if (isMetadataOnly()){
            publishMetadata();
        } else {
            publish();
        }
    }
    
    
    
    public void publishMetadata4Javaee8(){
        logger.log(Level.INFO, "IngestPageView:publishMetadata");
        try {
            progressTest = "starting request";
            String filenameValue = localDatasetIdentifier;
            String filelocation = STORAGE_LOCATION_PREFIX+ localDatasetIdentifier 
                    + "/export_dataverse_json.cached";
            String payloadFileName = STORAGE_LOCATION_PREFIX + localDatasetIdentifier 
                    +"/filtered-result.json";
            
            String jsonbody="";
            
            try (InputStream rawIs = new FileInputStream(new File(filelocation));
                    JsonReader jsonReader = Json.createReader(rawIs);
                    PrintWriter printWriter = new PrintWriter(new File(payloadFileName), "UTF-8");
                    JsonWriter jsonWriter = Json.createWriter(printWriter)) {

                JsonObject rawJsonObject = jsonReader.readObject();
                logger.log(Level.INFO, "rawJsonObject={0}", rawJsonObject);

                // create a new Json object to store JsonPointers
                JsonObject object = Json.createObjectBuilder().build();

                //The following block works with Java-ee-api-8 package
                // create the two JsonPointer instances 
                JsonPointer metadataBlock = 
                        Json.createPointer(JsonPointerForDataset.POINTER_METADATABLOCKS);
                JsonPointer files = Json.createPointer(JsonPointerForDataset.POINTER_FILES);

                // get the value for each of the above JsonPointer instances
                JsonValue metadataBlockValue = metadataBlock.getValue(rawJsonObject);
                JsonValue filesValue = files.getValue(rawJsonObject);

                // create a JsonPatchBuilder object and add JsonValu instances
                JsonPatchBuilder builder = Json.createPatchBuilder();

                JsonObject payloadObject = builder
                        .add(JsonPointerForDataset.POINTER_METADATABLOCKS_FILTERED, metadataBlockValue)
                        .add(JsonPointerForDataset.POINTER_FILES_FILTERED, filesValue)
                        .build()
                        .apply(object);

                logger.log(Level.INFO, "actual={0}", payloadObject);
                jsonWriter.writeObject(payloadObject);
                jsonbody = payloadObject.toString();
                
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
            
            
            
            logger.log(Level.FINE, "jsonbody={0}", jsonbody);
            
            logger.log(Level.INFO, "publish Metadata-only API case");
            String apiEndpoint = dataverseServer + "/api/datasets/"+ targetDatasetId
                    + "/" +PATH_ADD_METADATA;
            logger.log(Level.INFO, "apiEndpoint={0}", apiEndpoint);
            HttpResponse<JsonNode> jsonResponse
                    = Unirest.post(apiEndpoint)
                            .header("X-Dataverse-key", apiKey)
                            .body(jsonbody)
                            .asJson();

            logger.log(Level.INFO, "status code={0}", jsonResponse.getStatus());
            logger.log(Level.INFO, "response body={0}", jsonResponse.getBody().toString());

            addMessage(jsonResponse.getBody().toString());
            progressTest = "finishing publishing(metadata-only) request";
            
            gotoDataverseButtonEnabled=true;
            publishButtonEnabled=false;
        } catch (UnirestException ex) {
            logger.log(Level.SEVERE, "UnirestException", ex);
        }
    }
    
    
    public void publishMetadata(){
    
        progressTest = "starting publishing Metadata request";
        logger.log(Level.INFO, "publish the metadata of this dataset");
        try {
            progressTest = "starting request";
            String filenameValue = localDatasetIdentifier;
            String filelocation =  STORAGE_LOCATION_PREFIX 
                    + localDatasetIdentifier + "/export_dataverse_json.cached";
            String payloadFileName = STORAGE_LOCATION_PREFIX 
                    + localDatasetIdentifier +"/filtered-result.json";
            JsonFilter filter = new JsonFilter();
            filter.filterApiPayloadMetadataOnly(filelocation, payloadFileName);
            Object payloadObject = filter.selectMetadataPayload(filelocation);
            
            logger.log(Level.INFO, "payloadObject={0}", payloadObject);
            String jsonbody = payloadObject.toString();
            
            
            logger.log(Level.FINE, "jsonbody={0}", jsonbody);
            
            logger.log(Level.INFO, "Add Metadata API case");
            String apiEndpoint = dataverseServer 
                    + "/api/datasets/"
                    + targetDatasetId
                    + "/" +PATH_ADD_METADATA;
            logger.log(Level.INFO, "apiEndpoint={0}", apiEndpoint);
            HttpResponse<JsonNode> jsonResponse
                    = Unirest.post(apiEndpoint)
                            .header("X-Dataverse-key", apiKey)
                            .body(jsonbody)
                            .asJson();

            logger.log(Level.INFO, "status code={0}", 
                    jsonResponse.getStatus());
            logger.log(Level.INFO, "response body={0}", 
                    jsonResponse.getBody().toString());

            addMessage(jsonResponse.getBody().toString());
            progressTest = "finishing publishing request";
            
            gotoDataverseButtonEnabled=true;
            publishButtonEnabled=false;
        } catch (UnirestException ex) {
            logger.log(Level.SEVERE, "UnirestException", ex);
        }
    
    }
    
    
    
    public void publish() {
        progressTest = "starting publishing request";
        logger.log(Level.INFO, "publish this dataset");
        try {
            progressTest = "starting request";
            String filenameValue = localDatasetIdentifier;
            String filelocation = STORAGE_LOCATION_PREFIX
                    + localDatasetIdentifier + "/export_dataverse_json.cached";
            String payloadFileName = STORAGE_LOCATION_PREFIX 
                    + localDatasetIdentifier +"/filtered-result.json";
            JsonFilter filter = new JsonFilter();
            filter.filterApiPayload(filelocation, payloadFileName);
            
//            File payload = new File(STORAGE_LOCATION);
            File payload = new File(payloadFileName);
            if (!payload.exists() || payload.length() == 0L) {
                throw new FileNotFoundException("payload file does not exist or empty");
            }
            
            Scanner scanner = new Scanner(new File(payloadFileName));
            String jsonbody="";
            while (scanner.hasNextLine()) {
                    jsonbody +=scanner.nextLine();
            }
            scanner.close();
            logger.log(Level.FINE, "jsonbody={0}", jsonbody);
            
            logger.log(Level.INFO, "publish API case");
            String apiEndpoint = dataverseServer 
                    + "/api/dataverses/"
                    + targetDataverseId
                    +"/datasets";
            logger.log(Level.INFO, "apiEndpoint={0}", apiEndpoint);
            HttpResponse<JsonNode> jsonResponse
                    = Unirest.post(apiEndpoint)
                            .header("X-Dataverse-key", apiKey)
//                            .queryString("dv", targetDataverseId)
//                            .queryString("filename", filenameValue)
//                            .queryString("key", apiKey)
                            .queryString("identifier", targetDataverseId)
//                            .field("file", new File(payloadFileName))
                            .body(jsonbody)
                            .asJson();

            logger.log(Level.INFO, "status code={0}", jsonResponse.getStatus());
            
            String responseString = jsonResponse.getBody().toString();
            logger.log(Level.INFO, "response body={0}", responseString);
            
            
            JsonFilter jsFilter = new JsonFilter();

             String datasetIdString = jsFilter.parseDatasetCreationResponse(responseString);
            
            
            logger.log(Level.INFO, "datasetIdString={0}", datasetIdString);
            // parse the response and get the DatasetId
            long newDatasetId = Long.parseLong(datasetIdString);
            saveHostInfo(hostInfo, newDatasetId);
            HostInfo newHostInfo = hostInfoFacade.findByDatasetId(newDatasetId);
            
            logger.log(Level.INFO, "newHostInfo={0}", newHostInfo);
//            Long rowId = hostInfo.getId();
//            // save the DatasetId
//            hostInfoFacade.edit(hostInfo);
//            logger.log(Level.INFO, "after update:hostInfo={0}", hostInfoFacade.find(rowId));
            addMessage(responseString);
            progressTest = "finishing publishing request"+": assigned Dataset Id="+newDatasetId;
            
            gotoDataverseButtonEnabled=true;
            publishButtonEnabled=false;
            
            
            
            
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "payload file was not available", ex);

        } catch (UnirestException ex) {
            logger.log(Level.SEVERE, "UnirestException", ex);
        } finally {

        }

        clearSession();
    }
    
    private void clearSession(){
        logger.log(Level.INFO, "sessionscoped data are reset");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }
    
    private void saveHostInfo(HostInfo hi, long newDatasetId){
        
        logger.log(Level.INFO, "newDatasetId={0}", newDatasetId);

        HostInfo newHostInfo = new HostInfo();
        newHostInfo.setHosturl(hi.getHosturl());
        newHostInfo.setApitoken(hi.getApitoken());
        newHostInfo.setDataverseid(hi.getDataverseid());
        newHostInfo.setDataversealias(hi.getDataversealias());
        newHostInfo.setDataversetitle(hi.getDataversetitle());
        newHostInfo.setDatasetid(newDatasetId);
        logger.log(Level.INFO, "newHostInfo={0}", newHostInfo);
        hostInfoFacade.create(newHostInfo);
        
    }
    
    

    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void goToDataverseSite() {

        try {
            goToExternalSite(dataverseServer);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IOException was thrown before tried to jump to the site", ex);
        }
    }

    public void goToExternalSite(String url) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();

        externalContext.redirect(url);
    }
    
    boolean gotoDataverseButtonEnabled=false;

    public boolean isGotoDataverseButtonEnabled() {
        return gotoDataverseButtonEnabled;
    }

    public void setGotoDataverseButtonEnabled(boolean value) {
        this.gotoDataverseButtonEnabled = value;
    }
    
    boolean publishButtonEnabled=false;

    public boolean isPublishButtonEnabled() {
        return publishButtonEnabled;
    }

    public void setPublishButtonEnabled(boolean publishButtonEnabled) {
        this.publishButtonEnabled = publishButtonEnabled;
    }
    
    
    public String goHome() {
        logger.log(Level.INFO, "back to the home");
        return "/index.xhtml";
    }
    
    
    public String goToTrsaProfilePage() {
        logger.log(Level.INFO, "go to the Trsa profile page");
        return "/trsaProfile/List.xhtml";
    }
    
}
