package fr.sorbonne_u.datacenter.software.requestdispatcher.interfaces;
import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface RequestDispatcherManagementI 
extends		
OfferedI,
RequiredI{
public void connectVm(String vmURI, String RequestSubmissionInboundPortURI) throws Exception;
    
    public void disconnectVm() throws Exception;

}
