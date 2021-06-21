package us.cyberimpact.trsa.web;

import edu.harvard.iq.dataverse.entities.DataFile;
import edu.harvard.iq.dataverse.entities.DatasetVersion;
import edu.harvard.iq.dataverse.entities.DatasetVersionFacade;
import edu.harvard.iq.dataverse.export.ExportException;
import edu.harvard.iq.dataverse.ingest.IngestException;
import edu.harvard.iq.dataverse.ingest.IngestService;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Faces;
import org.primefaces.PrimeFaces;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import us.cyberimpact.trsa.entities.DsTemplateData;
import us.cyberimpact.trsa.entities.HostInfoFacade;
import us.cyberimpact.trsa.entities.TrsaProfile;
import us.cyberimpact.trsa.entities.TrsaProfileFacade;

@Named("fileUploadView")
@ViewScoped
public class FileUploadView implements Serializable {

    private static final Logger logger = Logger.getLogger(FileUploadView.class.getName());

    @Inject
    IngestService ingestService;

    @Inject
    DatasetVersionFacade datasetVersionFcd;
    
    @Inject
    TrsaProfileFacade trsaProfileFacade;
    
    List<TrsaProfile> trsaProfileTable = new ArrayList<>();

    @Inject
    HostInfoFacade hostInfoFacade;
    
    
    
    
    private String selectedDatasetId;
    
    @Inject
    DsTemplateSelectionView dsTemplateSelectionView;
    
    private UploadedFile file;
    private String destination = "/tmp/";

    
    private String mimeType="application/octet-stream";

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    private DsTemplateData selectedDsTemplateData; 

    public String getSelectedDatasetId() {
        return selectedDatasetId;
    }

    public void setSelectedDatasetId(String selectedDatasetId) {
        this.selectedDatasetId = selectedDatasetId;
    }
    
    private RequestType selectedRequestType;

    public RequestType getSelectedRequestType() {
        return selectedRequestType;
    }

