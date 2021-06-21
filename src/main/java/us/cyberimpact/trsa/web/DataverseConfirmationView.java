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
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Faces;
import org.primefaces.PrimeFaces;
import org.primefaces.component.inputtext.InputText;
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
    
    // UIInput items that carry out their server-side validation
    
    InputText dataverseUrl;

    public InputText getDataverseUrl() {
        return dataverseUrl;
    }

    public void setDataverseUrl(InputText dataverseUrl) {
        this.dataverseUrl = dataverseUrl;
    }
    
    
    
    
    InputText apitokenField;

    public InputText getApitokenField() {
        return apitokenField;
    }

    public void setApitokenField(InputText apitokenField) {
        this.apitokenField = apitokenField;
    }
    
    InputText dataverseAlias;

    public InputText getDataverseAlias() {
        return dataverseAlias;
    }

    public void setDataverseAlias(InputText dataverseAlias) {
        this.dataverseAlias = dataverseAlias;
    }
    
    
    String summaryMsg="Confirmation Failure";
    
    String stackTrace;
    
    
    
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
    
    public String confirmDataverseData(ActionEvent ae) throws InterruptedException {
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
        
        String detailedMsg;
        
        try {
            jsonResponse = Unirest.get(apiEndpoint)
                    .headers(headers)
                    .queryString("identifier", selectedDataverseId).asJson();
            logger.log(Level.INFO, "status code={0}", jsonResponse.getStatus());
        } catch (UnirestException ex) {
            logger.log(Level.SEVERE, "confirmation request failed", ex);
            
//            stackTrace=ExceptionUtils.getStackTrace(ex);
            Throwable rootEx = ExceptionUtils.getRootCause(ex);
//            showMsgButtonEnabled=true;
            dataverseUrl.setValid(false);
            dataverseUrl.setValidatorMessage("Wrong Dataverse URL: server name and/or port-number");
            
            
            if (rootEx instanceof JSONException){
                logger.log(Level.SEVERE, "confirmation-request failure due to JSONException:{0}", 
                  rootEx.getMessage());
                
                detailedMsg ="Possible cause(s): wrong server-name and/or wrong port-number; enter correct data and click the confirm button again";
//                Faces.getContext().addMessage("topMessage", 
//                        new FacesMessage(FacesMessage.SEVERITY_ERROR, 
//                                summaryMsg, detailedMsg));
//                
                
                
                postExceptionCommonSteps(ExceptionUtils.getStackTrace(ex), 
                  summaryMsg, detailedMsg);
                
                
                return "";
                
                
            } else if (rootEx instanceof NoRouteToHostException){
                logger.log(Level.SEVERE, "confirmation-request failure due to NoRouteToHostException:{0}", rootEx);
                
                detailedMsg ="Possible cause: wrong server-name or IP address; enter correct data and click the confirm button again";
//                Faces.getContext().addMessage("topMessage", 
//                        new FacesMessage(FacesMessage.SEVERITY_ERROR, 
//                                summaryMsg, detailedMsg));
                
                postExceptionCommonSteps(ExceptionUtils.getStackTrace(ex), 
                  summaryMsg, detailedMsg);
                
                
                
                return "";
            } else if (rootEx instanceof SocketTimeoutException) {
                logger.log(Level.SEVERE, "confirmation-request failure due to the SocketTimeoutException:{0}", rootEx);

                detailedMsg = "Possible cause: The Dataverse server is not responding to your request; check whether the Dataverse is ready to accept a Native-API request and request again later.";
//                Faces.getContext().addMessage("topMessage",
//                  new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                    summaryMsg, detailedMsg));
                
                
                postExceptionCommonSteps(ExceptionUtils.getStackTrace(ex), 
                  summaryMsg, detailedMsg);
                
                
                return "";
                
            } else if (rootEx instanceof HttpHostConnectException  || 
              rootEx instanceof ConnectException){
                logger.log(Level.SEVERE, "confirmation-request failure due to the HttpHostConnectException:{0}", rootEx);

                detailedMsg = "Possible cause: A wrong port-number was entered ; enter correct data and click the confirm button again.";
//                Faces.getContext().addMessage("topMessage",
//                  new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                    summaryMsg, detailedMsg));
                
                
                postExceptionCommonSteps(ExceptionUtils.getStackTrace(ex), 
                  summaryMsg, detailedMsg);
                
                
                return "";
                
            } else {
                logger.log(Level.SEVERE, "confirmation-request failure due to the following exception:{0}", rootEx.getClass().getCanonicalName());
                
                detailedMsg ="check/re-enter your data and click the confirm button again";
//                Faces.getContext().addMessage("topMessage", 
//                        new FacesMessage(FacesMessage.SEVERITY_ERROR, 
//                                summaryMsg, detailedMsg));
                
                postExceptionCommonSteps(ExceptionUtils.getStackTrace(ex), 
                  summaryMsg, detailedMsg);
                
                
                return "";
            }
        }
//        logger.log(Level.INFO, "========== DataverseConfirmationView#confirmDataverseData : end ==========");
        logger.log(Level.INFO, "Submission ended anyway; check the return code");
        return responseHandler(jsonResponse);
    }

    private String responseHandler(HttpResponse<JsonNode> jsonResponse) {
        logger.log(Level.INFO, "responseHandler: start here");
        int statusCode = jsonResponse.getStatus();
        logger.log(Level.INFO, "status code={0}", statusCode);
        
        String detailedMsg;
        
        FacesMessage msg;
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

                JsonResponseParser jsonParser = new JsonResponseParser();

                String dataverseAlias
                  = jsonParser.ParseTargetStringField(responseString, "/data/alias");
                logger.log(Level.INFO, "dataverseAlias={0}", dataverseAlias);
                String dataverseTitle
                  = jsonParser.ParseTargetStringField(responseString, "/data/name");
                // save the title 
                hostInfo.setDataversetitle(dataverseTitle);
                logger.log(Level.INFO, "dataverseTitle={0}", dataverseTitle);
                String dataverseId
                  = jsonParser.ParseTargetNumericField(responseString, "/data/id").toString();

                logger.log(Level.INFO, "dataverseId={0}", dataverseId);
                hostInfo.setDataverseid(Long.parseLong(dataverseId));
                // set the placeholder value, 0L
                hostInfo.setDatasetid(0L);
                logger.log(Level.INFO, "saving Dataverse data::{0}", hostInfo);
                
                if (dataverseAlias.equals(hostInfo.getDataversealias())) {
                    logger.log(Level.INFO, "dataverseAlias is confirmed");

                    saveHostInfo(hostInfo);

                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                      "Confirmation success",
                      "The Dataverse Alias was confirmed");
                    Faces.getContext().addMessage("topMessage", msg);
                    mainButtonEnabled = true;
                    showMsgButtonEnabled = false;
                    
                    return "";
                } else {

                    logger.log(Level.INFO, "Given dataverse alias was not confirmed");
                    // 200 or 201 response-code means the following lines are 
                    // unnecessary because the current API setting returns 
                    // the 404 error for this case
                    
                    detailedMsg="Confirmation of the Dataverse alias failed: ";
                    
//                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                      summaryMsg, detailedMsg
//                      );
//                    Faces.getContext().addMessage("topMessage", msg);
//                    showMsgButtonEnabled = true;
//                    stackTrace = responseString;
                    
                    postExceptionCommonSteps(responseString, summaryMsg, 
                  detailedMsg);
                    
                    
                    return "";
                }

            } else if (statusCode == 401) {
                logger.log(Level.INFO, "response code: 401: invalid API token case");

                apitokenField.setValid(false);
                apitokenField.setValidatorMessage("wrong API token");

                detailedMsg="Response code=401: Data for the API token was wrong; correct data and submit again ";
                
//                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, summaryMsg,
//                 detailedMsg);
//                Faces.getContext().addMessage("topMessage", msg);
//                stackTrace = responseString;
//                showMsgButtonEnabled = true;
                
                
                postExceptionCommonSteps(responseString, summaryMsg, 
                  detailedMsg);
                
                return "";

            } else if (statusCode == 404) {
                logger.log(Level.INFO, "response code: 404: invalid Dataverse Alias");
                // 
                dataverseAlias.setValid(false);
                dataverseAlias.setValidatorMessage("wrong dataverse alias");
                detailedMsg="Response code=404: Data for the Dataverse Alias was wrong; correct data and submit again";
//                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, summaryMsg,
//                  detailedMsg);
//                Faces.getContext().addMessage("topMessage", msg);
//                stackTrace = responseString;
//                showMsgButtonEnabled = true;
                
                postExceptionCommonSteps(responseString, summaryMsg, 
                  detailedMsg);
                
                return "";

            } else {
                detailedMsg="Probably incorrect data were entered; correct data and submit again";
                
//                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, summaryMsg,
//                  detailedMsg);
//                Faces.getContext().addMessage("topMessage", msg);
//                stackTrace = responseString;
//                showMsgButtonEnabled = true;
                
                postExceptionCommonSteps(responseString, summaryMsg, 
                  detailedMsg);
                return "";

            }

        } else {
            logger.log(Level.WARNING, "Response is null");
            // failed locally or no response
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
              summaryMsg,
              " Null response: Failed to get a non-null response from the Dataverse server");
            Faces.getContext().addMessage("topMessage", msg);
            return "";

        }
    }
    
    
    
    private void postExceptionCommonSteps(String exceptionMsg, String summaryMsg, String detailedMsg) {
        Faces.setSessionAttribute("currentMessageKey", "dvConfirmationMsg");
        Faces.setSessionAttribute("dvConfirmationMsg", exceptionMsg);
        Faces.getContext().addMessage("topMessage",
          new FacesMessage(FacesMessage.SEVERITY_ERROR,
            summaryMsg, detailedMsg));

        showMsgButtonEnabled = true;

    }
    
    
    public void showExceptionMessage(){
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", true);
        options.put("draggable", true);
        options.put("modal", true);
        options.put("width", 800);
        options.put("height", 400);
        options.put("contentWidth", "100%");
        options.put("contentHeight", "100%");
        PrimeFaces.current().dialog().openDynamic("/ingestFailureMessage.xhtml", 
          options, null);
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
    
    boolean showMsgButtonEnabled=false;

    public boolean isShowMsgButtonEnabled() {
        return showMsgButtonEnabled;
    }

    public void setShowMsgButtonEnabled(boolean showMsgButtonEnabled) {
        this.showMsgButtonEnabled = showMsgButtonEnabled;
    }
    
    
    
//    public void showExceptionMessage(){
//        logger.log(Level.FINE, "responseBody={0}", stackTrace);
//        
//        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
//                "Stack Trace", stackTrace);
//        PrimeFaces.current().dialog().showMessageDynamic(message);
//    }
//    
    
    
}
