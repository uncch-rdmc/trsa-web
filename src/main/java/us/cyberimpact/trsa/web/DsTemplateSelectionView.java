/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.web;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import edu.unc.odum.dataverse.util.json.JsonFilter;
import java.io.File;
import java.io.FileNotFoundException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import us.cyberimpact.trsa.entities.DsTemplateData;
import us.cyberimpact.trsa.entities.DsTemplateDataFacade;

/**
 *
 * @author akios
 */
@Named(value = "dsTemplateSelectionView")
@SessionScoped
public class DsTemplateSelectionView implements Serializable {

    private static final Logger logger = Logger.getLogger(DsTemplateSelectionView.class.getName());
    /**
     * Creates a new instance of DsTemplateSelectionView
     */
    public DsTemplateSelectionView() {
    }
    
    @Inject
    DsTemplateDataFacade dsTemplateDataFacade;
    
    List<DsTemplateData> dsTemplatesTable= new ArrayList<>();

    public List<DsTemplateData> getDsTemplatesTable() {
        return dsTemplatesTable;
    }

    public void setDsTemplatesTable(List<DsTemplateData> dsTemplatesTable) {
        this.dsTemplatesTable = dsTemplatesTable;
    }
    
    
    private DsTemplateData selectedDsTemplateData; 

    public DsTemplateData getSelectedDsTemplateData() {
        return selectedDsTemplateData;
    }

    public void setSelectedDsTemplateData(DsTemplateData selectedDsTemplateData) {
        this.selectedDsTemplateData = selectedDsTemplateData;
    }
    
    
    @PostConstruct
    public void init() {
        dsTemplatesTable = dsTemplateDataFacade.findAll();
        logger.log(Level.INFO, "dsTemplatesTable:size={0}", dsTemplatesTable.size());
        if (dsTemplatesTable.isEmpty()){
            logger.log(Level.WARNING, "template data are empty");
        } else {
            logger.log(Level.INFO, "template data are not empty");
            selectedDsTemplateData= dsTemplatesTable.get(dsTemplatesTable.size()-1);
            logger.log(Level.INFO, "selectedDsTemplateData={0}", selectedDsTemplateData);
            
            
            
            
            
            
        }
    }
    
    // first one step submission ; later confirmation stage
    public String selectTemplate(DsTemplateData dsTemplateData){
        logger.log(Level.INFO, "within the method DsTemplateSelectionView#selectTemplate");
        logger.log(Level.INFO, "selected dsTemplate={0}", dsTemplateData);
        logger.log(Level.INFO, "dsTemplateId={0}", dsTemplateData.getId());
        
        
        selectedDsTemplateData = dsTemplateData;
        logger.log(Level.INFO, "selectedDsTemplateData={0}", selectedDsTemplateData);
        // step 1: build a json file
        
        
        // step 2: submit the json file
        
        
        // step 3: capture its response
        
        // step 4: jump to the confirmation page
        
        logger.log(Level.INFO, "go to destination-selection page");
        return "/destination.xhtml";
    }
    
    
}
