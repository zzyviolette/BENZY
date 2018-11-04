package fr.sorbonne_u.datacenter.software.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;

public class ApplicationNotificationConnector extends AbstractConnector implements ApplicationNotificationI {

    @Override
    public void notifyRequestGeneratorCreated( String apURI)
            throws Exception {
        ( ( ApplicationNotificationI ) this.offering )
                .notifyRequestGeneratorCreated( apURI );
    }


}