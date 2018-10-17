package fr.sorbonne_u.datacenterclient.applicationprovider.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenterclient.applicationprovider.interfaces.ApplicationProviderManagementI;

public class ApplicationProviderManagementConnector extends AbstractConnector
implements ApplicationProviderManagementI {

@Override
public void sendApplication() throws Exception {
( ( ApplicationProviderManagementI ) this.offering ).sendApplication();
}

@Override
public void stopApplication() throws Exception {
( ( ApplicationProviderManagementI ) this.offering ).stopApplication();
}

}