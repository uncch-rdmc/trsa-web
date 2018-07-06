/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.entities;

import edu.harvard.iq.dataverse.util.StringUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

/**
 *
 * @author Akio Sone, Univ, of North Carolina at Chapel Hill, H.W. Odum Inst.
 */
@Entity
@Table(indexes = {@Index(columnList="dataset_id")},
        uniqueConstraints = @UniqueConstraint(columnNames = {"dataset_id,versionnumber,minorversionnumber"}))
public class DatasetVersion implements Serializable{
    private static final Logger logger = Logger.getLogger(DatasetVersion.class.getName());

    public DatasetVersion() {
    }
    
    public enum VersionState {
        DRAFT, RELEASED, ARCHIVED, DEACCESSIONED
    };
    
    public enum License {
        NONE, CC0
    }
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    
    
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @OneToMany(mappedBy = "datasetVersion", cascade = {CascadeType.ALL})    
    private List<FileMetadata> fileMetadatas = new ArrayList();    

    public List<FileMetadata> getFileMetadatas() {
        return fileMetadatas;
    }
    
    public List<FileMetadata> getFileMetadatasSorted() {
        Collections.sort(fileMetadatas, FileMetadata.compareByLabel);
        return fileMetadatas;
    }

    public void setFileMetadatas(List<FileMetadata> fileMetadatas) {
        this.fileMetadatas = fileMetadatas;
    }    
    
    @ManyToOne
    private Dataset dataset;
    
    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }
    
    /**
     * A longer, human-friendlier name. Punctuation allowed.
     */
    @Column(name = "title", columnDefinition = "TEXT")
    private String title;
    
    
