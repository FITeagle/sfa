package org.fiteagle.north.sfa.exceptions;

public class EmptyReplyException extends RuntimeException{
  
  private static final long serialVersionUID = 3084952835284992423L;
  
  public EmptyReplyException() {
    super(" (Empty reply from repo)");
  }
  
  public EmptyReplyException(String message) {
    super(" (" + message + ")");
  }
  
}
