package org.fiteagle.north.sfa.am;

import org.fiteagle.api.core.IGeni;

public enum ReservationStateEnum {
  
  Allocated(IGeni.GENI_ALLOCATED), Unallocated(IGeni.GENI_UNALLOCATED), Provisioned(IGeni.GENI_PROVISIONED);
  
  String geniState;
  
  private ReservationStateEnum(String geniState){
    this.geniState = geniState;
    }
  
  
  public String getGeniState(){
    return this.geniState;
    }
}
