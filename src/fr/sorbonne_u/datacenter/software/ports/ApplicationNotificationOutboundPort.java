package fr.sorbonne_u.datacenter.software.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;

public class ApplicationNotificationOutboundPort extends AbstractOutboundPort implements ApplicationNotificationI {

    public ApplicationNotificationOutboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , ApplicationNotificationI.class , owner );
    }
    public				ApplicationNotificationOutboundPort(
    		ComponentI owner
    		) throws Exception
    	{
    		super(ApplicationNotificationI.class, owner);
    	}


//    @Override
//    public void notifyRequestGeneratorCreated(String requestNotificationInboundPortURI , String rdnopUri ) throws Exception {
//        ( ( ApplicationNotificationI ) this.connector )
//                .notifyRequestGeneratorCreated(r);
//    }
    @Override
    public void notifyRequestGeneratorCreated( String requestNotificationInboundPortURI , String rdnopUri ) throws Exception {
        ( ( ApplicationNotificationI ) this.connector )
                .notifyRequestGeneratorCreated( requestNotificationInboundPortURI , rdnopUri );
    }

}
