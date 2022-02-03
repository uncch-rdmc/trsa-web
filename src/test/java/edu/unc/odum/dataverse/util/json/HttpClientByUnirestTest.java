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
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 *
 * @author asone
 */
@RunWith(JUnitPlatform.class)
public class HttpClientByUnirestTest {
    
    public HttpClientByUnirestTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
        System.out.println("HttpClientByUnirestTest begins");
    }
    
    @AfterAll
    public static void tearDownClass() {
        System.out.println("HttpClientByUnirestTest ends");
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
    @DisplayName("\n\ntesting requestGetCall")
    @Test
    public void testRequestGetCall() {
        String keyPath = "datasets";
        String apiPath = "versions/1.0/files";
        String selectedDatasetId = "6";
        String apiKey="4988fbdd-de13-49c9-a6cc-aad6c6261822";
        String server="https://impacttest.irss.unc.edu";
        HttpClientByUnirest instance = new HttpClientByUnirest(server, apiKey);
        // curl's oder is different from Unirest's one
        //String expResult0 = "{\"status\":\"OK\",\"data\":[]}";
        String expResult  = "{\"data\":[],\"status\":\"OK\"}";
        String result = instance.requestGetCall(keyPath, apiPath, selectedDatasetId);
        assertEquals(expResult, result);
        JSONAssert.assertEquals(expResult, result, JSONCompareMode.STRICT);
        System.out.println("ending to test requestGetCall\n\n");
    }

    /**
     * Test of getLastestVersionOfDataset method, of class HttpClientByUnirest.
     */
    @DisplayName("\n\ntesting getLastestVersionOfDataset")
    @Test
    public void testGetLastestVersionOfDataset() throws IOException {
        String datasetId = "24";

        
        String apiKey="4988fbdd-de13-49c9-a6cc-aad6c6261822";
        String server="https://impacttest.irss.unc.edu";
        HttpClientByUnirest instance = new HttpClientByUnirest(server, apiKey);
        String responseFileName = "/json/latestVerDataset.json";
        String expResult = "";
                
        try (InputStream is = getClass().getResourceAsStream(responseFileName);
            Scanner sc = new Scanner(is);){
            expResult = sc.nextLine();
        }
        
        

        String result = instance.getLastestVersionOfDataset(datasetId);
        
        //System.out.println("result:\n"+result);
        JSONAssert.assertEquals(expResult, result, JSONCompareMode.LENIENT);

        //assertEquals(expResult, result);
        System.out.println("ending to test getLastestVersionOfDataset\n\n");
    }

    /**
     * Test of getLatestSetOfFilenamesFromDataset method, of class HttpClientByUnirest.
     */
    @DisplayName("\n\ntesting getLatestSetOfFilenamesFromDataset")
    @Test
    public void testGetLatestSetOfFilenamesFromDataset() {
        String datasetId = "24";
        String apiKey="4988fbdd-de13-49c9-a6cc-aad6c6261822";
        String server="https://impacttest.irss.unc.edu";
        HttpClientByUnirest instance = new HttpClientByUnirest(server, apiKey);
        Set<String> expResult = 
          Stream.of("ImpactTRSAConcept.png", 
            "TRSA_Definition_GUI_Requirements.docx").collect(
              Collectors.toCollection(LinkedHashSet::new));
        Set<String> result = instance.getLatestSetOfFilenamesFromDataset(datasetId);
        assertEquals(expResult, result);
        System.out.println("ending to test getLatestSetOfFilenamesFromDataset\n\n");
    }
    
}
