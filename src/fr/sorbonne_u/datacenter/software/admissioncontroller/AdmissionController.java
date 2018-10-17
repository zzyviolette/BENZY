package fr.sorbonne_u.datacenter.software.admissioncontroller;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;

public class AdmissionController extends AbstractComponent
implements AdmissionControllerManagementI{
	
	public AdmissionController(){
	super(1, 1);
	}



	public void notifyRequestGeneratorCreated(String requestNotificationInboundPortURI, String rdnop) {
		// TODO Auto-generated method stub
		
	}



	public String[] submitApplication(int nbVM) {
		// TODO Auto-generated method stub
		return null;
	}





}
