package fr.sorbonne_u.datacenter.software.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ApplicationSubmissionI extends OfferedI, RequiredI{
	public void acceptSubmitApplicationAndNotify(final RequestI r) throws Exception;
}
