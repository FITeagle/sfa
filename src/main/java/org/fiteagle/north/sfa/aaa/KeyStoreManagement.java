package org.fiteagle.north.sfa.aaa;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStrictStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.openssl.PEMWriter;
import org.fiteagle.north.sfa.util.URN;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

//
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.StringWriter;
//import java.security.Key;
//import java.security.KeyStore;
//import java.security.KeyStore.Entry;
//import java.security.KeyStore.PasswordProtection;
//import java.security.KeyStore.PrivateKeyEntry;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.PrivateKey;
//import java.security.UnrecoverableEntryException;
//import java.security.cert.Certificate;
//import java.security.cert.CertificateEncodingException;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//import java.util.ArrayList;
//import java.util.Enumeration;
//import java.util.LinkedList;
//import java.util.List;
//
//import org.bouncycastle.asn1.x500.RDN;
//import org.bouncycastle.asn1.x500.X500Name;
//import org.bouncycastle.asn1.x500.style.BCStrictStyle;
//import org.bouncycastle.asn1.x500.style.IETFUtils;
//import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
//import org.bouncycastle.openssl.PEMWriter;
//import org.fiteagle.core.config.FiteaglePreferences;
//import org.fiteagle.core.config.FiteaglePreferencesXML;
//import org.fiteagle.core.util.URN;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
public class KeyStoreManagement {

//private FiteaglePreferences preferences;

private final String KEYSTORE_LOCATION= System.getProperty("jboss.server.config.dir") + System.getProperty("file.separator") + System.getProperty("keystore_name");
private final String KEYSTORE_PASSWORD = System.getProperty("keystore_pass");
private final String CA_ALIAS =System.getProperty("ca_alias");
private final String CA_PRK_PASS = System.getProperty("ca_prk_pass");
private final String PRK_PASS= System.getProperty("prk_pass");
private final String TRUSTSTORE_LOCATION= System.getProperty("jboss.server.config.dir") + System.getProperty("file.separator") + System.getProperty("truststore_name");
private final String TRUSTSTORE_PASSWORD =  System.getProperty("truststore_pass");
private final String SERVER_ALIAS =System.getProperty("server_alias");
private final String SA_ALIAS=System.getProperty("sa_alias");
private final String SA_PASS=System.getProperty("sa_prkey_pass");
private final String RESOURCE_STORE_LOCATION = System.getProperty("jboss.server.config.dir") + System.getProperty("file.separator") + System.getProperty("resourceStore_name");
private final String RESOURCE_STORE_PASS = System.getProperty("resourceStore_pass");
private static KeyStoreManagement keyStoreManagement;
//
private enum StoreType{
  KEYSTORE,TRUSTSTORE,RESOURCESTORE;
}
public static KeyStoreManagement getInstance(){
  if(keyStoreManagement == null)
    keyStoreManagement = new KeyStoreManagement();
  return keyStoreManagement;

}
private KeyStoreManagement(){

}


protected KeyStore loadKeyStore(StoreType type) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{
  KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
  FileInputStream fis = new FileInputStream(getStorePath(type));
  char[] pass = getStorePass(type);
  ks.load(fis, pass);
  return ks;
}

