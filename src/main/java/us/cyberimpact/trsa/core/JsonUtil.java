package us.cyberimpact.trsa.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;

/**
 *
 * @author asone
 */
public class JsonUtil {
    private static final Logger logger = Logger.getLogger(JsonUtil.class.getName());


    
    public void serializeJsonObject(JsonObject object, File jsonFileName){
        
        try (OutputStream outputStream = new FileOutputStream(jsonFileName);
                Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
                JsonWriter jsonWriter = Json.createWriter(writer)){
            jsonWriter.writeObject(object);
            writer.flush();
            
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "output json file was not found", ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IOException occurred", ex);
        }
    }
    
    
    public JsonObject deserializeJsonObject(File jsonFileName){
        JsonObject jsonObject = null;
        try (InputStream inputStream = new FileInputStream(jsonFileName);
                Reader reader = new InputStreamReader(inputStream, "UTF-8");
                JsonReader jsonReader = Json.createReader(reader)){
            jsonObject = jsonReader.readObject();
            
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "input json file was not found", ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IOException occurred", ex);
        }
        return jsonObject;
    }
}
