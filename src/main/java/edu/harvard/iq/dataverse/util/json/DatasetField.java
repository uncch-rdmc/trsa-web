/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.util.json;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Akio Sone, Univ, of North Carolina at Chapel Hill, H.W. Odum Inst.
 */
public class DatasetField {

    public DatasetField(String typeName, Boolean multiple, String typeClass) {
        this.typeName = typeName;
        this.multiple = multiple;
        this.typeClass = typeClass;
    }
    
    

    String typeName;
    Boolean multiple;
    String typeClass;// enum ?  primitive | compound | controlledVocabulary
    String value;
//    
//    List<DatasetField> children = new ArrayList<>();
//    private List<DatasetFieldValue> datasetFieldValues = new ArrayList<>();
    private List<DatasetField> values = new ArrayList<>();    

    public List<DatasetField> getValues() {
        return values;
    }
    
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(Boolean multiple) {
        this.multiple = multiple;
    }

    public String getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(String typeClass) {
        this.typeClass = typeClass;
    }

//    public String getValue() {
//        return value;
//    }
//
    public void setValue(String value) {
        this.value = value;
    }
    
    public boolean isControlledVocabulary() {
//        return this.typeClass.equals("controlledVocabulary");
     return controlledVocabularyValues != null && !controlledVocabularyValues.isEmpty();
    }

    public boolean isPrimitive() {
        //return this.typeClass.equals("primitive");
        return !isCompound();
        
    }
    
    public boolean isCompound() {
        return this.typeClass.equals("compound");
//        return datasetFieldCompoundValues != null && !datasetFieldCompoundValues.isEmpty() &&
//                datasetFieldCompoundValues.size() >1;
        
    }
   
//    public void addChild(DatasetField dsf){
//        children.add(dsf);
//    }
//
//    public List<DatasetField> getChildren() {
//        return children;
//    }
//
//    public void setChildren(List<DatasetField> children) {
//        this.children = children;
//    }
    
    
    public String getValue() {
        if (isPrimitive()) {
            //return datasetFieldValues.get(0).getValue();
            return this.value;//values.get(0).value;
        }
//        } else if (controlledVocabularyValues != null && !controlledVocabularyValues.isEmpty()) {
//            if (controlledVocabularyValues.get(0) != null){
//                return controlledVocabularyValues.get(0).getStrValue();                
//            }
//            
//            
//            
//        }
        return null;
    }
    

    private List<DatasetFieldCompoundValue> datasetFieldCompoundValues = new ArrayList<>();

    public List<DatasetFieldCompoundValue> getDatasetFieldCompoundValues() {
        return datasetFieldCompoundValues;
    }



    private List<ControlledVocabularyValue> controlledVocabularyValues = new ArrayList<>();

    public List<ControlledVocabularyValue> getControlledVocabularyValues() {
        return controlledVocabularyValues;
    }
}
