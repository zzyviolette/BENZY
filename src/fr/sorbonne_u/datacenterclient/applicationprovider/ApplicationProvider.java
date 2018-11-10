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

	protected String asipURI;
	protected String anipURI;
		
    protected String rgURI;	
    protected String rgmipURI;
    protected String rgmopURI;
	
	protected ApplicationSubmissionOutboundPort asop;
	protected ApplicationNotificationOutboundPort anop;
	protected ApplicationProviderManagementInboundPort apmip;

	protected RequestGenerator rg;
	protected RequestGeneratorManagementOutboundPort rgmop;
	

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

		//les ports pour la connextion avec admission controller
		this.addRequiredInterface(ApplicationSubmissionI.class);
		this.asop = new ApplicationSubmissionOutboundPort(this);
		this.addPort(this.asop);
		this.asop.publishPort();
		
		this.addRequiredInterface(ApplicationNotificationI.class);
		this.anop = new ApplicationNotificationOutboundPort(this);
		this.addPort(this.anop);
		this.anop.publishPort();

		
		this.rgURI = createRgURI("rg");
		this.rgmipURI = createRgURI("rgmip");
		this.rgmopURI = createRgURI("rgmop");
	    

	}
	
	private String createRgURI(String portType){

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
		
		this.logMessage(this.apURI + " envoye la demande au control");
		String rdportURI[] = this.asop.submitApplication(nbVM);
		//si rdportURI nest pas null creer rg 
		if(rdportURI==null){
			this.logMessage("la demande de "+ this.apURI + " est refuse par AdmissionController ");
		}else{
			this.logMessage("la demande de "+ this.apURI + " est accepte par AdmissionController ");
			this.rg = new RequestGenerator(this.rgURI, // generator component URI
					100.0, // mean time between two requests 500
					6000000000L, // mean number of instructions in requests
					this.rgmipURI, rdportURI[0],rdportURI[1]);
			AbstractCVM.getCVM().addDeployedComponent( this.rg );
			this.rg.toggleTracing();
			this.rg.toggleLogging();
			
			this.rgmop = new RequestGeneratorManagementOutboundPort(this);
	        this.rgmop.publishPort();
	        this.rgmop.doConnection( this.rgmipURI , RequestGeneratorManagementConnector.class.getCanonicalName() );
	       
	        //envoyer la notification a admission controller pour lancer rd et vm
	        this.anop.notifyRequestGeneratorCreated();
	      
			this.rg.start();
			this.rgmop.startGeneration() ;
			// wait 20 seconds
			Thread.sleep(2000L) ;
			// then stop the generation.
			this.stopApplication();
	        
		}

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
	      if ( this.rgmop.connected() ) {
              this.rgmop.doDisconnection();
          } 	
	      if ( this.asop.connected() ) {
              this.asop.doDisconnection();
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
			if (this.anop.isPublished()) this.anop.unpublishPort();
			
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}

		super.shutdown();
	}	
	
 

}
