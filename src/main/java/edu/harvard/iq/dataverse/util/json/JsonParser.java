/*
 * Copyright 2018 Akio Sone, Univ, of North Carolina at Chapel Hill, H.W. Odum Inst..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.harvard.iq.dataverse.util.json;


import com.google.gson.JsonParseException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import edu.harvard.iq.dataverse.api.Util;
import edu.harvard.iq.dataverse.entities.DataFile;
import edu.harvard.iq.dataverse.datavariable.DataVariable;
import edu.harvard.iq.dataverse.datavariable.SummaryStatistic;
import edu.harvard.iq.dataverse.datavariable.VariableCategory;
import edu.harvard.iq.dataverse.entities.DataTable;
import edu.harvard.iq.dataverse.entities.Dataset;
import edu.harvard.iq.dataverse.entities.DatasetVersion;
import edu.harvard.iq.dataverse.entities.FileMetadata;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Akio Sone, Univ, of North Carolina at Chapel Hill, H.W. Odum Inst.
 */
public class JsonParser {
    private static final Logger logger = Logger.getLogger(JsonParser.class.getName());
    static XStream xstream = new XStream(new JsonHierarchicalStreamDriver());    
    private static String defaultAuthority ="10.5072/FK2";
    private static String defaultProtocol ="doi";
    private static String defaultDoiSeparator="/";
    
    
    public DatasetVersion parseDatasetVersion(JsonObject obj) throws JsonParseException {
        return parseDatasetVersion(obj, new DatasetVersion());
    }

    public Dataset parseDataset(JsonObject obj) throws JsonParseException {
        Dataset dataset = new Dataset();

        dataset.setAuthority(obj.getString("authority", null) == null ? defaultAuthority : obj.getString("authority"));
        dataset.setProtocol(obj.getString("protocol", null) == null ? defaultProtocol : obj.getString("protocol"));
        dataset.setDoiSeparator(obj.getString("doiSeparator", null) == null ? defaultDoiSeparator : obj.getString("doiSeparator"));
        dataset.setIdentifier(obj.getString("identifier",null));

        DatasetVersion dsv = new DatasetVersion(); 
        dsv.setDataset(dataset);
        dsv = parseDatasetVersion(obj.getJsonObject("datasetVersion"), dsv);
        LinkedList<DatasetVersion> versions = new LinkedList<>();
        versions.add(dsv);

        dataset.setVersions(versions);
        return dataset;
    }

