package fr.sorbonne_u.datacenter.software.admissioncontroller.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;

public class AdmissionControllerManagementInboundPort extends AbstractInboundPort
implements AdmissionControllerManagementI{
	  private static final long serialVersionUID = 1L;

	    public AdmissionControllerManagementInboundPort( ComponentI owner ) throws Exception {
	        super( AdmissionControllerManagementI.class , owner );

	        assert owner instanceof AdmissionControllerManagementI;
	    }

	    public AdmissionControllerManagementInboundPort( String uri , ComponentI owner ) throws Exception {
	        super( uri , AdmissionControllerManagementI.class , owner );

	        assert uri != null && owner instanceof AdmissionControllerManagementI;
	    }


}
