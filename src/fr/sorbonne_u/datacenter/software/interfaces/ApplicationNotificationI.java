package fr.sorbonne_u.datacenter.software.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface ApplicationNotificationI extends OfferedI, RequiredI {

    public void notifyRequestGeneratorCreated( String requestNotificationInboundPortURI , String rdnopUri ) throws Exception;

}