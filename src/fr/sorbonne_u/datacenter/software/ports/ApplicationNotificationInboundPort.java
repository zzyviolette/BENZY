package fr.sorbonne_u.datacenter.software.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.software.admissioncontroller.AdmissionController;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationHandlerI;

public class ApplicationNotificationInboundPort extends AbstractInboundPort implements ApplicationNotificationI {

    private static final long serialVersionUID = 1L;

    public ApplicationNotificationInboundPort( ComponentI owner ) throws Exception {
        super( ApplicationNotificationI.class , owner );

    }

    public ApplicationNotificationInboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , ApplicationNotificationI.class , owner );
    }

    @Override
    public void notifyRequestGeneratorCreated(final String requestNotificationInboundPortURI,final String rdnop ) throws Exception {
        
    	this.getOwner().handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						(( AdmissionController)this.getOwner()).
						notifyRequestGeneratorCreated(requestNotificationInboundPortURI, rdnop );
						return null;
					}
				}) ;
    }

}