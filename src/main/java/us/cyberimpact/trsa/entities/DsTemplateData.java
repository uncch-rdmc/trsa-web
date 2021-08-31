/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author akios
 */
@Entity
@Table(name = "DS_TEMPLATE_DATA", schema = "PUBLIC")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DsTemplateData.findAll", query = "SELECT d FROM DsTemplateData d"),
    @NamedQuery(name = "DsTemplateData.findById", query = "SELECT d FROM DsTemplateData d WHERE d.id = :id"),
    @NamedQuery(name = "DsTemplateData.findByTitle", query = "SELECT d FROM DsTemplateData d WHERE d.title = :title"),
    @NamedQuery(name = "DsTemplateData.findByAffiliation", query = "SELECT d FROM DsTemplateData d WHERE d.affiliation = :affiliation"),
    @NamedQuery(name = "DsTemplateData.findByAuthor", query = "SELECT d FROM DsTemplateData d WHERE d.author = :author"),
    @NamedQuery(name = "DsTemplateData.findByDescription", query = "SELECT d FROM DsTemplateData d WHERE d.description = :description"),
    @NamedQuery(name = "DsTemplateData.findBySubject", query = "SELECT d FROM DsTemplateData d WHERE d.subject = :subject"),
    @NamedQuery(name = "DsTemplateData.findBySsnotetype", query = "SELECT d FROM DsTemplateData d WHERE d.ssnotetype = :ssnotetype"),
    @NamedQuery(name = "DsTemplateData.findBySsnotetext", query = "SELECT d FROM DsTemplateData d WHERE d.ssnotetext = :ssnotetext"),
    @NamedQuery(name = "DsTemplateData.findByUser", query = "SELECT d FROM DsTemplateData d WHERE d.user = :user")})
public class DsTemplateData implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false, length = 255)
    private String title;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false, length = 255)
    private String affiliation;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false, length = 255)
    private String author;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false, length = 255)
    private String description;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false, length = 255)
    private String subject;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false, length = 255)
    private String ssnotetype;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false, length = 255)
    private String ssnotetext;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false, length = 255, name="\"user\"")
    private String user;

    public DsTemplateData() {
    }

    public DsTemplateData(Long id) {
        this.id = id;
    }

    public DsTemplateData(Long id, String title, String affiliation, String author, String description, String subject, String ssnotetype, String ssnotetext, String user) {
        this.id = id;
        this.title = title;
        this.affiliation = affiliation;
        this.author = author;
        this.description = description;
        this.subject = subject;
        this.ssnotetype = ssnotetype;
        this.ssnotetext = ssnotetext;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSsnotetype() {
        return ssnotetype;
    }

    public void setSsnotetype(String ssnotetype) {
        this.ssnotetype = ssnotetype;
    }

    public String getSsnotetext() {
        return ssnotetext;
    }

    public void setSsnotetext(String ssnotetext) {
        this.ssnotetext = ssnotetext;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DsTemplateData)) {
            return false;
        }
        DsTemplateData other = (DsTemplateData) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DsTemplateData{" + "id=" + id + ", title=" + title + ", affiliation=" + affiliation + ", author=" + author + ", description=" + description + ", subject=" + subject + ", ssnotetype=" + ssnotetype + ", ssnotetext=" + ssnotetext + ", user=" + user + '}';
    }
    
}
