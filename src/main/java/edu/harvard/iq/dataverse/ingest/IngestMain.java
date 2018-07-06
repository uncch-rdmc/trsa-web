/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.ingest;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import edu.harvard.iq.dataverse.entities.Dataset;
import edu.harvard.iq.dataverse.util.json.JsonParser;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 *
 * @author Akio Sone, Univ, of North Carolina at Chapel Hill, H.W. Odum Inst.
 */
public class IngestMain {

    private static final Logger logger = Logger.getLogger(IngestMain.class.getName());
    static XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
    public static void main(String[] args) {

        JsonObject json;
        String jsonBody;

        // Create JsonReader object
        

        // json/dataset-create-new.json
        try (InputStream input = IngestMain.class.getResourceAsStream("/api-test-data/YIQB7Q001.json"); 
                JsonReader jsonReader = Json.createReader(input)) {
            // Create JsonReader object

            // Get JsonObject (root object).
            JsonObject rootJSON = jsonReader.readObject();
            JsonParser parser = new JsonParser();
            Dataset ds = parser.parseDataset(rootJSON);
            logger.log(Level.INFO, "dataset={0}", xstream.toXML(ds));
            
            
            
            logger.log(Level.INFO, "identifier={0}", rootJSON.get("identifier"));
            logger.log(Level.INFO, "authority={0}", rootJSON.get("authority"));
            logger.log(Level.INFO, "protocol={0}", rootJSON.get("protocol"));
            logger.log(Level.INFO, "persistentUrl={0}", rootJSON.get("persistentUrl"));
            jsonBody = rootJSON.toString();

//            try (StringReader rdr = new StringReader(jsonBody)) {
//                 json = Json.createReader(rdr).readObject();
                JsonObject jsonVersion = rootJSON.getJsonObject("datasetVersion");
                logger.log(Level.INFO, "jsonVersion={0}", jsonVersion);
////                
////                
//                
//            } catch (JsonParsingException jpe) {
//                logger.log(Level.SEVERE, "Json: {0}", jsonBody);
//            }

        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

}
