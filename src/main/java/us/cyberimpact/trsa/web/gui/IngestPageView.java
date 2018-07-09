/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.web.gui;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.File;
import java.io.FileNotFoundException;
import javax.inject.Named;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
/**
 *
 * @author akios
 */
@ManagedBean(name = "ingestPageView")
@SessionScoped
public class IngestPageView implements Serializable {
    private static final Logger logger = Logger.getLogger(IngestPageView.class.getName());
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
    
    
    
    String progressTest ;

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
    
    
    @PostConstruct
    public void init(){
        datasetIdentifier = fileUploadView.getDatasetIdentifier();
        logger.log(Level.INFO, "datasetIdentifier passed={0}", datasetIdentifier);
        targetDataverseId="6";
        apiKey="1b9da6d3-6870-4ea2-a5ab-331d43d92c53";
        dataverseServer="https://impacttest.irss.unc.edu";
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
    
    public void getInfo(ActionEvent actionEvent){
        try {
            progressTest="starting getInfo request";
            logger.log(Level.INFO, "testing the version of the target Dataverse");
            HttpResponse<JsonNode> jsonResponse = 
                    Unirest.get("https://impacttest.irss.unc.edu/api/info/version").header("X-Dataverse-key",
                            apiKey).asJson();
            
            
            logger.log(Level.INFO, "status code={0}", jsonResponse.getStatus());
            logger.log(Level.INFO, "body={0}", jsonResponse.getBody().toString());
            
            
            
            addMessage(jsonResponse.getBody().toString());
            progressTest="finishing request";
        } catch (UnirestException ex) {
            logger.log(Level.SEVERE, "UnirestException", ex);
        }
    }
    public void publish(){
        progressTest="starting publishing request";
        logger.log(Level.INFO, "publish this dataset");
        try {
            progressTest="starting request";
            String filenameValue=datasetIdentifier;
            String filelocation="/tmp/files/10.5072/FK2/"+datasetIdentifier+"/export_dataverse_json.cached";
            File payload = new File(filelocation);
            if (!payload.exists() || payload.length() == 0L){
                throw new FileNotFoundException("payload file does not exist or empty");
            }
            logger.log(Level.INFO, "publish API case");
            String apiEndpoint = dataverseServer+"/api/batch/importwoi";
            HttpResponse<JsonNode> jsonResponse = 
                    Unirest.post(apiEndpoint)
                            .header("X-Dataverse-key",
                            apiKey)
                            .queryString("dv", targetDataverseId)
                            .queryString("filename", filenameValue)
                            .queryString("key", apiKey)
                            .field("file", payload)
                            .asJson();
            
            
            logger.log(Level.INFO, "status code={0}", jsonResponse.getStatus());
            logger.log(Level.INFO, "response body={0}", jsonResponse.getBody().toString());
            
            
            
            addMessage(jsonResponse.getBody().toString());
            progressTest="finishing publishing request";
        }  catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "payload file was not available", ex);
        
        } catch (UnirestException ex) {
            logger.log(Level.SEVERE, "UnirestException", ex);
        }  
        
        //return "/ingest.xhtml";
    }
    
     
    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    
    
}
