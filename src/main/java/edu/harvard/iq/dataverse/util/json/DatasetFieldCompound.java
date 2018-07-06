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
public class DatasetFieldCompound extends DatasetField {
    
    public DatasetFieldCompound(String typeName, Boolean multiple, String typeClass, List<DatasetField> children) {
        super(typeName, multiple, typeClass);
        values= children;
    }
    
    public DatasetFieldCompound(String typeName, Boolean multiple, String typeClass) {
        super(typeName, multiple, typeClass);
    }
    
    List<DatasetField> values = new ArrayList<>();
//
//    public List<DatasetField> getValues() {
//        return values;
//    }

    public void setValues(List<DatasetField> values) {
        this.values = values;
    }

    
}
