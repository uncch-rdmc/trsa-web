/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.entities;

import com.google.gson.annotations.Expose;
import edu.harvard.iq.dataverse.util.FileUtil;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author Akio Sone, Univ, of North Carolina at Chapel Hill, H.W. Odum Inst.
 */
@Entity
public class DataFile implements Serializable {

    private static final Logger logger = Logger.getLogger(DataFile.class.getName());

    public static final char INGEST_STATUS_NONE = 65;
    public static final char INGEST_STATUS_SCHEDULED = 66;
    public static final char INGEST_STATUS_INPROGRESS = 67;
    public static final char INGEST_STATUS_ERROR = 68;

    public static final Long ROOT_DATAFILE_ID_DEFAULT = (long) -1;

    public DataFile() {
        this.fileMetadatas = new ArrayList<>();
        initFileReplaceAttributes();
    }

    public DataFile(String contentType) {
        this.contentType = contentType;
        this.fileMetadatas = new ArrayList<>();
        initFileReplaceAttributes();
    }

    /**
     * All constructors should use this method to intitialize this file replace
     * attributes
     */
    private void initFileReplaceAttributes() {
        this.rootDataFileId = ROOT_DATAFILE_ID_DEFAULT;
        this.previousDataFileId = null;
    }
//    public DataFile(Long id, Long filesize, String contentType, List<DataTable> dataTables, List<FileMetadata> fileMetadatas, String fileSystemName) {
//        this.id = id;
//        this.filesize = filesize;
//        this.contentType = contentType;
//        this.dataTables = dataTables;
//        this.fileMetadatas = fileMetadatas;
//        this.fileSystemName = fileSystemName;
//    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Expose
    @Column(nullable = true)
    private Long filesize;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotBlank
    @Column(nullable = false)
    @Pattern(regexp = "^.*/.*$", message = "Content-Type must contain a slash")
    private String contentType;

    private char ingestStatus = INGEST_STATUS_NONE;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /*
     * start: FILE REPLACE ATTRIBUTES
     */
    // For the initial version of a file, this will be equivalent to the ID
    // Default is -1 until the intial id is generated
    @Expose
    @Column(nullable = false)
    private Long rootDataFileId;

    /**
     * @todo We should have consistency between "Id" vs "ID" for rootDataFileId
     * vs. previousDataFileId.
     */
    // null for initial version; subsequent versions will point to the previous file
    //
    @Expose
    @Column(nullable = true)
    private Long previousDataFileId;

    /*
     * endt: FILE REPLACE ATTRIBUTES
     */

    /**
     * Get property filesize, number of bytes
     *
     * @return value of property filesize.
     */
    public long getFilesize() {
        if (this.filesize == null) {
            // -1 means "unknown"
            return -1;
        }
        return this.filesize;
    }

    /**
     * Set property filesize in bytes
     *
     * Allow nulls, but not negative numbers.
     *
     * @param filesize new value of property filesize.
     */
    public void setFilesize(long filesize) {
        if (filesize < 0) {
            return;
        }
        this.filesize = filesize;
    }

    public void setIngestStatus(char ingestStatus) {
        this.ingestStatus = ingestStatus;
    }

    public boolean isIngestScheduled() {
        return (ingestStatus == INGEST_STATUS_SCHEDULED);
    }

    public boolean isIngestInProgress() {
        return ((ingestStatus == INGEST_STATUS_SCHEDULED)
                || (ingestStatus == INGEST_STATUS_INPROGRESS));
    }

    public boolean isIngestProblem() {
        return (ingestStatus == INGEST_STATUS_ERROR);
    }

    public void setIngestScheduled() {
        ingestStatus = INGEST_STATUS_SCHEDULED;
    }

    public void setIngestInProgress() {
        ingestStatus = INGEST_STATUS_INPROGRESS;
    }

    public void setIngestProblem() {
        ingestStatus = INGEST_STATUS_ERROR;
    }

