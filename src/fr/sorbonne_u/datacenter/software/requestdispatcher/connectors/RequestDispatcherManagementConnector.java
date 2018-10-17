package fr.sorbonne_u.datacenter.software.requestdispatcher.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;

public class RequestDispatcherManagementConnector extends AbstractConnector
implements RequestDispatcherManagementI{

	@Override
	public void connectVm(String vmURI, String RequestSubmissionInboundPortURI) throws Exception {
		// TODO Auto-generated method stub
		( ( RequestDispatcherManagementI ) this.offering ).connectVm(vmURI, RequestSubmissionInboundPortURI);
	}

	@Override
	public void disconnectVm() throws Exception {
		// TODO Auto-generated method stub
		( ( RequestDispatcherManagementI ) this.offering ).disconnectVm();
	}

}