    public DatasetVersion parseDatasetVersion(JsonObject obj, DatasetVersion dsv) throws JsonParseException {
        try {

//            String archiveNote = obj.getString("archiveNote", null);
//            if (archiveNote != null) {
//                dsv.setArchiveNote(archiveNote);
//            }
//
//            dsv.setDeaccessionLink(obj.getString("deaccessionLink", null));
//            
            
            int versionNumberInt = obj.getInt("versionNumber", -1);
            Long versionNumber = null;
            if (versionNumberInt !=-1) {
                versionNumber = new Long(versionNumberInt);
            }
            dsv.setVersionNumber(versionNumber);
            dsv.setMinorVersionNumber(parseLong(obj.getString("minorVersionNumber", null)));
            // if the existing datasetversion doesn not have an id
            // use the id from the json object.
            if (dsv.getId()==null) {
                 dsv.setId(parseLong(obj.getString("id", null)));
            }
           
            String versionStateStr = obj.getString("versionState", null);
            if (versionStateStr != null) {
                dsv.setVersionState(DatasetVersion.VersionState.valueOf(versionStateStr));
            }
            dsv.setReleaseTime(parseDate(obj.getString("releaseDate", null)));
            dsv.setLastUpdateTime(parseTime(obj.getString("lastUpdateTime", null)));
            dsv.setCreateTime(parseTime(obj.getString("createTime", null)));
//            dsv.setArchiveTime(parseTime(obj.getString("archiveTime", null)));
            dsv.setUNF(obj.getString("UNF", null));
            // Terms of Use related fields
//            TermsOfUseAndAccess terms = new TermsOfUseAndAccess();
//            terms.setTermsOfUse(obj.getString("termsOfUse", null));           
//            terms.setTermsOfAccess(obj.getString("termsOfAccess", null));
//            terms.setConfidentialityDeclaration(obj.getString("confidentialityDeclaration", null));
//            terms.setSpecialPermissions(obj.getString("specialPermissions", null));
//            terms.setRestrictions(obj.getString("restrictions", null));
//            terms.setCitationRequirements(obj.getString("citationRequirements", null));
//            terms.setDepositorRequirements(obj.getString("depositorRequirements", null));
//            terms.setConditions(obj.getString("conditions", null));
//            terms.setDisclaimer(obj.getString("disclaimer", null));
//            terms.setDataAccessPlace(obj.getString("dataAccessPlace", null));
//            terms.setOriginalArchive(obj.getString("originalArchive", null));
//            terms.setAvailabilityStatus(obj.getString("availabilityStatus", null));
//            terms.setContactForAccess(obj.getString("contactForAccess", null));
//            terms.setSizeOfCollection(obj.getString("sizeOfCollection", null));
//            terms.setStudyCompletion(obj.getString("studyCompletion", null));
//            terms.setLicense(parseLicense(obj.getString("license", null)));
//            dsv.setTermsOfUseAndAccess(terms);
            
//            dsv.setDatasetFields(parseMetadataBlocks(obj.getJsonObject("metadataBlocks")));

            JsonArray filesJson = obj.getJsonArray("files");
            if (filesJson == null) {
                filesJson = obj.getJsonArray("fileMetadatas");
            }
            if (filesJson != null) {
                dsv.setFileMetadatas(parseFiles(filesJson, dsv));
            }
            return dsv;

        } catch (ParseException ex) {
            throw new JsonParseException("Error parsing date:" + ex.getMessage(), ex);
        } catch (NumberFormatException ex) {
            throw new JsonParseException("Error parsing number:" + ex.getMessage(), ex);
        }
    }
    

