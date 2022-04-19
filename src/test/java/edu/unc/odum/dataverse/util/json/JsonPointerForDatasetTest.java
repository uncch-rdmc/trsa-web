package edu.unc.odum.dataverse.util.json;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
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
import javax.json.JsonObject;
import javax.json.JsonPatchBuilder;
import javax.json.JsonPointer;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 *
 * @author asone
 */
public class JsonPointerForDatasetTest {
    private static final Logger logger = Logger.getLogger(JsonPointerForDatasetTest.class.getName());
    
    public JsonPointerForDatasetTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testJsonPointer() throws IOException, JSONException {
        
        String rawJsonFileName = "/json/export_dataverse_json.cached";
        String dumpFileName = "serialized.json";
        String filteredResultFile = "/json/filtered-result_2.json";

        // read the raw json file and expected result file
        try (InputStream rawIs = getClass().getResourceAsStream(rawJsonFileName);
            InputStream resultIs = getClass().getResourceAsStream(filteredResultFile);
                JsonReader  jsonReader = Json.createReader(rawIs);
                JsonReader jsonReaderActual = Json.createReader(resultIs);
                PrintWriter printWriter = new PrintWriter(new File(dumpFileName), "UTF-8");
                JsonWriter jsonWriter = Json.createWriter(printWriter)
                ) {

            JsonObject expected = jsonReaderActual.readObject();
            logger.log(Level.INFO, "expected={0}", expected);
            JsonObject rawJsonObject = jsonReader.readObject();

            logger.log(Level.INFO, "rawJsonObject={0}", rawJsonObject);

            // create a new Json object to store JsonPointers
            JsonObject object = Json.createObjectBuilder().build();
            
            // create the two JsonPointer instances 
            JsonPointer metadataBlock = Json.createPointer(JsonPointerForDataset.POINTER_METADATABLOCKS);
            JsonPointer files = Json.createPointer(JsonPointerForDataset.POINTER_FILES);
            
            // get the value for each of the above JsonPointer instances
            JsonValue metadataBlockValue = metadataBlock.getValue(rawJsonObject);
            JsonValue filesValue         = files.getValue(rawJsonObject);
            
            // create a JsonPatchBuilder object and add JsonValu instances
            JsonPatchBuilder builder = Json.createPatchBuilder();
            
            JsonObject actual = builder
                    .add(JsonPointerForDataset.POINTER_METADATABLOCKS_FILTERED, metadataBlockValue)
                    .add(JsonPointerForDataset.POINTER_FILES_FILTERED, filesValue)
                    .build()
                    .apply(object);
            
            logger.log(Level.INFO, "actual={0}", actual);
            jsonWriter.writeObject(actual);
            JSONAssert.assertEquals(expected.toString(), actual.toString(), false);
        }
        
        
        
        
    }
    
    // returns an MD5 value from a JSON-response string
    @Test
    public void testJsonPointerToOneMd5() throws IOException, JSONException {
        String responseFileName = "/json/response-string-1.json";
        String expResult = "2cf42a13f2ae1961646d3c57643b2ec3";
        logger.log(Level.INFO, "expResult={0}", expResult);
        try (InputStream is = getClass().getResourceAsStream(responseFileName);
          JsonReader jsonReader = Json.createReader(is);) {

            JsonStructure jsonStructure = jsonReader.read();
            JsonPointer pFiles = Json.createPointer(JsonPointerForDataset.POINTER_TO_LV_FILES);
            JsonArray Files = (JsonArray) pFiles.getValue(jsonStructure);
            JsonPointer pMd5 = Json.createPointer(JsonPointerForDataset.POINTER_TO_MD5_VALUE);
            String result = "";
            for (JsonValue jvFile : Files) {
                JsonString jsMd5 = (JsonString) pMd5.getValue(jvFile.asJsonObject());
                result = jsMd5.getString();
                logger.log(Level.INFO, "result={0}", result);
            }
            logger.log(Level.INFO, "result={0}", result);
            assertEquals(expResult, result);
        }
    }
    