//    public String getTitle() {
//        String retVal = "";
//        for (DatasetField dsfv : this.getDatasetFields()) {
//            if (dsfv.getDatasetFieldType().getName().equals(DatasetFieldConstant.title)) {
//                retVal = dsfv.getDisplayValue();
//            }
//        }
//        return retVal;
//    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isLatestVersion() {
        return this.equals(this.getDataset().getLatestVersion());
    }    
    
    public boolean isWorkingCopy() {
        return versionState.equals(VersionState.DRAFT);
    }
    @Enumerated(EnumType.STRING)
    private VersionState versionState;    
    
    public VersionState getVersionState() {
        return versionState;
    }

    public void setVersionState(VersionState versionState) {
        this.versionState = versionState;
    }
    
    
    @Version
    private Long version;
    
    
    /**
     * This is JPA's optimistic locking mechanism, and has no semantic meaning in the DV object model.
     * @return the object db version
     */
    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
    }
    
    
    
    
    public String getCitation() {
        return getCitation(false);
    }

    public String getCitation(boolean html) {
        return new DataCitation(this).toString(html);
    }
    
    private Long versionNumber;
        
    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }
    
    
    private Long minorVersionNumber=0L;
    
    public Long getMinorVersionNumber() {
        return minorVersionNumber;
    }
    
    public void setMinorVersionNumber(Long minorVersionNumber) {
        this.minorVersionNumber = minorVersionNumber;
    }
    public String getProductionDate() {
        //todo get "Production Date" from datasetfieldvalue table
        return "Production Date";
    }
    
    
    private String UNF;

    
    
    public String getUNF() {
        return UNF;
    }

    public void setUNF(String UNF) {
        this.UNF = UNF;
    }
    
    
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column( nullable=false )
    private Date createTime;
    
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column( nullable=false )
    private Date lastUpdateTime;
    
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        if (createTime == null) {
            createTime = lastUpdateTime;
        }
        this.lastUpdateTime = lastUpdateTime;
    }
    
    
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date releaseTime;
    

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }
    
    
    
    public String getRootDataverseNameforCitation(){
                    //Get root dataverse name for Citation
//        Dataverse root = this.getDataset().getOwner();
//        while (root.getOwner() != null) {
//            root = root.getOwner();
//        }
//        String rootDataverseName = root.getName();
//        if (!StringUtil.isEmpty(rootDataverseName)) {
//            return rootDataverseName;
//        } else {
//            return "";
//        }
        // temp fix ; ultimately externally given
        return "UNC Dataverse";
    }
    
    
    
    public boolean isReleased() {
        return versionState.equals(VersionState.RELEASED);
    }

    public boolean isDraft() {
        return versionState.equals(VersionState.DRAFT);
    } 
    
    public boolean isDeaccessioned() {
        return versionState.equals(VersionState.DEACCESSIONED);
    }
    
    public boolean isArchived() {
        return versionState.equals(VersionState.ARCHIVED);
    }    
    
    public String getAuthorsStr() {
        return getAuthorsStr(true);
    }

    public String getAuthorsStr(boolean affiliation) {
        String str = "";
        for (DatasetAuthor sa : getDatasetAuthors()) {
            if (sa.getName() == null) {
                break;
            }
            if (str.trim().length() > 1) {
                str += "; ";
            }
            str += sa.getName();
            if (affiliation) {
                if (sa.getAffiliation() != null) {
                    if (!StringUtil.isEmpty(sa.getAffiliation())) {
                        str += " (" + sa.getAffiliation() + ")";
                    }
                }
            }
        }
        return str;
    }

    public List<DatasetAuthor> getDatasetAuthors() {
        
        // injected from a setting files?
        
        
        
        //TODO get "List of Authors" from datasetfieldvalue table
        List <DatasetAuthor> retList = new ArrayList<>();
//        for (DatasetField dsf : this.getDatasetFields()) {
//            Boolean addAuthor = true;
//            if (dsf.getDatasetFieldType().getName().equals(DatasetFieldConstant.author)) {
//                for (DatasetFieldCompoundValue authorValue : dsf.getDatasetFieldCompoundValues()) {                   
//                    DatasetAuthor datasetAuthor = new DatasetAuthor();
//                    for (DatasetField subField : authorValue.getChildDatasetFields()) {
//                        if (subField.getDatasetFieldType().getName().equals(DatasetFieldConstant.authorName)) {
//                            if (subField.isEmptyForDisplay()) {
//                                addAuthor = false;
//                            }
//                            datasetAuthor.setName(subField);
//                        }
//                        if (subField.getDatasetFieldType().getName().equals(DatasetFieldConstant.authorAffiliation)) {
//                            datasetAuthor.setAffiliation(subField);
//                        }
//                        if (subField.getDatasetFieldType().getName().equals(DatasetFieldConstant.authorIdType)){
//                             datasetAuthor.setIdType(subField.getDisplayValue());
//                        }
//                        if (subField.getDatasetFieldType().getName().equals(DatasetFieldConstant.authorIdValue)){
//                            datasetAuthor.setIdValue(subField.getDisplayValue());
//                        }
//                    }
//                    if (addAuthor) {                       
//                        retList.add(datasetAuthor);
//                    }
//                }
//            }
//        }
        return retList;
    }
    
    

    public String getDatasetProducersString(){
        String retVal = "";
//        for (DatasetField dsf : this.getDatasetFields()) {
//            if (dsf.getDatasetFieldType().getName().equals(DatasetFieldConstant.producer)) {
//                for (DatasetFieldCompoundValue authorValue : dsf.getDatasetFieldCompoundValues()) {
//                    for (DatasetField subField : authorValue.getChildDatasetFields()) {
//                        if (subField.getDatasetFieldType().getName().equals(DatasetFieldConstant.producerName)) {
//                            if (retVal.isEmpty()){
//                                retVal = subField.getDisplayValue();
//                            } else {
//                                retVal += ", " +  subField.getDisplayValue();
//                            }                           
//                        }
//                    }
//                }
//            }
//        }
        return retVal;
    }
    
//    public List<DatasetField> getDatasetFields() {
//        return datasetFields;
//    }
}
