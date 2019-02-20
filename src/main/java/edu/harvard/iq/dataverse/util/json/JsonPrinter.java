package edu.harvard.iq.dataverse.util.json;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import edu.harvard.iq.dataverse.api.Util;
import edu.harvard.iq.dataverse.datavariable.DataVariable;
import edu.harvard.iq.dataverse.datavariable.SummaryStatistic;
import edu.harvard.iq.dataverse.datavariable.VariableCategory;
import edu.harvard.iq.dataverse.entities.DataFile;
import edu.harvard.iq.dataverse.entities.DataTable;
import edu.harvard.iq.dataverse.entities.Dataset;
import edu.harvard.iq.dataverse.entities.DatasetVersion;
import edu.harvard.iq.dataverse.entities.FileMetadata;
import static edu.harvard.iq.dataverse.util.json.NullSafeJsonBuilder.jsonObjectBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Convert objects to Json.
 *
 * @author michael
 */
public class JsonPrinter {

    private static final Logger logger = Logger.getLogger(JsonPrinter.class.getCanonicalName());
    static XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
//    static SettingsServiceBean settingsService;
//
//    public JsonPrinter(SettingsServiceBean settingsService) {
//        this.settingsService = settingsService;
//    }
    static Map<String, String> requiredDSF= new LinkedHashMap<String, String>();
    static {
        requiredDSF.put("title", "This is the tiltle of this dataset");
        requiredDSF.put("authorName", "inverted dataset atuher-name, here");
        requiredDSF.put("datasetContactEmail", "your-address@yourdomain");
        requiredDSF.put("dsDescriptionValue", "This is a required field about dataset-description");
        requiredDSF.put("subject", "Other");

    }
    public JsonPrinter() {
//        this(null);
    }

    public static final BriefJsonPrinter brief = new BriefJsonPrinter();

    public static JsonArrayBuilder asJsonArray(Collection<String> strings) {
        JsonArrayBuilder arr = Json.createArrayBuilder();
        for (String s : strings) {
            arr.add(s);
        }
        return arr;
    }




    
    /**
     * Export formats such as DDI require the persistent identifier components
     * such as "protocol", "authority" and "identifier" to be included so we
     * create a JSON object we can convert to a DatasetDTO which can include a
     * DatasetVersionDTO, which has all the metadata fields we need to export.
     * See https://github.com/IQSS/dataverse/issues/2579 for more on DDI export.
     *
     * @todo Instead of having this separate method, should "datasetVersion" be
     * added to the regular `json` method for Dataset? Will anything break? Unit
     * tests for that method could not be found. If we keep this method as-is
     * should the method be renamed?
     */
    public static JsonObjectBuilder jsonAsDatasetDto(DatasetVersion dsv) {
        JsonObjectBuilder datasetDtoAsJson = JsonPrinter.json(dsv.getDataset());
        datasetDtoAsJson.add("datasetVersion", jsonWithCitation(dsv));
        return datasetDtoAsJson;
    }
    
    public static JsonObjectBuilder json(Dataset ds) {
        return jsonObjectBuilder()
                .add("id", ds.getId())
                .add("identifier", ds.getIdentifier())
                .add("persistentUrl", ds.getPersistentURL())
                .add("protocol", ds.getProtocol())
                .add("authority", ds.getAuthority())
                .add("publisher", getRootDataverseNameforCitation(ds))
                .add("publicationDate", ds.getPublicationDateFormattedYYYYMMDD());
    }
    
    
// helper methods
    
    private static String getRootDataverseNameforCitation(Dataset dataset) {
//        Dataverse root = dataset.getOwner();
//        while (root.getOwner() != null) {
//            root = root.getOwner();
//        }
//        String rootDataverseName = root.getName();
//        if (!StringUtil.isEmpty(rootDataverseName)) {
//            return rootDataverseName;
//        } else {
//            return "";
//        }
//        
        
        return "";
    }    
    
    
    
