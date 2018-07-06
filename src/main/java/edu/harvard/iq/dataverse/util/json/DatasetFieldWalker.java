/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.util.json;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Akio Sone, Univ, of North Carolina at Chapel Hill, H.W. Odum Inst.
 */
public class DatasetFieldWalker {
    private static final Logger logger = Logger.getLogger(DatasetFieldWalker.class.getName());
    public interface Listener {
        void startField( DatasetField f );
        void endField( DatasetField f );
        void primitiveValue( DatasetField dsfv );
        void controledVocabularyValue( DatasetField cvv );
        void startCompoundValue( DatasetField dsfcv );
        void endCompoundValue( DatasetField dsfcv );
    }
    
    
    private Listener l;
    
    
    
    
    
    public DatasetFieldWalker(Listener l) {
        this.l = l;
    }
    
    public DatasetFieldWalker(){
        this( null );
    }
    
    
    
    
    public static void walk(List<DatasetField> fields, Listener l) {
        DatasetFieldWalker joe = new DatasetFieldWalker(l);
        for ( DatasetField dsf : fields ) {
            joe.walk(dsf);
        }
    }
    
    
    
    
    
    
    public void walk(DatasetField df) {
        l.startField(df);

        if (df.isControlledVocabulary()) {
            logger.log(Level.INFO, "isControlledVocabulary");
            l.controledVocabularyValue(df);
        } else if (df.isPrimitive()) {
            logger.log(Level.INFO, "isPrimitive");
            l.primitiveValue(df);
        } else if (df.isCompound()) {
            logger.log(Level.INFO, "isCompound");
//            for (DatasetFieldCompoundValue dsfcv : df.getDatasetFieldCompoundValues()) {
//                startCompoundValue(df);
//                for (DatasetField dsf : dsfcv.getChildDatasetFields()) {
//                    // here a recursive call
//                    walk(dsf);
//                }
//                endCompoundValue(df);
//            }

//            for (DatasetFieldCompound dsfcv : df.getDatasetFieldCompoundValues()) {
//                startCompoundValue(df);
//                for (DatasetField dsf : dsfcv.getValues()) {
//                    // here a recursive call
//                    walk(dsf);
//                }
//                endCompoundValue(df);
//            }
            logger.log(Level.INFO, "df.getValues()={0}", df.getValues().size());
//            for (DatasetFieldEntry dsfcv : df.getValues()) {
                l.startCompoundValue(df);
                for (DatasetField dsf : df.getValues()) {
//                for (DatasetFieldEntry dsf : dsfcv.getValues()) {
                    // here a recursive call
                    logger.log(Level.INFO, "recursive call ....");
//                    walk(df);
                    walk(dsf);
                }
                l.endCompoundValue(df);
        }
        l.endField(df);
    }
    public void setL(Listener l) {
        this.l = l;
    }
}
