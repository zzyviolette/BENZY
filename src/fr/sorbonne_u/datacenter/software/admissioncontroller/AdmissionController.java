package fr.sorbonne_u.datacenter.software.admissioncontroller;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
import fr.sorbonne_u.datacenter.software.admissioncontroller.ports.AdmissionControllerManagementInboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.sorbonne_u.datacenter.software.connectors.ApplicationNotificationConnector;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationSubmissionI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.ports.ApplicationNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.ApplicationSubmissionInboundPort;
import fr.sorbonne_u.datacenter.software.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.datacenter.software.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import fr.sorbonne_u.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.datacenter.software.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class AdmissionController extends AbstractComponent implements AdmissionControllerManagementI {

	protected String acURI;
	
	protected String anipURI;
	
	protected String csmipURI;

	protected AdmissionControllerManagementInboundPort acmip;

	protected ApplicationSubmissionInboundPort asip;

	protected ApplicationNotificationOutboundPort anop;


	public static final String RG_RequestSubmissionInboundPortURI = "rg-rsibp";
	public static final String RG_RequestNotificationInboundPortURI = "rg-rnibp";

	public static final String VM_RequestSubmissionInboundPortURI = "vm-rsibp";
	public static final String VM_RequestNotificationInboundPortURI = "vm-rnibp";

	public static final String RequestGeneratorManagementInboundPortURI = "rgmip";
	public static final String RequestDispatcherManagementInboundPortURI = "rdmip";
	public static final String ApplicationVMManagementInboundPortURI = "avm-ibp";

	

	protected ApplicationVM vm;

	protected RequestGenerator rg;

	protected RequestDispatcher rd;

	protected ComputerServicesOutboundPort csop;

	protected RequestGeneratorManagementOutboundPort rmop;

	protected RequestDispatcherManagementOutboundPort rdmop;

	protected ApplicationVMManagementOutboundPort avmop;

	public AdmissionController(String acURI, String acmip, String asip, String anip, String csmipURI) throws Exception {
		super(1, 1);

		// Preconditions
		assert acURI != null;
		assert acmip != null;
		assert asip != null;
		assert anip != null;
		assert csmipURI!=null;

		this.acURI = acURI;
		this.anipURI = anip;
		this.csmipURI = csmipURI;

		// connect to application provider
		this.addOfferedInterface(AdmissionControllerManagementI.class);
		this.acmip = new AdmissionControllerManagementInboundPort(acmip, this);
		this.addPort(this.acmip);
		this.acmip.publishPort();

		this.addOfferedInterface(ApplicationSubmissionI.class);
		this.asip = new ApplicationSubmissionInboundPort(asip, this);
		this.addPort(this.asip);
		this.asip.publishPort();

		this.addRequiredInterface(ApplicationNotificationI.class);
		this.anop = new ApplicationNotificationOutboundPort(this);
		this.addPort(this.anop);
		this.anop.publishPort();

	}

	@Override
	public void start() throws ComponentStartException {
		// TODO Auto-generated method stub
		super.start();
		try {

			// connect to rg
			this.doPortConnection(this.anop.getPortURI(), anipURI,
					ApplicationNotificationConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}

	}

	@Override
	public void finalise() throws Exception {
		this.doPortDisconnection(this.anop.getPortURI());
		this.doPortDisconnection(this.csop.getPortURI());
		this.doPortDisconnection(this.rmop.getPortURI());
		this.doPortDisconnection(this.avmop.getPortURI());
		this.doPortDisconnection(this.rdmop.getPortURI());
		super.finalise();
	}
	


	public void doConnect() throws Exception {
		try {
			this.doPortConnection(this.csop.getPortURI(), csmipURI,
					ComputerServicesConnector.class.getCanonicalName());
			this.doPortConnection(this.rmop.getPortURI(), RequestGeneratorManagementInboundPortURI,
					RequestGeneratorManagementConnector.class.getCanonicalName());
			this.doPortConnection(this.avmop.getPortURI(), ApplicationVMManagementInboundPortURI,
					ApplicationVMManagementConnector.class.getCanonicalName());
			this.doPortConnection(this.rdmop.getPortURI(), RequestDispatcherManagementInboundPortURI,
					RequestDispatcherManagementConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}

	}

	public void create() throws Exception {

		this.vm = new ApplicationVM("vm0", // application vm component URI
				ApplicationVMManagementInboundPortURI, VM_RequestSubmissionInboundPortURI,
				VM_RequestNotificationInboundPortURI);

		this.rg = new RequestGenerator("benzy-rg", // generator component URI
				100.0, // mean time between two requests 500
				6000000000L, // mean number of instructions in requests
				RequestGeneratorManagementInboundPortURI, RG_RequestSubmissionInboundPortURI,
				RG_RequestNotificationInboundPortURI);

		this.rd = new RequestDispatcher("benzy-rd*", RequestDispatcherManagementInboundPortURI,
				RG_RequestSubmissionInboundPortURI, RG_RequestNotificationInboundPortURI,
				VM_RequestSubmissionInboundPortURI, VM_RequestNotificationInboundPortURI);

		this.addRequiredInterface(ComputerServicesI.class);
		this.csop = new ComputerServicesOutboundPort(this);
		this.addPort(this.csop);
		this.csop.publishPort();

		this.addRequiredInterface(RequestGeneratorManagementI.class);
		this.rmop = new RequestGeneratorManagementOutboundPort(this);
		this.addPort(rmop);
		this.rmop.publishPort();
		
		this.addRequiredInterface(ApplicationVMManagementI.class);
		this.avmop = new ApplicationVMManagementOutboundPort(this);
		this.addPort(this.avmop);
		this.avmop.publishPort();

		this.addRequiredInterface(RequestDispatcherManagementI.class);
		this.rdmop = new RequestDispatcherManagementOutboundPort(this);
		this.addPort(this.rdmop);
		this.rdmop.publishPort();
		
		doConnect();

		this.vm.start();
		this.vm.toggleTracing();
		this.vm.toggleLogging();

		this.rg.start();
		this.rg.toggleTracing();
		this.rg.toggleLogging();

		this.rd.start();
		this.rd.toggleTracing();
		this.rd.toggleLogging();
		
		
		AllocatedCore[] ac = this.csop.allocateCores(4);
		this.avmop.allocateCores(ac);
		

		//commencer a generer des requetes
		this.rmop.startGeneration();
		// wait 20 seconds
		Thread.sleep(2000L);
		
//		this.rmop.startGeneration();
//		// wait 20 seconds
//		Thread.sleep(2000L);
		
		// then stop the generation.
		this.rmop.stopGeneration();
		

	}

	public void acceptSubmitApplicationAndNotify(final RequestI r) throws Exception {
		// TODO Auto-generated method stub
		this.logMessage(this.acURI + " recevoir la " + r.getRequestURI());
		
		// verifier les ressources
		this.logMessage( "verifier les ressources " );
		Thread.sleep(3000L);
		// Thread.sleep(2000L);
		this.logMessage(this.acURI + " accepte la demande et envoye la notification a l'application ");
		this.anop.notifyRequestGeneratorCreated(r);
		create();
		

	}

}
