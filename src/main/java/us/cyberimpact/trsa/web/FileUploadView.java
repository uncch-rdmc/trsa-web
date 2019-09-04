package us.cyberimpact.trsa.web;

import edu.harvard.iq.dataverse.entities.DataFile;
import us.cyberimpact.trsa.entities.TrsaProfileFacade;
import edu.harvard.iq.dataverse.entities.DatasetVersion;
import edu.harvard.iq.dataverse.entities.DatasetVersionFacade;
import edu.harvard.iq.dataverse.export.ExportException;
import edu.harvard.iq.dataverse.ingest.IngestService;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.annotation.ManagedProperty;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import us.cyberimpact.trsa.entities.DsTemplateData;
import us.cyberimpact.trsa.entities.TrsaProfile;
import us.cyberimpact.trsa.entities.HostInfo;
import us.cyberimpact.trsa.entities.HostInfoFacade;

@Named("fileUploadView")
@SessionScoped
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
    
    
    @Inject
    DestinationSelectionView destinationSelectionView; 
    
    
//    List<HostInfo> hostInfoTable = new ArrayList<>();
    

    private String selectedDatasetId;
    
    @Inject
    DsTemplateSelectionView dsTemplateSelectionView;
    
    @Inject
    HomePageView homePageView;
    
    
    private UploadedFile file;
    private String destination = "/tmp/";
    private String fileName;
    
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

    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "=========== FileUploadView#init: start ===========");
        setFileNameOnly(null);
        logger.log(Level.INFO, "requestType={0}", homePageView.getSelectedRequestType());
        selectedRequestType=homePageView.getSelectedRequestType();
        
        logger.log(Level.INFO, "selectedRequestType={0}", selectedRequestType);

        logger.log(Level.INFO, "destinationSelectionView.getSelectedDatasetId()={0}", destinationSelectionView.getSelectedDatasetId());
        selectedDatasetId=destinationSelectionView.getSelectedDatasetId();
        
        logger.log(Level.INFO, "selectedDatasetId={0}", selectedDatasetId);
        logger.log(Level.INFO, "isMetadataOnly={0}", homePageView.isMetadataOnly());
        
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
//            
//            logger.log(Level.INFO, "url={0}",trsaProfileTable.get(0).getDataverseurl()); 
//            logger.log(Level.INFO, "api-token={0}",trsaProfileTable.get(0).getApitoken());
            
            
        }
        
        
//        hostInfoTable = hostInfoFacade.findAll();
//        logger.log(Level.INFO, "FileUploadView:hostInfoTable:howMany={0}", hostInfoTable.size());
//        if (hostInfoTable.isEmpty()){
//            logger.log(Level.INFO, "hostInfoTable is empty");
//        } else {
//            logger.log(Level.INFO, "FileUploadView:hostInfoTable exists and not empty");
//        }

        selectedDsTemplateData= dsTemplateSelectionView.getSelectedDsTemplateData();
        logger.log(Level.INFO, "selectedDsTemplateData={0}", selectedDsTemplateData);
        logger.log(Level.INFO, "=========== FileUploadView#init: end ===========");
    }

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
        publishButtonEnabled = true;
    }

    public void upload(FileUploadEvent event) {
        logger.log(Level.INFO, "=========== FileUploadView#upload: start ===========");
        file = event.getFile();
        String filePath = "";
        if (file != null) {
            FacesMessage message = new FacesMessage("Succesful",
                    file.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            byte[] bytes = null;
            try {
//                copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
                bytes = file.getContents();
                fileNameOnly = FilenameUtils.getName(file.getFileName());
//                mimeType = FilenameUtils.getExtension(file.getFileName());
                fileName = destination + fileNameOnly;
                logger.log(Level.INFO, "fileName is set to={0}", fileName);
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(new File(fileName)));
                stream.write(bytes);
                stream.close();

            } catch (IOException e) {
                logger.log(Level.INFO, "IOException was thrown", e);
            }

//      file = event.getFile();
//      byte[] contents = file.getContents();
//      fileContent = new String(contents);
//      fileName = file.getFileName();
//            
            FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO, "Your file (File Name " + file.getFileName() + " with size " + file.getSize() + ")  Uploaded Successfully", ""));
        }

