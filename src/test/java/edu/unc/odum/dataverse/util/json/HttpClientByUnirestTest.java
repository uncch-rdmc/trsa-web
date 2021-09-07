/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unc.odum.dataverse.util.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 *
 * @author asone
 */
public class HttpClientByUnirestTest {
    
    public HttpClientByUnirestTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of requestGetCall method, of class HttpClientByUnirest.
     */
    @Test
    public void testRequestGetCall() {
        System.out.println("requestGetCall");
        String keyPath = "datasets";
        String apiPath = "versions/1.0/files";
        String selectedDatasetId = "6";
        String apiKey="c40e4923-1360-497f-9906-d4d67970ee83";
        String server="https://impacttest.irss.unc.edu";
        HttpClientByUnirest instance = new HttpClientByUnirest(server, apiKey);
        // curl's oder is different from Unirest's one
        //String expResult0 = "{\"status\":\"OK\",\"data\":[]}";
        String expResult  = "{\"data\":[],\"status\":\"OK\"}";
        String result = instance.requestGetCall(keyPath, apiPath, selectedDatasetId);
        assertEquals(expResult, result);
        JSONAssert.assertEquals(expResult, result, JSONCompareMode.STRICT);
    }

    /**
     * Test of getLastestVersionOfDataset method, of class HttpClientByUnirest.
     */
    @Test
    public void testGetLastestVersionOfDataset() throws IOException {
        System.out.println("getLastestVersionOfDataset");
        String datasetId = "24";

        
        String apiKey="c40e4923-1360-497f-9906-d4d67970ee83";
        String server="https://impacttest.irss.unc.edu";
        HttpClientByUnirest instance = new HttpClientByUnirest(server, apiKey);
        String responseFileName = "/json/latestVerDataset.json";
        String expResult = "";
                
        try (InputStream is = getClass().getResourceAsStream(responseFileName);
            Scanner sc = new Scanner(is);){
            expResult = sc.nextLine();
        }
        
        

        String result = instance.getLastestVersionOfDataset(datasetId);
        JSONAssert.assertEquals(expResult, result, true);

    }

    /**
     * Test of getLatestSetOfFilenamesFromDataset method, of class HttpClientByUnirest.
     */
    @Test
    public void testGetLatestSetOfFilenamesFromDataset() {
        System.out.println("getLatestSetOfFilenamesFromDataset");
        String datasetId = "24";
        String apiKey="c40e4923-1360-497f-9906-d4d67970ee83";
        String server="https://impacttest.irss.unc.edu";
        HttpClientByUnirest instance = new HttpClientByUnirest(server, apiKey);
        Set<String> expResult = 
          Stream.of("ImpactTRSAConcept.png", 
            "TRSA_Definition_GUI_Requirements.docx").collect(
              Collectors.toCollection(LinkedHashSet::new));
        Set<String> result = instance.getLatestSetOfFilenamesFromDataset(datasetId);
        assertEquals(expResult, result);

    }
    
}