    /**
     * Export formats such as DDI require the citation to be included. See
     * https://github.com/IQSS/dataverse/issues/2579 for more on DDI export.
     *
     * @todo Instead of having this separate method, should "citation" be added
     * to the regular `json` method for DatasetVersion? Will anything break?
     * Unit tests for that method could not be found.
     */
    public static JsonObjectBuilder jsonWithCitation(DatasetVersion dsv) {
        JsonObjectBuilder dsvWithCitation = JsonPrinter.json(dsv);
        dsvWithCitation.add("citation", dsv.getCitation());
        return dsvWithCitation;
    }
    
    
    
    public static JsonObjectBuilder json(DatasetVersion dsv) {
        JsonObjectBuilder bld = jsonObjectBuilder()
            .add("id",                 dsv.getId())
            .add("versionNumber",      dsv.getVersionNumber())
            .add("versionMinorNumber", dsv.getMinorVersionNumber())  //? json only
            .add("versionState",       dsv.getVersionState().name())
            // .add("versionNote",        dsv.getVersionNote())    //not minimum
            //.add("archiveNote",        dsv.getArchiveNote())    // not minimum
            //.add("deaccessionLink",    dsv.getDeaccessionLink()) // not minimum
            //.add("distributionDate",   dsv.getDistributionDate())  // not minimum
            .add("productionDate",     dsv.getProductionDate())   // json only
            .add("UNF",                dsv.getUNF())
            //.add("archiveTime", format(dsv.getArchiveTime()))    // not minimum
            .add("lastUpdateTime", format(dsv.getLastUpdateTime())) // json only
            .add("releaseTime", format(dsv.getReleaseTime()))       // json only
            .add("createTime", format(dsv.getCreateTime()))         // json only
            .add("license", getLicense(dsv))                     // temp fix
            .add("termsOfUse", getLicenseInfo(dsv))              // json only
            //.add("confidentialityDeclaration", dsv.getTermsOfUseAndAccess().getConfidentialityDeclaration() != null ? dsv.getTermsOfUseAndAccess().getConfidentialityDeclaration() : null) // not minimum
            //.add("availabilityStatus", dsv.getTermsOfUseAndAccess().getAvailabilityStatus() != null ? dsv.getTermsOfUseAndAccess().getAvailabilityStatus() : null)         // not minimum
            //.add("specialPermissions", dsv.getTermsOfUseAndAccess().getSpecialPermissions() != null ? dsv.getTermsOfUseAndAccess().getSpecialPermissions() : null)         // not minimum
            //.add("restrictions", dsv.getTermsOfUseAndAccess().getRestrictions() != null ? dsv.getTermsOfUseAndAccess().getRestrictions() : null)              // not minimum
            //.add("citationRequirements", dsv.getTermsOfUseAndAccess().getCitationRequirements() != null ? dsv.getTermsOfUseAndAccess().getCitationRequirements() : null)      // not minimum
            //.add("depositorRequirements", dsv.getTermsOfUseAndAccess().getDepositorRequirements() != null ? dsv.getTermsOfUseAndAccess().getDepositorRequirements() : null)     // not minimum
            //.add("conditions", dsv.getTermsOfUseAndAccess().getConditions() != null ? dsv.getTermsOfUseAndAccess().getConditions() : null)                                         // not minimum
            //.add("disclaimer", dsv.getTermsOfUseAndAccess().getDisclaimer() != null ? dsv.getTermsOfUseAndAccess().getDisclaimer() : null)                                         // not minimum
            //.add("termsOfAccess", dsv.getTermsOfUseAndAccess().getTermsOfAccess() != null ? dsv.getTermsOfUseAndAccess().getTermsOfAccess() : null)                                      // not minimum
            //.add("dataAccessPlace", dsv.getTermsOfUseAndAccess().getDataAccessPlace() != null ? dsv.getTermsOfUseAndAccess().getDataAccessPlace() : null)                                  // not minimum
            //.add("originalArchive", dsv.getTermsOfUseAndAccess().getOriginalArchive() != null ? dsv.getTermsOfUseAndAccess().getOriginalArchive() : null)                                  // not minimum
            //.add("availabilityStatus", dsv.getTermsOfUseAndAccess().getAvailabilityStatus() != null ? dsv.getTermsOfUseAndAccess().getAvailabilityStatus() : null)        // not minimum
            //.add("contactForAccess", dsv.getTermsOfUseAndAccess().getContactForAccess() != null ? dsv.getTermsOfUseAndAccess().getContactForAccess() : null)          // not minimum
            //.add("sizeOfCollection", dsv.getTermsOfUseAndAccess().getSizeOfCollection() != null ? dsv.getTermsOfUseAndAccess().getSizeOfCollection() : null)          // not minimum
            //.add("studyCompletion", dsv.getTermsOfUseAndAccess().getStudyCompletion() != null ? dsv.getTermsOfUseAndAccess().getStudyCompletion() : null)                                  // not minimum
                            ;

        
        List<DatasetField> fs = getFields();
        logger.log(Level.FINE, "fs={0}", xstream.toXML(fs));
        JsonObject joFields = jsonByBlocks(fs).build();
        logger.log(Level.FINE, "joFields={0}", xstream.toXML(joFields));
        JsonObject joMetaBlcks = buildMetadataBlocks(joFields).build();
        
        
        
        
        // jsonByBlocks(requiredDSF)
        bld.add("metadataBlocks", joMetaBlcks.get("metadataBlocks")); // json only

        bld.add("files", jsonFileMetadatas(dsv.getFileMetadatas()));     // joson only

        return bld;
    }
    
    
    public static JsonArrayBuilder jsonFileMetadatas(Collection<FileMetadata> fmds) {
        JsonArrayBuilder filesArr = Json.createArrayBuilder();
        for (FileMetadata fmd : fmds) {
            filesArr.add(JsonPrinter.json(fmd));
        }

        return filesArr;
    }
    
    
    
    
    public static JsonObjectBuilder json(FileMetadata fmd) {
        return jsonObjectBuilder()
                // deprecated: .add("category", fmd.getCategory())
                // TODO: uh, figure out what to do here... it's deprecated 
                // in a sense that there's no longer the category field in the 
                // fileMetadata object; but there are now multiple, oneToMany file 
                // categories - and we probably need to export them too!) -- L.A. 4.5
                .add("description", fmd.getDescription())
                .add("label", fmd.getLabel()) // "label" is the filename
                .add("restricted", fmd.isRestricted()) 
                .add("directoryLabel", fmd.getDirectoryLabel())
                .add("version", fmd.getVersion())
                .add("datasetVersionId", fmd.getDatasetVersion().getId())
                .add("categories", getFileCategories(fmd))
                .add("dataFile", JsonPrinter.json(fmd.getDataFile(), fmd));
    }
    

