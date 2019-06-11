/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.settings;


import edu.harvard.iq.dataverse.util.StringUtil;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author asone (This class was adapted from Dataverse's original)
 */
@Named
@Stateless
public class SettingsServiceBean {
    private static final Logger logger = Logger.getLogger(SettingsServiceBean.class.getName());
    
    
    @PersistenceContext
    EntityManager em;
    
    
    public enum Key{
        // for settings of TRSA, to avoid mixing Dataverse's counterparts, 
        // TRSA-specific ones are prefixed with "trsa"
        
        /**
         * If the data replicated around the world using RSAL (Repository
         * Storage Abstraction Layer) is locally available, this is its file
         * path, such as "/programs/datagrid".
         *
         * TODO: Think about if it makes sense to make this a column in the
         * StorageSite database table.
         */
        LocalDataAccessPath,
        
        /** protocol for global id */
        Protocol,
        /** authority for global id */
        Authority,
        /** DoiProvider for global id */
        DoiProvider,
        /** Shoulder for global id - used to create a common prefix on identifiers */
        Shoulder,
        
        ZipDownloadLimit,
        /* zip upload number of files limit */
        ZipUploadFilesLimit,
        /* the number of files the GUI user is allowed to upload in one batch, 
            via drag-and-drop, or through the file select dialog */
        MultipleUploadFilesLimit,
        
        /* size limit for Tabular data file ingests */
        /* (can be set separately for specific ingestable formats; in which 
        case the actual stored option will be TabularIngestSizeLimit:{FORMAT_NAME}
        where {FORMAT_NAME} is the format identification tag returned by the 
        getFormatName() method in the format-specific plugin; "sav" for the 
        SPSS/sav format, "RData" for R, etc.
        for example: :TabularIngestSizeLimit:RData */
        TabularIngestSizeLimit,
        /* This corresponds to LOCAL_FILES of Dataverse        */
        TrsaLocalFiles
        
        ;

        @Override
        public String toString() {
            return ":" + name();
        }
        
    }
    /**
     * Basic functionality - get the name, return the setting, or {@code null}.
     * @param name of the setting
     * @return the actual setting, or {@code null}.
     */
    public String get( String name ) {
        Settings s = em.find( Settings.class, name );
        return (s!=null) ? s.getContent() : null;
    }
    
    /**
     * Same as {@link #get(java.lang.String)}, but with static checking.
     * @param key Enum value of the name.
     * @return The setting, or {@code null}.
     */
    public String getValueForKey( Key key ) {
        return get(key.toString());
    }
    
    
    /**
     * Attempt to convert the value to an integer
     *  - Applicable for keys such as MaxFileUploadSizeInBytes
     * 
     * On failure (key not found or string not convertible to a long), returns null
     * @param key
     * @return 
     */
       public Long getValueForKeyAsLong(Key key){
        
        String val = this.getValueForKey(key);

        if (val == null){
            return null;
        }

        try {
            long valAsInt = Long.parseLong(val);
            return valAsInt;
        } catch (NumberFormatException ex) {
            logger.log(Level.WARNING, "Incorrect setting.  Could not convert \"{0}\" from setting {1} to long.", new Object[]{val, key.toString()});
            return null;
        }
        
    }
    
    
    /**
     * Return the value stored, or the default value, in case no setting by that
     * name exists. The main difference between this method and the other {@code get()}s
     * is that is never returns null (unless {@code defaultValue} is {@code null}.
     * 
     * @param name Name of the setting.
     * @param defaultValue The value to return if no setting is found in the DB.
     * @return Either the stored value, or the default value.
     */
    public String get( String name, String defaultValue ) {
        String val = get(name);
        return (val!=null) ? val : defaultValue;
    }
    
    public String getValueForKey( Key key, String defaultValue ) {
        return get( key.toString(), defaultValue );
    }
     
    public Settings set( String name, String content ) {
        Settings s = new Settings( name, content );
        s = em.merge(s);
//        actionLogSvc.log( new ActionLogRecord(ActionLogRecord.ActionType.Settings, "set")
//                            .setInfo(name + ": " + content));
        return s;
    }
    
    public Settings setValueForKey( Key key, String content ) {
        return set( key.toString(), content );
    }
    
    /**
     * The correct way to decide whether a string value in the
     * settings table should be considered as {@code true}.
     * @param name name of the setting.
     * @param defaultValue logical value of {@code null}.
     * @return boolean value of the setting.
     */
    public boolean isTrue( String name, boolean defaultValue ) {
        String val = get(name);
        return ( val==null ) ? defaultValue : StringUtil.isTrue(val);
    }
    
    public boolean isTrueForKey( Key key, boolean defaultValue ) {
        return isTrue( key.toString(), defaultValue );
    }

    public boolean isFalseForKey( Key key, boolean defaultValue ) {
        return ! isTrue( key.toString(), defaultValue );
    }
            
    public void deleteValueForKey( Key name ) {
        delete( name.toString() );
    }
    
    public void delete( String name ) {
//        actionLogSvc.log( new ActionLogRecord(ActionLogRecord.ActionType.Settings, "delete")
//                            .setInfo(name));
        em.createNamedQuery("Settings.deleteByName")
                .setParameter("name", name)
                .executeUpdate();
    }
    
    public Set<Settings> listAll() {
        return new HashSet<>(em.createNamedQuery("Settings.findAll", Settings.class).getResultList());
    }
    
}
