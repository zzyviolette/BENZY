package fr.sorbonne_u.datacenterclient.applicationprovider;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.software.connectors.ApplicationNotificationConnector;
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
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementInboundPort;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;
import fr.sorbonne_u.datacenterclient.utils.TimeProcessing;

public class ApplicationProvider extends AbstractComponent {

	/** the URI of the component. */
	protected String apURI;

	/** the inbound port used to send/stop application **/
	protected ApplicationProviderManagementInboundPort apmip;

	protected String asipURI;
	protected String anipURI;
	
	protected ApplicationSubmissionOutboundPort asop;
	protected ApplicationNotificationOutboundPort anop;

	protected RequestGenerator rg;
	protected RequestGeneratorManagementOutboundPort rgmop;
	
	
    /** Request generator management outbound port */
    protected String rgmipUri = "rgmip";

	
	public static final String RequestGeneratorManagementInboundPortURI = "rgmip";

	public ApplicationProvider(String apURI, String apmip, String asip, String anip) throws Exception {
		super(1, 1);
		assert apURI !=null && apmip !=null && asip !=null && anip !=null;
		this.apURI = apURI;
		this.asipURI = asip;
		this.anipURI = anip;

		this.addOfferedInterface(ApplicationProviderManagementI.class);
		this.apmip = new ApplicationProviderManagementInboundPort(apmip, this);
		this.addPort(this.apmip);
		this.apmip.publishPort();

		this.addRequiredInterface(ApplicationSubmissionI.class);
		this.asop = new ApplicationSubmissionOutboundPort(this);
		this.addPort(this.asop);
		this.asop.publishPort();
		
		this.addRequiredInterface(ApplicationNotificationI.class);
		this.anop = new ApplicationNotificationOutboundPort(this);
		this.addPort(this.anop);
		this.anop.publishPort();

		// TODO Auto-generated constructor stub

	}

	@Override
	public void start() throws ComponentStartException {
		super.start();

		try {
			this.doPortConnection(this.asop.getPortURI(), asipURI,
					ApplicationSubmissionConnector.class.getCanonicalName());
			this.doPortConnection(this.anop.getPortURI(), anipURI,
					ApplicationNotificationConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	public void sendApplication() throws Exception {
		// TODO Auto-generated method stub
//		Request r = new Request("demande - 0", 100000);
		this.logMessage(this.apURI + " envoye la demande au control");
//		this.asop.acceptSubmitApplicationAndNotify(r);
		String res[] = this.asop.submitApplication( 4 );
		System.out.println(res[0]+"***"+res[1]);
		
		this.rg = new RequestGenerator("rg", // generator component URI
				100.0, // mean time between two requests 500
				6000000000L, // mean number of instructions in requests
				RequestGeneratorManagementInboundPortURI, res[0],
				res[1]);
//		AbstractCVM.getCVM().addDeployedComponent(rg);

		// Toggle on tracing and logging in the request generator to
		// follow the submission and end of execution notification of
		// individual requests.
		this.rg.toggleTracing();
		this.rg.toggleLogging();
		
		this.rgmop = new RequestGeneratorManagementOutboundPort(this);
        this.rgmop.publishPort();
        this.rgmop.doConnection( this.rgmipUri , RequestGeneratorManagementConnector.class.getCanonicalName() );
		
        this.anop.notifyRequestGeneratorCreated( "test1" , "test2");
        
		this.rg.start();
		this.rgmop.startGeneration() ;
//		// wait 20 seconds
		Thread.sleep(2000L) ;
//		// then stop the generation.
		this.stopApplication();
        
	}

	public void stopApplication() {
		// TODO Auto-generated method stub
		try {
			this.rgmop.stopGeneration() ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void			finalise() throws Exception
	{
           	
	      if ( this.asop.connected() ) {
              this.asop.doDisconnection();
          }
          if ( this.rgmop.connected() ) {
              this.rgmop.doDisconnection();
          }
          if ( this.anop.connected() ) {
              this.anop.doDisconnection();
          }
		
		super.finalise() ;
	}

	@Override
	public void shutdown() throws ComponentShutdownException {

		try {
			if (this.rgmop.isPublished()) this.rgmop.unpublishPort();
			if (this.asop.isPublished()) this.asop.unpublishPort();
			if (this.asop.isPublished()) this.asop.unpublishPort();
			
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}

		super.shutdown();
	}	
	
 

}
