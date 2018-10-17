package fr.sorbonne_u.datacenter.software.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationNotificationI;

public class ApplicationNotificationConnector extends AbstractConnector implements ApplicationNotificationI {

    @Override
    public void notifyRequestGeneratorCreated( String requestNotificationInboundPortURI , String rdnopUri )
            throws Exception {
        ( ( ApplicationNotificationI ) this.offering )
                .notifyRequestGeneratorCreated( requestNotificationInboundPortURI , rdnopUri );
    }

}