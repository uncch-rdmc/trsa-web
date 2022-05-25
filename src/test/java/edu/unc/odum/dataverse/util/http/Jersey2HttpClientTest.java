package edu.unc.odum.dataverse.util.http;

import edu.unc.odum.dataverse.util.json.JsonResponseParser;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import us.cyberimpact.trsa.web.WebAppConstants;

/**
 *
 * @author asone
 */
public class Jersey2HttpClientTest {
    private static final Logger logger = Logger.getLogger(Jersey2HttpClientTest.class.getName());
    
    Jersey2HttpClient client;
    
    @BeforeAll
    public static void initAll() {
//        
//        client = ClientBuilder.newClient();
    }
    
    @AfterAll
    public static void tearDownAll() {
    }
    
    @BeforeEach
    public void init() {
        client= new Jersey2HttpClient("https://impacttest.irss.unc.edu", 
          "", "1");
        
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of confirmDataverseSetup method, of class Jersey2HttpClient.
     */
    @DisplayName("testing Jersey2HttpClientTest#confirmDataverseSetup method")
    @Disabled("local-test only")
    @Test
    public void testConfirmDataverseSetup() {
        logger.log(Level.INFO, "++++++++++++ Jersey2HttpClientTest#testConfirmDataverseSetup: start ++++++++++");
        System.out.println("confirmDataverseSetup");
        String selectedDataverseId = "trsawebtest";
        String trsaRegNmbr = "1";
        String expResult = "TRSA-Web Test";
        Response response = client.confirmDataverseSetup(selectedDataverseId, trsaRegNmbr);
        String responseString = response.readEntity(String.class);
        
        System.out.println("responseString:\n"+responseString);
        JsonResponseParser jsonParser = new JsonResponseParser();
        String result
                  = jsonParser.parseTargetStringField(responseString, "/data/name");
        logger.log(Level.INFO, "dataverseTitle={0}", result);
        
        assertEquals(expResult, result);
        logger.log(Level.INFO, "++++++++++++ Jersey2HttpClientTest#testConfirmDataverseSetup: end ++++++++++");
    }
    
    
    @DisplayName("testing Jersey2HttpClientTest#confirmDataverseSetup method")
    @Disabled("local-test only")
    @Test
    public void testConfirmDataverseSetupVer2() {
        logger.log(Level.INFO, "++++++++++++ Jersey2HttpClientTest#confirmDataverseSetupVer2: start ++++++++++");
        System.out.println("testing confirmDataverseSetupVer2");
        String selectedDataverseId = "trsawebtest";
        String expResult = "TRSA-Web Test";
        Response response = client.confirmDataverseSetupVer2(selectedDataverseId);
        String responseString = response.readEntity(String.class);
        
        System.out.println("responseString:\n"+responseString);
        JsonResponseParser jsonParser = new JsonResponseParser();
        String result
                  = jsonParser.parseTargetStringField(responseString, "/data/name");
        logger.log(Level.INFO, "dataverseTitle={0}", result);
        
        assertEquals(expResult, result);
        logger.log(Level.INFO, "++++++++++++ Jersey2HttpClientTest#confirmDataverseSetupVer2: end ++++++++++");
    }
    

    /**
     * Test of get method, of class Jersey2HttpClient.
     */
    @DisplayName("testing Jersey2HttpClientTest#get method")
    @Disabled("local-test only")
    @Test
    public void testGet() {
        System.out.println("testing get");
        String path1 = WebAppConstants.PATH_DATAVERSE_API;
        String path2 = "trsawebtest";
        String path3 = null;
        Response jsonResponse = client.get(path1, path2, path3);
        String responseString = jsonResponse.readEntity(String.class);
        // datasetPersistentId	"doi:10.33563/FK2/EPS6UD"
        
        String expResult = "TRSA-Web Test";
        
        JsonResponseParser jsonParser = new JsonResponseParser();
        String result
                  = jsonParser.parseTargetStringField(responseString, "/data/name");
        logger.log(Level.INFO, "dataverseTitle={0}", result);
        
        assertEquals(expResult, result);
    }
    
}
