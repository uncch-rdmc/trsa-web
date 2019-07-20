/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unc.odum.dataverse.util.json;

/**
 *
 * @author Akio Sone, Univ, of North Carolina at Chapel Hill, H.W. Odum Inst.
 */
public class JsonPointerForDataset {
    
    
    public static String POINTER_METADATABLOCKS = "/datasetVersion/metadataBlocks";
    
    public static String POINTER_FILES="/datasetVersion/files";
    
    public static String POINTER_METADATABLOCKS_FILTERED = "/metadataBlocks";
    
    public static String POINTER_FILES_FILTERED="/files";
    
    public static String POINTER_TO_DATASET_ID="/data/id";
    
    public static String POINTER_TO_DATASET_DOI="/data/persistentId";
    // for Dataset title
    public static String POINTER_TO_DATASET_TITLE="/datasetVersion/metadataBlocks/citation/fields/0/value";
    // FOR authorAffiliation
    
    public static String POINTER_TO_DATASET_AUTHOR_AFFILIATION="/datasetVersion/metadataBlocks/citation/fields/1/value/authorAffiliation/value";
    
    // auther name
    public static String POINTER_TO_AUTHOR_NAME="/datasetVersion/metadataBlocks/citation/fields/1/value/authorName/value";
    
    // email address
    public static String POINTER_TO_EMAIL="/datasetVersion/metadataBlocks/citation/fields/2/value/datasetContactEmail/value";
    
    // Description
    public static String POINTER_TO_DATASET_DESCRIPTION="/datasetVersion/metadataBlocks/citation/fields/3/value/dsDescriptionValue/value";
    
    // subject (array)
    public static String POINTER_TO_SUBJECTS="/datasetVersion/metadataBlocks/citation/fields/4/value/";

    // social science metadata note type
    public static String POINTER_TO_SSN_TYPE="/datasetVersion/metadataBlocks/socialscience/fields/0/value/socialScienceNotesType/value";
    
    // social science metadata note text
    public static String POINTER_TO_SSN_TEXT="/datasetVersion/metadataBlocks/socialscience/fields/0/value/socialScienceNotesText/value";
   
    // data tags: array
    public static String POINTER_TO_FILE_DATATAG="datasetVersion/files/5/categories/";
    
    
}
