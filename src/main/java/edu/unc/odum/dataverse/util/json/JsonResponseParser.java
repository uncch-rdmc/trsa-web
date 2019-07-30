/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unc.odum.dataverse.util.json;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 *
 * @author asone
 */
public class JsonResponseParser {
    
    private static final Logger logger = Logger.getLogger(JsonResponseParser.class.getName());
    


    public JsonResponseParser() {
    }
    // the following method will be activated after Java ee 8 is used
    public String parseDatasetIdFromCreationResponse(String responseString){
        logger.log(Level.INFO, "JsonResponseParser#parseDatasetIdFromCreationResponse");
        logger.log(Level.INFO, "responseString={0}", responseString);
        JsonReader jsonReader = Json.createReader(new StringReader(responseString));
        JsonObject jsonObject = jsonReader.readObject();
        JsonPointer pDatasetId = Json.createPointer(JsonPointerForDataset.POINTER_TO_DATASET_ID);
        JsonValue datasetIdValue = pDatasetId.getValue(jsonObject);
        logger.log(Level.INFO, "datasetIdValue={0}", datasetIdValue);
        return ((JsonNumber)datasetIdValue).toString();
        //return datasetIdValue.toString();
    }
    
    public String parseDatasetDoiFromDsCreationResponse(String responseString){
        logger.log(Level.INFO, "JsonResponseParser#parseDatasetDoiFromDsCreationResponse");
        logger.log(Level.INFO, "responseString={0}", responseString);
        JsonReader jsonReader = Json.createReader(new StringReader(responseString));
        JsonObject jsonObject = jsonReader.readObject();
        JsonPointer pDatasetDoi = Json.createPointer(JsonPointerForDataset.POINTER_TO_DATASET_DOI);
        JsonValue datasetDoiValue = pDatasetDoi.getValue(jsonObject);
        logger.log(Level.INFO, "datasetDoiValue: JsonValue before type-wise casting={0}", datasetDoiValue);
        // warning: the following cast is necessary to remove quotation marks from a resulting string;
        // should not return a toStringed raw JsonValue object
        return ((JsonString)datasetDoiValue).getString();
        //return datasetDoiValue.toString();
    }
    
    
    public JsonValue ParseTargetField(String responseString, String jsonPointerExpr){
        logger.log(Level.INFO, "========== JsonResponseParser#ParseTargetField : start ==========");
        
        logger.log(Level.INFO, "responseString={0}", responseString);
        logger.log(Level.INFO, "jsonPointerExpr={0}", jsonPointerExpr);
        JsonReader jsonReader = Json.createReader(new StringReader(responseString));
        JsonObject jsonObject = jsonReader.readObject();
        JsonPointer jsnPointer = Json.createPointer(jsonPointerExpr);
        logger.log(Level.INFO, "jsnPointer={0}", jsnPointer);
        JsonValue parsedResult = jsnPointer.getValue(jsonObject);
        logger.log(Level.INFO, "parsedResult={0}", parsedResult);
        logger.log(Level.INFO, "========== JsonResponseParser#ParseTargetField : end ==========");        
        return parsedResult;
    }
    
    public String ParseTargetStringField(String responseString, String jsonPointerExpr){
        logger.log(Level.INFO, "========== JsonResponseParser#ParseTargetStringField : start ==========");
        JsonValue parsedResult = ParseTargetField(responseString, jsonPointerExpr);
        
        logger.log(Level.INFO, "parsedResult={0}", parsedResult);
        logger.log(Level.INFO, "========== JsonResponseParser#ParseTargetStringField : end ==========");        
        return ((JsonString) parsedResult).getString();
    }
    
    public JsonNumber ParseTargetNumericField(String responseString, String jsonPointerExpr){
        logger.log(Level.INFO, "========== JsonResponseParser#ParseTargetNumericField : start ==========");
        JsonValue parsedResult = ParseTargetField(responseString, jsonPointerExpr);
        
        logger.log(Level.INFO, "parsedResult={0}", parsedResult);
        logger.log(Level.INFO, "========== JsonResponseParser#ParseTargetNumericField : end ==========");        
        return (JsonNumber) parsedResult;
    }
}
