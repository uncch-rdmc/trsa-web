package edu.harvard.iq.dataverse.ingest;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import edu.harvard.iq.dataverse.dataaccess.TabularSubsetGenerator;
import edu.harvard.iq.dataverse.datavariable.DataVariable;
import edu.harvard.iq.dataverse.datavariable.SummaryStatistic;
import edu.harvard.iq.dataverse.entities.DataFile;
import edu.harvard.iq.dataverse.entities.DataTable;
import edu.harvard.iq.dataverse.entities.Dataset;
import edu.harvard.iq.dataverse.entities.DatasetFacade;
import edu.harvard.iq.dataverse.entities.DatasetVersion;
import edu.harvard.iq.dataverse.entities.FileMetadata;
import edu.harvard.iq.dataverse.export.ExportException;
import edu.harvard.iq.dataverse.export.JSONExporter;
import edu.harvard.iq.dataverse.export.ddi.DdiExportUtil;
import edu.harvard.iq.dataverse.export.dublincore.DublinCoreExportUtil;
import edu.harvard.iq.dataverse.ingest.tabulardata.TabularDataFileReader;
import edu.harvard.iq.dataverse.ingest.tabulardata.TabularDataIngest;
import edu.harvard.iq.dataverse.ingest.tabulardata.impl.plugins.csv.CSVFileReader;
import edu.harvard.iq.dataverse.ingest.tabulardata.impl.plugins.csv.CSVFileReaderSpi;
import edu.harvard.iq.dataverse.ingest.tabulardata.impl.plugins.dta.DTA117FileReader;
import edu.harvard.iq.dataverse.ingest.tabulardata.impl.plugins.dta.DTAFileReader;
import edu.harvard.iq.dataverse.ingest.tabulardata.impl.plugins.por.PORFileReader;
import edu.harvard.iq.dataverse.ingest.tabulardata.impl.plugins.por.PORFileReaderSpi;
import edu.harvard.iq.dataverse.ingest.tabulardata.impl.plugins.rdata.RDATAFileReader;
import edu.harvard.iq.dataverse.ingest.tabulardata.impl.plugins.rdata.RDATAFileReaderSpi;
import edu.harvard.iq.dataverse.ingest.tabulardata.impl.plugins.sav.SAVFileReader;
import edu.harvard.iq.dataverse.ingest.tabulardata.impl.plugins.sav.SAVFileReaderSpi;
import edu.harvard.iq.dataverse.ingest.tabulardata.impl.plugins.xlsx.XLSXFileReader;
import edu.harvard.iq.dataverse.ingest.tabulardata.impl.plugins.xlsx.XLSXFileReaderSpi;
import edu.harvard.iq.dataverse.util.FileUtil;
import edu.harvard.iq.dataverse.util.SumStatCalculator;
import edu.harvard.iq.dataverse.util.SystemConfig;
import edu.harvard.iq.dataverse.util.json.JsonPrinter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import static org.apache.commons.text.CharacterPredicates.ARABIC_NUMERALS;
import static org.apache.commons.text.CharacterPredicates.ASCII_UPPERCASE_LETTERS;
import org.apache.commons.text.RandomStringGenerator;
import org.dataverse.unf.UNFUtil;
import org.dataverse.unf.UnfException;
import us.cyberimpact.trsa.settings.AppConfig;
import us.cyberimpact.trsa.settings.SettingsServiceBean;
import us.cyberimpact.trsa.web.jsf.util.JsfUtil;

/**
 *
 * @author Akio Sone
 */
@Stateless
@Named
public class IngestService {

    private static final Logger logger = Logger.getLogger(IngestService.class.getName());

    @PersistenceContext(unitName = "trsa-WebPU")
    private EntityManager em;
    @EJB
    protected SettingsServiceBean settingsSvc;
    
    @EJB
    DatasetFacade datasetFacade;
    
    static XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
    
    // The following contants are to be replaced with those from Settings Table
    private static String FilesRootDirectory = "/tmp/files";
    // private static String FilesRootDirectory = "/tmp/files";
    
    
    private static String FilesTempDirectory= "";//FilesRootDirectory+"/temp";
    private static String defaultAuthority ="10.5072/FK2";
    private static String defaultProtocol ="doi";
    
    //
    private static String defaultDoiSeparator="/";
    
    
    
    private static String dateTimeFormat_ymdhmsS = "yyyy-MM-dd HH:mm:ss.SSS";
    private static String dateFormat_ymd = "yyyy-MM-dd";
    
    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "========== IngestService#init() starts here ==========");
        // remove the attached forward slash 
        String shoulder = settingsSvc.getValueForKey(SettingsServiceBean.Key.Shoulder).replace("/", "");
        logger.log(Level.INFO, "shoulder={0}", shoulder);
        String authority = settingsSvc.getValueForKey(SettingsServiceBean.Key.Authority);
        logger.log(Level.INFO, "authority={0}", authority);
        String protocol = settingsSvc.getValueForKey(SettingsServiceBean.Key.Protocol);
        logger.log(Level.INFO, "protocol={0}", protocol);
        String doiBase = authority + "/"+shoulder;
        logger.log(Level.INFO, "doiBase={0}", doiBase);
        String filesRootDir = settingsSvc.getValueForKey(SettingsServiceBean.Key.TrsaLocalFiles);
        
        // update the defaults
        
        defaultProtocol = protocol;
        logger.log(Level.INFO, "defaultProtocol={0}", defaultProtocol);
        
        defaultAuthority = authority +"/"+shoulder;
        
        logger.log(Level.INFO, "defaultAuthority={0}", defaultAuthority);
        
        FilesRootDirectory=filesRootDir;
        logger.log(Level.INFO, "filesRootDir={0}", filesRootDir);
        
        FilesTempDirectory=FilesRootDirectory+"/temp";
        logger.log(Level.INFO, "FilesTempDirectory={0}", FilesTempDirectory);
        logger.log(Level.INFO, "========== IngestService#init() ends here ==========");
    }
    
    // moved from VariableServiceBean 
    public static final String[] summaryStatisticTypes = {"mean", "medn", "mode", "vald", "invd", "min", "max", "stdev"};

    List<DataFile> initialFileList;

    static SystemConfig systemConfig = new SystemConfig();
