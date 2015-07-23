package org.fiteagle.north.sfa.exceptions;

  
  public class URNParsingException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private String message;
    
    public URNParsingException(String message){
      this.message = message;
    }
    
    @Override
    public String getMessage(){
      return this.message;
    }

}
  
