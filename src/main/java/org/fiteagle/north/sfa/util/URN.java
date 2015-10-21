package org.fiteagle.north.sfa.util;


import info.openmultinet.ontology.translators.geni.CommonMethods;

import org.fiteagle.north.sfa.exceptions.URNParsingException;

/**
 * Created by dne on 12.01.15.
 */
public class URN {


    private String subject;
    private String domain;
    private String type;
    private String project;
    private static String prefix = "urn:publicid:IDN";

    public URN(String urnString) {
        parseURNString(urnString);
    }

    private void parseURNString(String urnString) {
        String[] splitted = urnString.split("\\+");
        if (isCorrectLength(splitted)) {
            if (isCorrectPrefix(splitted[0])) {
                parseDomain(splitted[1]);
                this.type = splitted[2];
                this.subject = splitted[3];
            } else {
                throw new URNParsingException("Failure by parsing URN. Please consider supported form for sliceuUrn");
            }

        } else {
            splitted = urnString.split("\\.");
            if (splitted.length == 2) {
                parseDomain(splitted[0]);
                this.type = "user";
                this.subject = splitted[1];
            } else {
                throw new URNParsingException("Failure by parsing URN. URNs shouldn't be null. Please consider supported form for sliceUrn");
            }
        }
    }

    private boolean isCorrectPrefix(String prefix) {
        return this.prefix.equals(prefix);
    }

    private boolean isCorrectLength(String[] splitted) {
        return splitted.length == 4;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }
    
    public String getProject(){
      return this.project;
    }
    
    public void setProject(String project){
      this.project = project;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
      if(this.project.isEmpty())
        return prefix + "+" + domain + "+" + type + "+" + subject;
      else 
        return prefix + "+" + domain + ":" + project + "+" + type + "+" + subject;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof URN) {
            URN toCompare = (URN) o;
            return toCompare.toString().equals(this.toString());
        }
        return false;
    }


    public String getSubjectAtDomain() {
        if (domain.contains(":")) {
            return subject + "@" + domain.replace(":", ".");
        }
        return subject + "@" + domain;
    }

    public void parseDomain(String domainString){
      String[] splittedDomain = domainString.split("\\:");
      if(splittedDomain.length > 1) {
        this.domain = splittedDomain[0];
        this.project = splittedDomain[1];
      }
      else {
        this.domain = domainString;
        this.project = "";
      }
    }


}