    public void setIngestDone() {
        ingestStatus = INGEST_STATUS_NONE;
    }

    public int getIngestStatus() {
        return ingestStatus;
    }

    @OneToMany(mappedBy = "dataFile", cascade = {CascadeType.ALL})
    private List<DataTable> dataTables;

    public List<DataTable> getDataTables() {
        return dataTables;
    }

    public void setDataTables(List<DataTable> dataTables) {
        this.dataTables = dataTables;
    }

    public DataTable getDataTable() {
        if (getDataTables() != null && getDataTables().size() > 0) {
            return getDataTables().get(0);
        } else {
            return null;
        }
    }

    public void setDataTable(DataTable dt) {
        if (this.getDataTables() == null) {
            this.setDataTables(new ArrayList<>());
        } else {
            this.getDataTables().clear();
        }

        this.getDataTables().add(dt);
    }

    @OneToMany(mappedBy = "dataFile", cascade = {CascadeType.ALL})
    private List<FileMetadata> fileMetadatas;

    public List<FileMetadata> getFileMetadatas() {
        return fileMetadatas;
    }

    public void setFileMetadatas(List<FileMetadata> fileMetadatas) {
        this.fileMetadatas = fileMetadatas;
    }

    public FileMetadata getFileMetadata() {
        return getLatestFileMetadata();
    }

    private FileMetadata getLatestFileMetadata() {
        FileMetadata fmd = null;

        // for newly added or harvested, just return the one fmd
//        if (fileMetadatas.size() == 1) {
        return fileMetadatas.get(0);
//        }
//        
//        for (FileMetadata fileMetadata : fileMetadatas) {
//            // if it finds a draft, return it
//            if (fileMetadata.getDatasetVersion().getVersionState().equals(VersionState.DRAFT)) {
//                return fileMetadata;
//            }            
//            
//            // otherwise return the one with the latest version number
//            if (fmd == null || fileMetadata.getDatasetVersion().getVersionNumber().compareTo( fmd.getDatasetVersion().getVersionNumber() ) > 0 ) {
//                fmd = fileMetadata;
//            } else if ((fileMetadata.getDatasetVersion().getVersionNumber().compareTo( fmd.getDatasetVersion().getVersionNumber())==0 )&& 
//                   ( fileMetadata.getDatasetVersion().getMinorVersionNumber().compareTo( fmd.getDatasetVersion().getMinorVersionNumber()) > 0 )   ) {
//                fmd = fileMetadata;
//        }
//        }
//        return fmd;
    }

    
    private boolean restricted;
    
    public boolean isRestricted() {
        return restricted;
    }

    
    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }
    public boolean isTabularData() {
        return getDataTables() != null && getDataTables().size() > 0;
    }
    
    @Column(nullable = false)
    private boolean notaryServiceBound=false;

    /**
     * Get the value of notaryServiceBound
     *
     * @return the value of notaryServiceBound
     */
    public boolean isNotaryServiceBound() {
        return notaryServiceBound;
    }

    /**
     * Set the value of notaryServiceBound
     *
     * @param notaryServiceBound new value of notaryServiceBound
     */
    public void setNotaryServiceBound(boolean notaryServiceBound) {
        this.notaryServiceBound = notaryServiceBound;
    }

    
    

//    @Column(nullable = true)
//    private String fileSystemName;

//    public Path getFileSystemLocation() {
        // TEMPORARY HACK!
        // (only used in batch ingest testing -- L.A. 4.0 beta)
//        if (this.fileSystemName != null && this.fileSystemName.startsWith("/")) {
//        return Paths.get(this.fileSystemName);
//        }

//        Path studyDirectoryPath = this.getOwner().getFileSystemDirectory();
//        if (studyDirectoryPath == null) {
//            return null;
//        }
//        String studyDirectory = studyDirectoryPath.toString();
// 
//        return Paths.get(studyDirectory, this.fileSystemName);
//    }

