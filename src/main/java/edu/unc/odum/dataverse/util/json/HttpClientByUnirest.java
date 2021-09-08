package edu.unc.odum.dataverse.util.json;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.omnifaces.util.Faces;
import us.cyberimpact.trsa.web.WebAppConstants;

/**
 *
 * @author asone
 */
public class HttpClientByUnirest {

    private static final Logger logger = Logger.getLogger(HttpClientByUnirest.class.getName());
    
    public HttpClientByUnirest(String server,String apiKey) {
        this.server = server;
        this.apiKey = apiKey;
    }
    
    String server;
    
    String apiKey;
    
    String summaryMsg="";
    
    String detailedMsg="";
    
    public String requestGetCall(String keyPath, String apiPath, String selectedDatasetId){
        logger.log(Level.INFO, "request the lastest version of a target Dataset");
        String apiEndpoint = this.server + 
                              "/api/"+
                              keyPath+
                              "/" + 
                              selectedDatasetId +
                              "/" + apiPath;
        logger.log(Level.INFO, "apiEndpoint={0}", apiEndpoint);
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Dataverse-key", apiKey);

        HttpResponse<JsonNode> jsonResponse=null;
        try {
            jsonResponse = Unirest.get(apiEndpoint)
                    .headers(headers).asJson();
        } catch (UnirestException ex) {
            logger.log(Level.SEVERE, "Request failed with UnirestException");
            this.summaryMsg="GET Request Failure";
            logger.log(Level.SEVERE, "GET request failed", ex);
            
            Throwable rootEx = ExceptionUtils.getRootCause(ex);
            
            
            if (rootEx instanceof JSONException){
                logger.log(Level.SEVERE, "GET-request failure due to JSONException:{0}", 
                  rootEx.getMessage());
                
                this.detailedMsg ="Possible cause(s): wrong server-name and/or wrong port-number; enter correct data and click the confirm button again";
                
//                postExceptionCommonSteps(ExceptionUtils.getStackTrace(ex), 
//                  summaryMsg, detailedMsg);
                
                return "";
                
            } else if (rootEx instanceof NoRouteToHostException){
                logger.log(Level.SEVERE, "GET-request failure due to NoRouteToHostException:{0}", rootEx);
                
                this.detailedMsg ="Possible cause: wrong server-name or IP address; enter correct data and click the confirm button again";
                
//                postExceptionCommonSteps(ExceptionUtils.getStackTrace(ex), 
//                  summaryMsg, detailedMsg);
                
                return "";
            } else if (rootEx instanceof SocketTimeoutException) {
                logger.log(Level.SEVERE, "GET-request failure due to the SocketTimeoutException:{0}", rootEx);

                this.detailedMsg = "Possible cause: The Dataverse server is not responding to your request; check whether the Dataverse is ready to accept a Native-API request and request again later.";
                
//                postExceptionCommonSteps(ExceptionUtils.getStackTrace(ex), 
//                  summaryMsg, detailedMsg);
                
                return "";
                
            } else if (rootEx instanceof HttpHostConnectException  || 
              rootEx instanceof ConnectException){
                logger.log(Level.SEVERE, "GET-request failure due to the HttpHostConnectException:{0}", rootEx);

                this.detailedMsg = "Possible cause: A wrong port-number was entered ; enter correct data and click the confirm button again.";
                
//                postExceptionCommonSteps(ExceptionUtils.getStackTrace(ex), 
//                  summaryMsg, detailedMsg);
                
                return "";
                
            } else {
                logger.log(Level.SEVERE, "confirmation-request failure due to the following exception:{0}", rootEx.getClass().getCanonicalName());
                
                this.detailedMsg ="check/re-enter your data and click the confirm button again";
                
//                postExceptionCommonSteps(ExceptionUtils.getStackTrace(ex), 
//                  summaryMsg, detailedMsg);
                
                
                return "";
            }
        }
//        logger.log(Level.INFO, "========== DataverseConfirmationView#confirmDataverseData : end ==========");
        logger.log(Level.INFO, "Request ended without exception; check the return code");
        return responseHandler(jsonResponse);
        
    }
    
    private String responseHandler(HttpResponse<JsonNode> jsonResponse) {
        logger.log(Level.INFO, "responseHandler: start here");
        int statusCode = jsonResponse.getStatus();
        logger.log(Level.INFO, "status code={0}", statusCode);
        
        // for unknown response cases, return null
        
        if (jsonResponse != null) {
            String responseString = jsonResponse.getBody().toString();

            if (statusCode == 200 || statusCode == 201) {
                logger.log(Level.INFO, "code: 200 or 201: OK case");
                //String responseString = jsonResponse.getBody().toString();
                logger.log(Level.INFO, "response body={0}", responseString);
                // JsonPath
                // /data/id
                // /data/alias 
                // /data/name 

                return responseString;

            } else if (statusCode == 401) {
                logger.log(Level.INFO, "response code: 401: invalid API token case");
                logger.log(Level.INFO, "responseString(401)={0}", responseString);

                this.detailedMsg="Response code: 401: Data for the API token was wrong; correct data and submit again ";
//                postExceptionCommonSteps(responseString, summaryMsg, 
//                  detailedMsg);
                
                return "";

            } else if (statusCode == 404) {
                logger.log(Level.INFO, "response code: 404: NOT found the requested resource");
                this.detailedMsg="Response code: 404: NOT FOUND the requested resource";
                logger.log(Level.INFO, "responseString(404)={0}", responseString);
//                postExceptionCommonSteps(responseString, summaryMsg, 
//                  detailedMsg);
                
                return "";

            } else {
                detailedMsg="Unknow reason: request again later";
                logger.log(Level.INFO, "responseString(others)={0}", responseString);
//                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, summaryMsg,
//                  detailedMsg);
//                Faces.getContext().addMessage("topMessage", msg);
//                stackTrace = responseString;
//                showMsgButtonEnabled = true;
                
//                postExceptionCommonSteps(responseString, summaryMsg, 
//                  detailedMsg);
                return null;

            }

        } else {
            logger.log(Level.WARNING, "Response is null");
            // failed locally or no response
            detailedMsg="Null response: Failed to get a non-null response from the Dataverse server";
            
//            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
//              summaryMsg,
//              detailedMsg);
//            Faces.getContext().addMessage("topMessage", msg);
            return null;

        }
    }
    
    
    public String getLastestVersionOfDataset(String datasetId){
        String responseString=null;
        HttpClientByUnirest client = new HttpClientByUnirest(this.server,
          this.apiKey);
        responseString = client.requestGetCall("datasets", "versions/:latest", datasetId);
        logger.log(Level.INFO, "responseString={0}", responseString);
        return responseString;
    }
    
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
    
}