package fr.sorbonne_u.datacenter.software.requestdispatcher;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
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

	// rg - RequestSubmissionInboundPort/RequestNotificationOutboundPort
	// avm - RequestNotificationInboundPort/RequestSubmissionOutboundPort

	/** URI of this request dispatcher RD */
	protected String rdURI;

	/** RequestSubmissionInboundPort */
	protected RequestSubmissionInboundPort rdsip;

	/** InboundPort uses to get the notification task end (by VM) */
	protected RequestNotificationInboundPort rnip;

	/** OutboundPort to send requests to the connected ApplicationVM */
	protected RequestSubmissionOutboundPort rdsop;

	/** Outbound port used by the RD to notify tasks' end to the generator. */
	protected RequestNotificationOutboundPort rnop;

	protected RequestDispatcherManagementInboundPort rdmip;
	
	protected String							rg_requestSubmissionInboundPortURI ;
	protected String							rg_requestNotificationInboundPortURI ;
	protected String							vm_requestSubmissionInboundPortURI ;
	protected String						    vm_requestNotificationInboundPortURI ;

	public RequestDispatcher(String rdURI, String rdmip, String rg_rdsip, String rg_rnip,String vm_rdsip, String vm_rnip)
			throws Exception {
		super(1, 1);

		// Preconditions
	

		this.rdURI = rdURI;
		this.rg_requestSubmissionInboundPortURI =	rg_rdsip ;
		this.rg_requestNotificationInboundPortURI = rg_rnip;
		this.vm_requestSubmissionInboundPortURI =	vm_rdsip ;
		this.vm_requestNotificationInboundPortURI = vm_rnip;

		// Creates and add ports to the component

		this.addOfferedInterface(RequestDispatcherManagementI.class);
		this.rdmip = new RequestDispatcherManagementInboundPort(rdmip, this);
		this.addPort(this.rdmip);
		this.rdmip.publishPort();

		this.addOfferedInterface(RequestSubmissionI.class);
		this.rdsip = new RequestSubmissionInboundPort(rg_rdsip, this);
		this.addPort(this.rdsip);
		this.rdsip.publishPort();

		this.addOfferedInterface(RequestNotificationI.class);
		this.rnip = new RequestNotificationInboundPort(vm_rnip, this);
		this.addPort(this.rnip);
		this.rnip.publishPort();

		this.addRequiredInterface(RequestSubmissionI.class);
		this.rdsop = new RequestSubmissionOutboundPort(this);
		this.addPort(this.rdsop);
		this.rdsop.publishPort();

		this.addRequiredInterface(RequestNotificationI.class);
		this.rnop = new RequestNotificationOutboundPort(this);
		this.addPort(this.rnop);
		this.rnop.publishPort();

	}

	/**
	 * Notify the Requests termination to the RequestGenerator
	 */
	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		// TODO Auto-generated method stub
		assert r != null;
		this.logMessage( "Request dispatcher " + this.rdURI + "  notified the request " + r.getRequestURI() + " has ended." );
		this.rnop.notifyRequestTermination( r );

	}
	/**
	 * Send the Request r to the ApplicationVM
	 */
	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		// TODO Auto-generated method stub
		System.out.println( this.rdURI + " submits request " + r.getRequestURI() );
		this.logMessage( "RequestDispatcher" + rdURI + " submits request " + r.getRequestURI() );
		this.rdsop.submitRequest(r);

	}
	
	/**
	 * Send the Request r to the ApplicationVM and notify its termination to the
	 * RequestGenerator
	 */

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		// TODO Auto-generated method stub

		//System.out.println( this.rdURI + " submits request " + r.getRequestURI() );
		this.logMessage( "RequestDispatcher " + rdURI + " submits request " + r.getRequestURI() );
		this.rdsop.submitRequestAndNotify( r );
//		System.out.println("rdsopuri"+rdsop.getPortURI());

	}
	
	
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;

		try {
			//connect to vm
			this.doPortConnection(
					this.rdsop.getPortURI(),
					vm_requestSubmissionInboundPortURI,
					RequestSubmissionConnector.class.getCanonicalName()) ;
			//connect to rg
			this.doPortConnection(
					this.rnop.getPortURI(),
					rg_requestNotificationInboundPortURI,
					RequestNotificationConnector.class.getCanonicalName()) ;
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}
	
	
	@Override
	public void			finalise() throws Exception
	{
		this.doPortDisconnection(this.rdsop.getPortURI()) ;
		this.doPortDisconnection(this.rnop.getPortURI()) ;
		super.finalise() ;
	}

	
	
	@Override
	public void connectVm(String vmURI, String RequestSubmissionInboundPortURI) throws Exception {
		// TODO Auto-generated method stub

		
	}

	@Override
	public void disconnectVm() throws Exception {
		// TODO Auto-generated method stub
		
	}


}