/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang3.StringUtils;
import us.cyberimpact.trsa.exception.TrsaConfigurationException;
import us.cyberimpact.trsa.TrsaExceptionInterceptor;

/**
 *
 * @author asone
 */
@Startup
@Singleton
public class AppConfig {
    private static final Logger logger = Logger.getLogger(AppConfig.class.getName());
    
    
    // While the factory default settings are stored in trsa.config file, 
    // the location of trsa.config file is given by a JVM option, 
    // -Dtrsa.configfile.directory=
    // the file-storage directory can be set both JVM option and trsa.config
    // the value for ":TrsaLocalFiles" in trsa.config can be 
    // overridden by a JVM option, -Dtrsa.files.directory=
    // 
    
    public static final String TRSA_CONFIG_FILE_DIR="trsa.configfile.directory";
    public static final String TRSA_CONFIG_FILE_NAME="trsa.config";
    public static final String TRSA_FILES_DIR = ":TrsaLocalFiles";
    public static final String JVM_OPTION_TRSA_FILES="trsa.files.directory";
    
    @EJB
    protected SettingsServiceBean settingsSvc;

//    @PersistenceContext(unitName = "trsa-WebPU")
//    private EntityManager em;

    boolean appConfigStateReady=false;
    
    String appConfigStateMessage="";

    public boolean isAppConfigStateReady() {
        return appConfigStateReady;
    }

    public String getAppConfigStateMessage() {
        return appConfigStateMessage;
    }
    
    AppConfigState startupState;

    public AppConfigState getStartupState() {
        return startupState;
    }
    
    
    
    Map<String, String> trsaConfigMap = new ConcurrentHashMap<>();
    
    String trsaFilesDirectory;

    public String getTrsaFilesBaseDirectory() {
        return trsaFilesDirectory;
    }
    
    
    
    String trsaFilesPath;
    
    @PostConstruct
    @Interceptors(TrsaExceptionInterceptor.class)
    public void init() {
        logger.log(Level.INFO, "============ TRSA-WEB: AppConfig#init() starts here ============");
        
        // first read the trsa.config file with the help of a JVM option
        
        trsaConfigMap= readConfigFile();
        logger.log(Level.INFO, "trsaConfigMap={0}", trsaConfigMap);
        if (!trsaConfigMap.isEmpty() && trsaConfigMap.containsKey(TRSA_FILES_DIR)) {
            trsaFilesDirectory = trsaConfigMap.get(TRSA_FILES_DIR);
            logger.log(Level.INFO, "trsaFilesDirectory={0}", trsaFilesDirectory);
        } else {
          // the key was not found;
          // next step will try to find it within the JVM options later
            logger.log(Level.INFO,
                    "key:TRSA_FILES_DIR={0} was not found in the trsa config file",
                    TRSA_FILES_DIR);
        }
        
        logger.log(Level.INFO, "checking the JVM options");       
        String filesRootDirectory = System.getProperty(JVM_OPTION_TRSA_FILES);
        if (StringUtils.isBlank(filesRootDirectory)){
            logger.log(Level.INFO, "filesRootDirectory was not set by a JVM option:{0}",
                    JVM_OPTION_TRSA_FILES);
            // do nothing: use the one in trsa.config
            
        } else {
            logger.log(Level.INFO, "filesRootDirectory={0}", filesRootDirectory);
            logger.log(Level.INFO, "filesRootDirectory is set by a JVM option to:{0}", 
                    filesRootDirectory);
        }
        
        
        if (StringUtils.isNotBlank(trsaFilesDirectory) && 
            StringUtils.isNotBlank(filesRootDirectory)){
            
            if (filesRootDirectory.equals(trsaFilesDirectory)) {
                // no action necessary 
                logger.log(Level.INFO, "config file and jvm option are the same");
            } else {
                logger.log(Level.INFO, "config file and jvm option differ");
                logger.log(Level.INFO, "trsaFilesDirectory={0}", trsaFilesDirectory);
                logger.log(Level.INFO, "filesRootDirectory={0}", filesRootDirectory);
                logger.log(Level.INFO, "filesRootDirectory from JVM option:{0} is used", filesRootDirectory);
                
                // prefer the JVM option
                trsaFilesDirectory= filesRootDirectory;
                settingsSvc.setValueForKey(SettingsServiceBean.Key.TrsaLocalFiles, filesRootDirectory);
                logger.log(Level.INFO, "TrsaLocalFiles: updated value={0}", settingsSvc.getValueForKey(SettingsServiceBean.Key.TrsaLocalFiles));
            }
        } else if (StringUtils.isBlank(trsaFilesDirectory) && 
            StringUtils.isNotBlank(filesRootDirectory)) {
            logger.log(Level.INFO, "The JVM-option is chosen because trsa.config is blank");
            trsaFilesDirectory=filesRootDirectory;
            settingsSvc.setValueForKey(SettingsServiceBean.Key.TrsaLocalFiles, filesRootDirectory);
            logger.log(Level.INFO, "TrsaLocalFiles: updated value={0}", settingsSvc.getValueForKey(SettingsServiceBean.Key.TrsaLocalFiles));
            
        } else if (StringUtils.isNotBlank(trsaFilesDirectory) && 
            StringUtils.isBlank(filesRootDirectory)) {
            logger.log(Level.INFO, "The JVM-option is blank but trsa.config is not blank: use trsa.config");
            // no action is necessary
        } else if (StringUtils.isBlank(trsaFilesDirectory) && 
            StringUtils.isBlank(filesRootDirectory)) {
            logger.log(Level.SEVERE, "both trsa.config/JVM option lack a storage setting value");
            if (startupState == null){
                startupState = new AppConfigState();
                startupState.setState("Both JVM-option/trsa.config lack a storage-directory value");
                startupState.setSolution("Set the JVM option: -Dtrsa.files.directory=your_value and set the key-value pair (key=:TrsaLocalFiles) in trsa.config file");
            }

        }
        
        trsaConfigMap.forEach((k,v) -> settingsSvc.set(k, v));
        
        trsaFilesPath = trsaFilesDirectory
                +"/"+settingsSvc.getValueForKey(SettingsServiceBean.Key.Authority)
                +"/"+ settingsSvc.getValueForKey(SettingsServiceBean.Key.Shoulder);
        
        logger.log(Level.INFO, "trsaFilesPath={0}", trsaFilesPath);
        logger.log(Level.INFO, "============ TRSA-WEB: AppConfig#init() ends here ============");
    }
    
    

