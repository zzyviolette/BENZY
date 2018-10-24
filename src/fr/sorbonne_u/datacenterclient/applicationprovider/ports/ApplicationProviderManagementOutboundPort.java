package fr.sorbonne_u.datacenterclient.applicationprovider.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.sorbonne_u.datacenterclient.applicationprovider.interfaces.ApplicationProviderManagementI;

public class ApplicationProviderManagementOutboundPort extends AbstractOutboundPort
implements ApplicationProviderManagementI {

	

public				ApplicationProviderManagementOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ApplicationProviderManagementI.class, owner) ;
	}
public ApplicationProviderManagementOutboundPort( String uri , ComponentI owner ) throws Exception {
super( uri , ApplicationProviderManagementI.class , owner );
}

@Override
public void sendApplication() throws Exception {
( ( ApplicationProviderManagementI ) this.connector ).sendApplication();;

}

@Override
public void stopApplication() throws Exception {
( ( ApplicationProviderManagementI ) this.connector ).stopApplication();;
}

}
