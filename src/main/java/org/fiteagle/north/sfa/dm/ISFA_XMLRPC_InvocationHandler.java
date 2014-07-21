package org.fiteagle.north.sfa.dm;

import java.security.cert.X509Certificate;

import redstone.xmlrpc.XmlRpcInvocationHandler;

public interface ISFA_XMLRPC_InvocationHandler extends XmlRpcInvocationHandler {
	public void setPath(String path);

	public void setCert(X509Certificate cert);
}
