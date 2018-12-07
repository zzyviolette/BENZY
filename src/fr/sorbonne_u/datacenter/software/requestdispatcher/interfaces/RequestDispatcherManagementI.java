package fr.sorbonne_u.datacenter.software.requestdispatcher.interfaces;
import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;

public interface RequestDispatcherManagementI 
extends		
OfferedI,
RequiredI{

	void disconnectVm(String vmURI) throws Exception;

	void connectVm(String vmURI, String RequestSubmissionInboundPortURI, String RequestNotificationInboundPortURI,String avmipURI)
			throws Exception;

	void allocatedCore(String vmURI, AllocatedCore[] allocatedCores) throws Exception;

}
