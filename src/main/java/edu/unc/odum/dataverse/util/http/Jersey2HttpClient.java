package edu.unc.odum.dataverse.util.http;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import us.cyberimpact.trsa.web.WebAppConstants;

/**
 *
 * @author asone
 */
public class Jersey2HttpClient {
    
    private static final Logger logger = Logger.getLogger(Jersey2HttpClient.class.getName());
    
    private String dvServer;
    private Client client;
    private String apiKey;
    private String trsaRegNmbr;
    
    public Jersey2HttpClient() {
    }

    public Jersey2HttpClient(String server, String key, String trsaRegNumber) {
        dvServer = server;
        apiKey = key;
        trsaRegNmbr = trsaRegNumber;
        client = ClientBuilder.newClient();
    }
    
    public Response confirmDataverseSetup(String selectedDataverseId, String trsaRegNmbr){
        logger.log(Level.INFO, "===== Jersey2HttpClient#confirmDataverseSetup: start =====");
        WebTarget dvsWebTarget = client.target(this.dvServer).path(WebAppConstants.PATH_DATAVERSE_API);
        WebTarget dvWebTarget = dvsWebTarget.path(selectedDataverseId);
        Invocation.Builder invocationBuilder = dvWebTarget.request(MediaType.APPLICATION_JSON);
        invocationBuilder.header("X-Dataverse-key", this.apiKey);
        invocationBuilder.header("X-TRSA-registrationId", trsaRegNmbr);
        logger.log(Level.INFO, "===== Jersey2HttpClient#confirmDataverseSetup: end =====");
        return invocationBuilder.get();
    }
    
    // how to express the above method by get() method below?
    public Response confirmDataverseSetupVer2(String selectedDataverseId){
        logger.log(Level.INFO, "===== Jersey2HttpClient#confirmDataverseSetupVer2: start =====");
        logger.log(Level.INFO, "===== Jersey2HttpClient#confirmDataverseSetupVer2: end =====");
        return get(WebAppConstants.PATH_DATAVERSE_API, 
          selectedDataverseId, null);
    }
    
    
    // this method handles the most common,  three-path-parameter API-call cases
    public Response get(String path1, String path2, String path3){
        
        
        
        // setting target
        WebTarget rootWT = client.target(this.dvServer).path(path1).path(path2);
        WebTarget pathWT = null;
        if (StringUtils.isNotBlank(path3)){
           pathWT = rootWT.path(path3);
        } else if (StringUtils.isBlank(path3)) {
           pathWT = rootWT;
        }

        Invocation.Builder invocationBuilder = pathWT.request(MediaType.APPLICATION_JSON);
        // setting headers 
        invocationBuilder.header("X-Dataverse-key", this.apiKey);
        invocationBuilder.header("X-TRSA-registrationId", this.trsaRegNmbr);
        return invocationBuilder.get();
    }
    
    
    // This method is an abstraction of the following segment
    // of  SubmissionPageView#uploadMetadataOnlyVer2 method
/*
    
    Client client = ClientBuilder.newClient();
    WebTarget dsWTroot = client.target(this.dataverseServer).path(this.selectedDatasetId);
    WebTarget dsWT = dsWTroot.path(WebAppConstants.PATH_ADD_METADATA);
    Invocation.Builder invocationBuilder = dsWT.request(MediaType.APPLICATION_JSON);
    invocationBuilder.header("X-Dataverse-key", this.apiKey);
    invocationBuilder.header("X-TRSA-registrationId", trsaRegNmbr);
    jsonResponse = invocationBuilder.post(Entity.entity(jsonBody, MediaType.APPLICATION_JSON));
    
*/
    public Response post(String path1, String path2, String mediaType, String jsonBody) {
        WebTarget dsWTroot = client.target(this.dvServer).path(path1);
        WebTarget dsWT = dsWTroot.path(path2);
        Invocation.Builder invocationBuilder = dsWT.request(mediaType);
        invocationBuilder.header("X-Dataverse-key", this.apiKey);
        invocationBuilder.header("X-TRSA-registrationId", this.trsaRegNmbr);
        return invocationBuilder.post(Entity.entity(jsonBody, mediaType));
    }
    
/*
    
    public String getLastestVersionOfDataset(String datasetId){
        Response jsonResponse = get("datasets", datasetId, "versions/:latest");
        String responseString = jsonResponse.readEntity(String.class);
        int statusCode = jsonResponse.getStatus();
        if (statusCode == 200 || statusCode == 201) {
            logger.log(Level.INFO, "code: 200 or 201: OK case");
            logger.log(Level.INFO, "responseString={0}", responseString);
        } else {
            // TODO
            logger.log(Level.INFO, "non-200 response={0}", statusCode);
        }
        return responseString;
    }
*/
/*
    public Set<String> getLatestSetOfFilenamesFromDataset(String datasetId){
        Set<String> setOfFilenames = new LinkedHashSet<>();
        String responseString=getLastestVersionOfDataset(datasetId);
        logger.log(Level.INFO, "responseString={0}", responseString);
        try {
            setOfFilenames = (new JsonResponseParser()).getFilenameSetFromResponse(responseString);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IOException is thrown; fail to get the set of filename", ex);
        }
        logger.log(Level.INFO, "setOfFilenames={0}", setOfFilenames);
        return setOfFilenames; 
    }
    
*/

    public String getDvServer() {
        return dvServer;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getTrsaRegNmbr() {
        return trsaRegNmbr;
    }
    
    
}
