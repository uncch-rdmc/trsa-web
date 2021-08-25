/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unc.odum.dataverse.util.json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;

/**
 *
 * @author asone
 */
public class JsonResponseParser {
    
    private static final Logger logger = Logger.getLogger(JsonResponseParser.class.getName());
    
    /**
     *
     */
    public JsonResponseParser() {
    }
    // the following method will be activated after Java ee 8 is used

    /**
     * gets the DatasetId from a Dataset-creation response
     * 
     * @param responseString
     * @return a string whose value is the DatasetId
     */
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
    
    /**
     * gets the DOI from a Dataset-creation response
     * 
     * @param responseString
     * @return a string whose value is a DOI
     */
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
    
    /**
     *
     * @param responseString
     * @param jsonPointerExpr
     * @return
     */
    public JsonValue parseTargetField(String responseString, String jsonPointerExpr){
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
    
    /**
     *
     * @param responseString
     * @param jsonPointerExpr
     * @return
     */
    public String parseTargetStringField(String responseString, String jsonPointerExpr){
        logger.log(Level.INFO, "========== JsonResponseParser#ParseTargetStringField : start ==========");
        JsonValue parsedResult = parseTargetField(responseString, jsonPointerExpr);
        
        logger.log(Level.INFO, "parsedResult={0}", parsedResult);
        logger.log(Level.INFO, "========== JsonResponseParser#ParseTargetStringField : end ==========");        
        return ((JsonString) parsedResult).getString();
    }
    
    /**
     *
     * @param responseString
     * @param jsonPointerExpr
     * @return
     */
    public JsonNumber parseTargetNumericField(String responseString, String jsonPointerExpr){
        logger.log(Level.INFO, "========== JsonResponseParser#ParseTargetNumericField : start ==========");
        JsonValue parsedResult = parseTargetField(responseString, jsonPointerExpr);
        
        logger.log(Level.INFO, "parsedResult={0}", parsedResult);
        logger.log(Level.INFO, "========== JsonResponseParser#ParseTargetNumericField : end ==========");        
        return (JsonNumber) parsedResult;
    }
    
    /**
     * Returns the MD5 value of a Datafile within a Dataset from a response string
     * 
     * @param responseString
     * @return
     * @throws IOException 
     */
//    public String parseMD5values(String responseString) throws IOException {
//        
//       try (InputStream is = new ByteArrayInputStream(responseString.getBytes());
//          JsonReader jsonReader = Json.createReader(is);) {
//
//            JsonStructure jsonStructure = jsonReader.read();
//            JsonPointer pFiles = Json.createPointer(JsonPointerForDataset.POINTER_TO_LV_FILES);
//            JsonArray Files = (JsonArray) pFiles.getValue(jsonStructure);
//            JsonPointer pMd5 = Json.createPointer("/dataFile/md5");
//            List<String> md5List = new ArrayList<>();
//            
//            for (JsonValue jvFile : Files) {
//                JsonString jsMd5 = (JsonString) pMd5.getValue(jvFile.asJsonObject());
//                String md5Value = jsMd5.getString();
//                logger.log(Level.INFO, "md5value={0}", md5Value);
//                md5List.add(md5Value);
//            }
//            logger.log(Level.INFO, "md5List={0}", md5List);
//            int result = md5List.size();
//            
//            logger.log(Level.INFO, "result={0}", result);
//        }
//        
//        
//        
//        return "";
//    }
    
    
    /**
     * Returns a List of the MD5 values of Datafiles within a Dataset from a response string
     * 
     * @param responseString
     * @return the list of MD5 values as string
     * @throws IOException 
     */
    public List<String> getMD5ValueList(String responseString) throws IOException {
        List<String> md5List = new ArrayList<>();
        try (InputStream is = new ByteArrayInputStream(responseString.getBytes());
          JsonReader jsonReader = Json.createReader(is);) {
            JsonStructure jsonStructure = jsonReader.read();
            JsonPointer pFiles = Json.createPointer(JsonPointerForDataset.POINTER_TO_LV_FILES);
            JsonArray Files = (JsonArray) pFiles.getValue(jsonStructure);
            JsonPointer pMd5 = Json.createPointer("/dataFile/md5");
            
            for (JsonValue jvFile : Files) {
                JsonString jsMd5 = (JsonString) pMd5.getValue(jvFile.asJsonObject());
                String md5Value = jsMd5.getString();
                logger.log(Level.INFO, "md5value={0}", md5Value);
                md5List.add(md5Value);
            }
            logger.log(Level.INFO, "md5List={0}", md5List);
            int result = md5List.size();
            
            logger.log(Level.INFO, "result={0}", result);
        }
        return md5List;
    }
    
    /**
     * Returns a list of dulicated MD5 values that are already saved in the Dataset
     * @param responseString
     * @return the list of duplicated MD5 values
     * @throws IOException
     */
    public List<String> getListOfDuplicatedMD5Values(String responseString) throws IOException{
        
        List<String> md5List = new ArrayList<>();

        try (InputStream is =  new ByteArrayInputStream(responseString.getBytes());
          JsonReader jsonReader = Json.createReader(is);) {

            JsonStructure jsonStructure = jsonReader.read();
            JsonPointer pFiles = Json.createPointer(JsonPointerForDataset.POINTER_TO_LV_FILES);
            JsonArray Files = (JsonArray) pFiles.getValue(jsonStructure);
            JsonPointer pMd5 = Json.createPointer(JsonPointerForDataset.POINTER_TO_MD5_VALUE);

            
            for (JsonValue jvFile : Files) {
                JsonString jsMd5 = (JsonString) pMd5.getValue(jvFile.asJsonObject());
                String md5Value = jsMd5.getString();
                logger.log(Level.INFO, "md5value={0}", md5Value);
                md5List.add(md5Value);
            }
            logger.log(Level.INFO, "md5List={0}", md5List);
            int result = md5List.size();
            
            logger.log(Level.INFO, "result={0}", result);

        }
        
        List<String> duplicates = 
          md5List.stream().collect(Collectors.groupingBy(Function.identity()))
            .entrySet()
            .stream()
            .filter(e -> e.getValue().size() > 1)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        if (duplicates==null){
            duplicates = new ArrayList<>();
        }
        logger.log(Level.INFO, "duplicates={0}", duplicates);
        int result = duplicates.size();
        logger.log(Level.INFO, "result={0}", result);
        return duplicates;
    }
    
    
    
    /**
     * Returns the Map of MD5-value-to-Filename from an existing Dataset
     * @param responseString
     * @return the Map of MD5-value to-Filename
     * @throws IOException
     */
    public Map<String, String> GetMD5ValueToFilenameTable(String responseString) throws IOException{
        Map<String, String> md5toFileName = new LinkedHashMap<>();

        try (InputStream is = new ByteArrayInputStream(responseString.getBytes());
          JsonReader jsonReader = Json.createReader(is);) {

            JsonStructure jsonStructure = jsonReader.read();
            JsonPointer pFiles = Json.createPointer(JsonPointerForDataset.POINTER_TO_LV_FILES);
            JsonArray Files = (JsonArray) pFiles.getValue(jsonStructure);
            JsonPointer pMd5 = Json.createPointer(JsonPointerForDataset.POINTER_TO_MD5_VALUE);
            JsonPointer pFilename = Json.createPointer(JsonPointerForDataset.POINTER_TO_FILENAME);
            List<String> md5List = new ArrayList<>();
            
            for (JsonValue jvFile : Files) {
                JsonString jsMd5 = (JsonString) pMd5.getValue(jvFile.asJsonObject());
                JsonString jsFilename = (JsonString) pFilename.getValue(jvFile.asJsonObject());
                String md5Value = jsMd5.getString();
                String filename = jsFilename.getString();
                logger.log(Level.INFO, "md5value={0}", md5Value);
                logger.log(Level.INFO, "filename={0}", filename);
                md5List.add(md5Value);
                md5toFileName.put(md5Value, filename);
            }
            logger.log(Level.INFO, "md5List={0}", md5List);
            logger.log(Level.INFO, "fileNameToMD5={0}", md5toFileName);
            //int result = md5List.size();
            int result = md5toFileName.size();
            logger.log(Level.INFO, "result={0}", result);
        }
        
        return md5toFileName;
    }
    
}
