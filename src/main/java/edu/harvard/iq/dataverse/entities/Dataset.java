package edu.harvard.iq.dataverse.entities;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author Akio Sone, Univ, of North Carolina at Chapel Hill, H.W. Odum Inst.
 */

@Entity
public class Dataset implements Serializable {
    
    public Dataset() {
        DatasetVersion datasetVersion = new DatasetVersion();
        datasetVersion.setDataset(this);
        datasetVersion.setVersionState(DatasetVersion.VersionState.DRAFT);
        datasetVersion.setFileMetadatas(new ArrayList<>());
        datasetVersion.setVersionNumber((long) 1);
        datasetVersion.setMinorVersionNumber((long) 0);
        versions.add(datasetVersion);
    }
    
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    
    
    
    @OneToMany(mappedBy = "dataset", cascade = CascadeType.ALL)
    @OrderBy("id")
    private List<DataFile> files = new ArrayList<>();
    
    public List<DataFile> getFiles() {
        return files;
    }

    public void setFiles(List<DataFile> files) {
        this.files = files;
    }
    

    
    @NotBlank(message = "Please enter an identifier for your dataset.")
    @Column(nullable = false)
    private String identifier;
    
    
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    
    @OneToMany(mappedBy = "dataset", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<DatasetVersion> versions = new ArrayList<>();
    
    
    public DatasetVersion getLatestVersion() {
        return getVersions().get(0);
    }

    public DatasetVersion getLatestVersionForCopy() {
        for (DatasetVersion testDsv : getVersions()) {
            if (testDsv.isReleased() || testDsv.isArchived()) {
                return testDsv;
            }
        }
        return getVersions().get(0);
    }

    public List<DatasetVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<DatasetVersion> versions) {
        this.versions = versions;
    }
    
    /**
     * The "edit version" is the most recent *draft* of a dataset, and if the
     * latest version of a dataset is published, a new draft will be created.
     * 
     * @return The edit version {@code this}.
     */
    public DatasetVersion getEditVersion() {
        return getEditVersion(null);
    }
    
    public DatasetVersion getEditVersion(String template) {
        DatasetVersion latestVersion = this.getLatestVersion();
//        if (!latestVersion.isWorkingCopy() || template != null) {        
        if (!latestVersion.isWorkingCopy()) {
            // if the latest version is released or archived, create a new version for editing
            return createNewDatasetVersion(template);
        } else {
            // else, edit existing working copy
            return latestVersion;
        }
    }
    
    
    private DatasetVersion createNewDatasetVersion(String template) {
        DatasetVersion dsv = new DatasetVersion();
        dsv.setVersionState(DatasetVersion.VersionState.DRAFT);
        dsv.setFileMetadatas(new ArrayList<>());
        DatasetVersion latestVersion;

        //if the latest version has values get them copied over
        if (template == null) {
//            dsv.updateDefaultValuesFromTemplate(template);
//            setVersions(new ArrayList());
//        } else {
            latestVersion = getLatestVersionForCopy();
            
            if (latestVersion.getUNF() != null){
                dsv.setUNF(latestVersion.getUNF());
            }
            
//            if (latestVersion.getDatasetFields() != null && !latestVersion.getDatasetFields().isEmpty()) {
//                dsv.setDatasetFields(dsv.copyDatasetFields(latestVersion.getDatasetFields()));
//            }
            
//            if (latestVersion.getTermsOfUseAndAccess()!= null){
//                dsv.setTermsOfUseAndAccess(latestVersion.getTermsOfUseAndAccess().copyTermsOfUseAndAccess());
//            } else {
//                TermsOfUseAndAccess terms = new TermsOfUseAndAccess();
//                terms.setDatasetVersion(dsv);
//                terms.setLicense(TermsOfUseAndAccess.License.CC0);
//                dsv.setTermsOfUseAndAccess(terms);
//            }

            for (FileMetadata fm : latestVersion.getFileMetadatas()) {
                FileMetadata newFm = new FileMetadata();
                // TODO: 
                // the "category" will be removed, shortly. 
                // (replaced by multiple, tag-like categories of 
                // type DataFileCategory) -- L.A. beta 10
                //newFm.setCategory(fm.getCategory());
                // yep, these are the new categories:
//                newFm.setCategories(fm.getCategories());
                newFm.setDescription(fm.getDescription());
                newFm.setLabel(fm.getLabel());
                newFm.setDirectoryLabel(fm.getDirectoryLabel());
                newFm.setRestricted(fm.isRestricted());
                newFm.setDataFile(fm.getDataFile());
                newFm.setDatasetVersion(dsv);
                dsv.getFileMetadatas().add(newFm);
            }
        }

        // I'm adding the version to the list so it will be persisted when
        // the study object is persisted.
        if (template == null) {
            getVersions().add(0, dsv);
        } else {
            this.setVersions(new ArrayList<>());
            getVersions().add(0, dsv);
        }

        dsv.setDataset(this);
        return dsv;
    }
    
    
    
    
    
    
    
    public String getCitation() {
        return getCitation(false, getLatestVersion());
    }

    public String getCitation(DatasetVersion version) {
        return version.getCitation();
    }
    

    public String getCitation(boolean isOnlineVersion, DatasetVersion version) {
        return version.getCitation(isOnlineVersion);
    }
    
    private String protocol;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    private String authority;
    
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
    
    private String doiSeparator;
    
    public String getDoiSeparator() {
        return doiSeparator;
    }

    public void setDoiSeparator(String doiSeparator) {
        this.doiSeparator = doiSeparator;
    }
    
    
    public String getPersistentURL() {
        return new GlobalId(this).toURL().toString();
    }
    
    
    public String getPublicationDateFormattedYYYYMMDD() {
        if (getPublicationDate() != null){
            return new SimpleDateFormat("yyyy-MM-dd").format(getPublicationDate()); 
        }
        return null;
    }
    
    private Timestamp publicationDate;
    
    
    public Timestamp getPublicationDate() {
        return publicationDate;
    }
    
    public void setPublicationDate(Timestamp publicationDate) {
        this.publicationDate = publicationDate;
    }
    
    @Column( nullable = false )
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
     * @param modificationTime
     */
    public void setModificationTime(Timestamp modificationTime) {
        this.modificationTime = modificationTime;
    }
    
    public Path getFileSystemDirectory() {
        Path studyDir = null;

        String filesRootDirectory = System.getProperty("dataverse.files.directory");
        if (filesRootDirectory == null || filesRootDirectory.equals("")) {
            filesRootDirectory = "/tmp/files";
        }

        if (this.getAuthority() != null && this.getIdentifier() != null) {
            studyDir = Paths.get(filesRootDirectory, this.getAuthority(), this.getIdentifier());
        }

        return studyDir;
    }
}