    public static String format(Date d) {
        return (d == null) ? null : Util.getDateTimeFormat().format(d);
    }

    private static JsonArrayBuilder getFileCategories(FileMetadata fmd) {
        if (fmd == null) {
            return null;
        }
        List<String> categories = fmd.getCategoriesByName();
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        JsonArrayBuilder fileCategories = Json.createArrayBuilder();
        for (String category : categories) {
            fileCategories.add(category);
        }
        return fileCategories;
    }

    
    
    
    
    public static JsonObjectBuilder json(DataFile df) {
        return JsonPrinter.json(df, null);
    }
    
    public static JsonObjectBuilder json(DataFile df, FileMetadata fileMetadata) {
        // File names are no longer stored in the DataFile entity; 
        // (they are instead in the FileMetadata (as "labels") - this way 
        // the filename can change between versions... 
        // It does appear that for some historical purpose we still need the
        // filename in the file DTO (?)... We rely on it to be there for the 
        // DDI export, for example. So we need to make sure this is is the 
        // *correct* file name - i.e., that it comes from the right version. 
        // (TODO...? L.A. 4.5, Aug 7 2016)
        String fileName = null;
        
        if (fileMetadata != null) {
            fileName = fileMetadata.getLabel();
        } else if (df.getFileMetadata() != null) {
            // Note that this may not necessarily grab the file metadata from the 
            // version *you want*! (L.A.)
            fileName = df.getFileMetadata().getLabel();
        }
        
        
        return jsonObjectBuilder()
                .add("id",                  df.getId())
                .add("filename",               fileName)
                .add("contentType",         df.getContentType())            
                .add("filesize",            df.getFilesize())            
                .add("description",         df.getDescription())                // not json
                //.add("released", df.isReleased())
                .add("restricted", df.isRestricted())
                .add("storageIdentifier",   df.getStorageIdentifier())          // json only
                .add("originalFileFormat",  df.getOriginalFileFormat())         // json only
                .add("originalFormatLabel", df.getOriginalFormatLabel())        // json only
                .add("UNF",                 df.getUnf())
                //---------------------------------------------
                // For file replace: rootDataFileId, previousDataFileId
                //---------------------------------------------
                .add("rootDataFileId",      df.getRootDataFileId())             // json only
                //.add("previousDataFileId",  df.getPreviousDataFileId())         // masked not minimum
                //---------------------------------------------
                // Checksum
                // * @todo Should we deprecate "md5" now that it's under
                // * "checksum" (which may also be a SHA-1 rather than an MD5)?
                //---------------------------------------------
                .add("md5",getMd5IfItExists(               // json only
                                            df.getChecksumValue()))
//                .add("checksum", getChecksumTypeAndValue(
//                                            df.getChecksumType(), 
//                                            df.getChecksumValue()))             // json only
                .add("checksum", getChecksumTypeAndValue(
                                            
                                            df.getChecksumValue()))             // json only
                
/*  here expected results are like below:
                    "md5": "504e5c3a4431ab2518bbdd6f3993a346",
                    "checksum": {
                        "type": "MD5",
                        "value": "504e5c3a4431ab2518bbdd6f3993a346"
                    }                
*/                
                //.add("tabularTags", getTabularFileTags(df))           //masked not minimum
                .add("dataTables", !CollectionUtils.isEmpty(df.getDataTables()) ? JsonPrinter.jsonDT(df.getDataTables()) : null)
                ;
    }
    
//    public static String getMd5IfItExists(DataFile.ChecksumType checksumType, String checksumValue) {
//        if (DataFile.ChecksumType.MD5.equals(checksumType)) {
//            return checksumValue;
//        } else {
//            return null;
//        }
//    }
    
