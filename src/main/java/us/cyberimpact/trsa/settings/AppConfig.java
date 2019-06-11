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
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author asone
 */
@Startup
@Singleton
public class AppConfig {
    private static final Logger logger = Logger.getLogger(AppConfig.class.getName());
    
    public static final String TRSA_CONFIG_FILE_DIR="trsa.configfile.directory";
    public static final String TRSA_CONFIG_FILE_NAME="trsa.config";
    public static final String TRSA_FILES_DIR = ":TrsaLocalFiles";
    public static final String JVM_OPTION_TRSA_FILES="trsa.files.directory";
    @EJB
    protected SettingsServiceBean settingsSvc;

//    @PersistenceContext(unitName = "trsa-WebPU")
//    private EntityManager em;

    
    
    Map<String, String> trsaConfigMap = new ConcurrentHashMap<>();
    String trsaFilesDirectory;
    String trsaFilesPath;
    
    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "============ TRSA-WEB: AppConfig#init() starts here \"============");
        
        String filesRootDirectory = System.getProperty(JVM_OPTION_TRSA_FILES);
        if (filesRootDirectory == null || filesRootDirectory.equals("")) {
            logger.log(Level.INFO, "filesRootDirectory was not set by a JVM option:{0}",
                    JVM_OPTION_TRSA_FILES);
            // filesRootDirectory = "/tmp/files";
        } else {
            logger.log(Level.INFO, "filesRootDirectory={0}", filesRootDirectory);
            logger.log(Level.INFO, "filesRootDirectory is set by a JVM option to:{0}", 
                    filesRootDirectory);
        }
        
        
        trsaConfigMap= readConfigFile();
        logger.log(Level.INFO, "trsaConfigMap={0}", trsaConfigMap);
        if (!trsaConfigMap.isEmpty() && trsaConfigMap.containsKey(TRSA_FILES_DIR)) {
            trsaFilesDirectory = trsaConfigMap.get(TRSA_FILES_DIR);
            logger.log(Level.INFO, "trsaFilesDirectory={0}", trsaFilesDirectory);
            
            
            if (StringUtils.isNotBlank(trsaFilesDirectory)){
                if (filesRootDirectory.equals(trsaFilesDirectory)) {
                    logger.log(Level.INFO, "config file and jvm option are the same");
                } else {
                    logger.log(Level.INFO, "config file and jvm option are NOT the same");
                    logger.log(Level.INFO, "trsaFilesDirectory={0}", trsaFilesDirectory);
                    logger.log(Level.INFO, "filesRootDirectory={0}", filesRootDirectory);
                }
            }

            
            
            
        } else {
            logger.log(Level.INFO,
                    "key:TRSA_FILES_DIR={0} was not found in the trsa config file",
                    TRSA_FILES_DIR);
            // TODO: add the factory default dirctory
        }
        
        trsaConfigMap.forEach((k,v) -> settingsSvc.set(k, v));
        
        trsaFilesPath = trsaFilesDirectory
                +"/"+settingsSvc.getValueForKey(SettingsServiceBean.Key.Authority)
                +"/"+ settingsSvc.getValueForKey(SettingsServiceBean.Key.Shoulder);
        
        logger.log(Level.INFO, "trsaFilesPath={0}", trsaFilesPath);
        logger.log(Level.INFO, "============ TRSA-WEB: AppConfig#init() ends here \"============");
    }
    
    

    @PreDestroy
    public void destroy() {

    }
    private String getTrsaConfigFileDir() {
        Properties property = System.getProperties();
        String appConfigDir = property.getProperty(TRSA_CONFIG_FILE_DIR);
        logger.log(Level.INFO, "appConfigDir={0}", appConfigDir);

        return appConfigDir;
    }
    
//    private Map<String, String> readConfigFile() {
//        Properties property = System.getProperties();
//        String appConfigDir = property.getProperty(TRSA_CONFIG_FILE_DIR);
//        
//        Map<String, String> stringStringMap = new ConcurrentHashMap<>();
//        
//        try (Stream<String> lines = Files.lines(Paths.get(appConfigDir+"/"+TRSA_CONFIG_FILE_NAME))) {
//            
//            lines
//                    //.filter(s -> s.length() > 20)
//                    //.sorted(Comparator.comparingInt(String::length).reversed())
//                    
//                    //.limit(10)
//                    //.forEach(w -> System.out.printf("%s (%d)%n", w, w.length()));
//                    
//          .map(s -> {
//            String[] splitStrings = s.split("\t", -1);
//
//            stringStringMap.put(splitStrings[0], splitStrings[1]);
//            return stringStringMap;
//          }).forEach(System.out::println);
//
//        } catch (IOException e) {
//            
//        }
//        return stringStringMap;
//    }
    
    
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
    
    
    
}
