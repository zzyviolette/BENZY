package fr.sorbonne_u.datacenter.software.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationNotificationI;

public class ApplicationNotificationOutboundPort extends AbstractOutboundPort implements ApplicationNotificationI {

    public ApplicationNotificationOutboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , ApplicationNotificationI.class , owner );
    }

    @Override
    public void notifyRequestGeneratorCreated( String requestNotificationInboundPortURI , String rdnopUri ) throws Exception {
        ( ( ApplicationNotificationI ) this.connector )
                .notifyRequestGeneratorCreated( requestNotificationInboundPortURI , rdnopUri );
    }

}
