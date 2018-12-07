package fr.sorbonne_u.datacenter.software.requestdispatcher.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;

public class RequestDispatcherManagementOutboundPort extends AbstractOutboundPort
implements RequestDispatcherManagementI {
	
	private static final long serialVersionUID = 1L;

	public RequestDispatcherManagementOutboundPort( ComponentI owner ) throws Exception {
		super( RequestDispatcherManagementI.class , owner );
		assert	owner != null ;
	}

	public RequestDispatcherManagementOutboundPort( String uri , ComponentI owner ) throws Exception {
		super( uri , RequestDispatcherManagementI.class , owner );
		assert	uri != null && owner != null ;
	}

	@Override
	public void connectVm(String vmURI, String RequestSubmissionInboundPortURI, String RequestNotificationInboundPortURI,String avmipURI) throws Exception {
		( ( RequestDispatcherManagementI ) this.connector ).connectVm(vmURI, RequestSubmissionInboundPortURI,RequestNotificationInboundPortURI,avmipURI);

	}

	@Override
	public void disconnectVm(String vmURI) throws Exception {
		( ( RequestDispatcherManagementI ) this.connector ).disconnectVm(vmURI);

	}

	@Override
	public void allocatedCore(String vmURI, AllocatedCore[] allocatedCores) throws Exception {
		// TODO Auto-generated method stub
		( ( RequestDispatcherManagementI ) this.connector ).allocatedCore(vmURI, allocatedCores);
	}

}
