package org.fiteagle.north.sfa.aaa;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import org.fiteagle.north.sfa.util.URN;


public class CertificateAuthority {


	private static CertificateAuthority CA = null;

	public static CertificateAuthority getInstance() {
		if (CA == null)
			CA = new CertificateAuthority();
		return CA;
	}

	private CertificateAuthority() {

	}

	private KeyStoreManagement keyStoreManagement = KeyStoreManagement.getInstance();

	public X509Certificate createCertificate( URN urn,PublicKey publicKey)
			throws Exception {
		CertficateAuthorityDelegate certficateAuthorityDelegate = new CertficateAuthorityDelegate(urn).invoke();
		X500Name issuer = certficateAuthorityDelegate.getIssuer();
		ContentSigner contentsigner = certficateAuthorityDelegate.getContentsigner();
		X500Name subject = certficateAuthorityDelegate.getSubject();
		SubjectPublicKeyInfo subjectsPublicKeyInfo = getPublicKey(publicKey);
		return getX509Certificate(urn, issuer, contentsigner, subject, subjectsPublicKeyInfo);
	}

	private X509Certificate getX509Certificate(URN urn, X500Name issuer, ContentSigner contentsigner, X500Name subject, SubjectPublicKeyInfo subjectsPublicKeyInfo) throws IOException, CertificateException {
		X509v3CertificateBuilder ca_gen = new X509v3CertificateBuilder(issuer,
				new BigInteger(new SecureRandom().generateSeed(256)),
				new Date(),
				new Date(System.currentTimeMillis() + 31500000000L), subject,
				subjectsPublicKeyInfo);
		BasicConstraints ca_constraint = new BasicConstraints(false);
		ca_gen.addExtension(X509Extension.basicConstraints, true, ca_constraint);
		GeneralNames subjectAltName = new GeneralNames(new GeneralName(
				GeneralName.uniformResourceIdentifier, urn.toString()));

		X509Extension extension = new X509Extension(false, new DEROctetString(
				subjectAltName));
		ca_gen.addExtension(X509Extension.subjectAlternativeName, false,
				extension.getParsedValue());
		X509CertificateHolder holder = (X509CertificateHolder) ca_gen
				.build(contentsigner);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		return (X509Certificate) cf
				.generateCertificate(new ByteArrayInputStream(holder
						.getEncoded()));
	}


	public X509Certificate getSliceAuthorityCertificate() {
		try {
			return keyStoreManagement.getSliceAuthorityCert();
		} catch (KeyStoreException | NoSuchAlgorithmException
				| CertificateException | IOException e) {
			throw new CertificateNotFoundException();
		}
	}


	private SubjectPublicKeyInfo getPublicKey(PublicKey key) throws Exception {

		SubjectPublicKeyInfo subPubInfo = new SubjectPublicKeyInfo(
				(ASN1Sequence) ASN1Sequence.fromByteArray(key.getEncoded()));
		return subPubInfo;
	}

	private X500Name createX500Name(URN name) {
		X500Principal prince = new X500Principal("CN=" + name.getSubject());
		X500Name x500Name = new X500Name(prince.getName());
		return x500Name;
	}


	
	public class EncodeCertificateException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

	public class CertificateNotFoundException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

	public X509Certificate createCertificate(X509Certificate xCert)
			throws Exception {

		PublicKey pubkey = xCert.getPublicKey();
		return createCertificate(null, pubkey);
	}



	private class CertficateAuthorityDelegate {
		private URN userURN;
		private X500Name issuer;
		private ContentSigner contentsigner;
		private X500Name subject;

		public CertficateAuthorityDelegate(URN userURN) {
			this.userURN = userURN;
		}

		public X500Name getIssuer() {
			return issuer;
		}

		public ContentSigner getContentsigner() {
			return contentsigner;
		}

		public X500Name getSubject() {
			return subject;
		}

		public CertficateAuthorityDelegate invoke() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableEntryException, OperatorCreationException {
			X509Certificate caCert = keyStoreManagement.getCACert();
			issuer = new JcaX509CertificateHolder(caCert).getSubject();
			PrivateKey caPrivateKey = keyStoreManagement.getCAPrivateKey();
			contentsigner = new JcaContentSignerBuilder(
					"SHA1WithRSAEncryption").build(caPrivateKey);

			subject = createX500Name(userURN);
			return this;
		}
	}
}