    @PreDestroy
    public void destroy() {

    }
    
    
    private Map<String, String> readConfigFile() {
        Map<String, String> stringStringMap = new ConcurrentHashMap<>();

        try (Stream<String> lines = Files.lines(Paths.get(getTrsaConfigFileDir()
                + "/" + TRSA_CONFIG_FILE_NAME))) {

            stringStringMap=lines
                .map(
                    s -> {
                            String[] splitStrings = s.split("\t", -1);
                            logger.log(Level.INFO, "splitStrings={0}", splitStrings);
                            return splitStrings;
                        }
                    ).collect(Collectors.toMap(x->x[0], x->x[1]));
        } catch (IOException e) {
            logger.log(Level.WARNING, "IOException was thrown", e);
        }
        logger.log(Level.INFO, "splitStrings={0}", stringStringMap);
        return stringStringMap;
    }

    public String getTrsaFilesPath() {
        return trsaFilesPath;
    }
    
    private String getTrsaConfigFileDir() {
        Properties property = System.getProperties();
        String appConfigDir = property.getProperty(TRSA_CONFIG_FILE_DIR);
        // if jvm option is not set, this asppConfigDir is null 
        
        if (StringUtils.isBlank(appConfigDir)){
            // failure #1
            String errorSummary ="The value for trsa.configfile.directory is not found among JVM options";
            startupState = new AppConfigState();
            startupState.setState("Configuration Error: Two required JVM option are missing");
            startupState.setSolution("Add the following JVM options: -Dtrsa.configfile.directory=your_value and -Dtrsa.files.directory=your_value");
            logger.log(Level.SEVERE, errorSummary);
        } else {
            logger.log(Level.INFO, "appConfigDir={0}", appConfigDir);
        }
        return appConfigDir;
    }
    
    
    
    
    public boolean isNotaryServiceEnabled() {
        String notaryServiceEnabled = settingsSvc.getValueForKey(
                SettingsServiceBean.Key.NotaryServiceEnabled, null);
        if("true".equalsIgnoreCase(notaryServiceEnabled)){         
            return true;
        }
        return false;
    }
    
}