    public void setSelectedRequestType(RequestType selectedRequestType) {
        this.selectedRequestType = selectedRequestType;
    }
    
    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "=========== FileUploadView#init: start ===========");
        selectedRequestType = Faces.getSessionAttribute("selectedRequestType");
        logger.log(Level.INFO, "requestType received={0}", selectedRequestType);
        selectedDatasetId=Faces.getSessionAttribute("selectedDatasetId");
        logger.log(Level.INFO, "selectedDatasetId received={0}",selectedDatasetId);
        
        logger.log(Level.INFO, "selectedDatasetId={0}", selectedDatasetId);
        
        trsaProfileTable= trsaProfileFacade.findAll();
        logger.log(Level.INFO, "FileUploadView:TrsaProfileTable={0}", trsaProfileTable);
        if (trsaProfileTable.isEmpty()){
            logger.log(Level.INFO, "trsa profile is empty");
            // turn off the publish button 
           isTrsaProfileReady=false;
        } else {
            // turn on the publish button
            isTrsaProfileReady=true;
            logger.log(Level.INFO, "FileUploadView:trsa profile exists");
        }
        
        selectedDsTemplateData= dsTemplateSelectionView.getSelectedDsTemplateData();
        logger.log(Level.INFO, "selectedDsTemplateData={0}", selectedDsTemplateData);
        logger.log(Level.INFO, "=========== FileUploadView#init: end ===========");
    }

    private String fileName;    
    
    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    
    private String fileNameOnly;

    public String getFileNameOnly() {
        return fileNameOnly;
    }

    public void setFileNameOnly(String fileNameOnly) {
        this.fileNameOnly = fileNameOnly;
    }
    
    
    
    
    private String datasetIdentifier;

    public String getDatasetIdentifier() {
        return datasetIdentifier;
    }

    public void setDatasetIdentifier(String datasetIdentifier) {
        this.datasetIdentifier = datasetIdentifier;
    }

    public FileUploadView() {
        ingestButtonEnabled = false;
        publishButtonEnabled = false;
    }

    public void upload(FileUploadEvent event) {
        logger.log(Level.INFO, "=========== FileUploadView#upload: start ===========");
        file = event.getFile();
        logger.log(Level.INFO, "file.getFileName()={0}", file.getFileName());
        logger.log(Level.INFO, "fileName={0}", fileName);

        String summaryMsg="Uploading Failure";
        if (file == null) {
                logger.log(Level.SEVERE, "file is null");
                wipeOutIngestFailure();
                ingestButtonEnabled = false;
                publishButtonEnabled = false;
                String detailedMessage = "Uplolading " + fileName + " failed: your selected file was null"; 
                String errorMessage ="Your selected file was null";
                postExceptionCommonSteps(errorMessage, summaryMsg, detailedMessage);
                
                return;
        } else {
            byte[] bytes = null;
            try {
                bytes = file.getContent();
                fileNameOnly = FilenameUtils.getName(file.getFileName());
                Faces.setSessionAttribute("fileNameOnly", fileNameOnly);
                fileName = destination + fileNameOnly;
                logger.log(Level.INFO, "fileName is set to={0}", fileName);
                try (BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(new File(fileName)))) {
                    stream.write(bytes);
                }

            } catch (IOException ex) {
                logger.log(Level.SEVERE, "IOException was thrown during the uploading", ex);
                String detailedMessage = "Uplolading " + fileName + " failed due to IOException:"; 
                postExceptionCommonSteps(ExceptionUtils.getStackTrace(ex), summaryMsg, detailedMessage);
                return;
            } catch (EJBException ex) {
                logger.log(Level.SEVERE, "uploading ended with a failure with EJBException");
                String detailedMessage = "Uploading of (" + fileName + ") failed:EJBException:" + ex.getMessage();
                postExceptionCommonSteps(ExceptionUtils.getStackTrace(ex), summaryMsg, detailedMessage);
                return;
            }

            Faces.getContext().addMessage("topMessage",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
     "Uploading Success", "Uploading " + file.getFileName() + " Successfully ended"));
        }

        setIngestButtonEnabled(true);
        setUploadButtonEnabled(false);
        logger.log(Level.INFO, "=========== FileUploadView#upload: end ===========");
    }

    public void execIngest() {
        logger.log(Level.INFO, "=========== FileUploadView#execIngest: start ===========");
        
        logger.log(Level.INFO, "fileName={0}", fileName);
        logger.log(Level.INFO, "selectedDatasetId={0}", selectedDatasetId);
        
        if (selectedRequestType==RequestType.METADATA_ONLY){
            logger.log(Level.INFO, "this is a metadata-only case");
            datasetIdentifier = selectedDatasetId;
        } else {
            datasetIdentifier = IngestService.generateTempDatasetIdentifier(6);
            logger.log(Level.INFO, "this is a new-dataset-creation case");
        }
        logger.log(Level.INFO, "datasetIdentifier={0}", datasetIdentifier);

        // Warning: contentType expects a / included mime type
        // without "/" a validation error is returned
        logger.log(Level.INFO, "contentType={0}", mimeType);
        String summaryMsg="Ingest Failure";
        try {

            ingestedDataFileList = ingestService.run(fileName, mimeType, datasetIdentifier);

            // result-check: step 1
            if (ingestedDataFileList.isEmpty()) {
                logger.log(Level.WARNING, "returned ingest-file list is empty: ingest-failure is suspected");
                throw new IngestException("Ingest failure: the returned file-list is empty");
            } else {
                logger.log(Level.INFO, "The ingest result seems OK");
                logger.log(Level.INFO, "ingestedDataFileList: size={0}", ingestedDataFileList.size());
            }
            logger.log(Level.INFO, "datafileId={0}", ingestedDataFileList.get(0).getId());

            for (DataFile datafile: ingestedDataFileList){
                fileIdList.add(datafile.getId());
            }
            logger.log(Level.INFO, "fileIdList={0}", fileIdList);
            logger.log(Level.INFO, "dumping metadata files");
            
            
            List<DatasetVersion> versions = datasetVersionFcd.findAll();
            if (versions != null && !versions.isEmpty()) {
                logger.log(Level.INFO, "how many dataset versions={0}", versions.size());
                exportedDatasetVerion= versions.get(versions.size()-1);
                ingestService.exportDataset(exportedDatasetVerion);
            } else {
                logger.log(Level.INFO, "DatasetVersion is null/empty");
            }
            
            String message = fileName + " has been successfully ingested and new dataset (Id=" + datasetIdentifier + ") was created";
            Faces.getContext().addMessage("topMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, "Ingest Success", message));
            Faces.setSessionAttribute("datasetIdentifier", datasetIdentifier);
            Faces.setSessionAttribute("ingestedDataFileList", ingestedDataFileList);
            Faces.setSessionAttribute("fileName", fileName);
            ingestButtonEnabled = false;
            publishButtonEnabled = true;
            uploadButtonEnabled=false;
        } catch (ConstraintViolationException ex){
            logger.log(Level.SEVERE, "Ingest of {0} failed with ConstraintViolationException", fileName);
            String detailedMessage;
            
            if (ex instanceof ConstraintViolationException) {
                StringBuilder sb = new StringBuilder();
                ConstraintViolationException cve = (ConstraintViolationException) ex;
                for (ConstraintViolation cv : cve.getConstraintViolations()) {
                    sb.append(cv.toString());
                    logger.log(Level.SEVERE, "CONSTRAINT VIOLOATION : {0}", cv.toString());
                }
                
                detailedMessage = "The ingest of (" + fileName + ") failed:Validation Error: ConstraintViolationException:" + sb.toString();
                
            } else {
                logger.log(Level.WARNING, "ConstraintViolationException: message={0}", ex.getMessage());
                detailedMessage = "The ingest of (" + fileName + ") failed:Validation Error: ConstraintViolationException:" + ex.getMessage();
                
            }
            postExceptionCommonSteps(ExceptionUtils.getStackTrace(ex), summaryMsg, detailedMessage);
            return;
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Ingest of {0} failed with IOException", fileName);
            String detailedMessage = "The ingest of (" + fileName + ") failed:IOException:" + ex.getMessage();
            postExceptionCommonSteps(ExceptionUtils.getStackTrace(ex), summaryMsg, detailedMessage);
            return;
        } catch (XMLStreamException ex) {
            logger.log(Level.SEVERE, "Ingest of {0} failed with XMLStreamException", fileName);
            String detailedMessage = "The ingest of (" + fileName + ") failed:XMLStreamException:" + ex.getMessage();
            postExceptionCommonSteps(ExceptionUtils.getStackTrace(ex), summaryMsg, detailedMessage);
            return;
        } catch (ExportException ex) {
            logger.log(Level.SEVERE, "Ingest of {0} failed with ExportException", fileName);
            String detailedMessage = "The ingest of (" + fileName + ") failed:XMLStreamException:" + ex.getMessage();
            postExceptionCommonSteps(ExceptionUtils.getStackTrace(ex), summaryMsg, detailedMessage);
            return;
        } catch (EJBException ex) {
            logger.log(Level.SEVERE, "Ingest of {0} failed with EJBException", fileName);
            String detailedMessage = "The ingest of (" + fileName + ") failed:EJBException:" + ex.getMessage();
            postExceptionCommonSteps(ExceptionUtils.getStackTrace(ex), summaryMsg, detailedMessage);
            return;
        } catch (IngestException ex) {
            logger.log(Level.SEVERE, "Ingest of {0} failed with IngestException", fileName);
            String detailedMessage = "The ingest of (" + fileName + ") failed:IngestException:" + ex.getMessage();
            postExceptionCommonSteps(ExceptionUtils.getStackTrace(ex), summaryMsg, detailedMessage);
            return;
        }
        

        logger.log(Level.INFO, "=========== FileUploadView#execIngest: end ===========");
    }
    
    private void postExceptionCommonSteps(String exceptionMsg, String summaryMsg, String detailedMsg) {
        Faces.setSessionAttribute("currentMessageKey", "fileuploadIngestMsg");
        Faces.setSessionAttribute("fileuploadIngestMsg", exceptionMsg);
        Faces.getContext().addMessage("topMessage",
          new FacesMessage(FacesMessage.SEVERITY_ERROR,
            summaryMsg, detailedMsg));
        wipeOutIngestFailure();
        showMsgButtonEnabled = true;
        ingestButtonEnabled = false;
        publishButtonEnabled = false;
        uploadButtonEnabled = false;
    }
    
    
    public void showExceptionMessage(){
        Map<String, Object> options = new HashMap<>();
        options.put("resizable", true);
        options.put("draggable", true);
        options.put("modal", true);
        options.put("width", 800);
        options.put("height", 400);
        options.put("contentWidth", "100%");
        options.put("contentHeight", "100%");
        PrimeFaces.current().dialog().openDynamic("/ingestFailureMessage.xhtml", 
          options, null);
    }

    public String goHome() {
        logger.log(Level.INFO, "back to the home");
        return "/index.xhtml?faces-redirect=true";
    }

    public String goPublish() {
        logger.log(Level.INFO, "go to publish page");
        return "/ingest.xhtml?faces-redirect=true";
    }
    
    public String goSubmissionPage() {
        logger.log(Level.INFO, "go to submission page");
        setFileNameOnly(null);
        return "/submission.xhtml?faces-redirect=true";
    }
    
    public String goDestinationPage(){
        logger.log(Level.INFO, "go to destination page");
        return "/destination.xhtml?faces-redirect=true";
    }
    
    public void copyFile(String fileName, InputStream in) {
        try {

            // write the inputStream to a FileOutputStream
            OutputStream out = new FileOutputStream(new File(destination + fileName));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            in.close();
            out.flush();
            out.close();

            logger.log(Level.INFO, "New file created!");
        } catch (IOException e) {
            logger.log(Level.INFO, "IOException was thrown", e);
        }
    }

    boolean ingestButtonEnabled = false;

    public boolean isIngestButtonEnabled() {
        return ingestButtonEnabled;
    }

    public void setIngestButtonEnabled(boolean value) {
        ingestButtonEnabled = value;
    }

    boolean publishButtonEnabled = false;

    public boolean isPublishButtonEnabled() {
        return publishButtonEnabled;
    }

    public void setPublishButtonEnabled(boolean value) {
        this.publishButtonEnabled = value;
    }
    
    boolean isTrsaProfileReady = false;
    

    private List<Long> fileIdList= new ArrayList<>();

    public List<Long> getFileIdList() {
        return fileIdList;
    }

    public void setFileIdList(List<Long> fileIdList) {
        this.fileIdList = fileIdList;
    }
    
    private List<DataFile> ingestedDataFileList = new ArrayList<>();

    public List<DataFile> getIngestedDataFileList() {
        return ingestedDataFileList;
    }

    public void setIngestedDataFileList(List<DataFile> ingestedDataFileList) {
        this.ingestedDataFileList = ingestedDataFileList;
    }
    
    
    private DatasetVersion exportedDatasetVerion;

    public DatasetVersion getExportedDatasetVerion() {
        return exportedDatasetVerion;
    }

    public void setExportedDatasetVerion(DatasetVersion exportedDatasetVerion) {
        this.exportedDatasetVerion = exportedDatasetVerion;
    }
    
    private void wipeOutIngestFailure() {
        // To be implemented
    }
    
    boolean showMsgButtonEnabled=false;

    public boolean isShowMsgButtonEnabled() {
        return showMsgButtonEnabled;
    }

    public void setShowMsgButtonEnabled(boolean showMsgButtonEnabled) {
        this.showMsgButtonEnabled = showMsgButtonEnabled;
    }
    
    boolean uploadButtonEnabled=true;

    public boolean isUploadButtonEnabled() {
        return uploadButtonEnabled;
    }

    public void setUploadButtonEnabled(boolean uploadButtonEnabled) {
        this.uploadButtonEnabled = uploadButtonEnabled;
    }
    
    
    


}
