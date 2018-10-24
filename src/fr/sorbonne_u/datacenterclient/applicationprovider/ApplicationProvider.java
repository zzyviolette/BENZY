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
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementInboundPort;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;
import fr.sorbonne_u.datacenterclient.utils.TimeProcessing;

public class ApplicationProvider extends AbstractComponent {

	/** the URI of the component. */
	protected String apURI;
	public static final String RequestGeneratorManagementInboundPortURI = "rgmip";
	protected RequestGeneratorManagementOutboundPort rmop;

	// ------------------------------------------------------------------
	// PORTS
	// ------------------------------------------------------------------

	/** the inbound port used to send/stop application **/
	protected ApplicationProviderManagementInboundPort apmip;

	protected String asipURI;

	protected ApplicationNotificationInboundPort anip;

	protected ApplicationSubmissionOutboundPort asop;
	
	protected RequestGenerator rg;
	
	protected String[] portInfos;
	
	

	public ApplicationProvider(String apURI, String apmip, String asip, String anip) throws Exception {
		super(1, 1);
		this.apURI = apURI;
		this.asipURI = asip;

		this.addOfferedInterface(ApplicationProviderManagementI.class);
		this.apmip = new ApplicationProviderManagementInboundPort(apmip, this);
		this.addPort(this.apmip);
		this.apmip.publishPort();

		this.addRequiredInterface(ApplicationSubmissionI.class);
		this.asop = new ApplicationSubmissionOutboundPort(this);
		this.addPort(this.asop);
		this.asop.publishPort();

		this.addOfferedInterface(ApplicationNotificationI.class);
		this.anip = new ApplicationNotificationInboundPort(anip, this);
		this.addPort(this.anip);
		this.anip.publishPort();

		// TODO Auto-generated constructor stub

	}

	@Override
	public void start() throws ComponentStartException {
		super.start();

		try {
			this.doPortConnection(this.asop.getPortURI(), asipURI,
					ApplicationSubmissionConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	public void sendApplication() throws Exception {
		// TODO Auto-generated method stub
		Request r = new Request("demande - 0", 100000);
		this.logMessage(this.apURI + " envoye la " + r.getRequestURI() + " au control");
		this.asop.acceptSubmitApplicationAndNotify(r);
	}

	public void stopApplication() {
		// TODO Auto-generated method stub

	}
	
	public void createRG() throws Exception{
		this.rg = new RequestGenerator("benzy-rg", // generator component URI
				100.0, // mean time between two requests 500
				6000000000L, // mean number of instructions in requests
				RequestGeneratorManagementInboundPortURI, this.portInfos[0],
				this.portInfos[1]);
		
		this.addRequiredInterface(RequestGeneratorManagementI.class);
		this.rmop = new RequestGeneratorManagementOutboundPort(this);
		this.addPort(rmop);
		this.rmop.publishPort();
		
		this.doPortConnection(this.rmop.getPortURI(), RequestGeneratorManagementInboundPortURI,
		RequestGeneratorManagementConnector.class.getCanonicalName());
		
		this.rg.start();
		this.rg.toggleTracing();
		this.rg.toggleLogging();
		
		this.rmop.startGeneration();
		// wait 20 seconds
		Thread.sleep(2000L);
		
		// then stop the generation.
		this.rmop.stopGeneration();
		
	}

	public void notifyRequestGeneratorCreated(RequestI r) throws Exception {
		// TODO Auto-generated method stub
		this.logMessage(this.apURI + " recevoit la notificatin de controle pour la " + r.getRequestURI());

	}

}