    // returns a list of MD5 values in a Json-response string
    @Test
    public void testJsonPointerToMd5List() throws IOException, JSONException {
        String responseFileName = "/json/response-string-3.json";
        //String expResult = "2cf42a13f2ae1961646d3c57643b2ec3";
        int expResult = 3;
        logger.log(Level.INFO, "expResult={0}", expResult);
        try (InputStream is = getClass().getResourceAsStream(responseFileName);
          JsonReader jsonReader = Json.createReader(is);) {

            JsonStructure jsonStructure = jsonReader.read();
            JsonPointer pFiles = Json.createPointer(JsonPointerForDataset.POINTER_TO_LV_FILES);
            JsonArray Files = (JsonArray) pFiles.getValue(jsonStructure);
            JsonPointer pMd5 = Json.createPointer(JsonPointerForDataset.POINTER_TO_MD5_VALUE);
            List<String> md5List = new ArrayList<>();
            
            for (JsonValue jvFile : Files) {
                JsonString jsMd5 = (JsonString) pMd5.getValue(jvFile.asJsonObject());
                String md5Value = jsMd5.getString();
                logger.log(Level.INFO, "md5value={0}", md5Value);
                md5List.add(md5Value);
            }
            logger.log(Level.INFO, "md5List={0}", md5List);
            int result = md5List.size();
            
            logger.log(Level.INFO, "result={0}", result);
            assertEquals(expResult, result);
        }
    }
    
    
    // returns a map of filename-to-MD5-value from a Json-response string
    @Test
    public void testGetFilenameToMD5ValueTable() throws IOException, JSONException {
        String responseFileName = "/json/response-string-3.json";
        //String expResult = "2cf42a13f2ae1961646d3c57643b2ec3";
        int expResult = 3;
        logger.log(Level.INFO, "expResult={0}", expResult);
        Map<String, String> fileNameToMD5 = new LinkedHashMap<>();
        try (InputStream is = getClass().getResourceAsStream(responseFileName);
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
                fileNameToMD5.put(filename, md5Value);
            }
            logger.log(Level.INFO, "md5List={0}", md5List);
            logger.log(Level.INFO, "fileNameToMD5={0}", fileNameToMD5);
            //int result = md5List.size();
            int result = fileNameToMD5.size();
            logger.log(Level.INFO, "result={0}", result);
            assertEquals(expResult, result);
        }
    }
    
    
    // returns a map of filename-to-MD5-value from a Json-response string
    @Test
    public void testGetMD5ValueToFilenameTable() throws IOException, JSONException {
        String responseFileName = "/json/response-string-3.json";
        //String expResult = "2cf42a13f2ae1961646d3c57643b2ec3";
        int expResult = 3;
        logger.log(Level.INFO, "expResult={0}", expResult);
        Map<String, String> MD5toFileName = new LinkedHashMap<>();
        try (InputStream is = getClass().getResourceAsStream(responseFileName);
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
                MD5toFileName.put(md5Value, filename);
            }
            logger.log(Level.INFO, "md5List={0}", md5List);
            logger.log(Level.INFO, "fileNameToMD5={0}", MD5toFileName);
            //int result = md5List.size();
            int result = MD5toFileName.size();
            logger.log(Level.INFO, "result={0}", result);
            assertEquals(expResult, result);
        }
    }
    
    
    // 
    @Test
    public void testCheckDuplicated() throws IOException, JSONException {
        
        String responseFileName = "/json/response-string.json";
        List<String> md5List = new ArrayList<>();
        String newMd5Value = "";
        int expResult = 1;
        try (InputStream is = getClass().getResourceAsStream(responseFileName);
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
        newMd5Value = md5List.get(0);
        logger.log(Level.INFO, "newMd5Value={0}", newMd5Value);
        md5List.add(newMd5Value);

        List<String> duplicates = 
          md5List.stream().collect(Collectors.groupingBy(Function.identity()))
            .entrySet()
            .stream()
            .filter(e -> e.getValue().size() > 1)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        logger.log(Level.INFO, "duplicates={0}", duplicates);
        int result = duplicates.size();
        
        assertEquals(expResult, result);
        assertEquals(newMd5Value, duplicates.get(0));
    }
    
    
    // 
    @Test
    public void testCheckDuplicatedFilename() throws IOException, JSONException {
        
        String responseFileName = "/json/response-string.json";
        List<String> md5List = new ArrayList<>();
        Map<String, String> MD5toFileName = new LinkedHashMap<>();
        String newMd5Value = "";
        int expResult = 1;
        try (InputStream is = getClass().getResourceAsStream(responseFileName);
          JsonReader jsonReader = Json.createReader(is);) {

            JsonStructure jsonStructure = jsonReader.read();
            JsonPointer pFiles = Json.createPointer(JsonPointerForDataset.POINTER_TO_LV_FILES);
            JsonArray Files = (JsonArray) pFiles.getValue(jsonStructure);
            JsonPointer pMd5 = Json.createPointer(JsonPointerForDataset.POINTER_TO_MD5_VALUE);
            JsonPointer pFilename = Json.createPointer(JsonPointerForDataset.POINTER_TO_FILENAME);
            
            for (JsonValue jvFile : Files) {
                JsonString jsMd5 = (JsonString) pMd5.getValue(jvFile.asJsonObject());
                String md5Value = jsMd5.getString();
                JsonString jsFilename = (JsonString) pFilename.getValue(jvFile.asJsonObject());
                String filename = jsFilename.getString();
                logger.log(Level.INFO, "md5value={0}", md5Value);
                logger.log(Level.INFO, "filename={0}", filename);
                md5List.add(md5Value);
                MD5toFileName.put(md5Value, filename);
            }
            logger.log(Level.INFO, "md5List={0}", md5List);
            int result = md5List.size();
            
            
            logger.log(Level.INFO, "result={0}", result);

        }
        newMd5Value = md5List.get(0);
        logger.log(Level.INFO, "newMd5Value={0}", newMd5Value);
        String filename0 = MD5toFileName.get(newMd5Value);
        logger.log(Level.INFO, "filename0={0}", filename0);
        md5List.add(newMd5Value);

        List<String> duplicates = 
          md5List.stream().collect(Collectors.groupingBy(Function.identity()))
            .entrySet()
            .stream()
            .filter(e -> e.getValue().size() > 1)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        logger.log(Level.INFO, "duplicates={0}", duplicates);
        int result = duplicates.size();
        String duplicatedMD5value = duplicates.get(0);
        String duplicatedFilename = MD5toFileName.get(duplicatedMD5value);
        logger.log(Level.INFO, "duplicatedFilename={0}", duplicatedFilename);
        assertEquals(expResult, result);
        assertEquals(newMd5Value, duplicatedMD5value);
        assertEquals(filename0, duplicatedFilename);
    }
}
