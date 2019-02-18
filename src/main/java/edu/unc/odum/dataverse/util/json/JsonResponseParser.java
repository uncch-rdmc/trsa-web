/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unc.odum.dataverse.util.json;

import com.jayway.jsonpath.JsonPath;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import javax.json.JsonReader;
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
//    public String parseDatasetCreationResponse(String responseString){
//        logger.log(Level.INFO, "responseString={0}", responseString);
//        JsonReader jsonReader = Json.createReader(new StringReader(responseString));
//        JsonObject jsonObject = jsonReader.readObject();
//        JsonPointer pDatasetId = Json.createPointer(JsonPointerForDataset.POINTER_TO_DATASET_ID);
//        JsonValue datasetIdValue = pDatasetId.getValue(jsonObject);
//        logger.log(Level.INFO, "datasetIdValue={0}", datasetIdValue);
//        return datasetIdValue.toString();
//    }
    
}
