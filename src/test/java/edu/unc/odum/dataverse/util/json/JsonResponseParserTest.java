package edu.unc.odum.dataverse.util.json;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.Disabled;

/**
 *
 * @author asone
 */
public class JsonResponseParserTest {
    
    private static final Logger logger = 
            Logger.getLogger(JsonResponseParserTest.class.getName());
    
    
    public JsonResponseParserTest() {
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

    static String RESPONSE_NEW_DATASET_CREATION="{\"status\":\"OK\",\"data\":{\"id\":29,\"persistentId\":\"doi:10.5072/FK2/UEEXAV\"}}";
    static String RESPONSE_NEW_DATASET_CREATION_X ="{\"data\":{\"persistentId\":\"doi:10.33563/FK2/FLIAPV\",\"id\":25},\"status\":\"OK\"}";
    
    
    
    /**
     * Test of parseDatasetIdFromCreationResponse method, of class JsonResponseParser.
     */
    @Test
    public void testParseDatasetCreationResponse() {
        System.out.println("parseDatasetCreationResponse");
        JsonResponseParser instance = new JsonResponseParser();
        String expResult = "29";
        logger.log(Level.INFO, "expResult={0}", expResult);
        String actual = instance.parseDatasetIdFromCreationResponse(RESPONSE_NEW_DATASET_CREATION);
        logger.log(Level.INFO, "actual={0}", actual);
        assertEquals(expResult, actual);
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
        assertEquals(expResult, actual);
    }
    
}
