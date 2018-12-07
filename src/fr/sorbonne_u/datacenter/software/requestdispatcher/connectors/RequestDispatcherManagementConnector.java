package fr.sorbonne_u.datacenter.software.requestdispatcher.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;

public class RequestDispatcherManagementConnector extends AbstractConnector
implements RequestDispatcherManagementI{

	@Override
	public void disconnectVm(String vmURI) throws Exception {
		// TODO Auto-generated method stub
		((RequestDispatcherManagementI)this.offering).disconnectVm(vmURI);
		
	}

	@Override
	public void connectVm(String vmURI, String RequestSubmissionInboundPortURI,
			String RequestNotificationInboundPortURI, String avmipURI) throws Exception {
		// TODO Auto-generated method stub
		((RequestDispatcherManagementI)this.offering).connectVm(vmURI,RequestSubmissionInboundPortURI,
				RequestNotificationInboundPortURI ,avmipURI);
		
	}

	@Override
	public void allocatedCore(String vmURI, AllocatedCore[] allocatedCores) throws Exception {
		// TODO Auto-generated method stub
		((RequestDispatcherManagementI)this.offering).allocatedCore(vmURI, allocatedCores);
		
	}


}
