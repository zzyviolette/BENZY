package fr.sorbonne_u.datacenter.software.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationSubmissionI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;

public class ApplicationSubmissionOutboundPort extends AbstractOutboundPort implements ApplicationSubmissionI {

	public ApplicationSubmissionOutboundPort(ComponentI owner) throws Exception {
		super(ApplicationSubmissionI.class, owner);
	}

	public ApplicationSubmissionOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ApplicationSubmissionI.class, owner);
	}

	@Override
	public void acceptSubmitApplicationAndNotify(final RequestI r) throws Exception {
		 ((ApplicationSubmissionI) this.connector).acceptSubmitApplicationAndNotify(r);
	}

}