    public static JsonArrayBuilder jsonDT(List<DataTable> ldt) {
        JsonArrayBuilder ldtArr = Json.createArrayBuilder();
        for(DataTable dt: ldt){
            ldtArr.add(jsonObjectBuilder().add("dataTable", JsonPrinter.json(dt)));
        }
        return ldtArr;
    }
    
    public static JsonObjectBuilder json(DataTable dt) {
        return jsonObjectBuilder()
                .add("varQuantity", dt.getVarQuantity())
                .add("caseQuantity", dt.getCaseQuantity())
                .add("UNF", dt.getUnf())
                .add("dataVariables", JsonPrinter.json(dt.getDataVariables()))
                ;
    }
    
    public static JsonArrayBuilder json(List<DataVariable> dvl) {
        JsonArrayBuilder varArr = Json.createArrayBuilder();
        for (DataVariable dv: dvl){
            varArr.add(JsonPrinter.json(dv));
        }
        return varArr;
    }
    
    // TODO: add sumstat and variable categories, check formats
    public static JsonObjectBuilder json(DataVariable dv) {
    return jsonObjectBuilder()
            .add("name", dv.getName())
            .add("label", dv.getLabel())
            .add("weighted", dv.isWeighted())
            .add("variableIntervalType", dv.getIntervalLabel())
            .add("variableFormatType", dv.getType().name()) // varFormat
            .add("formatCategory", dv.getFormatCategory())
            .add("orderedFactor", dv.isOrderedCategorical()) 
            .add("fileOrder", dv.getFileOrder()) 
            .add("UNF",dv.getUnf())
            .add("summaryStatistics", CollectionUtils.isNotEmpty(dv.getSummaryStatistics()) ? JsonPrinter.jsonSumStat(dv.getSummaryStatistics()) : null)
            .add("variableCategories", CollectionUtils.isNotEmpty(dv.getCategories()) ? JsonPrinter.jsonCatStat(dv.getCategories()) : null) 
            ;
    }
    
