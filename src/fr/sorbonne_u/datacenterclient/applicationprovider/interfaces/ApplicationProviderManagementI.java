package fr.sorbonne_u.datacenterclient.applicationprovider.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ApplicationProviderManagementI extends OfferedI, RequiredI {

	   
	public void sendApplication(int nbVM) throws Exception;

    public void stopApplication() throws Exception;

 
}