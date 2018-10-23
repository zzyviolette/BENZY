package fr.sorbonne_u.datacenterclient.applicationprovider;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.software.connectors.ApplicationSubmissionConnector;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationSubmissionI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.ApplicationNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.ApplicationNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.ApplicationSubmissionOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.sorbonne_u.datacenterclient.applicationprovider.interfaces.ApplicationProviderManagementI;
import fr.sorbonne_u.datacenterclient.applicationprovider.ports.ApplicationProviderManagementInboundPort;
import fr.sorbonne_u.datacenterclient.requestgenerator.Request;
import fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementInboundPort;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;
import fr.sorbonne_u.datacenterclient.utils.TimeProcessing;

public class ApplicationProvider extends AbstractComponent{
	
	/** the URI of the component. */
    protected String apURI;

    // ------------------------------------------------------------------
    // PORTS
    // ------------------------------------------------------------------

    /** the inbound port used to send/stop application **/
    protected ApplicationProviderManagementInboundPort apmip;
    
    protected String							asipURI ;

    protected ApplicationNotificationInboundPort anip;
    
    protected ApplicationSubmissionOutboundPort asop;


	public ApplicationProvider(String apURI ,String apmip, String asip ,
            String anip) throws Exception {
		super(1, 1);
		this.apURI = apURI;
		this.asipURI = asip;
		
		this.addOfferedInterface(ApplicationProviderManagementI.class) ;
		this.apmip = new ApplicationProviderManagementInboundPort(
										apmip, this) ;
		this.addPort(this.apmip) ;
		this.apmip.publishPort() ;

		this.addRequiredInterface(ApplicationSubmissionI.class) ;
		this.asop = new ApplicationSubmissionOutboundPort(this) ;
		this.addPort(this.asop) ;
		this.asop.publishPort() ;

		this.addOfferedInterface(ApplicationNotificationI.class) ;
		this.anip =
			new ApplicationNotificationInboundPort(
						anip, this) ;
		this.addPort(this.anip) ;
		this.anip.publishPort() ;
		
		

		// TODO Auto-generated constructor stub
		
	}
	
	
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;

		try {
			this.doPortConnection(
					this.asop.getPortURI(),
					asipURI,
					ApplicationSubmissionConnector.class.getCanonicalName()) ;
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}
	
	
	
	

	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		super.execute();
		sendApplication();
	}


	public void sendApplication() throws Exception {
		// TODO Auto-generated method stub
		Request r = new Request("demande - 0", 100000) ;
	    this.logMessage(this.apURI+ " envoye la " + r.getRequestURI() +" au control");
		this.asop.acceptSubmitApplicationAndNotify(r);		
	}

	public void stopApplication() {
		// TODO Auto-generated method stub
		
	}


	public void notifyRequestGeneratorCreated(RequestI r) {
		// TODO Auto-generated method stub
		 this.logMessage(this.apURI + " recevoit la notificatin de controle pour la " +r.getRequestURI() );
		
	}



}
