package fr.sorbonne_u.datacenter.software.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.software.admissioncontroller.AdmissionController;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationSubmissionI;

public class ApplicationSubmissionInboundPort extends AbstractInboundPort implements ApplicationSubmissionI {

    private static final long serialVersionUID = 1L;

    public ApplicationSubmissionInboundPort( ComponentI owner ) throws Exception {
        super( ApplicationSubmissionI.class , owner );

    }

    public ApplicationSubmissionInboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , ApplicationSubmissionI.class , owner );

    }

    @Override
    public String[] submitApplication( final int nbVM ) throws Exception {

        return this.getOwner().handleRequestSync( new AbstractComponent.AbstractService<String[]>() {

			@Override
			public String[] call() throws Exception {
				// TODO Auto-generated method stub
				return  (( AdmissionController)this.getOwner()).submitApplication(nbVM);
			}

      
        } );
    }

}

