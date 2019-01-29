package us.cyberimpact.trsa.web;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import edu.unc.odum.dataverse.util.json.JsonFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.inject.Named;

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
import us.cyberimpact.trsa.core.TrsaProfile;

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
    
    @ManagedProperty("#{fileUploadView}")
    private FileUploadView fileUploadView;

    private String datasetIdentifier;

    public String getDatasetIdentifier() {
        return datasetIdentifier;
    }

    public void setDatasetIdentifier(String datasetIdentifier) {
        this.datasetIdentifier = datasetIdentifier;
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
        
        trsaProfileTable= trsaProfileFacade.findAll();
        
        logger.log(Level.INFO, "IngestPageView:init():TrsaProfileTable={0}", trsaProfileTable);
        if (trsaProfileTable.isEmpty()){
            logger.log(Level.INFO, "IngestPageView:trsaProfileTableis empty");
        } else {
            logger.log(Level.INFO, "trsaProfileTable is available: ={0}", trsaProfileTable);
            // update fields

            logger.log(Level.INFO, "IngestPageView:init():trsa profile exists");

            logger.log(Level.INFO, "IngestPageView:init():url={0}",trsaProfileTable.get(0).getDataverseurl());
            dataverseServerHCValue = trsaProfileTable.get(0).getDataverseurl();
            
            logger.log(Level.INFO, "IngestPageView:init():api-token={0}",trsaProfileTable.get(0).getApitoken());
            apiKeyHCValue=trsaProfileTable.get(0).getApitoken();
            
            // turn on the switch
            logger.log(Level.INFO, "IngestPageView:init():before turned on: state of isTrsaProfileReady={0}", isTrsaProfileReady);
            isTrsaProfileReady=true;
            logger.log(Level.INFO, "IngestPageView:init():after update: isTrsaProfileReady={0}", isTrsaProfileReady);
        }
        
        // datasetID
        datasetIdentifier = fileUploadView.getDatasetIdentifier();
        logger.log(Level.INFO, "IngestPageView:init():datasetIdentifier passed={0}", datasetIdentifier);
        
        // target dataverse (currently hard-coded)
        targetDataverseId = dataverseIdHCValue; //"6";
        logger.log(Level.INFO, "IngestPageView:init():targetDataverseId={0}", targetDataverseId);
        
        // api-key from TRSA-Profile table
        apiKey = apiKeyHCValue ;
        logger.log(Level.INFO, "IngestPageView:init():apiKey={0}", apiKey);
        
        // target dataverse server
        dataverseServer = dataverseServerHCValue;
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

    public void publish() {
        progressTest = "starting publishing request";
        logger.log(Level.INFO, "publish this dataset");
        try {
            progressTest = "starting request";
            String filenameValue = datasetIdentifier;
            String filelocation = "/tmp/files/10.5072/FK2/" + datasetIdentifier + "/export_dataverse_json.cached";
            String payloadFileName = "/tmp/files/10.5072/FK2/" + datasetIdentifier +"/filtered-result.json";
            JsonFilter filter = new JsonFilter();
            filter.filterApiPayload(filelocation, payloadFileName);
            
//            File payload = new File(filelocation);
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
            logger.log(Level.INFO, "jsonbody={0}", jsonbody);
            
            logger.log(Level.INFO, "publish API case");
            String apiEndpoint = dataverseServer + "/api/dataverses/"+ targetDataverseId
                    +"/datasets";// "/api/batch/importwoi";
            logger.log(Level.INFO, "apiEndpoint={0}", apiEndpoint);
            HttpResponse<JsonNode> jsonResponse
                    = Unirest.post(apiEndpoint)
                            .header("X-Dataverse-key",
                                    apiKey)
//                            .queryString("dv", targetDataverseId)
//                            .queryString("filename", filenameValue)
//                            .queryString("key", apiKey)
                            .queryString("identifier", targetDataverseId)
//                            .field("file", new File(payloadFileName))
                            .body(jsonbody)
                            .asJson();

            logger.log(Level.INFO, "status code={0}", jsonResponse.getStatus());
            logger.log(Level.INFO, "response body={0}", jsonResponse.getBody().toString());

            addMessage(jsonResponse.getBody().toString());
            progressTest = "finishing publishing request";
            
            gotoDataverseButtonEnabled=true;
            publishButtonEnabled=false;
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "payload file was not available", ex);

        } catch (UnirestException ex) {
            logger.log(Level.SEVERE, "UnirestException", ex);
        }

        //return "/ingest.xhtml";
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
    
    boolean publishButtonEnabled=true;

    public boolean isPublishButtonEnabled() {
        return publishButtonEnabled;
    }

    public void setPublishButtonEnabled(boolean publishButtonEnabled) {
        this.publishButtonEnabled = publishButtonEnabled;
    }
    
    
    public String goHome() {
        logger.log(Level.INFO, "back to the home");
        return "/new_index.xhtml";
    }
    
    
    public String goToTrsaProfilePage() {
        logger.log(Level.INFO, "go to the Trsa profile page");
        return "/trsaProfile/List.xhtml";
    }
    
}