    public static JsonObjectBuilder jsonSumStat(Collection<SummaryStatistic> sumStat){
        //JsonArrayBuilder sumStatArr = Json.createArrayBuilder();
        JsonObjectBuilder sumStatObj = Json.createObjectBuilder();
        for (SummaryStatistic stat: sumStat){
            sumStatObj.add(stat.getTypeLabel(), stat.getValue());
        }
        return sumStatObj;
    }
    
    
    public static JsonArrayBuilder jsonCatStat(Collection<VariableCategory> catStat){
        JsonArrayBuilder catArr = Json.createArrayBuilder();

        for (VariableCategory stat: catStat){
            JsonObjectBuilder catStatObj = Json.createObjectBuilder();
            catStatObj.add("label", stat.getLabel())
                      .add("value", stat.getValue())
                      //.add("frequency", stat.getFrequency()) // frequency is not calculated
                    ;
            catArr.add(catStatObj);
        }
        return catArr;
    }
    
    
    
    
    public static String getMd5IfItExists(String checksumValue) {
//        if (DataFile.ChecksumType.MD5.equals(checksumType)) {
            return checksumValue;
//        } else {
//            return null;
//        }
    }
//    public static JsonObjectBuilder getChecksumTypeAndValue(DataFile.ChecksumType checksumType, String checksumValue) {
//        if (checksumType != null) {
//            return Json.createObjectBuilder()
//                    .add("type", checksumType.toString())
//                    .add("value", checksumValue);
//        } else {
//            return null;
//        }
//    }
    public static JsonObjectBuilder getChecksumTypeAndValue(String checksumValue) {
//        if (checksumType != null) {
            return Json.createObjectBuilder()
                    .add("type", "MD5")
                    .add("value", checksumValue);
//        } else {
//            return null;
//        }
    }

    
    
    private static String getLicenseInfo(DatasetVersion dsv) {
//        if (dsv.getTermsOfUseAndAccess().getLicense() != null && dsv.getTermsOfUseAndAccess().getLicense().equals(TermsOfUseAndAccess.License.CC0)) {
//            return "CC0 Waiver";
//        }
//        return dsv.getTermsOfUseAndAccess().getTermsOfUse();
        
        // this is a temporal fix
        return "CC0 Waiver";
    }
    
    private static String getLicense(DatasetVersion dsv){
//        if (dsv.getTermsOfUseAndAccess().getLicense() != null){
//            return dsv.getTermsOfUseAndAccess().getLicense().toString();
//        } else {
//            return null;
//        }
        // temp fix
        return "CC0";
    }
    
//    
    
    public static JsonObjectBuilder jsonByBlocks(Map<String, String> fields) {
//    public static JsonObjectBuilder jsonByBlocks(List<DatasetField> fields) {
        JsonObjectBuilder blocksBld = jsonObjectBuilder();
//
//        for (Map.Entry<MetadataBlock, List<DatasetField>> blockAndFields : DatasetField.groupByBlock(fields).entrySet()) {
//            MetadataBlock block = blockAndFields.getKey();
//            blocksBld.add(block.getName(), JsonPrinter.json(block, blockAndFields.getValue()));
//        }
        return blocksBld;
    
    }
//    
//    
    
    public static JsonObjectBuilder jsonByBlocks(List<DatasetField> fields) {
        JsonObjectBuilder blocksBld = jsonObjectBuilder();

        MetadataBlock block = new MetadataBlock();
        blocksBld.add(block.getName(), json(block, fields));

        return blocksBld;
    }
    
    /**
     * Create a JSON object for the block and its fields. The fields are assumed
     * to belong to the block - there's no checking of that in the method.
     *
     * @param block
     * @param fields
     * @return JSON Object builder with the block and fields information.
     */
    public static JsonObjectBuilder json(MetadataBlock block, List<DatasetField> fields) {
        JsonObjectBuilder blockBld = jsonObjectBuilder();

        blockBld.add("displayName", block.getDisplayName());
        final JsonArrayBuilder fieldsArray = Json.createArrayBuilder();

        DatasetFieldWalker.walk(fields, new DatasetFieldsToJson(fieldsArray));

        blockBld.add("fields", fieldsArray);
        return blockBld;
    }

