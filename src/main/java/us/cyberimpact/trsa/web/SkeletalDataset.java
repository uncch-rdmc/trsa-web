/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.web;

import java.util.Objects;
import java.util.logging.Logger;

/**
 *
 * @author asone
 */
public class SkeletalDataset {
    private static final Logger logger = Logger.getLogger(SkeletalDataset.class.getName());

    public SkeletalDataset(String datasetTitle, String authorName, 
            String authorAffiliation, String datasetContactEmail, 
            String dsDescriptionValue, String subject) {
        this.datasetTitle = datasetTitle;
        this.authorName = authorName;
        this.authorAffiliation = authorAffiliation;
        this.datasetContactEmail = datasetContactEmail;
        this.dsDescriptionValue = dsDescriptionValue;
        this.subject = subject;
    }

    public SkeletalDataset() {
    }
    
    
    private String datasetTitle;
    
    private String authorName;
    
    private String authorAffiliation;
    
    private String datasetContactEmail;
    
    private String dsDescriptionValue;
    
    private String subject;

    public String getDatasetTitle() {
        return datasetTitle;
    }

    public void setDatasetTitle(String datasetTitle) {
        this.datasetTitle = datasetTitle;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAffiliation() {
        return authorAffiliation;
    }

    public void setAuthorAffiliation(String authorAffiliation) {
        this.authorAffiliation = authorAffiliation;
    }

    public String getDatasetContactEmail() {
        return datasetContactEmail;
    }

    public void setDatasetContactEmail(String datasetContactEmail) {
        this.datasetContactEmail = datasetContactEmail;
    }

    public String getDsDescriptionValue() {
        return dsDescriptionValue;
    }

    public void setDsDescriptionValue(String dsDescriptionValue) {
        this.dsDescriptionValue = dsDescriptionValue;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.datasetTitle);
        hash = 59 * hash + Objects.hashCode(this.authorName);
        hash = 59 * hash + Objects.hashCode(this.authorAffiliation);
        hash = 59 * hash + Objects.hashCode(this.datasetContactEmail);
        hash = 59 * hash + Objects.hashCode(this.dsDescriptionValue);
        hash = 59 * hash + Objects.hashCode(this.subject);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SkeletalDataset other = (SkeletalDataset) obj;
        if (!Objects.equals(this.datasetTitle, other.datasetTitle)) {
            return false;
        }
        if (!Objects.equals(this.authorName, other.authorName)) {
            return false;
        }
        if (!Objects.equals(this.authorAffiliation, other.authorAffiliation)) {
            return false;
        }
        if (!Objects.equals(this.datasetContactEmail, other.datasetContactEmail)) {
            return false;
        }
        if (!Objects.equals(this.dsDescriptionValue, other.dsDescriptionValue)) {
            return false;
        }
        if (!Objects.equals(this.subject, other.subject)) {
            return false;
        }
        return true;
    }

    
    
    
    @Override
    public String toString() {
        return "SkeletalDataset{" + "datasetTitle=" + datasetTitle + ", authorName=" + authorName + ", authorAffiliation=" + authorAffiliation + ", datasetContactEmail=" + datasetContactEmail + ", dsDescriptionValue=" + dsDescriptionValue + ", subject=" + subject + '}';
    }
    
    
}
