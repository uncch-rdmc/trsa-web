package edu.unc.odum.dataverse.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asone
 */
public class JsonFilter {
    private static final Logger logger = Logger.getLogger(JsonFilter.class.getName());
    
    static {
        Configuration.setDefaults(new Configuration.Defaults() {
            private final JsonProvider jsonProvider = new GsonJsonProvider();
            private final MappingProvider mappingProvider = new GsonMappingProvider();
            private final Set<Option> options = EnumSet.noneOf(Option.class);

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return options;
            }
        });
    }
    
    
    public void filterApiPayload(String jsonFileName, String destPath) {
        try (InputStream inputStream = new FileInputStream(jsonFileName)) {
            filterApiPayload(inputStream, destPath);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "creating the inputstream throws the IOException", ex);
        }
    }

    public void filterApiPayload(File jsonFile, String destPath) {
        try (InputStream inputStream = new FileInputStream(jsonFile)){
            filterApiPayload(inputStream, destPath);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "creating the inputstream throws the IOException", ex);
        }
        
    }

    public void filterApiPayload(InputStream inputStream, String destPath) {
        Configuration configuration = Configuration.defaultConfiguration();

        DocumentContext dc = JsonPath.using(configuration).parse(inputStream);

        Object dsObj = dc.read("$.datasetVersion['metadataBlocks', 'files']");
        logger.log(Level.FINE, "dsObj={0}", dsObj);
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        JsonObject filtered = new JsonObject();
        filtered.add("datasetVersion", gson.toJsonTree(dsObj));
        logger.log(Level.FINE, "filtered={0}", filtered);

        try (Writer writer = new FileWriter(destPath)) {
            gson.toJson(filtered, writer);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IOException was thrown while dumping the filtered json object", ex);
        }

        
        
    }
    
    
    public Object selectMetadataPayload(InputStream inputStream) {
        Configuration configuration = Configuration.defaultConfiguration();

        DocumentContext dc = JsonPath.using(configuration).parse(inputStream);

        Object dsObj = dc.read("$.datasetVersion['metadataBlocks', 'files']");
        logger.log(Level.FINE, "dsObj={0}", dsObj);
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return dsObj;
//        try (Writer writer = new FileWriter(destPath)) {
//            gson.toJson(dsObj, writer);
//        } catch (IOException ex) {
//            logger.log(Level.SEVERE, "IOException was thrown while dumping the filtered json object", ex);
//        }
    }    
    
    
    
    public Object selectMetadataPayload(String jsonFileName) {
        Object dsObjct = null;
        try (InputStream inputStream = new FileInputStream(jsonFileName)) {
            dsObjct =  selectMetadataPayload(inputStream);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "creating the inputstream throws the IOException", ex);
        }
        return dsObjct;
    }

    public Object selectMetadataPayload(File jsonFile) {
        Object dsObjct = null;
        try (InputStream inputStream = new FileInputStream(jsonFile)){
            dsObjct=selectMetadataPayload(inputStream);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "creating the inputstream throws the IOException", ex);
        }
        return dsObjct;
    }
    
    
    
    
    
    
    
    
    
    public void filterApiPayloadMetadataOnly(InputStream inputStream, String destPath) {
        Configuration configuration = Configuration.defaultConfiguration();

        DocumentContext dc = JsonPath.using(configuration).parse(inputStream);

        Object dsObj = dc.read("$.datasetVersion['metadataBlocks', 'files']");
        logger.log(Level.FINE, "dsObj={0}", dsObj);
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        try (Writer writer = new FileWriter(destPath)) {
            gson.toJson(dsObj, writer);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IOException was thrown while dumping the filtered json object", ex);
        }
    }
    
    
    
    public void filterApiPayloadMetadataOnly(String jsonFileName, String destPath) {
        try (InputStream inputStream = new FileInputStream(jsonFileName)) {
            filterApiPayloadMetadataOnly(inputStream, destPath);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "creating the inputstream throws the IOException", ex);
        }
    }

    public void filterApiPayloadMetadataOnly(File jsonFile, String destPath) {
        try (InputStream inputStream = new FileInputStream(jsonFile)){
            filterApiPayloadMetadataOnly(inputStream, destPath);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "creating the inputstream throws the IOException", ex);
        }
        
    }
    
    
    
    public String parseDatasetCreationResponse(String responseString){
        logger.log(Level.INFO, "responseString={0}", responseString);
        
        Configuration configuration = Configuration.defaultConfiguration();

        DocumentContext dc = JsonPath.using(configuration).parse(responseString);
        String path = "$.data.id";
        Object dsObj = dc.read(path);
        logger.log(Level.INFO, "dsObj={0}", dsObj);
        
        return dsObj.toString();
    }
    
    
//    public static void main(String[] args) throws FileNotFoundException {
//        JsonFilter demo = new JsonFilter();
//        String datasetIdentifier ="6NBQ5E";
//        String fileLocationRoot = "/tmp/files/10.5072/FK2/";
//        String fileLocation =  fileLocationRoot+ datasetIdentifier + "/export_dataverse_json.cached";
//        String pathString = fileLocationRoot+ datasetIdentifier + "/filtered-result.json";
//        InputStream inputStream =  new FileInputStream(fileLocation);
//        demo.filterApiPayload(inputStream, pathString);
//    }
    
    
}
