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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author asone
 */
@Entity
@Table(name = "HOST_INFO", catalog = "TRSA", schema = "PUBLIC", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ID"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HostInfo.findAll", query = "SELECT h FROM HostInfo h"),
    @NamedQuery(name = "HostInfo.findById", query = "SELECT h FROM HostInfo h WHERE h.id = :id"),
    @NamedQuery(name = "HostInfo.findByHosturl", query = "SELECT h FROM HostInfo h WHERE h.hosturl = :hosturl"),
    @NamedQuery(name = "HostInfo.findByApitoken", query = "SELECT h FROM HostInfo h WHERE h.apitoken = :apitoken"),
    @NamedQuery(name = "HostInfo.findByDataverseid", query = "SELECT h FROM HostInfo h WHERE h.dataverseid = :dataverseid"),
    @NamedQuery(name = "HostInfo.findByDataversealias", query = "SELECT h FROM HostInfo h WHERE h.dataversealias = :dataversealias"),
    @NamedQuery(name = "HostInfo.findByDatasetid", query = "SELECT h FROM HostInfo h WHERE h.datasetid = :datasetid"),
    @NamedQuery(name = "HostInfo.findByDataversetitle", query = "SELECT h FROM HostInfo h WHERE h.dataversetitle = :dataversetitle")})
public class HostInfo implements Serializable {

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
    private String hosturl;
    
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false, length = 255)
    private String apitoken;
    
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private long dataverseid;
    
    
    @Size(max = 255)
    @Column(length = 255)
    private String dataversealias;
    
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private long datasetid;
    
    @Size(max = 255)
    @Column(length = 255)
    private String dataversetitle;

    public HostInfo() {
    }

    public HostInfo(Long id) {
        this.id = id;
    }

    public HostInfo(Long id, String hosturl, String apitoken, long dataverseid, long datasetid) {
        this.id = id;
        this.hosturl = hosturl;
        this.apitoken = apitoken;
        this.dataverseid = dataverseid;
        this.datasetid = datasetid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHosturl() {
        return hosturl;
    }

    public void setHosturl(String hosturl) {
        this.hosturl = hosturl;
    }

    public String getApitoken() {
        return apitoken;
    }

    public void setApitoken(String apitoken) {
        this.apitoken = apitoken;
    }

    public long getDataverseid() {
        return dataverseid;
    }

    public void setDataverseid(long dataverseid) {
        this.dataverseid = dataverseid;
    }

    public String getDataversealias() {
        return dataversealias;
    }

    public void setDataversealias(String dataversealias) {
        this.dataversealias = dataversealias;
    }

    public long getDatasetid() {
        return datasetid;
    }

    public void setDatasetid(long datasetid) {
        this.datasetid = datasetid;
    }

    public String getDataversetitle() {
        return dataversetitle;
    }

    public void setDataversetitle(String dataversetitle) {
        this.dataversetitle = dataversetitle;
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
        if (!(object instanceof HostInfo)) {
            return false;
        }
        HostInfo other = (HostInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "HostInfo{" + "id=" + id + ", hosturl=" + hosturl + ", apitoken=" 
                + apitoken + ", dataverseid=" + dataverseid + ", dataversealias=" 
                + dataversealias + ", datasetid=" + datasetid + ", dataversetitle=" 
                + dataversetitle + '}';
    }
    
}