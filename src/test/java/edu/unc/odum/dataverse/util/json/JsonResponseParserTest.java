package edu.unc.odum.dataverse.util.json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonPointer;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;

/**
 *
 * @author asone
 */
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
public class JsonResponseParserTest {
    
    private static final Logger logger = 
            Logger.getLogger(JsonResponseParserTest.class.getName());
    
    
    public JsonResponseParserTest() {
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

    static String RESPONSE_NEW_DATASET_CREATION="{\"status\":\"OK\",\"data\":{\"id\":29,\"persistentId\":\"doi:10.5072/FK2/UEEXAV\"}}";
    static String RESPONSE_NEW_DATASET_CREATION_X ="{\"data\":{\"persistentId\":\"doi:10.33563/FK2/FLIAPV\",\"id\":25},\"status\":\"OK\"}";
    
    
    
    /**
     * Test of parseDatasetIdFromCreationResponse method, of class JsonResponseParser.
     */
    @Test
    public void testParseDatasetIdFromCreationResponse() {
        System.out.println("parseDatasetIdFromCreationResponse");
        JsonResponseParser instance = new JsonResponseParser();
        String expResult = "29";
        logger.log(Level.INFO, "expResult={0}", expResult);
        String actual = instance.parseDatasetIdFromCreationResponse(RESPONSE_NEW_DATASET_CREATION);
        logger.log(Level.INFO, "actual={0}", actual);
        Assertions.assertEquals(expResult, actual);
    }

    /**
     * Test of parseDatasetDoiFromDsCreationResponse method, of class JsonResponseParser.
     */
    @Test
    public void testParseDatasetDoiFromDsCreationResponse() {
        System.out.println("parseDatasetDoiFromDsCreationResponse");
        JsonResponseParser instance = new JsonResponseParser();
        String expResult = "doi:10.5072/FK2/UEEXAV";
        logger.log(Level.INFO, "expected={0}", expResult);
        String actual = instance.parseDatasetDoiFromDsCreationResponse(RESPONSE_NEW_DATASET_CREATION);
        logger.log(Level.INFO, "actual={0}", actual);
        Assertions.assertEquals(expResult, actual);
    }

    /**
     * Test of parseTargetField method, of class JsonResponseParser.
     */
    @Test
    @DisplayName("testParseTargetField: a case of /data/storageIdentifier")
    public void testParseTargetField() throws IOException {
        System.out.println("parseTargetField");
        String responseFileName = "/json/response-string-1.json";
        String expResultString = "file://10.33563/FK2/KMPVRL";
        String jsonPointerExpr = JsonPointerForDataset.POINTER_TO_STORAGEIDENTIFIER;
        try (InputStream is = getClass().getResourceAsStream(responseFileName);
            Scanner sc = new Scanner(is);){
            String responseString = sc.nextLine();
            JsonResponseParser instance = new JsonResponseParser();
            JsonValue result = instance.parseTargetField(responseString, jsonPointerExpr);

            Assertions.assertEquals(expResultString, result.toString().substring(1, result.toString().length()-1));     
        }
    }

    /**
     * Test of parseTargetStringField method, of class JsonResponseParser.
     */
    @Test
    public void testParseTargetStringField() throws IOException {
        System.out.println("parseTargetStringField");
        String responseFileName = "/json/response-string-1.json";
        String expResult = "doi";
        
        String jsonPointerExpr = JsonPointerForDataset.POINTER_TO_PROTOCOL;
        try (InputStream is = getClass().getResourceAsStream(responseFileName);
            Scanner sc = new Scanner(is);){
            String responseString = sc.nextLine();
            JsonResponseParser instance = new JsonResponseParser();
            String result = instance.parseTargetStringField(responseString, jsonPointerExpr);
            Assertions.assertEquals(expResult, result);
        
        }

    }

    /**
     * Test of parseTargetNumericField method, of class JsonResponseParser.
     */
    @Test
    public void testParseTargetNumericField() throws IOException {
        System.out.println("parseTargetNumericField");

        int expResult = 15;
        String responseFileName = "/json/response-string-1.json";
        String jsonPointerExpr = JsonPointerForDataset.POINTER_TO_DATASET_ID;
        try (InputStream is = getClass().getResourceAsStream(responseFileName);
            Scanner sc = new Scanner(is);){
            String responseString = sc.nextLine();
            JsonResponseParser instance = new JsonResponseParser();
            JsonNumber result = instance.parseTargetNumericField(responseString, jsonPointerExpr);
            Assertions.assertEquals(expResult, result.intValue());
        
        }
    }



    /**
     * Test of getMD5List method, of class JsonResponseParser.
     */
    @Test
    public void testGetMD5ValueList() throws Exception {
        System.out.println("getMD5ValueList");
        
        
        String responseFileName = "/json/response-string-3.json";
        int expResult = 3;
        List<String> expected = new ArrayList<>(Arrays.asList("2cf42a13f2ae1961646d3c57643b2ec3",
          "2065ba6591dbd21b5dc6297efe6ebba0", "ce11c09ee4d22ad595978d4efe5c7998"));
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
            Assertions.assertEquals(expResult, result);
            Assertions.assertEquals(expected, md5List);
            
        }
        
        
        
        
    }

    /**
     * Test of getListOfDuplicatedMD5Values method, of class JsonResponseParser.
     */
//    @Disabled
//    @Test
//    public void testGetListOfDuplicatedMD5Values() throws Exception {
//        System.out.println("getListOfDuplicatedMD5Values");
//        String responseFileName = "/json/response-string.json";
//        
//        //String responseString = "";
//        //JsonResponseParser instance = new JsonResponseParser();
//        List<String> expResult = new ArrayList<>(List.of("13 enforcer_menu_2012-10-08.png"));
//        List<String> result;
//        try (InputStream is = getClass().getResourceAsStream(responseFileName);
//            Scanner sc = new Scanner(is);){
//            String responseString = sc.nextLine();
//            JsonResponseParser instance = new JsonResponseParser();
//            result = instance.getListOfDuplicatedMD5Values(responseString);
//        }
//        assertEquals(expResult, result);
//    }

    /**
     * Test of getMD5ValueToFilenameTable method, of class JsonResponseParser.
     */
    @Disabled
    @Test
    public void testGetMD5ValueToFilenameTable() throws IOException {
        System.out.println("getMD5ValueToFilenameTable");
        String responseFileName = "/json/response-string-3.json";
        int expResult = 3;
        Map<String, String> result;
        logger.log(Level.INFO, "expResult={0}", expResult);
        try (InputStream is = getClass().getResourceAsStream(responseFileName);
            Scanner sc = new Scanner(is);){
            String responseString = sc.nextLine();
            JsonResponseParser instance = new JsonResponseParser();
            result = instance.getMD5ValueToFilenameTable(responseString);
        }
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getFilenameSetFromResponse method, of class JsonResponseParser.
     */
    @Test
    public void testGetFilenameSetFromResponse() throws IOException {
        System.out.println("getFilenameSetFromResponse");
        String responseFileName = "/json/response-string_latest-draft-all-in-1.json";
        int expResult = 5;
        Set<String> result;
        
        try (InputStream is = getClass().getResourceAsStream(responseFileName);
            Scanner sc = new Scanner(is);){
            String responseString = sc.nextLine();
            logger.log(Level.INFO, "responseString={0}", responseString);
            JsonResponseParser instance = new JsonResponseParser();
            result = instance.getFilenameSetFromResponse(responseString);
            logger.log(Level.INFO, "result={0}", result);
        }
        int actual = result.size();
        assertEquals(expResult, actual);
    }


    
    
    
}
