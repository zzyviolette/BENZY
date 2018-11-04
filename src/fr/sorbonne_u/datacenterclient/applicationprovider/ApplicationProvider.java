package fr.sorbonne_u.datacenterclient.applicationprovider;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.software.connectors.ApplicationNotificationConnector;
import fr.sorbonne_u.datacenter.software.connectors.ApplicationSubmissionConnector;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.ApplicationNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.ApplicationSubmissionOutboundPort;
import fr.sorbonne_u.datacenterclient.applicationprovider.interfaces.ApplicationProviderManagementI;
import fr.sorbonne_u.datacenterclient.applicationprovider.ports.ApplicationProviderManagementInboundPort;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

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
	
	protected String rgURI;
	
    protected String rgmipURI;
    protected String rgmopURI;

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
		this.rgURI = createRGURI("rg");
		this.rgmipURI = createRGURI("rgmip");
		this.rgmopURI = createRGURI("rgmop");
	    

	}
	
	private String createRGURI(String portType){

       return  this.apURI + "-" + portType;
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

	public void sendApplication(int nbVM) throws Exception {
		// TODO Auto-generated method stub

		this.logMessage(this.apURI + " envoye la demande au control");
		String res[] = this.asop.submitApplication(this.apURI, nbVM );
		System.out.println("ap"+"*************"+this.apURI);
		System.out.println("ap"+"*************"+res[0] + "*********" + res[1]);
		this.rg = new RequestGenerator(this.rgURI, // generator component URI
				100.0, // mean time between two requests 500
				6000000000L, // mean number of instructions in requests
				this.rgmipURI, res[0],
				res[1]);
		AbstractCVM.getCVM().addDeployedComponent( this.rg );
		this.rg.toggleTracing();
		this.rg.toggleLogging();
		
		this.rgmop = new RequestGeneratorManagementOutboundPort(this);
        this.rgmop.publishPort();
        this.rgmop.doConnection( this.rgmipURI , RequestGeneratorManagementConnector.class.getCanonicalName() );
        this.anop.notifyRequestGeneratorCreated( this.apURI );
        
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