//    public String getFileSystemName() {
//        return this.fileSystemName;
//    }

    @ManyToOne
    private Dataset dataset;

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    
    // JsonParser
    
    public Dataset getOwner() {
        return getDataset();
    }

    public void setOwner(Dataset dataset) {
        setDataset(dataset);
    }
    
    public enum ChecksumType {

        MD5("MD5"),
        SHA1("SHA-1");

        private final String text;

        private ChecksumType(final String text) {
            this.text = text;
        }

        public static ChecksumType fromString(String text) {
            if (text != null) {
                for (ChecksumType checksumType : ChecksumType.values()) {
                    if (text.equals(checksumType.text)) {
                        return checksumType;
                    }
                }
            }
            throw new IllegalArgumentException("ChecksumType must be one of these values: " + Arrays.asList(ChecksumType.values()) + ".");
        }

        @Override
        public String toString() {
            return text;
        }
    }

    //@Expose
    @Column(nullable = false)
//    @Enumerated(EnumType.STRING)
    private ChecksumType checksumType;
//    private String checksumType;
    /**
     * Examples include "f622da34d54bdc8ee541d6916ac1c16f" as an MD5 value or
     * "3a484dfdb1b429c2e15eb2a735f1f5e4d5b04ec6" as a SHA-1 value"
     */
    //@Expose
    @Column(nullable = false)
    private String checksumValue;

//    public String getChecksumType() {
//        return checksumType;
//    }
//
//    public void setChecksumType(String checksumType) {
//        this.checksumType = checksumType;
//    }
    public ChecksumType getChecksumType() {
        return checksumType;
    }

    public void setChecksumType(ChecksumType checksumType) {
        this.checksumType = checksumType;
    }

    public String getChecksumValue() {
        return this.checksumValue;
    }

    public void setChecksumValue(String checksumValue) {
        this.checksumValue = checksumValue;
    }

    public String getDescription() {
        FileMetadata fmd = getLatestFileMetadata();

        if (fmd == null) {
            return null;
        }
        return fmd.getDescription();
    }

    public void setDescription(String description) {
        FileMetadata fmd = getLatestFileMetadata();

        if (fmd != null) {
            fmd.setDescription(description);
        }
    }

    @Column
    private String storageIdentifier;

    public String getStorageIdentifier() {
        return storageIdentifier;
    }

    public void setStorageIdentifier(String storageIdentifier) {
        this.storageIdentifier = storageIdentifier;
    }

    public String getOriginalFileFormat() {
        if (isTabularData()) {
            DataTable dataTable = getDataTable();
            if (dataTable != null) {
                return dataTable.getOriginalFileFormat();
            }
        }
        return null;
    }

    /*
     * A user-friendly version of the "original format":
     */
    public String getOriginalFormatLabel() {
        return FileUtil.getUserFriendlyOriginalType(this);
    }

    public String getUnf() {
        if (this.isTabularData()) {
            // (isTabularData() method above verifies that that this file 
            // has a datDatable associated with it, so the line below is 
            // safe, in terms of a NullPointerException: 
            return this.getDataTable().getUnf();
        }
        return null;
    }

    /**
     * Set rootDataFileId
     *
     * @param rootDataFileId
     */
    public void setRootDataFileId(Long rootDataFileId) {
        this.rootDataFileId = rootDataFileId;
    }

    /**
     * Get for rootDataFileId
     *
     * @return Long
     */
    public Long getRootDataFileId() {
        return this.rootDataFileId;
    }

    @Column(nullable = false)
    private Timestamp createDate;

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    @Column(nullable = false)
    private Timestamp modificationTime;

    public Timestamp getModificationTime() {
        return modificationTime;
    }

    /**
     * modificationTime is used for comparison with indexTime so we know if the
     * Solr index is stale.
     *
     * @param modificationTime
     */
    public void setModificationTime(Timestamp modificationTime) {
        this.modificationTime = modificationTime;
    }
    
    
    
}