    public JsonObjectBuilder buildDisplayName() {
        JsonObjectBuilder bld = Json.createObjectBuilder();
        bld.add("displayName", "Citation Metadata");
        bld.add("fields", buildFields());
        return bld;
    }

    public JsonObjectBuilder buildCitation() {
        JsonObjectBuilder bld = Json.createObjectBuilder();
        bld.add("citation", buildDisplayName());
        return bld;
    }

    // metadataBlocks:{citation:{displayName: , fields:[{datasetfield}, {}, {}]}}
    public JsonObjectBuilder buildMetadataBlocks() {
        JsonObjectBuilder bld = Json.createObjectBuilder();
        bld.add("metadataBlocks", buildCitation());
        return bld;
    }

    public static JsonObjectBuilder buildMetadataBlocks(JsonObject metablocksValue) {
        JsonObjectBuilder bld = Json.createObjectBuilder();
        bld.add("metadataBlocks", metablocksValue);
        return bld;
    }

    public JsonArrayBuilder buildFields() {
        JsonArrayBuilder bld = Json.createArrayBuilder();

//        for (DatasetField df : datasetFields) {
//
//            JsonObjectBuilder job = Json.createObjectBuilder();
//            job.add("typeName", df.getTypeName())
//                    .add("multiple", df.isMultiple())
//                    .add("typeClass", df.getTypeClass());
//
//            //  : df.getValue()  
//            if (df.isMultiple()) {
//                job.add("value", buildValues(df));
//            } else {
//                job.add("value", df.getValue());
//            }
//
//            bld.add(job);
//        }
        return bld;
    }
    
    
    
    
    
    
    public static List<DatasetField> getSampleFieldsData() {

        System.out.println("DatasetFieldTest2: getSampleFieldsData():generate a java tree-like object");
        List<DatasetField> fields = new ArrayList<>();

        // title
        DatasetField title = new DatasetField("title", Boolean.FALSE, "primitive");
        title.setValue("minimum dataset without datafiles");
        fields.add(title);

        // author
        DatasetField author = new DatasetFieldCompound("author", Boolean.TRUE, "compound");
        DatasetField authName = new DatasetField("authorName", Boolean.FALSE, "primitive");
        authName.setValue("Sone, Akio");
        DatasetField authAff = new DatasetField("authorAffiliation", Boolean.FALSE, "primitive");
        authAff.setValue("Dataverse.org");

        author.getValues().add(authName);
        author.getValues().add(authAff);
        fields.add(author);

        // datasetContact
        DatasetFieldCompound datasetContact = new DatasetFieldCompound("datasetContact", Boolean.TRUE, "compound");
        // datasetContactEmail
        DatasetField datasetContactEmail = new DatasetField("datasetContactEmail", Boolean.FALSE, "primitive");
        datasetContactEmail.setValue("asone@unc.edu");
        datasetContact.values.add(datasetContactEmail);
        fields.add(datasetContact);

        // dsDescription
        DatasetField dsDescription = new DatasetFieldCompound("dsDescription", Boolean.TRUE, "compound");
        // dsDescriptionValue
        DatasetField dsDescriptionValue = new DatasetField("dsDescriptionValue", Boolean.FALSE, "primitive");
        dsDescriptionValue.setValue("this dataset does not have a datafile");

        dsDescription.getValues().add(dsDescriptionValue);
        fields.add(dsDescription);

        // subject
        //List<String> subjectList = Arrays.asList("Social Sciences", "Other");
        String singleSubject = "Other";
        DatasetField subject = new DatasetField("subject", Boolean.FALSE, "controlledVocabulary");
        subject.setValue(singleSubject);
//        DatasetFieldCV subject = new DatasetFieldCV("subject", Boolean.TRUE, "controlledVocabulary", subjectList);
        fields.add(subject);

        return fields;
    }
    
////////////////////////////////////////////////////////////////////////////////
    
    
    
    private static class DatasetFieldsToJson implements DatasetFieldWalker.Listener {

        static XStream xstream = new XStream(new JsonHierarchicalStreamDriver());

        Deque<JsonObjectBuilder> objectStack = new LinkedList<>();
        Deque<JsonArrayBuilder> valueArrStack = new LinkedList<>();
        JsonObjectBuilder result = null;

