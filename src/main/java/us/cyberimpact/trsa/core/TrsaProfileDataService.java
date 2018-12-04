/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonWriter;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author asone
 */
public class TrsaProfileDataService {
    
    private static final Logger logger = Logger.getLogger(TrsaProfileDataService.class.getName());
    
    private static TrsaProfileDataService instance = new TrsaProfileDataService();
    
    public static TrsaProfileDataService getInstance(){
        return instance;
    }
    
    public TrsaProfileDTO createNewProfile(TrsaProfileDTO newProfile){
        
        // serialize this profile
        Jsonb jsonb = JsonbBuilder.create();
        String jsonProfileToBeSaved = jsonb.toJson(newProfile);
        logger.log(Level.INFO, "jsonProfileToBeSaved={0}", jsonProfileToBeSaved);
        File jsonFile = new File(".TrsaProfile");
        jsonFile.mkdirs();
//        try (OutputStream os = new FileOutputStream(jsonFile)){
//            JsonWriter jsonWriter = Json.createWriter(os);
//            jsonWriter.writeObject(object);
//        } catch (IOException ex) {
//            logger.log(Level.SEVERE, null, ex);
//        }
        try (Writer fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonFile)))){
            fw.write(jsonProfileToBeSaved);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IOException occurred when saving a profile", ex);
        }
        return newProfile;
        }
    
    
    // move the following method to a new class under util package
    
    private Properties getPropertiesFile() {

        Properties gfJvmProps = System.getProperties();
        Properties configProps = new Properties();

        if (gfJvmProps.containsKey("irods.config.file")) {
            String irodsConfigFileName
                    = gfJvmProps.getProperty("irods.config.file");

            if (StringUtils.isNotBlank(irodsConfigFileName)) {
                // load the configuration file
                logger.log(Level.INFO, "irodsConfigFileName={}", irodsConfigFileName);

                InputStream is = null;
//                File irodsConfigFile = null;
                
                try {
//                    irodsConfigFile = new File(irodsConfigFileName);
                    is = new FileInputStream(new File(irodsConfigFileName));
                    
                    configProps.load(is);

                    for (String key : configProps.stringPropertyNames()) {
                        logger.log(Level.INFO,
                                "key={}:value={}", new Object[]{key,
                                    configProps.getProperty(key)});
                    }

//
//                } catch (FileNotFoundException ex) {
//                    log.warn("specified config file was not found", ex);
                } catch (IOException ex) {
                    logger.log(Level.WARNING,"IO error occurred", ex);
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException ex) {
                            logger.log(Level.WARNING,"failed to close the opened local config file", ex);
                        }
                    }
                }

            } else {
                // irodsConfigFileName is null or empty
                logger.log(Level.SEVERE,"configFileName is null or empty");
            }
        } else {
            // no entry within jvm options
            logger.log(Level.SEVERE,"config.file is not included in the JVM options");

        }
        return configProps;
    }
    
    
    
}
