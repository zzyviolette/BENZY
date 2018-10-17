package fr.sorbonne_u.datacenter.software.admissioncontroller.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;

public class AdmissionControllerManagementOutboundPort extends AbstractOutboundPort
implements AdmissionControllerManagementI {

public AdmissionControllerManagementOutboundPort( ComponentI owner ) throws Exception {
super( AdmissionControllerManagementI.class , owner );
}

public AdmissionControllerManagementOutboundPort( String uri , ComponentI owner ) throws Exception {
super( uri , AdmissionControllerManagementI.class , owner );
}


}