//        logger.log(Level.INFO, "FileUploadView:upload():TrsaProfileTable={0}", trsaProfileTable);
//        logger.log(Level.INFO, "upload():url={0}", trsaProfileTable.get(0).getDataverseurl());
//        logger.log(Level.INFO, "upload():api-token={0}", trsaProfileTable.get(0).getApitoken());
//        logger.log(Level.INFO, "upload():isTrsaProfileReady={0}", isTrsaProfileReady);
        setIngestButtonEnabled(true);
        logger.log(Level.INFO, "=========== FileUploadView#upload: end ===========");
//        return "/index.xhtml";
    }

    public void execIngest() {
        logger.log(Level.INFO, "=========== FileUploadView#execIngest: start ===========");
        
        FacesContext context = FacesContext.getCurrentInstance();
        logger.log(Level.INFO, "fileName={0}", fileName);
        // 2019-06-12 update: for metadata-uploading cases, use the pre-selected dataset Id
//        datasetIdentifier = IngestService.generateTempDatasetIdentifier(6);

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
        try {

            ingestedDataFileList = ingestService.run(fileName, mimeType, datasetIdentifier);
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
            
            
            
            
        } catch (ConstraintViolationException ex){
            logger.log(Level.SEVERE, "ConstraintViolationException occurred", ex);
            if (ex instanceof ConstraintViolationException) {
                StringBuilder sb = new StringBuilder();
                ConstraintViolationException cve = (ConstraintViolationException) ex;
                for (ConstraintViolation cv : cve.getConstraintViolations()) {
                    sb.append(cv.toString());
                    logger.log(Level.SEVERE, "CONSTRAINT VIOLOATION : {0}", cv.toString());
                }
        context.addMessage("topMessage", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", sb.toString()));
            }

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IOException", ex);
        } catch (XMLStreamException ex) {
            logger.log(Level.SEVERE, "XMLStreamException", ex);
        } catch (ExportException ex) {
            logger.log(Level.SEVERE, "ExportException", ex);
        }
        

        String message = fileName + " has been successfully ingested and new dataset (Id=" + datasetIdentifier + ") was created";
        context.addMessage("topMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, "info", message));
        
/*
        logger.log(Level.INFO, "execIngest():isTrsaProfileReady={0}", isTrsaProfileReady);
        logger.log(Level.INFO, "FileUploadView:execIngest():TrsaProfileTable={0}", trsaProfileTable);
        logger.log(Level.INFO, "execIngest():url={0}",trsaProfileTable.get(0).getDataverseurl());            
        logger.log(Level.INFO, "execIngest():api-token={0}", trsaProfileTable.get(0).getApitoken());
        if (isTrsaProfileReady){
            logger.log(Level.INFO, "TRSA Profile is available");
        } else {
            logger.log(Level.INFO, "TRSA Profile is not available");
            publishButtonEnabled = true;
            String messageWarning = "TRSA Profile is not set; please add a profile";
            context.addMessage("topMessage", new FacesMessage(FacesMessage.SEVERITY_WARN, "warn", messageWarning));
            
            publishButtonEnabled = false;
            try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/trsaProfile/List.xhtml/");
            } catch (IOException ex){
                logger.log(Level.INFO, "redirection error", ex);
            }
            
        }
*/
        ingestButtonEnabled = false;
        //return "/ingest.xhtml";
        logger.log(Level.INFO, "=========== FileUploadView#execIngest: end ===========");
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

//    List<DatasetVersion> getIngestedDatasetVersion(){
//        return em.createNamedQuery("findAll", DatasetVersion.class).getResultList();
//    }
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
    
    
}
