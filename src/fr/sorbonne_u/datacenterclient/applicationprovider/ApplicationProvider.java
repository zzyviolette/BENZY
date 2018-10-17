package fr.sorbonne_u.datacenterclient.applicationprovider;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.software.connectors.ApplicationSubmissionConnector;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationSubmissionI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.ApplicationNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.ApplicationNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.ApplicationSubmissionOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.sorbonne_u.datacenterclient.applicationprovider.interfaces.ApplicationProviderManagementI;
import fr.sorbonne_u.datacenterclient.applicationprovider.ports.ApplicationProviderManagementInboundPort;
import fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementInboundPort;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class ApplicationProvider extends AbstractComponent{
	
	/** the URI of the component. */
    protected String apURI;

    // ------------------------------------------------------------------
    // PORTS
    // ------------------------------------------------------------------

    protected RequestGeneratorManagementOutboundPort rgmop;

    protected ApplicationSubmissionOutboundPort asop;
    protected ApplicationNotificationInboundPort anip;
   

    /** the inbound port used to send/stop application **/
    protected ApplicationProviderManagementInboundPort apmip;
    
    protected String							applicationSubmissionInboundPortURI ;

    // ------------------------------------------------------------------
    // REQUEST GENERATOR URIs
    // ------------------------------------------------------------------
    /** RequestGenerator URI */
    protected String rgUri;

    /** Request generator management inbound port */
    protected String rgmipUri;

    /** Request submission outbound port */
    protected String rsopUri;

    /** Request notification inbound port */
    protected String rnipUri;

    /** Request generator management outbound port */
    protected String rgmopUri;

	public ApplicationProvider(String apURI ,String apManagementInboundPort, String applicationSubmissionInboundPortURI ,
            String applicationNotificationInboundPortURI) throws Exception {
		super(1, 1);
		this.apURI = apURI;
		this.applicationSubmissionInboundPortURI = applicationSubmissionInboundPortURI;
		
		this.addOfferedInterface(ApplicationProviderManagementI.class) ;
		this.apmip = new ApplicationProviderManagementInboundPort(
										apManagementInboundPort, this) ;
		this.addPort(this.apmip) ;
		this.apmip.publishPort() ;

		this.addRequiredInterface(ApplicationSubmissionI.class) ;
		this.asop = new ApplicationSubmissionOutboundPort(this) ;
		this.addPort(this.asop) ;
		this.asop.publishPort() ;

		this.addOfferedInterface(ApplicationNotificationI.class) ;
		this.anip =
			new ApplicationNotificationInboundPort(
						applicationNotificationInboundPortURI, this) ;
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
					applicationSubmissionInboundPortURI,
					ApplicationSubmissionConnector.class.getCanonicalName()) ;
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}

	public void sendApplication() {
		// TODO Auto-generated method stub
		
	}

	public void stopApplication() {
		// TODO Auto-generated method stub
		
	}

}