        DatasetFieldsToJson(JsonArrayBuilder result) {
            valueArrStack.push(result);
        }

        public void startField(DatasetField f) {
            objectStack.push(jsonObjectBuilder());
            // Invariant: all values are multiple. Diffrentiation between multiple and single is done at endField.
            valueArrStack.push(Json.createArrayBuilder());

            //DatasetFieldType typ = f.getDatasetFieldType();
            objectStack.peek().add("typeName", f.getTypeName());
            objectStack.peek().add("multiple", f.isMultiple());
            objectStack.peek().add("typeClass", f.getTypeClass());
        }

        public void endField(DatasetField f) {
            JsonObjectBuilder jsonField = objectStack.pop();
            JsonArray jsonValues = valueArrStack.pop().build();
            if (!jsonValues.isEmpty()) {
                jsonField.add("value",
                        f.isMultiple() ? jsonValues
                        : jsonValues.get(0));
                valueArrStack.peek().add(jsonField);
            }
        }

        public void primitiveValue(DatasetField dsfv) {
            if (dsfv.getValue() != null) {
                valueArrStack.peek().add(dsfv.getValue());
            }
        }

        public void controledVocabularyValue(DatasetField cvv) {
            valueArrStack.peek().add(cvv.getValue());
        }

        public void startCompoundValue(DatasetField dsfcv) {
            valueArrStack.push(Json.createArrayBuilder());
        }

        public void endCompoundValue(DatasetField dsfcv) {
            JsonArray jsonValues = valueArrStack.pop().build();
            if (!jsonValues.isEmpty()) {
                JsonObjectBuilder jsonField = jsonObjectBuilder();
                for (JsonObject jobj : jsonValues.getValuesAs(JsonObject.class)) {
                    jsonField.add(jobj.getString("typeName"), jobj);
                }
                valueArrStack.peek().add(jsonField);
            }
        }

    }
    
    
    
    
    
    
////////////////////////////////////////////////////////////////////////////////
    
    
    public static List<DatasetField> getFields() {

        System.out.println("DatasetFieldTest2: getFields(): generate a java tree-like object");
        List<DatasetField> fields = new ArrayList<>();

        // title
        DatasetField title = new DatasetField("title", Boolean.FALSE, "primitive");
        title.setValue("minimum dataset without datafiles");
        fields.add(title);

        // author
        DatasetField author = new DatasetFieldCompound("author", Boolean.TRUE, "compound");
        DatasetField authName = new DatasetField("authorName", Boolean.FALSE, "primitive");
        authName.setValue("TRSA, curator");
        DatasetField authAff = new DatasetField("authorAffiliation", Boolean.FALSE, "primitive");
        authAff.setValue("Dataverse.org");

        author.getValues().add(authName);
        author.getValues().add(authAff);
        fields.add(author);

        // datasetContact
        DatasetFieldCompound datasetContact = new DatasetFieldCompound("datasetContact", Boolean.TRUE, "compound");
        // datasetContactEmail
        DatasetField datasetContactEmail = new DatasetField("datasetContactEmail", Boolean.FALSE, "primitive");
        datasetContactEmail.setValue("trsa_curator@mailinator.com");
        datasetContact.getValues().add(datasetContactEmail);
        fields.add(datasetContact);

        // dsDescription
        DatasetField dsDescription = new DatasetFieldCompound("dsDescription", Boolean.TRUE, "compound");
        // dsDescriptionValue
        DatasetField dsDescriptionValue = new DatasetField("dsDescriptionValue", Boolean.FALSE, "primitive");
        dsDescriptionValue.setValue("this dataset does not have a datafile");

        dsDescription.getValues().add(dsDescriptionValue);
        fields.add(dsDescription);

        // subject
        //List<String> subjectList = Arrays.asList("Social Sciences", "Other");
        String singleSubject = "Other";
        DatasetField subject = new DatasetField("subject", Boolean.TRUE, "controlledVocabulary");
        subject.setValue(singleSubject);
//        DatasetFieldCV subject = new DatasetFieldCV("subject", Boolean.TRUE, "controlledVocabulary", subjectList);
        fields.add(subject);

        return fields;
    }
    
    
}
