package interfaces;

import utils.Result;

public interface FreebaseVerifierInterface {
	
	//set the subject on which the verification will be carried out.  
	public FreebaseVerifierInterface subject(String subject); 
	
	//set up one or more properties in order to make the verification more accurate (optional).
	public FreebaseVerifierInterface withProperty(String property, String value);
	
	//set up one or more metaschema values in order to make the verification more accurate (optional).
	//Metaschemas are more strict than properties, and require precise names offered by Freebase.
	//For more information, go to: https://developers.google.com/freebase/v1/search-cookbook#metaschema-constraints
	public FreebaseVerifierInterface withMetaschema(String metaschema, String value);
	
	//start the verification, checking whether the subject is the same type as the statement parameter.
	//The result is an object containing outcome, description and an accuracy score.
	public Result is(String statement);
}