    public List<FileMetadata> parseFiles(JsonArray metadatasJson, DatasetVersion dsv) throws JsonParseException {
        List<FileMetadata> fileMetadatas = new LinkedList<>();

        if (metadatasJson != null) {
            for (JsonObject filemetadataJson : metadatasJson.getValuesAs(JsonObject.class)) {
                String label = filemetadataJson.getString("label");
                boolean restricted = filemetadataJson.getBoolean("restricted", false);
                String directoryLabel = filemetadataJson.getString("directoryLabel", null);
                String description = filemetadataJson.getString("description", null);

                FileMetadata fileMetadata = new FileMetadata();
                fileMetadata.setLabel(label);
                fileMetadata.setRestricted(restricted);
                fileMetadata.setDirectoryLabel(directoryLabel);
                fileMetadata.setDescription(description);
                fileMetadata.setDatasetVersion(dsv);

                DataFile dataFile = parseDataFile(filemetadataJson.getJsonObject("dataFile"));

                fileMetadata.setDataFile(dataFile);
                dataFile.getFileMetadatas().add(fileMetadata);
                dataFile.setOwner(dsv.getDataset());
                dataFile.setRestricted(restricted);
                
                if (dsv.getDataset().getFiles() == null) {
                    dsv.getDataset().setFiles(new ArrayList<>());
                }
                dsv.getDataset().getFiles().add(dataFile);

                fileMetadatas.add(fileMetadata);
//                fileMetadata.setCategories(getCategories(filemetadataJson, dsv.getDataset()));
            }
        }

        return fileMetadatas;
    }
        
    
    public DataFile parseDataFile(JsonObject datafileJson) {
        DataFile dataFile = new DataFile();
        
        Timestamp timestamp = new Timestamp(new Date().getTime());
        dataFile.setCreateDate(timestamp);
        dataFile.setModificationTime(timestamp);
        
        
        
        //dataFile.setPermissionModificationTime(timestamp);
        
        
        
        String contentType = datafileJson.getString("contentType", null);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        String storageIdentifier = datafileJson.getString("storageIdentifier", " ");
        boolean isNotaryServiceBound= datafileJson.getBoolean("notaryServiceBound", false );

        // available items
        // filename
        String filename = datafileJson.getString("filename", null);
        // filesize
        long filesize = datafileJson.getJsonNumber("filesize").longValue();
        // originalFileFormat
        String originalFileFormat = datafileJson.getString("originalFileFormat", null);
        // originalFormatLabel
        String originalFormatLabel = datafileJson.getString("originalFormatLabel", null);
        // UNF
        String UNF = datafileJson.getString("UNF", null);
        // md5
        String MD5 = datafileJson.getString("md5", null);
        
        
        JsonObject checksum = datafileJson.getJsonObject("checksum");
        if (checksum != null) {
            // newer style that allows for SHA-1 rather than MD5
            /**
             * @todo Add more error checking. Do we really expect people to set
             * file metadata without uploading files? Some day we'd like to work
             * on a "native" API that allows for multipart upload of the JSON
             * describing the files (this "parseDataFile" method) and the bits
             * of the files themselves. See
             * https://github.com/IQSS/dataverse/issues/1612
             */
            String type = checksum.getString("type");
            if (type != null) {
                String value = checksum.getString("value");
                if (value != null) {
                    try {
                        dataFile.setChecksumType(DataFile.ChecksumType.fromString(type));
                        dataFile.setChecksumValue(value);
                    } catch (IllegalArgumentException ex) {
                        logger.info("Invalid");
                    }
                }
            }
        } else {
            // older, MD5 logic, still her for backward compatibility
            String md5 = datafileJson.getString("md5", null);
            if (md5 == null) {
                md5 = "unknown";
            }
            dataFile.setChecksumType(DataFile.ChecksumType.MD5);
            dataFile.setChecksumValue(md5);
        }

        // TODO: 
        // the UNF of DataFile is called via DataTable and therefor no set method
        // unf (if available)... etc.?
        
        dataFile.setContentType(contentType);
        dataFile.setStorageIdentifier(storageIdentifier);
        dataFile.setFilesize(filesize);
        dataFile.setNotaryServiceBound(isNotaryServiceBound);
        // parse DataTable
        JsonArray dataTablesJson = datafileJson.getJsonArray("dataTables");
        if ((dataTablesJson != null ) && (!dataTablesJson.isEmpty())){
            // get parsing results of a DataTable
            List<DataTable> dataTables = parseDataTables(dataTablesJson);
            dataFile.setDataTables(dataTables);
            dataFile.setDataTable(dataTables.get(0));
            dataTables.get(0).setDataFile(dataFile);
            dataTables.get(0).setOriginalFileFormat(originalFileFormat);
        }
        
        return dataFile;
    }
    
    
    
