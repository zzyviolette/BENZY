package fr.sorbonne_u.datacenter.software.requestdispatcher;

import java.util.ArrayList;
import java.util.Collections;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.sorbonne_u.datacenter.software.connectors.RequestNotificationConnector;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.sorbonne_u.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.datacenter.software.requestdispatcher.ports.RequestDispatcherManagementInboundPort;

public class RequestDispatcher extends AbstractComponent
		implements RequestSubmissionHandlerI, RequestNotificationHandlerI, RequestDispatcherManagementI {


	/** URI of this request dispatcher RD */
	protected String rdURI;
	
	protected RequestDispatcherManagementInboundPort rdmip;

	//uris de ports de rg 
	protected String							rd_rg_rsipURI;
	protected String							rd_rg_rnipURI;
	
	//uris de ports de vm
	protected ArrayList<String>                         vmURIList;
	protected ArrayList<String>							rd_vm_rsipURIList ;
	protected ArrayList<String>							rd_vm_rnipURIList ;
	
	protected RequestSubmissionInboundPort rd_rg_rsip;
	protected RequestNotificationOutboundPort rd_rg_rnop;

	protected ArrayList<RequestNotificationInboundPort> rd_vm_rnipList;
	protected ArrayList<RequestSubmissionOutboundPort> rd_vm_rsopList;
	
	protected ArrayList<ApplicationVMManagementOutboundPort> avmopList; 
	
	
	protected int choice ; 


	public RequestDispatcher(String rdURI, String rdmip, String rg_rsipURI, String rg_rnipURI)
			throws Exception {
		super(1, 1);

		// Preconditions
	    assert rdURI != null;
	    assert rdmip !=null;
	    assert rg_rsipURI !=null;
	    assert rg_rnipURI != null;
	    
		this.rdURI = rdURI;
		this.rd_rg_rsipURI = rg_rsipURI;
		this.rd_rg_rnipURI = rg_rnipURI;

		this.rd_vm_rnipList = new ArrayList<RequestNotificationInboundPort>();
		this.rd_vm_rsopList = new ArrayList<RequestSubmissionOutboundPort>();
		
		this.vmURIList = new ArrayList<>();
		this.avmopList = new ArrayList<ApplicationVMManagementOutboundPort>();
			

		// Creates and add ports to the component

		//management
		this.addOfferedInterface(RequestDispatcherManagementI.class);
		this.rdmip = new RequestDispatcherManagementInboundPort(rdmip, this);
		this.addPort(this.rdmip);
		this.rdmip.publishPort();

		//les ports pour la connexion avec rg
		this.addOfferedInterface(RequestSubmissionI.class);
		this.rd_rg_rsip = new RequestSubmissionInboundPort(this.rd_rg_rsipURI, this);
		this.addPort(this.rd_rg_rsip);
		this.rd_rg_rsip.publishPort();
		
		this.addRequiredInterface(RequestNotificationI.class);
		this.rd_rg_rnop = new RequestNotificationOutboundPort(this);
		this.addPort(this.rd_rg_rnop);
		this.rd_rg_rnop.publishPort();
		
		assert this.rd_rg_rsip != null && this.rd_rg_rsip instanceof RequestSubmissionI;
		assert this.rd_rg_rnop != null && this.rd_rg_rnop instanceof RequestNotificationI;


		
	}

	/**
	 * Send the Request r to the ApplicationVM
	 */
	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		// TODO Auto-generated method stub
		assert r != null;
		this.logMessage(  this.rdURI + " recevoit le request " + r.getRequestURI() +" de RG");
		
		choice = choice % this.vmURIList.size();
		this.rd_vm_rsopList.get(choice).submitRequest(r);
		choice++;	

	}
	
	/**
	 * Send the Request r to the ApplicationVM and notify its termination to the
	 * RequestGenerator
	 */

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {

		// TODO Auto-generated method stub
		assert r != null;
		
		this.logMessage(  this.rdURI + " recevoit le request " + r.getRequestURI() +" de request generator");
		choice =  choice % this.vmURIList.size();
		this.rd_vm_rsopList.get(choice).submitRequestAndNotify(r);
		this.logMessage( this.rdURI + " envoye le request " + r.getRequestURI() +" a " + this.vmURIList.get(choice) );
		choice++;

	}
	
	/**
	 * Notify the Requests termination to the RequestGenerator
	 */
	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		// TODO Auto-generated method stub
		assert r != null;
		this.logMessage( this.rdURI + "  revevoit la notification de le request " + r.getRequestURI() + " de VM" );
		this.rd_rg_rnop.notifyRequestTermination( r );
		this.logMessage( this.rdURI + "  envoit la notification " + r.getRequestURI() + " a RG" );	
		

	}
	
	
	@Override
	public void		start() throws ComponentStartException
	{
		super.start() ;
		try {
			//la connection avec rg
			this.doPortConnection(
					this.rd_rg_rnop.getPortURI(),
					this.rd_rg_rnipURI,
					RequestNotificationConnector.class.getCanonicalName()) ;
			
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}

	
	}
	
	
	@Override
	public void			finalise() throws Exception
	{  
		if(this.rd_rg_rnop.connected()) this.doPortDisconnection(this.rd_rg_rnop.getPortURI()) ;
		
		for(int i = 0; i<this.vmURIList.size();i++){
	       if(this.rd_vm_rsopList.get(i).connected()) 
	    	   this.doPortDisconnection(this.rd_vm_rsopList.get(i).getPortURI());
		}
		
		for (int i = 0; i < this.avmopList.size(); i++ ) { 
			if (this.avmopList.get(i).connected()) this.avmopList.get(i).doDisconnection();;			
		}
		
		super.finalise() ;
	}

	@Override
	public void shutdown() throws ComponentShutdownException {

		try {
			//rg
			if (this.rd_rg_rnop.isPublished()) this.rd_rg_rnop.unpublishPort();
			if (this.rd_rg_rsip.isPublished()) this.rd_rg_rsip.unpublishPort();
			//vm
			for (int i = 0; i < this.vmURIList.size(); i++ ) { 
				if (this.rd_vm_rsopList.get(i).isPublished()) this.rd_vm_rsopList.get(i).unpublishPort();
				if (this.rd_vm_rnipList.get(i).isPublished()) this.rd_vm_rnipList.get(i).unpublishPort();				
			}
			
			for (int i = 0; i < this.avmopList.size(); i++ ) { 
				if (this.avmopList.get(i).isPublished()) this.avmopList.get(i).unpublishPort();			
			}
			
			
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}

		super.shutdown();
	}	
	

	@Override
	public void disconnectVm(String vmURI) throws Exception {
		// TODO Auto-generated method stub
	    int index = this.vmURIList.indexOf(vmURI);  
	    
	    if(this.rd_vm_rsopList.get(index).connected()) 
	    	   this.doPortDisconnection(this.rd_vm_rsopList.get(index).getPortURI());
	    if(this.avmopList.get(index).connected()) 
	    	   this.doPortDisconnection(this.avmopList.get(index).getPortURI());
	    
	   
	    if (this.rd_vm_rsopList.get(index).isPublished()) this.rd_vm_rsopList.get(index).unpublishPort();	
	    if (this.rd_vm_rnipList.get(index).isPublished()) this.rd_vm_rnipList.get(index).unpublishPort();	
	    if (this.avmopList.get(index).isPublished()) this.avmopList.get(index).unpublishPort();	
	   
	    this.rd_vm_rnipList.remove(index);
	    this.rd_vm_rsopList.remove(index);
	    this.vmURIList.remove(index);
	    this.avmopList.remove(index);
		
	}
	
	@Override
	public void allocatedCore(String vmURI, AllocatedCore[] allocatedCores) throws Exception{
		 int index = this.vmURIList.indexOf(vmURI);  
	     this.avmopList.get(index).allocateCores(allocatedCores);
	}
	

	@Override
	public void connectVm(String vmURI, String RequestSubmissionInboundPortURI,
			String RequestNotificationInboundPortURI, String avmipURI) throws Exception {
		// TODO Auto-generated method stub
		
		this.vmURIList.add(vmURI);
		
		this.addOfferedInterface(RequestNotificationI.class);
		RequestNotificationInboundPort rnip = new RequestNotificationInboundPort(RequestNotificationInboundPortURI, this);
		this.rd_vm_rnipList.add(rnip);
		this.addPort(rnip);
		rnip.publishPort();	

		this.addRequiredInterface(RequestSubmissionI.class);
		RequestSubmissionOutboundPort rsop = new RequestSubmissionOutboundPort(this);
		this.rd_vm_rsopList.add(rsop);
		this.addPort(rsop);
		rsop.publishPort();
		
		this.doPortConnection(
				rsop.getPortURI(),
				RequestSubmissionInboundPortURI,
				RequestSubmissionConnector.class.getCanonicalName()) ;
		
		
		ApplicationVMManagementOutboundPort avmop = new ApplicationVMManagementOutboundPort(this);
		this.avmopList.add(avmop);
		this.addPort(avmop);
		avmop.publishPort();		
		
		
		this.doPortConnection(
				avmop.getPortURI(),
				avmipURI,
				ApplicationVMManagementConnector.class.getCanonicalName()) ;
			
	}
	
}
