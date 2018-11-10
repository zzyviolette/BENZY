package fr.sorbonne_u.datacenter.software.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationSubmissionI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;

public class ApplicationSubmissionConnector extends AbstractConnector implements ApplicationSubmissionI {


	@Override
	public String[] submitApplication(int nbVM) throws Exception {
		// TODO Auto-generated method stub
		  return ( ( ApplicationSubmissionI ) this.offering ).submitApplication(nbVM );
	}

}