    public List<DataTable> parseDataTables(JsonArray dataTablesJson){
        logger.log(Level.INFO, "dataTablesJson={0}", dataTablesJson);
        List<DataTable> dataTables = new LinkedList<>();
        if ((dataTablesJson !=null) && (!dataTablesJson.isEmpty())){
            for (JsonObject dataTableJsonL : dataTablesJson.getValuesAs(JsonObject.class)){
                logger.log(Level.INFO, "\ndataTableJsonL={0}", dataTableJsonL);
                JsonObject dataTableJson = dataTableJsonL.getJsonObject("dataTable");
                
                //logger.log(Level.INFO, "\ndataTableL: keyset={0}", dataTableJsonL.get("dataTable"));
                logger.log(Level.INFO, "\ndataTable={0}", dataTableJson);
                DataTable dataTable = new DataTable();
                
                // capture scalar items
                // varQuantity
                long varQuantity =  dataTableJson.getJsonNumber("varQuantity").longValue();
                dataTable.setVarQuantity(varQuantity);
                logger.log(Level.INFO, "varQuantity={0}", varQuantity);
                // caseQuantity
                long caseQuantity = dataTableJson.getJsonNumber("caseQuantity").longValue();
                dataTable.setCaseQuantity(caseQuantity);
                logger.log(Level.INFO, "caseQuantity={0}", caseQuantity);
                // UNF
                String UNF = dataTableJson.getString("UNF", null);
                dataTable.setUnf(UNF);
                logger.log(Level.INFO, "unf={0}", UNF);
                // call the method for pasring dataVariables array
                List<DataVariable> dataVariables = parseDataVariables(dataTableJson.getJsonArray("dataVariables"));
                dataTable.setDataVariables(dataVariables);
                dataTables.add(dataTable);
            }
        }
        return dataTables;
    }
    
    
    public List<DataVariable> parseDataVariables(JsonArray dataVariablesJson){
        List<DataVariable> dataVariables = new LinkedList<>();
        if ((dataVariablesJson != null) && (!dataVariablesJson.isEmpty())) {
            for (JsonObject dataVariableJson: dataVariablesJson.getValuesAs(JsonObject.class)){
                DataVariable dataVariable = new DataVariable();
                // capture scalar itemse.
                // name
                dataVariable.setName(dataVariableJson.getString("name", null));
                // label
                dataVariable.setLabel(dataVariableJson.getString("label", null));
                // weighted
                dataVariable.setWeighted(dataVariableJson.getBoolean("weighted", false));
                // variableIntervalType
                String variableIntervalType= dataVariableJson.getString("variableIntervalType", null);
                logger.log(Level.INFO, "variableIntervalType={0}", variableIntervalType);
                if (variableIntervalType!=null){
                    String variableIntervalTypeFinal = variableIntervalType.toUpperCase();
                    if (variableIntervalType.equals("contin")){
                        variableIntervalTypeFinal="CONTINUOUS";
                    }
                    dataVariable.setInterval(DataVariable.VariableInterval.valueOf(variableIntervalTypeFinal));
                }
                // variableFormatType
                String variableFormatType = dataVariableJson.getString("variableFormatType", null);
                if (variableFormatType!=null){
                    dataVariable.setType(DataVariable.VariableType.valueOf(variableFormatType));
                }
                // orderedFactor
                dataVariable.setOrderedCategorical(dataVariableJson.getBoolean("orderedFactor", false));
                // fileOrder
                dataVariable.setFileOrder(dataVariableJson.getInt("fileOrder"));
                
                // summaryStatistics
                dataVariable.setSummaryStatistics(parseSummaryStatistics(dataVariableJson.getJsonObject("summaryStatistics")));
                // variableCategories
                dataVariable.setCategories(parseVariableCategories(dataVariableJson.getJsonArray("variableCategories")));
                
                // UNF
                dataVariable.setUnf(dataVariableJson.getString("UNF", null));
            }
        }
        return dataVariables;
    }
    
    
    public List<SummaryStatistic> parseSummaryStatistics(JsonObject summaryStatisticsJson){
        List<SummaryStatistic> summaryStatistics = new LinkedList<>();
        if (summaryStatisticsJson !=null){
            // mean
            String meanjsn = summaryStatisticsJson.getString("mean", null);
            if (StringUtils.isNotBlank(meanjsn)){
                SummaryStatistic mean = new SummaryStatistic();
                mean.setType(SummaryStatistic.SummaryStatisticType.MEAN);
                mean.setValue(meanjsn);
                summaryStatistics.add(mean);
            }
            // medn
            String mednjsn = summaryStatisticsJson.getString("medn", null);
            if (StringUtils.isNotBlank(mednjsn)){
                SummaryStatistic medn = new SummaryStatistic();
                medn.setType(SummaryStatistic.SummaryStatisticType.MEDN);
                medn.setValue(mednjsn);
                summaryStatistics.add(medn);
            }
            // mode
            String modejsn = summaryStatisticsJson.getString("mode", null);
            if (StringUtils.isNotBlank(modejsn)){
                SummaryStatistic mode = new SummaryStatistic();
                mode.setType(SummaryStatistic.SummaryStatisticType.MODE);
                mode.setValue(modejsn);
                summaryStatistics.add(mode);
            }
            // vald
            String valdjsn = summaryStatisticsJson.getString("vald", null);
            if (StringUtils.isNotBlank(valdjsn)){
                SummaryStatistic vald = new SummaryStatistic();
                vald.setType(SummaryStatistic.SummaryStatisticType.VALD);
                vald.setValue(valdjsn);
                summaryStatistics.add(vald);
            }
            // invd
            String invdjsn = summaryStatisticsJson.getString("invd", null);
            if (StringUtils.isNotBlank(invdjsn)){
                SummaryStatistic invd = new SummaryStatistic();
                invd.setType(SummaryStatistic.SummaryStatisticType.INVD);
                invd.setValue(invdjsn);
                summaryStatistics.add(invd);
            }
            // min
            String minjsn = summaryStatisticsJson.getString("min", null);
            if (StringUtils.isNotBlank(minjsn)){
                SummaryStatistic min = new SummaryStatistic();
                min.setType(SummaryStatistic.SummaryStatisticType.MIN);
                min.setValue(minjsn);
                summaryStatistics.add(min);
            }
            // max
            String maxjsn = summaryStatisticsJson.getString("max", null);
            if (StringUtils.isNotBlank(maxjsn)){
                SummaryStatistic max = new SummaryStatistic();
                max.setType(SummaryStatistic.SummaryStatisticType.MAX);
                max.setValue(maxjsn);
                summaryStatistics.add(max);
            }
            // stdev
            String stdevjsn = summaryStatisticsJson.getString("stdev", null);
            if (StringUtils.isNotBlank(stdevjsn)){
                SummaryStatistic stdev = new SummaryStatistic();
                stdev.setType(SummaryStatistic.SummaryStatisticType.STDEV);
                stdev.setValue(stdevjsn);
                summaryStatistics.add(stdev);
            }
        }
        return summaryStatistics;
    }
    
    
    public List<VariableCategory> parseVariableCategories(JsonArray variableCategoriesJson){
         List<VariableCategory> variableCategories = new LinkedList<>();
         if ((variableCategoriesJson != null) && (!variableCategoriesJson.isEmpty())){
             for (JsonObject variableCategoryJson : variableCategoriesJson.getValuesAs(JsonObject.class)){
                 VariableCategory vc = new VariableCategory();
                // label
                String label = variableCategoryJson.getString("label", "");
                vc.setLabel(label);
                // value
                String value = variableCategoryJson.getString("value", "");
                vc.setValue(value);
                variableCategories.add(vc);
             }
         }
        return variableCategories;
    }
    
    
    private String jsonValueToString(JsonValue jv) {
        switch ( jv.getValueType() ) {
            case STRING: return ((JsonString)jv).getString();
            default: return jv.toString();
        }
    }
    
    Date parseDate(String str) throws ParseException {
        return str == null ? null : Util.getDateFormat().parse(str);
    }

    Date parseTime(String str) throws ParseException {
        return str == null ? null : Util.getDateTimeFormat().parse(str);
    }

    Long parseLong(String str) throws NumberFormatException {
        return (str == null) ? null : Long.valueOf(str);
    }

    int parsePrimitiveInt(String str, int defaultValue) {
        return str == null ? defaultValue : Integer.parseInt(str);
    }
    
}
