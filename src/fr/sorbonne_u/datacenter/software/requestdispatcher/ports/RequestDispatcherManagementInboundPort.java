package fr.sorbonne_u.datacenter.software.requestdispatcher.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.software.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;

public class RequestDispatcherManagementInboundPort extends AbstractInboundPort
implements RequestDispatcherManagementI{

    private static final long serialVersionUID = 1L;

    public RequestDispatcherManagementInboundPort( ComponentI owner ) throws Exception {
        super( RequestDispatcherManagementI.class , owner );

        assert owner instanceof RequestDispatcherManagementI;
    }

    public RequestDispatcherManagementInboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , RequestDispatcherManagementI.class , owner );

        assert uri != null && owner instanceof RequestDispatcherManagementI;
    }


}
