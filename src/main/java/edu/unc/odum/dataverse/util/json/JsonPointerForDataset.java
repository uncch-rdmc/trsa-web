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
    // required fields for a minimum Dataset
    // for Dataset title
    public static String POINTER_TO_DATASET_TITLE="/datasetVersion/metadataBlocks/citation/fields/0/value";
    
    // FOR authorAffiliation
    public static String POINTER_TO_DATASET_AUTHOR_AFFILIATION="/datasetVersion/metadataBlocks/citation/fields/1/value/0/authorAffiliation/value";
    
    // auther name
    public static String POINTER_TO_AUTHOR_NAME="/datasetVersion/metadataBlocks/citation/fields/1/value/0/authorName/value";
    
    // contact email address
    public static String POINTER_TO_EMAIL="/datasetVersion/metadataBlocks/citation/fields/2/value/0/datasetContactEmail/value";
    
    // Description
    public static String POINTER_TO_DATASET_DESCRIPTION="/datasetVersion/metadataBlocks/citation/fields/3/value/0/dsDescriptionValue/value";
    
    // subject (array)
    public static String POINTER_TO_SUBJECTS="/datasetVersion/metadataBlocks/citation/fields/4/value";

    // social science metadata note type
    public static String POINTER_TO_SSN_TYPE="/datasetVersion/metadataBlocks/socialscience/fields/0/value/socialScienceNotesType/value";
    
    // social science metadata note text
    public static String POINTER_TO_SSN_TEXT="/datasetVersion/metadataBlocks/socialscience/fields/0/value/socialScienceNotesText/value";
   
    // data tags: array
    public static String POINTER_TO_FILE_DATATAG="datasetVersion/files/5/categories/";
    
    
    
    public static String POINTER_TO_DATAVERSE_ID ="/data/id";
    
    public static String POINTER_TO_DATAVERSE_ALIAS="/data/alias";
    
    public static String POINTER_TO_DATAVERSE_TITLE="/data/name";
    
    // files tag: array
    public static String POINTER_TO_FILES="/data/latestVersion/files";
    
    public static String POINTER_TO_MD5_VALUE="/dataFile/md5";
    
    public static String POINTER_TO_FILENAME="/dataFile/filename";
}
