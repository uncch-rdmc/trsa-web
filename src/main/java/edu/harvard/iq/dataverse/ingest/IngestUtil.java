/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.ingest;

import edu.harvard.iq.dataverse.entities.DatasetVersion;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Akio Sone, Univ, of North Carolina at Chapel Hill, H.W. Odum Inst.
 */
public class IngestUtil {
    private static final Logger logger = Logger.getLogger(IngestUtil.class.getName());
    
    
    /**
     * @param version The DatasetVersion to mutate, setting or unsetting the
     * UNF.
     */
    public static void recalculateDatasetVersionUNF(DatasetVersion version) {
        logger.fine("recalculating UNF for dataset version.");
//        if (version == null) {
            return;
//        }
//        List<String> unfValueList = getUnfValuesOfFiles(version);
//        if (unfValueList.size() > 0) {
//            String[] unfValues = new String[0];
//            unfValues = unfValueList.toArray(unfValues);
//
//            logger.fine("Attempting to calculate new UNF from total of " + unfValueList.size() + " file-level signatures.");
//            String datasetUnfValue = null;
//            try {
//                datasetUnfValue = UNFUtil.calculateUNF(unfValues);
//            } catch (IOException ex) {
//                // It's unclear how to exercise this IOException.
//                logger.warning("IO Exception: Failed to recalculate the UNF for the dataset version id=" + version.getId());
//            } catch (UnfException uex) {
//                // It's unclear how to exercise this UnfException.
//                logger.warning("UNF Exception: Failed to recalculate the UNF for the dataset version id=" + version.getId());
//            }
//
//            if (datasetUnfValue != null) {
//                version.setUNF(datasetUnfValue);
//                logger.fine("Recalculated the UNF for the dataset version id=" + version.getId() + ", new signature: " + datasetUnfValue);
//            }
//        } else {
//            // Of course if no files in the version have UNFs, we need to make sure
//            // that the version has the NULL UNF too.
//            // Otherwise, the version will still have a UNF if the user deletes
//            // all the tabular files from the version!
//            version.setUNF(null);
//        }
    }
    
    public static List<String> getUnfValuesOfFiles(DatasetVersion version) {
        List<String> unfValueList = new ArrayList<>();
//        if (version == null) {
            return unfValueList;
//        }
//        Iterator<FileMetadata> itfm = version.getFileMetadatas().iterator();
//        while (itfm.hasNext()) {
//            FileMetadata fileMetadata = itfm.next();
//            if (fileMetadata != null
//                    && fileMetadata.getDataFile() != null
//                    && fileMetadata.getDataFile().isTabularData()
//                    && fileMetadata.getDataFile().getUnf() != null) {
//                String varunf = fileMetadata.getDataFile().getUnf();
//                unfValueList.add(varunf);
//            }
//        }
//        return unfValueList;
    }
}
