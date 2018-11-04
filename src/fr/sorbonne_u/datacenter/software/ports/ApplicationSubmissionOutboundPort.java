package fr.sorbonne_u.datacenter.software.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationSubmissionI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;

public class ApplicationSubmissionOutboundPort extends AbstractOutboundPort implements ApplicationSubmissionI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ApplicationSubmissionOutboundPort(ComponentI owner) throws Exception {
		super(ApplicationSubmissionI.class, owner);
	}

	public ApplicationSubmissionOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ApplicationSubmissionI.class, owner);
	}

	@Override
	public String[] submitApplication(String apURI, int nbVM) throws Exception {
		// TODO Auto-generated method stub
		return ( ( ApplicationSubmissionI ) this.connector ).submitApplication( apURI,nbVM );
	}

}
