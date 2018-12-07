package fr.sorbonne_u.datacenter.software.requestdispatcher.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.software.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;

public class RequestDispatcherManagementInboundPort extends AbstractInboundPort
implements RequestDispatcherManagementI{

    private static final long serialVersionUID = 1L;

    public RequestDispatcherManagementInboundPort( ComponentI owner ) throws Exception {
        super( RequestDispatcherManagementI.class , owner );

        assert owner instanceof RequestDispatcherManagementI;
    }

    public RequestDispatcherManagementInboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , RequestDispatcherManagementI.class , owner );

        assert uri != null && owner instanceof RequestDispatcherManagementI;
    }
    
   


    
	@Override
	public void disconnectVm(final String vmURI ) throws Exception {
		this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((RequestDispatcher)this.getOwner()).
						 disconnectVm(vmURI) ;
						return null;
					}
				}) ;
		
	}

	@Override
	public void connectVm(final String vmURI, final String RequestSubmissionInboundPortURI,final String RequestNotificationInboundPortURI,final String avmipURI ) throws Exception {
		this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((RequestDispatcher)this.getOwner()).
							connectVm(vmURI, RequestSubmissionInboundPortURI,RequestNotificationInboundPortURI,avmipURI); ;
						return null;
					}
				}) ;
		
	}
	
	@Override
	public void allocatedCore(final String vmURI,final AllocatedCore[] allocatedCores ) throws Exception {
		this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((RequestDispatcher)this.getOwner()).
							allocatedCore(vmURI, allocatedCores);
						return null;
					}
				}) ;
		
	}


}