/*
    public static void main(String[] args) throws FileNotFoundException, IOException, XMLStreamException, ExportException {
        String datasetIdentifier = null;
        String filename = args[0];
        String contentType = args[1];
        if (args.length > 2){
            datasetIdentifier = args[2];
        }
        if (StringUtils.isBlank(filename) || StringUtils.isBlank(contentType)) {
            System.err.println("Usage: java edu.harvard.iq.dataverse.ingest.IngestService <file name with full path> <known MIME-type of the file> <datasetId>");
            System.exit(1);
        }

        if (StringUtils.isBlank(datasetIdentifier)) {
            datasetIdentifier = generateTempDatasetIdentifier(6);
        }

        IngestService ingestService = new IngestService();
        ingestService.run(filename, contentType, datasetIdentifier);
    }
*/
    
    
    public static String generateTempDatasetIdentifier(int digits) {
        // taken from CreateDatasetCommand#execute()
        // RandomStringUtils.randomAlphanumeric(6).toUpperCase();
        return new RandomStringGenerator.Builder()
                .withinRange('0', 'Z')
                .filteredBy(ARABIC_NUMERALS, ASCII_UPPERCASE_LETTERS)
                .build()
                .generate(digits);
    }

    public void run(String filename, String contentType, String datasetIdentifier) 
            throws FileNotFoundException, IOException, XMLStreamException, ExportException {

        // setup an InputStream instance from the 1st argument as a local file name
        BufferedInputStream fileInputStream = null;
        Path datasetDir = null;

        try {
            fileInputStream = new BufferedInputStream(new FileInputStream(new File(filename)));
        } catch (FileNotFoundException notfoundEx) {
            logger.log(Level.INFO, "FileNotFoundException={0}", notfoundEx);
            fileInputStream = null;
        }

        if (fileInputStream == null) {
            System.err.println("Could not open file " + filename + ".");
            System.exit(1);
        }

        // dataset-level operation before each data file is ingested
        // 
        // requirements:
        // 
        // 1. Id of a target dataset
        // 2. InputStream backed by a local data file to be ingested
        // 
        // 1. means that the target dataset is known, i.e., an enduser has
        // created a target dataset already
        // 
        // this version starts from the step of a creating new, local 
        // place-holding dataset
        //
// stage 1: emulate the following API method Dataverses class that creates a new dataset
        //    @POST
        //    @Path("{identifier}/datasets")
        //    public Response createDataset( String jsonBody, @PathParam("identifier") String parentIdtf  ) {}
        // the following constructor set versionNumber=1L, minorVersion=0L, VersionState=DRAFT
        Dataset dataset = new Dataset();
        dataset.setIdentifier(datasetIdentifier);

        // TODO the following two institution-dependent values must be
        // externally given such as commandline arguments, etc.
        dataset.setAuthority(defaultAuthority);
        dataset.setProtocol(defaultProtocol);
        dataset.setDoiSeparator(defaultDoiSeparator);

        // the following DatasetVersion-related block will be loaded 
        // from a json file as JsonParser#parseDataset does below
        // JsonObject jsonVersion = json.getJsonObject("datasetVersion");
        // the following block was take from the above method
        // ==================================================
        DatasetVersion version = dataset.getVersions().get(0);//new DatasetVersion();
        //version.setDataset(dataset);
        // version = jsonParser().parseDatasetVersion(jsonVersion, version);

//        int versionNumberInt = -1;// or 1 obj.getInt("versionNumber", -1);
//        Long versionNumber = null;
//        if (versionNumberInt != -1) {
//            versionNumber = new Long(versionNumberInt);
//        }
//
//        version.setVersionNumber(versionNumber);
//        version.setMinorVersionNumber(null);
        version.setId(null);
        version.setReleaseTime(null);
        version.setLastUpdateTime(null);
        version.setCreateTime(null);
        version.setUNF(null);

        // version.setVersionNumber(null);
//        version.setVersionState(DatasetVersion.VersionState.DRAFT);
//        LinkedList<DatasetVersion> versions = new LinkedList<>();
//        versions.add(version);
//        version.setDataset(dataset);   // DatasetVersion <=Dataset
//        dataset.setVersions(versions); // Dataset        <= DatasetVersion
        // ======================================================
        logger.log(Level.FINE, "version:contents={0}", xstream.toXML(version));

        // the following block was taken from the following line:
        // Dataset managedDs = execCommand(new CreateDatasetCommand(ds, createDataverseRequest(u)));
        Timestamp createDate = new Timestamp(new Date().getTime());

        dataset.setCreateDate(createDate);
        version.setCreateTime(createDate);
        version.setLastUpdateTime(createDate);
        dataset.setModificationTime(createDate);
        // end of creating a Dataset

        logger.log(Level.FINE, "version:contents={0}", xstream.toXML(version));
        logger.log(Level.FINE, "dataset:contents={0}", xstream.toXML(dataset));
        logger.log(Level.FINE, "version from dataset:contents={0}", xstream.toXML(version.getDataset()));

        // The following step transfoms the target file to a list of Datafile(s)
        // and makes it ready for its ultimate ingest
        initialFileList = FileUtil.createDataFiles(version, fileInputStream, filename, contentType, systemConfig);

        logger.log(Level.INFO, "how many files in the initialFileList={0}", initialFileList.size());
        logger.log(Level.INFO, "initialFileList: contents={0}", xstream.toXML(initialFileList));
        logger.log(Level.FINE, "after FileUtil: datafile list:contents={0}", xstream.toXML(initialFileList));
        logger.log(Level.FINE, "after FileUtil: datasetversion:contents={0}", xstream.toXML(version));
        logger.log(Level.FINE, "after FileUtil: dataset:contents={0}", xstream.toXML(dataset));
        
        // set up an application-managed entity manager because this is a 
        // Java application not Java web application
        Timestamp now;

//        EntityManagerFactory emf
//                = Persistence.createEntityManagerFactory("trsa-WebPU");


//        EntityManager em = emf.createEntityManager();
//        em.getTransaction().begin();

// =============================================================================
// persistence-required segment: starts here
        // The following blocks emulates relevant methods of IngestServiceBean(ISB)
        // ISB# addFilesToDataset() line 158
        if (initialFileList != null && !initialFileList.isEmpty()) {
            //Dataset targetDataset = version.getDataset();

            for (DataFile targetDF : initialFileList) {
                logger.log(Level.INFO, "1st stage: working on each targetDF");
                // DataFile dataFile = initialFileList.get(0);//new DataFile();
                now = new Timestamp(new Date().getTime());
                if (targetDF.getCreateDate() == null) {
                    logger.log(Level.INFO, "targetDF.getCreateDate() is null case");
                    targetDF.setCreateDate(now);
                    logger.log(Level.INFO, "targetDF.getCreateDate() is set to {0}", targetDF.getCreateDate());
                }
                targetDF.setModificationTime(now);
                logger.log(Level.INFO, "targetDF: modificationTime:{0}", targetDF.getModificationTime());
                logger.log(Level.FINE, "initialFileList[0]:contents={0}", xstream.toXML(targetDF));
                
                
                // TRSA-specific setting: may be changed
//                targetDF.setRestricted(true);

                FileMetadata fileMetadata = targetDF.getFileMetadatas().get(0);
                
                // TRASA-specific setting: may be changed
//                fileMetadata.setRestricted(true);
                String fileName = fileMetadata.getLabel();

                // Attach the file to the dataset and to the version: 
                targetDF.setDataset(dataset);

                version.getFileMetadatas().add(targetDF.getFileMetadata());
                targetDF.getFileMetadata().setDatasetVersion(version);
                dataset.getFiles().add(targetDF);
            }

            logger.log(Level.FINE, "addFilesToDataset:dataset={0}", xstream.toXML(dataset));
            logger.log(Level.FINE, "addFilesToDataset:version={0}", xstream.toXML(version));
            // end of emulating ISB#addFilesToDataset method
        }

        // After the source file is determined as a single file or a list of 
        // files (zip file), each of the list is checked about its mime-type;
        // if its mime-type is ingestableAsTabular, then its ingest is scheduled
        
        // the following block enumlates addFiles() of ISB
        // here all (data/non-data) files are handled
        if (initialFileList != null && !initialFileList.isEmpty()) {
            // emulating line 195 of addFiles
            for (DataFile dataFile : initialFileList) {
                logger.log(Level.INFO, "2nd stage: working on each dataFile");
                String tempFileLocation = FileUtil.getFilesTempDirectory() 
                        + "/" + dataFile.getStorageIdentifier();
                logger.log(Level.INFO, "dataFile.getStorageIdentifier={0}", 
                        dataFile.getStorageIdentifier());
                // for the default case, e.g., 
                // tempFileLocation= /tmp/files/temp/1629c1eef17-aeb040a8bf62
                // the above line was never usde in the original. leftover deadwood?
                
                FileMetadata fileMetadata = dataFile.getFileMetadatas().get(0);
                String fileName = fileMetadata.getLabel();
                // the above line(line 201 of ISB) is never used, an editing issue?

                if (dataFile.getDataset() == null) {
                    dataFile.setDataset(dataset);

                    version.getFileMetadatas().add(dataFile.getFileMetadata());
                    dataFile.getFileMetadata().setDatasetVersion(version);
                    dataset.getFiles().add(dataFile);
                }
                // line 216 of ISB
                boolean metadataExtracted = false;

                if (FileUtil.ingestableAsTabular(dataFile)) {
                    logger.log(Level.INFO, "ingestableAsTabular: yes case");
                    dataFile.setIngestScheduled();

                } else if (fileMetadataExtractable(dataFile)) {
                    logger.log(Level.INFO, "handling fileMetadataExtractable is not imported");
                    // masked this block  until dependencies of a FITS case are sorted out
//                        try {
//                            // FITS is the only type supported for metadata 
//                            // extraction, as of now. -- L.A. 4.0 
//                            dataFile.setContentType("application/fits");
//                            metadataExtracted = extractMetadata(tempFileLocation, dataFile, version);
//                        } catch (IOException mex) {
//                            logger.severe("Caught exception trying to extract indexable metadata from file " + fileName + ",  " + mex.getMessage());
//                        }
//                        if (metadataExtracted) {
//                            logger.fine("Successfully extracted indexable metadata from file " + fileName);
//                        } else {
//                            logger.fine("Failed to extract indexable metadata from file " + fileName);
//                        }
                } else {
                    logger.log(Level.INFO, "ingestableAsTabular: no; FITS case: no");
                }
                // line 246 of ISB
                // Try to save the file in its permanent location: 
                // below line was introduced in 4.0.2 but no spec about "temp://" in the src tree
                String storageId = dataFile.getStorageIdentifier().replaceFirst("^tmp://", "");
                
                // line 251: below path is the same as the above tempFileLocation
                // some editing issue?
                Path tempLocationPath = Paths.get(FileUtil.getFilesTempDirectory() 
                        + "/" + storageId);
                logger.log(Level.INFO, "tempLocationPath={0}", 
                        tempLocationPath.toString());
                // emulating lines 259-306 of ISB: 
                // line 262: DataAccess.createNewStorageIO(dataFile, storageId);
                // line 262 ensures that the target dataset directory exists
                // dataset's directory
                datasetDir = Paths.get(FilesRootDirectory, dataset.getAuthority(), 
                        dataset.getIdentifier());
                
                if ((datasetDir != null) && (!Files.exists(datasetDir))){
                    Files.createDirectories(datasetDir);
                } else {
                    logger.log(Level.INFO, "datasetDir ({0}) already exists", 
                            datasetDir.toString());
                }
                
                
                // emulate the following line 299
                //
                // dataAccess.savePath(tempLocationPath);
                // => FileAccessIO.savePath(tempLocationPath)
                // 
                // FileUtil#filesRootDirectory is by default "/tmp/files"
                
                Path outputPath = Paths.get(datasetDir.toString(), 
                        dataFile.getStorageIdentifier());
                logger.log(Level.INFO, "outputPath={0}", outputPath);
                Files.copy(tempLocationPath, outputPath, StandardCopyOption.REPLACE_EXISTING);
                long newFileSize = outputPath.toFile().length();
                logger.log(Level.INFO, "newFileSize={0}", newFileSize);
                dataFile.setFilesize(newFileSize);
                // lines 314-347 thumnail files etc come here: skip
                // line 349
                try {
                    logger.log(Level.INFO, "Will attempt to delete the temp file ={0}",
                            tempLocationPath.toString());
                    Files.delete(tempLocationPath);
                } catch (IOException ex) {
                    logger.log(Level.WARNING, "Failed to delete temp file={0} ", 
                            tempLocationPath.toString());
                }
                
                
            } // for each loop
            logger.fine("Done! Finished saving new files in permanent storage.");
        } else {
            logger.log(Level.WARNING, "initialFileList is null or empty");
        }

        // The following block emulates ISB#startIngestJobs() and ingestAsTabular():line 651
        if (initialFileList != null && !initialFileList.isEmpty()) {
            logger.log(Level.INFO, "+++++++++++++++++ parsing block starts here +++++++++++++++++");
            for (DataFile dataFile : initialFileList) {
                logger.log(Level.INFO, "each parsing iteration starts here");
                String fileName = dataFile.getFileMetadata().getLabel();
                logger.log(Level.INFO, "parsing the file={0}", fileName);
                if (dataFile.isIngestScheduled()) {
                    logger.log(Level.INFO, "scheduled case");
                    // boolean ingestSuccessful = false;
                    // the line below is the same as ISB line 660
                    TabularDataFileReader ingestPlugin = 
                            getTabDataReaderByMimeType(dataFile.getContentType());

                    if (ingestPlugin == null) {
                        // ISB stores this state-info on dataFile: lines 662-668
                        // dataFile.setIngestProblem();
                        // dataFile = em.merge(dataFile);
                        // logger.warning("Ingest failure.");
                        // return false; 
                        // 

                        System.err.println("Could not locate an ingest plugin for type " + contentType + ".");
                        System.exit(1);
                    }

                    // here a missing block (ISB lines 700-710) is about IngestRequest 
                    // instance (entity) that stores extra-information about ingest
                    // (1) user(GUI)-specified encode-setting of an ingest request and
                    // (2) additional file to be used with this ingest request
                    // this may not be relevant to our key objectives
                    /*
                         * IngestRequest ingestRequest =
                         * dataFile.getIngestRequest(); if (ingestRequest !=
                         * null) { if (ingestRequest.getTextEncoding() != null
                         * && !ingestRequest.getTextEncoding().equals("") ) {
                         * logger.fine("Setting language encoding to
                         * "+ingestRequest.getTextEncoding());
                         * ingestPlugin.setDataLanguageEncoding(ingestRequest.getTextEncoding());
                         * } if (ingestRequest.getLabelsFile() != null) {
                         * additionalData = new
                         * File(ingestRequest.getLabelsFile()); } }
                     */
                    // the line below corresponds to ISB line 712
                    TabularDataIngest tabDataIngest = null;

                    String newFileName = datasetDir.toString() 
                            + "/" + dataFile.getStorageIdentifier();

                    logger.log(Level.INFO, "newFileName={0}", newFileName);

                   
                    try ( BufferedInputStream newflis = 
                            new BufferedInputStream(new FileInputStream(
                                    new File(newFileName)))) {
                        // the line below corresponds to ISB line 717 additionalData is null
                        tabDataIngest = ingestPlugin.read(newflis, null);
                    } catch (IOException ingestEx) {
                        logger.log(Level.INFO, "IOException={0}", ingestEx);
                        // ISB line 720-736 come here
                        // dataFile.SetIngestProblem();
                        // FileUtil.createIngestFailureReport(dataFile, ingestEx.getMessage());
                        // dataFile = fileService.save(dataFile);
                        // return false;
                        System.err.println("Caught an exception trying to ingest file " + fileName + ".");
                        System.exit(1);
                    }

                    String originalContentType = dataFile.getContentType();
//                    String originalFileName = dataFile.getFileMetadata().getLabel();
//                    long originalFileSize = dataFile.getFilesize();
                    
                    
                    
                    
                    try {
                        // the blow line corresponds to ISB line 746
                        if (tabDataIngest != null) {
                            logger.log(Level.INFO, "tabular-data ingest case");
                            logger.log(Level.FINE, "tabDataIngest:contents={0}", xstream.toXML(tabDataIngest));

                            // the below line corresponds to ISB line 747 and is the last common one 
                            // before the current ISB#ingestAsTabular() bifurcates
                            File tabFile = tabDataIngest.getTabDelimitedFile();
                            logger.log(Level.INFO, "tabFile={0}", tabFile.getAbsolutePath());
                            // the below line corresponds to ISB line 749-752
                            if (tabDataIngest.getDataTable() != null
                                    && tabFile != null
                                    && tabFile.exists()) {

                                logger.log(Level.FINE, "tabDataIngest.getDataTable() is not null:{0}",
                                        xstream.toXML(tabDataIngest.getDataTable()));

                                // copied: ISB lines 756 and 760
                                dataFile.setFilesize(tabFile.length());
                                dataFile.setContentType(FileUtil.MIME_TYPE_TAB);
                                
                                // The following lines are based on ISB line 761
                                // i.e, IngestUtil.modifyExistingFilename(
                                // dataFile.getOwner().getLatestVersion(),
                                // dataFile.getFileMetadata(), 
                                // FileUtil.replaceExtension(fileName, "tab"));
                                String tabFilename = FileUtil.replaceExtension(fileName, "tab");
                                logger.log(Level.INFO, "tabFilename={0}", tabFilename);
                                dataFile.getFileMetadata().setLabel(tabFilename);
                                // duplicate name-check par was skipped here
                                
                                // for non-cvs case: ISB: line 766
                                // tabDataIngest.getDataTable().setOriginalFileFormat(originalContentType);

                                if (FileUtil.MIME_TYPE_CSV_ALT.equals(dataFile.getContentType())) {
                                    tabDataIngest.getDataTable().setOriginalFileFormat(FileUtil.MIME_TYPE_CSV);
                                } else {
                                    tabDataIngest.getDataTable().setOriginalFileFormat(originalContentType);
                                }                                

                                

                                //logger.log(Level.INFO, "dataTable:contents={0}", xstream.toXML(tabDataIngest.getDataTable()));
                                //printDataTable(tabDataIngest.getDataTable());
                                
                                
                                
                                
                                // IBN: lines 769-770
                                // for DataFile
                                dataFile.setDataTable(tabDataIngest.getDataTable());

                                logger.log(Level.FINE, "before em: dataFile:contents={0}", xstream.toXML(dataFile));

                                // thellowing two have some problems
                                logger.log(Level.FINE, "before em: dataset:contents={0}", xstream.toXML(dataset));
                                logger.log(Level.FINE, "before em: version:contents={0}", xstream.toXML(version));
                                logger.log(Level.FINE, "before em:filemeta:contents={0}", xstream.toXML(version.getFileMetadatas()));

                                // for DataTable
                                tabDataIngest.getDataTable().setDataFile(dataFile);

                                // ISB: line 773-774
                                produceSummaryStatistics(dataFile, tabFile);
                                // when DataFile has dataFile.getFileSystemLocation()

                                //postIngestTasksSuccessful = true;
                                logger.log(Level.FINE, "post-summary stat: dataFile:contents={0}", xstream.toXML(dataFile));
                                logger.log(Level.FINE, "post-summary-stat: dataTable:contents={0}", xstream.toXML(tabDataIngest.getDataTable()));
                                
                                // ISB: line 790
                                dataFile.setIngestDone();
                                
                                
                                // ISB: lines 797-834 skipped
                                // ISB: 805:  dataFile = fileService.save(dataFile);
                                // DataFileServiceBean#save =>
                                // DataFile savedDataFile = em.merge(dataFile);
                                // this is the place where ingested-results are permanently saved
                                // ISB:lines 836-875 try block
                                
                                // Finally, let's swap the original and the tabular files:
                                // and we want to save the original of the ingested file: 
                                try {
                                    //dataAccess.backupAsAux(FileUtil.SAVED_ORIGINAL_FILENAME_EXTENSION);
                                    // Path auxPath = getAuxObjectAsPath(auxItemTag);
                                    // original_file_name => original_file_name.orig
                                    Path auxPath = Paths.get(datasetDir.toString(), dataFile.getStorageIdentifier() + "." + FileUtil.SAVED_ORIGINAL_FILENAME_EXTENSION);
                                    Path physicalPath = Paths.get(datasetDir.toString(), dataFile.getStorageIdentifier());
                                    Files.move(physicalPath, auxPath, StandardCopyOption.REPLACE_EXISTING);

                                    logger.log(Level.INFO, "Saved the ingested original as a backup aux file ={0} ", FileUtil.SAVED_ORIGINAL_FILENAME_EXTENSION);
                                } catch (IOException iox) {
                                    logger.log(Level.WARNING, "Failed to save the ingested original!={0}", iox.getMessage());
                                }

                                // Replace contents of the file with the tab-delimited data produced:
                                // original_file_name.tab => original_file_name
                                Path destPath = Paths.get(datasetDir.toString(), dataFile.getStorageIdentifier());
                                Files.copy(tabFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                                // Reset the file size: 
                                long newFileSize = destPath.toFile().length();
                                logger.log(Level.INFO, "newFileSize={0}", newFileSize);
                                dataFile.setFilesize(newFileSize);
                                
                                
                                // delete the temp tab-file:
                                tabFile.delete();
                                
                                now = new Timestamp(new Date().getTime());

                                dataset.getVersions().get(0).setLastUpdateTime(now);
                                dataset.setModificationTime(now);

                                // persisting all tables via Dataset
                                logger.log(Level.INFO, "persisting dataset");
                                em.persist(dataset);
                            } else {
                                logger.log(Level.SEVERE, "tabDataIngest.getDataTable() is null");
                                System.err.println("Ingest failed to produce tab file or data table for file " + filename + ".");
                                System.exit(1);
                            }
                        } else {
                            logger.log(Level.SEVERE, "Ingest resulted in a null tabDataIngest object for data file:{0}", filename);
                            System.err.println("Ingest resulted in a null tabDataIngest object for data file " + filename + ".");
                            System.exit(1);
                        }
                    } catch (IOException ex) {
                        logger.log(Level.INFO, "saving a tab file failed for data file:{0}", filename);
                        System.err.println("Caught an exception trying to save ingested data for file " + filename + ".");
                        System.exit(1);
                    }
                    
                    logger.log(Level.INFO, "end of each scheduled case");
                } else {
                    logger.log(Level.INFO, "not scheduled case");

                    now = new Timestamp(new Date().getTime());
                    dataset.getVersions().get(0).setLastUpdateTime(now);
                    dataset.setModificationTime(now);
                    logger.log(Level.FINE, "dataset={0}", xstream.toXML(dataset.getFiles()));
                    logger.log(Level.FINE, "dataset={0}", xstream.toXML(dataset.getFiles().get(0).getFileMetadata()));

                    logger.log(Level.FINE, "dataset={0}", xstream.toXML(dataset.getVersions().get(0)));

                    datasetFacade.create(dataset);

                }
                logger.log(Level.INFO, "end of each parsing iteration");
            }
            logger.log(Level.INFO, "after parsing iterations ended");
            logger.log(Level.INFO, "+++++++++++++++++ parsing block ends here +++++++++++++++++");
        }

        // persistence-required segment: end
//        em.getTransaction().commit();
//        em.close();

        logger.log(Level.INFO, "closing entity manager factory and exiting the application");
//        emf.close();

        logger.log(Level.FINE, "\n\nafter em: dataset={0}", xstream.toXML(dataset));
        
        
        

        
        
        
        
        // =============================================================================
        // from here serialization calls
        // the following block emulates ExportService#cacheExport()
        // esp.,  
        // exporter.exportDataset(version, datasetAsJson, outputStream);
        // cacheExport(DatasetVersion version, String format, JsonObject datasetAsJson, Exporter exporter)

        // required: dataset via DatasetVersion
        // requried: exporters (export formats): ddi, oai_ddi, dcterms, oai_dc, and dataverse_json.
        //DatasetVersion releasedVersion = dataset.getReleasedVersion();
        
        //exportDataset(version);

    }
    
//  private boolean constraintValidationsDetected(T entity) {
//    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//    Validator validator = factory.getValidator();
//    Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
//    if (constraintViolations.size() > 0) {
//      Iterator<ConstraintViolation<T>> iterator = constraintViolations.iterator();
//      while (iterator.hasNext()) {
//        ConstraintViolation<T> cv = iterator.next();
//        System.err.println(cv.getRootBeanClass().getName() + "." + cv.getPropertyPath() + " " + cv.getMessage());
//
//        JsfUtil.addErrorMessage(cv.getRootBeanClass().getSimpleName() + "." + cv.getPropertyPath() + " " + cv.getMessage());
//      }
//      return true;
//    }
//    else {
//      return false;
//    }
//  }
    
    
    

    private void handleConstraintViolation(ConstraintViolationException cve) {
        Set<ConstraintViolation<?>> cvs = cve.getConstraintViolations();
        for (ConstraintViolation<?> cv : cvs) {
            System.out.println("------------------------------------------------");
            System.out.println("Violation: " + cv.getMessage());
            System.out.println("Entity: " + cv.getRootBeanClass().getSimpleName());
            // The violation occurred on a leaf bean (embeddable)
            if (cv.getLeafBean() != null && cv.getRootBean() != cv.getLeafBean()) {
                System.out.println("Embeddable: "
                        + cv.getLeafBean().getClass().getSimpleName());
            }
            System.out.println("Attribute: " + cv.getPropertyPath());
            System.out.println("Invalid value: " + cv.getInvalidValue());
        }
    }
    
    public void exportDataset(DatasetVersion version){
        logger.log(Level.INFO, "exportDataset() starts here");

        final JsonObjectBuilder datasetAsJsonBuilder
                = JsonPrinter.jsonAsDatasetDto(version);
        
        JsonObject datasetAsJson = datasetAsJsonBuilder.build();
        logger.log(Level.FINE, "datasetAsJson={0}", datasetAsJson);

        String[] formatNames = {"oai_dc", "ddi", "dataverse_json"};
        
        
        for (String frmtName : formatNames) {
            logger.log(Level.INFO, "frmtName={0}", frmtName);
            
            Path cachedMetadataFilePath = Paths.get(version.getDataset().getFileSystemDirectory().toString(), "export_" + frmtName + ".cached");
            
            logger.log(Level.INFO, "exportFileName={0}", cachedMetadataFilePath.toString());
            
            try (FileOutputStream cachedExportOutputStream = new FileOutputStream(cachedMetadataFilePath.toFile())){
                switch (frmtName) {
                    case "oai_dc":
                        logger.log(Level.INFO, "This is an oai_dc case");
                        doOaiDc(datasetAsJson, cachedExportOutputStream, DublinCoreExportUtil.DC_FLAVOR_OAI);
                        break;
                    case "ddi":
                        logger.log(Level.INFO, "This is a DDI case");
                        doDDI(version, datasetAsJson, cachedExportOutputStream);
                        break;
                    case "dataverse_json":
                        logger.log(Level.INFO, "This is a dataverse_json case");
                        doJson(version, datasetAsJson, cachedExportOutputStream);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid format name="+frmtName);
                }

            } catch (Exception e) {
                logger.log(Level.INFO, "something happened for running case of {0}", frmtName);
                logger.log(Level.INFO, "exception happened={0}", e);
            }
        }
        logger.log(Level.INFO, "leaving exportDataset()");
    }
    
    

    
    private void printDataTable(DataTable dataTable) {

        System.out.println("NVARS: " + dataTable.getVarQuantity());
        System.out.println("NOBS: " + dataTable.getCaseQuantity());
        System.out.println("UNF: " + dataTable.getUnf());

        for (int i = 0; i < dataTable.getVarQuantity(); i++) {
            String vartype = "";

            if (dataTable.getDataVariables().get(i).isIntervalContinuous()) {
                vartype = "numeric-continuous";
            } else {
                if (dataTable.getDataVariables().get(i).isTypeNumeric()) {
                    vartype = "numeric-discrete";
                } else {
                    vartype = "character";
                }
            }

            System.out.print("VAR" + i + " ");
            System.out.print(dataTable.getDataVariables().get(i).getName() + " ");
            System.out.print(vartype + " ");
            System.out.print(dataTable.getDataVariables().get(i).getUnf());
            System.out.println();

        }
    }
    
    
    private void doOaiDc(JsonObject json, OutputStream outputStream, String flavor) throws XMLStreamException {

        // using DublinCoreExportUtilTest class
        // String formatName = "oai_dc";
        // Paths.get(datasetDirectory, dvObject.getStorageIdentifier() + "." + auxItemTag);
        // Paths.get(tempFile.getAbsolutePath()), "export_" + format + ".cached");
        // cacheExport(version, formatName, datasetAsJson, e);
        // DC exporter
        //DublinCoreExportUtil.datasetJson2dublincore(datasetAsJson, outputStream, DublinCoreExportUtil.DC_FLAVOR_OAI);
        DublinCoreExportUtil.datasetJson2dublincore(json, outputStream, DublinCoreExportUtil.DC_FLAVOR_OAI);
    }

    private void doDDI(DatasetVersion version, JsonObject json, OutputStream outputStream) throws XMLStreamException {
        // DdiExportUtil.datasetJson2ddi(json, version, outputStream);
        DdiExportUtil.datasetJson2ddi(json, version, outputStream);
    }

    private void doJson(DatasetVersion version, JsonObject json, OutputStream outputStream) throws ExportException {
        JSONExporter jsonExporter = new JSONExporter();
        jsonExporter.exportDataset(version, json, outputStream);
    }

    public static TabularDataFileReader getTabDataReaderByMimeType(String mimeType) { //DataFile dataFile) {
        /*
         * Same as the comment above; since we don't have any ingest plugins
         * loadable in real times yet, we can select them by a fixed list of
         * mime types. -- L.A. 4.0 beta.
         */

        //String mimeType = dataFile.getContentType();
        if (mimeType == null) {
            return null;
        }

        TabularDataFileReader ingestPlugin = null;

        if (mimeType.equals(FileUtil.MIME_TYPE_STATA)) {
            ingestPlugin = new DTAFileReader(new edu.harvard.iq.dataverse.ingest.tabulardata.impl.plugins.dta.DTAFileReaderSpi());
        } else if (mimeType.equals(FileUtil.MIME_TYPE_STATA13)) {
            ingestPlugin = new DTA117FileReader(new edu.harvard.iq.dataverse.ingest.tabulardata.impl.plugins.dta.DTAFileReaderSpi());
        } else if (mimeType.equals(FileUtil.MIME_TYPE_RDATA)) {
            ingestPlugin = new RDATAFileReader(new RDATAFileReaderSpi());
        } else if (mimeType.equals(FileUtil.MIME_TYPE_CSV) || mimeType.equals(FileUtil.MIME_TYPE_CSV_ALT)) {
            ingestPlugin = new CSVFileReader(new CSVFileReaderSpi());
        } else if (mimeType.equals(FileUtil.MIME_TYPE_XLSX)) {
            ingestPlugin = new XLSXFileReader(new XLSXFileReaderSpi());
        } else if (mimeType.equals(FileUtil.MIME_TYPE_SPSS_SAV)) {
            ingestPlugin = new SAVFileReader(new SAVFileReaderSpi());
        } else if (mimeType.equals(FileUtil.MIME_TYPE_SPSS_POR)) {
            ingestPlugin = new PORFileReader(new PORFileReaderSpi());
        }

        return ingestPlugin;
    }

    public void produceSummaryStatistics(DataFile dataFile, File generatedTabularFile) throws IOException {

        produceDiscreteNumericSummaryStatistics(dataFile, generatedTabularFile);
        produceContinuousSummaryStatistics(dataFile, generatedTabularFile);
        produceCharacterSummaryStatistics(dataFile, generatedTabularFile);

        recalculateDataFileUNF(dataFile);
        //recalculateDatasetVersionUNF(dataFile.getFileMetadata().getDatasetVersion());
        recalculateDatasetVersionUNF(null);
    }

    public void produceDiscreteNumericSummaryStatistics(DataFile dataFile, File generatedTabularFile) throws IOException {

        //TabularSubsetGenerator subsetGenerator = new TabularSubsetGenerator();
        for (int i = 0; i < dataFile.getDataTable().getVarQuantity(); i++) {
            if (dataFile.getDataTable().getDataVariables().get(i).isIntervalDiscrete()
                    && dataFile.getDataTable().getDataVariables().get(i).isTypeNumeric()) {
                logger.fine("subsetting discrete-numeric vector");

//                StorageIO<DataFile> storageIO = dataFile.getStorageIO();
//                storageIO.open();
                Long[] variableVector = TabularSubsetGenerator.subsetLongVector(new FileInputStream(generatedTabularFile), i, dataFile.getDataTable().getCaseQuantity().intValue());
                // We are discussing calculating the same summary stats for 
                // all numerics (the same kind of sumstats that we've been calculating
                // for numeric continuous type)  -- L.A. Jul. 2014
                calculateContinuousSummaryStatistics(dataFile, i, variableVector);
                // calculate the UNF while we are at it:
                logger.fine("Calculating UNF on a Long vector");
                calculateUNF(dataFile, i, variableVector);
                logger.fine("Done! (discrete numeric)");
                variableVector = null;
            }
        }
    }

    public void produceContinuousSummaryStatistics(DataFile dataFile, File generatedTabularFile) throws IOException {

        // quick, but memory-inefficient way:
        // - this method just loads the entire file-worth of continuous vectors 
        // into a Double[][] matrix. 
        //Double[][] variableVectors = subsetContinuousVectors(dataFile);
        //calculateContinuousSummaryStatistics(dataFile, variableVectors);
        // A more sophisticated way: this subsets one column at a time, using 
        // the new optimized subsetting that does not have to read any extra 
        // bytes from the file to extract the column:
        // TabularSubsetGenerator subsetGenerator = new TabularSubsetGenerator();
        for (int i = 0; i < dataFile.getDataTable().getVarQuantity(); i++) {
            if (dataFile.getDataTable().getDataVariables().get(i).isIntervalContinuous()) {
                logger.fine("subsetting continuous vector");

//                StorageIO<DataFile> storageIO = dataFile.getStorageIO();
//                storageIO.open();
                if ("float".equals(dataFile.getDataTable().getDataVariables().get(i).getFormat())) {
                    Float[] variableVector = TabularSubsetGenerator.subsetFloatVector(
                            new FileInputStream(generatedTabularFile), i, dataFile.getDataTable().getCaseQuantity().intValue());

                    logger.fine("Calculating summary statistics on a Float vector;");
                    calculateContinuousSummaryStatistics(dataFile, i, variableVector);
                    // calculate the UNF while we are at it:
                    logger.fine("Calculating UNF on a Float vector;");
                    calculateUNF(dataFile, i, variableVector);
                    variableVector = null;
                } else {
                    Double[] variableVector = TabularSubsetGenerator.subsetDoubleVector(new FileInputStream(generatedTabularFile), i, dataFile.getDataTable().getCaseQuantity().intValue());
                    logger.fine("Calculating summary statistics on a Double vector;");
                    calculateContinuousSummaryStatistics(dataFile, i, variableVector);
                    // calculate the UNF while we are at it:
                    logger.fine("Calculating UNF on a Double vector;");
                    calculateUNF(dataFile, i, variableVector);
                    variableVector = null;
                }
                logger.fine("Done! (continuous);");
            }
        }
    }

    private void calculateContinuousSummaryStatistics(DataFile dataFile, int varnum, Number[] dataVector) throws IOException {
        double[] sumStats = SumStatCalculator.calculateSummaryStatistics(dataVector);
        assignContinuousSummaryStatistics(dataFile.getDataTable().getDataVariables().get(varnum), sumStats);
    }

    private void assignContinuousSummaryStatistics(DataVariable variable, double[] sumStats) throws IOException {
        if (sumStats == null || sumStats.length != summaryStatisticTypes.length) {
            throw new IOException("Wrong number of summary statistics types calculated! (" + sumStats.length + ")");
        }

        for (int j = 0; j < summaryStatisticTypes.length; j++) {
            SummaryStatistic ss = new SummaryStatistic();
            ss.setTypeByLabel(summaryStatisticTypes[j]);
            if (!ss.isTypeMode()) {
                ss.setValue((new Double(sumStats[j])).toString());
            } else {
                ss.setValue(".");
            }
            ss.setDataVariable(variable);
            variable.getSummaryStatistics().add(ss);
        }

    }

    public void produceCharacterSummaryStatistics(DataFile dataFile, File generatedTabularFile) throws IOException {

        for (int i = 0; i < dataFile.getDataTable().getVarQuantity(); i++) {
            if (dataFile.getDataTable().getDataVariables().get(i).isTypeCharacter()) {

//                StorageIO<DataFile> storageIO = dataFile.getStorageIO();
//                storageIO.open();
                logger.fine("subsetting character vector");
                String[] variableVector = TabularSubsetGenerator.subsetStringVector(new FileInputStream(generatedTabularFile), i, dataFile.getDataTable().getCaseQuantity().intValue());
                //calculateCharacterSummaryStatistics(dataFile, i, variableVector);
                // calculate the UNF while we are at it:
                logger.log(Level.FINE, "variableVector={0}", xstream.toXML(variableVector));
                logger.fine("Calculating UNF on a String vector");
                calculateUNF(dataFile, i, variableVector);
                logger.fine("Done! (character)");
                variableVector = null;
            }
        }
    }

    public void recalculateDataFileUNF(DataFile dataFile) {
        String[] unfValues = new String[dataFile.getDataTable().getVarQuantity().intValue()];
        String fileUnfValue = null;

        for (int i = 0; i < dataFile.getDataTable().getVarQuantity(); i++) {
            String varunf = dataFile.getDataTable().getDataVariables().get(i).getUnf();
            unfValues[i] = varunf;
        }

        try {
            fileUnfValue = UNFUtil.calculateUNF(unfValues);
        } catch (IOException ex) {
            logger.warning("Failed to recalculate the UNF for the datafile id=" + dataFile.getId());
        } catch (UnfException uex) {
            logger.warning("UNF Exception: Failed to recalculate the UNF for the dataset version id=" + dataFile.getId());
        }

        if (fileUnfValue != null) {
            dataFile.getDataTable().setUnf(fileUnfValue);
        }
    }

    public void recalculateDatasetVersionUNF(DatasetVersion version) {
        IngestUtil.recalculateDatasetVersionUNF(version);
    }

    private void calculateUNF(DataFile dataFile, int varnum, Double[] dataVector) {
        String unf = null;
        try {
            unf = UNFUtil.calculateUNF(dataVector);
        } catch (IOException iex) {
            logger.warning("exception thrown when attempted to calculate UNF signature for (numeric, continuous) variable " + varnum);
        } catch (UnfException uex) {
            logger.warning("UNF Exception: thrown when attempted to calculate UNF signature for (numeric, continuous) variable " + varnum);
        }

        if (unf != null) {
            dataFile.getDataTable().getDataVariables().get(varnum).setUnf(unf);
        } else {
            logger.warning("failed to calculate UNF signature for variable " + varnum);
        }
    }

    private void calculateUNF(DataFile dataFile, int varnum, Long[] dataVector) {
        String unf = null;
        try {
            unf = UNFUtil.calculateUNF(dataVector);
        } catch (IOException iex) {
            logger.warning("exception thrown when attempted to calculate UNF signature for (numeric, discrete) variable " + varnum);
        } catch (UnfException uex) {
            logger.warning("UNF Exception: thrown when attempted to calculate UNF signature for (numeric, discrete) variable " + varnum);
        }

        if (unf != null) {
            dataFile.getDataTable().getDataVariables().get(varnum).setUnf(unf);
        } else {
            logger.warning("failed to calculate UNF signature for variable " + varnum);
        }
    }

    private void calculateUNF(DataFile dataFile, int varnum, String[] dataVector) throws IOException {
        String unf = null;

        String[] dateFormats = null;

        // Special handling for Character strings that encode dates and times:
        if ("time".equals(dataFile.getDataTable().getDataVariables().get(varnum).getFormatCategory())) {
            dateFormats = new String[dataVector.length];
            String savedDateTimeFormat = dataFile.getDataTable().getDataVariables().get(varnum).getFormat();
            String timeFormat = null;
            if (savedDateTimeFormat != null && !savedDateTimeFormat.equals("")) {
                timeFormat = savedDateTimeFormat;
            } else {
                timeFormat = dateTimeFormat_ymdhmsS;
            }

            /*
             * What follows is special handling of a special case of time values
             * non-uniform precision; specifically, when some have if some have
             * milliseconds, and some don't. (and that in turn is only n issue
             * when the timezone is present... without the timezone the time
             * string would still evaluate to the end, even if the format has
             * the .SSS part and the string does not. This case will be properly
             * handled internally, once we permanently switch to UNF6. -- L.A.
             * 4.0 beta 8
             */
            String simplifiedFormat = null;
            SimpleDateFormat fullFormatParser = null;
            SimpleDateFormat simplifiedFormatParser = null;

            if (timeFormat.matches(".*\\.SSS z$")) {
                simplifiedFormat = timeFormat.replace(".SSS", "");

                fullFormatParser = new SimpleDateFormat(timeFormat);
                simplifiedFormatParser = new SimpleDateFormat(simplifiedFormat);
            }

            for (int i = 0; i < dataVector.length; i++) {
                if (dataVector[i] != null) {

                    if (simplifiedFormatParser != null) {
                        // first, try to parse the value against the "full" 
                        // format (with the milliseconds part):
                        fullFormatParser.setLenient(false);

                        try {
                            logger.fine("trying the \"full\" time format, with milliseconds: " + timeFormat + ", " + dataVector[i]);
                            fullFormatParser.parse(dataVector[i]);
                        } catch (ParseException ex) {
                            // try the simplified (no time zone) format instead:
                            logger.fine("trying the simplified format: " + simplifiedFormat + ", " + dataVector[i]);
                            simplifiedFormatParser.setLenient(false);
                            try {
                                simplifiedFormatParser.parse(dataVector[i]);
                                timeFormat = simplifiedFormat;
                            } catch (ParseException ex1) {
                                logger.warning("no parseable format found for time value " + i + " - " + dataVector[i]);
                                throw new IOException("no parseable format found for time value " + i + " - " + dataVector[i]);
                            }
                        }

                    }
                    dateFormats[i] = timeFormat;
                }
            }
        } else if ("date".equals(dataFile.getDataTable().getDataVariables().get(varnum).getFormatCategory())) {
            dateFormats = new String[dataVector.length];
            String savedDateFormat = dataFile.getDataTable().getDataVariables().get(varnum).getFormat();
            for (int i = 0; i < dataVector.length; i++) {
                if (dataVector[i] != null) {
                    if (savedDateFormat != null && !savedDateFormat.equals("")) {
                        dateFormats[i] = savedDateFormat;
                    } else {
                        dateFormats[i] = dateFormat_ymd;
                    }
                }
            }
        }

        try {
            if (dateFormats == null) {
                logger.fine("calculating the UNF value for string vector; first value: " + dataVector[0]);
                unf = UNFUtil.calculateUNF(dataVector);
            } else {
                unf = UNFUtil.calculateUNF(dataVector, dateFormats);
            }
        } catch (IOException iex) {
            logger.warning("IO exception thrown when attempted to calculate UNF signature for (character) variable " + varnum);
        } catch (UnfException uex) {
            logger.warning("UNF Exception: thrown when attempted to calculate UNF signature for (character) variable " + varnum);
        }

        if (unf != null) {
            dataFile.getDataTable().getDataVariables().get(varnum).setUnf(unf);
        } else {
            logger.warning("failed to calculate UNF signature for variable " + varnum);
        }
    }

    // Calculating UNFs from *floats*, not *doubles* - this is to test dataverse
    // 4.0 Ingest against DVN 3.*; because of the nature of the UNF bug, reading
    // the tab file entry with 7+ digits of precision as a Double will result
    // in a UNF signature *different* from what was produced by the v. 3.* ingest,
    // from a STATA float value directly. 
    // TODO: remove this from the final production 4.0!
    // -- L.A., Jul 2014
    private void calculateUNF(DataFile dataFile, int varnum, Float[] dataVector) {
        String unf = null;
        try {
            unf = UNFUtil.calculateUNF(dataVector);
        } catch (IOException iex) {
            logger.warning("exception thrown when attempted to calculate UNF signature for numeric, \"continuous\" (float) variable " + varnum);
        } catch (UnfException uex) {
            logger.warning("UNF Exception: thrown when attempted to calculate UNF signature for numeric, \"continuous\" (float) variable" + varnum);
        }

        if (unf != null) {
            dataFile.getDataTable().getDataVariables().get(varnum).setUnf(unf);
        } else {
            logger.warning("failed to calculate UNF signature for variable " + varnum);
        }
    }

    private static void testDatasetJson2dublincore() {
        /*
         * File datasetVersionJson = new
         * File("src/test/java/edu/harvard/iq/dataverse/export/ddi/dataset-finch1.json");
         * String datasetVersionAsJson = new
         * String(Files.readAllBytes(Paths.get(datasetVersionJson.getAbsolutePath())));
         *
         * JsonReader jsonReader = Json.createReader(new
         * StringReader(datasetVersionAsJson)); JsonObject obj =
         * jsonReader.readObject();
         *
         * File dubCoreFile = new
         * File("src/test/java/edu/harvard/iq/dataverse/export/ddi/dataset-finchDC.xml");
         * String datasetAsDdi = XmlPrinter.prettyPrintXml(new
         * String(Files.readAllBytes(Paths.get(dubCoreFile.getAbsolutePath()))));
         * logger.info(datasetAsDdi);
         *
         * OutputStream output = new ByteArrayOutputStream();
         * DublinCoreExportUtil.datasetJson2dublincore(obj, output,
         * DublinCoreExportUtil.DC_FLAVOR_DCTERMS); String result =
         * XmlPrinter.prettyPrintXml(output.toString());
         *
         * logger.info(result); assertEquals(datasetAsDdi, result);
         */

    }

    public boolean fileMetadataExtractable(DataFile dataFile) {
        /* 
         * Eventually we'll be consulting the Ingest Service Provider Registry
         * to see if there is a plugin for this type of file;
         * for now - just a hardcoded list of mime types:
         *  -- L.A. 4.0 beta
         */
        if (dataFile.getContentType() != null && dataFile.getContentType().equals(FileUtil.MIME_TYPE_FITS)) {
            return true;
        }
        return false;
    }

    /* 
     * extractMetadata: 
     * framework for extracting metadata from uploaded files. The results will 
     * be used to populate the metadata of the Dataset to which the file belongs. 
     *//*
    public boolean extractMetadata(String tempFileLocation, DataFile dataFile, DatasetVersion editVersion) throws IOException {
        boolean ingestSuccessful = false;

        FileInputStream tempFileInputStream = null; 
        
        try {
            tempFileInputStream = new FileInputStream(new File(tempFileLocation));
        } catch (FileNotFoundException notfoundEx) {
            throw new IOException("Could not open temp file "+tempFileLocation);
        }
        
        // Locate metadata extraction plugin for the file format by looking
        // it up with the Ingest Service Provider Registry:
        //FileMetadataExtractor extractorPlugin = IngestSP.getMetadataExtractorByMIMEType(dfile.getContentType());
        FileMetadataExtractor extractorPlugin = new FITSFileMetadataExtractor();

        FileMetadataIngest extractedMetadata = extractorPlugin.ingest(new BufferedInputStream(tempFileInputStream));
        Map<String, Set<String>> extractedMetadataMap = extractedMetadata.getMetadataMap();

        // Store the fields and values we've gathered for safe-keeping:
        // from 3.6:
        // attempt to ingest the extracted metadata into the database; 
        // TODO: this should throw an exception if anything goes wrong.
        FileMetadata fileMetadata = dataFile.getFileMetadata();

        if (extractedMetadataMap != null) {
            logger.fine("Ingest Service: Processing extracted metadata;");
            if (extractedMetadata.getMetadataBlockName() != null) {
                logger.fine("Ingest Service: This metadata belongs to the "+extractedMetadata.getMetadataBlockName()+" metadata block."); 
                processDatasetMetadata(extractedMetadata, editVersion);
            }
            
            processFileLevelMetadata(extractedMetadata, fileMetadata);

        }

        ingestSuccessful = true;

        return ingestSuccessful;
    }
     */
}
