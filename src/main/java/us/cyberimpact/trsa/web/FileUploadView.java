package us.cyberimpact.trsa.web;

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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.io.FilenameUtils;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean(name = "fileUploadView")
@SessionScoped
public class FileUploadView implements Serializable {

    private static final Logger logger = Logger.getLogger(FileUploadView.class.getName());

    @EJB
    IngestService ingestService;

    @EJB
    DatasetVersionFacade datasetVersionFcd;

    private UploadedFile file;
    private String destination = "/tmp/";
    private String fileName;

    @PostConstruct
    public void init() {

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
        file = event.getFile();
        if (file != null) {
            FacesMessage message = new FacesMessage("Succesful",
                    file.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            byte[] bytes = null;
            try {
//                copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
                bytes = file.getContents();
                String filename = FilenameUtils.getName(file.getFileName());
                fileName = destination + filename;
                logger.log(Level.INFO, "filename is set to={0}", filename);
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
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

        setIngestButtonEnabled(true);
//        return "/index.xhtml";
    }

    public void execIngest() {
        logger.log(Level.INFO, "fileName={0}", fileName);
        datasetIdentifier = IngestService.generateTempDatasetIdentifier(6);
        logger.log(Level.INFO, "datasetIdentifier={0}", datasetIdentifier);

        String contentType = "application/zip";
        logger.log(Level.INFO, "contentType={0}", contentType);
        try {

            ingestService.run(fileName, contentType, datasetIdentifier);

            logger.log(Level.INFO, "dumping metadata files");
            List<DatasetVersion> versions = datasetVersionFcd.findAll();
            if (versions != null && !versions.isEmpty()) {
                ingestService.exportDataset(versions.get(0));
            } else {
                logger.log(Level.INFO, "DatasetVersion is null/empty");
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IOException", ex);
        } catch (XMLStreamException ex) {
            logger.log(Level.SEVERE, "XMLStreamException", ex);
        } catch (ExportException ex) {
            logger.log(Level.SEVERE, "ExportException", ex);
        }
        String Message = fileName + " has been successfully ingested and new dataset (Id=" + datasetIdentifier + ") was created";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", Message));
        publishButtonEnabled = true;
        ingestButtonEnabled = false;
        //return "/ingest.xhtml";
    }

    public String goHome() {
        logger.log(Level.INFO, "back to the home");
        return "/new_index.xhtml";
    }

    public String goPublish() {
        logger.log(Level.INFO, "go to publish page");
        return "/new_ingest.xhtml";
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

}