  private char[] getStorePass(StoreType type) {
    switch(type) {
      case KEYSTORE:
        return getKeyStorePassword();

      case TRUSTSTORE:
        return getTrustStorePassword();

      case RESOURCESTORE:
        return getResourceStorePass();

      default:
        return getTrustStorePassword();
      }
}
  private String getStorePath(StoreType type) {
    switch(type) {
    case KEYSTORE:
      return getKeyStorePath();
    case TRUSTSTORE:
      return getTrustStorePath();

    case RESOURCESTORE:
      return getResourceStorePath();

    default:
      return getTrustStorePath();
    }
}
  private char[] getTrustStorePassword() {
    return TRUSTSTORE_PASSWORD.toCharArray();
}
  private String getTrustStorePath() {
    return TRUSTSTORE_LOCATION;
}
  protected void storeCertificate(String alias, X509Certificate cert, StoreType type) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
    KeyStore ks = loadKeyStore(type);
    ks.setCertificateEntry(alias, cert);
    FileOutputStream fos = new FileOutputStream(getStorePath(type));
    char[] pass = getStorePass(type);
    ks.store(fos, pass);

  }

  private String getCAAlias() {

    return CA_ALIAS;
  }


  private String getKeyStorePath() {
    return KEYSTORE_LOCATION;
  }

  public char[] getPrivateKeyPassword(){
    return PRK_PASS.toCharArray();
  }
  private char[] getKeyStorePassword() {
    return KEYSTORE_PASSWORD.toCharArray();
  }
  public PrivateKey getCAPrivateKey() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableEntryException {
    PrivateKey privateKey = null;
    KeyStore  ks = loadKeyStore(StoreType.TRUSTSTORE);
    KeyStore.PasswordProtection protection = new KeyStore.PasswordProtection(getCAPrivateKeyPassword());
    KeyStore.Entry keyStoreEntry = ks.getEntry(getCAAlias(), protection);
    if(keyStoreEntry instanceof KeyStore.PrivateKeyEntry){
         KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStoreEntry;
         privateKey = privateKeyEntry.getPrivateKey();

    }else {
      throw new PrivateKeyException();
    }
    return privateKey;
  }

  private char[] getCAPrivateKeyPassword() {
    return CA_PRK_PASS.toCharArray();
  }
  protected X509Certificate getCACert() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException  {
    KeyStore ks = loadKeyStore(StoreType.TRUSTSTORE);
    X509Certificate caCert = (X509Certificate) ks.getCertificate(getCAAlias());
    return caCert;
  }

  protected List<X509Certificate> getTrustedCerts() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

    KeyStore ks = loadKeyStore(StoreType.TRUSTSTORE);
    List<X509Certificate> certificateList = new LinkedList<>();

    for(Enumeration<String> aliases=  ks.aliases(); aliases.hasMoreElements();){
            certificateList.add((X509Certificate)(ks.getCertificate(aliases.nextElement())));
    }

    return certificateList;
  }

  public List<String> getTrustedCertsCommonNames() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{
    List<X509Certificate> certs;
    certs = getTrustedCerts();

    List<String> certsAsStrings = new ArrayList<String>();
    for(X509Certificate cert : certs){
      certsAsStrings.add(getCertificateCommonName(cert));
    }

    return certsAsStrings;
  }

  private String getCertificateCommonName(X509Certificate cert) throws CertificateEncodingException {
    X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
    RDN commonName = x500name.getRDNs(BCStrictStyle.CN)[0];
   return IETFUtils.valueToString(commonName.getFirst().getValue());
  }

  public String getTrustedCertificate(String commonName) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{
    for(X509Certificate cert : getTrustedCerts()){
      if(getCertificateCommonName(cert).equals(commonName)){
        return convertToPem(cert);
      }
    }
    return null;
  }

  public String getAllTrustedCertificates() throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
    String all = "";
    for(X509Certificate cert : getTrustedCerts()){
        all+=convertToPem(cert);
      }
    return all;
  }

  private String convertToPem(X509Certificate cert) throws CertificateEncodingException, IOException{
    StringWriter sw = new StringWriter();
    PEMWriter pemwriter = new PEMWriter(sw);
    pemwriter.writeObject(cert);
    pemwriter.close();

    return sw.toString();
  }

  public X509Certificate[] getStoredCertificate(String alias) {
    try {
      KeyStore ks = loadKeyStore(StoreType.KEYSTORE);
      Certificate[] certChain =  ks.getCertificateChain(alias);
      X509Certificate[] returnChain = new X509Certificate[certChain.length];
      for(int i = 0; i<certChain.length; i++){
        returnChain[i] = (X509Certificate) certChain[i];
      }
      return returnChain;
    } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
      throw new CertificateNotFoundException();
    }
  }


  public PrivateKey getServerPrivateKey() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableEntryException {
    PrivateKey privateKey = null;
    KeyStore  ks = loadKeyStore(StoreType.KEYSTORE);
    KeyStore.PasswordProtection protection = new KeyStore.PasswordProtection(getPrivateKeyPassword());
    KeyStore.Entry keyStoreEntry = ks.getEntry(getServerAlias(), protection);
    if(keyStoreEntry instanceof KeyStore.PrivateKeyEntry){
         KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStoreEntry;
         privateKey = privateKeyEntry.getPrivateKey();

    }else {
      throw new PrivateKeyException();
    }
    return privateKey;
  }
  private String getServerAlias() {
    return SERVER_ALIAS;
  }
  public X509Certificate getSliceAuthorityCert() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
      String sa_alias = SA_ALIAS;
      X509Certificate cert = (X509Certificate) loadKeyStore(StoreType.TRUSTSTORE).getCertificate(sa_alias);
      return cert;
  }
  public PrivateKey getSAPrivateKey() throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException, UnrecoverableEntryException {
    PrivateKey privateKey = null;
    KeyStore  ks = loadKeyStore(StoreType.TRUSTSTORE);
    KeyStore.PasswordProtection protection = new KeyStore.PasswordProtection(getSA_PrivateKeyPassword());
    KeyStore.Entry keyStoreEntry = ks.getEntry(getSAAlias(), protection);
    if(keyStoreEntry instanceof KeyStore.PrivateKeyEntry){
         KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStoreEntry;
         privateKey = privateKeyEntry.getPrivateKey();

    }else {
      throw new PrivateKeyException();
    }
    return privateKey;
  }
  private String getSAAlias() {
    return SA_ALIAS;
  }
  private char[] getSA_PrivateKeyPassword() {
    return SA_PASS.toCharArray();
  }
  public X509Certificate getResourceCertificate(String alias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
    KeyStore userCertStore = loadKeyStore(StoreType.RESOURCESTORE);
    if(userCertStore.containsAlias(alias)){
      return (X509Certificate) userCertStore.getCertificate(alias);
    }
    throw new CertificateNotFoundException();
  }
  private char[] getResourceStorePass() {
    return RESOURCE_STORE_PASS.toCharArray();
  }
  private String getResourceStorePath() {
    return RESOURCE_STORE_LOCATION;
  }
  public void storeResourceCertificate(X509Certificate cert) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
    URN urn = X509Util.getURN(cert);
	String alias  = urn.getSubjectAtDomain();
    storeCertificate(alias, cert, StoreType.RESOURCESTORE);
  }

  public class PrivateKeyException extends RuntimeException {
    private static final long serialVersionUID = 2842186524464171483L;

  }
  public class CertificateNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -3514307715237455008L;

  }
  public List<X509Certificate> getResourceCertificates(List<String> urns) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
   KeyStore ks = loadKeyStore(StoreType.RESOURCESTORE);
   List<X509Certificate> certificates = new LinkedList<>();
   for(String urn: urns){
     if(ks.containsAlias(urn))
       certificates.add((X509Certificate) ks.getCertificate(urn));
   }

   return certificates;
  }

}
