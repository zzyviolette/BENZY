package fr.sorbonne_u.datacenterclient.tests;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.admissioncontroller.AdmissionController;
import fr.sorbonne_u.datacenter.software.admissioncontroller.connectors.AdmissionControllerManagementConnector;
import fr.sorbonne_u.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
import fr.sorbonne_u.datacenter.software.admissioncontroller.ports.AdmissionControllerManagementOutboundPort;
import fr.sorbonne_u.datacenterclient.applicationprovider.ApplicationProvider;
import fr.sorbonne_u.datacenterclient.applicationprovider.connectors.ApplicationProviderManagementConnector;
import fr.sorbonne_u.datacenterclient.applicationprovider.interfaces.ApplicationProviderManagementI;
import fr.sorbonne_u.datacenterclient.applicationprovider.ports.ApplicationProviderManagementOutboundPort;

public class TestAdmissionController extends AbstractCVM {
	
	
	public static final String ComputerServicesInboundPortURI = "cs-ibp";
	public static final String ComputerStaticStateDataInboundPortURI = "css-dip";
	public static final String ComputerDynamicStateDataInboundPortURI = "cds-dip";
	
	
	public static final String AdmissionControllerManagementInboundPortURI = "ac-mip";
	public static final String ApplicationProviderManagementInboundPortURI = "ap-mip";
	public static final String ApplicationSubmissionInboundPortURI = "asip";
	public static final String ApplicationNotificationInboundPortURI = "anip";

	protected ApplicationProvider                   ap;
	protected AdmissionController                   ac;
	protected ComputerMonitor						cm ;
		
	protected ApplicationProviderManagementOutboundPort apmop;
	protected AdmissionControllerManagementOutboundPort acmop;

	
	public TestAdmissionController() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public void deploy() throws Exception {
		// TODO Auto-generated method stub
		Processor.DEBUG = true ;


		// --------------------------------------------------------------------
		// Create and deploy a computer component with its 2 processors and
		// each with 2 cores.
		// --------------------------------------------------------------------
		String computerURI = "computer0";
		int numberOfProcessors = 2;
		int numberOfCores = 4;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>();
		admissibleFrequencies.add(1500); // Cores can run at 1,5 GHz
		admissibleFrequencies.add(3000); // and at 3 GHz
		Map<Integer, Integer> processingPower = new HashMap<Integer, Integer>();
		processingPower.put(1500, 1500000); // 1,5 GHz executes 1,5 Mips
		processingPower.put(3000, 3000000); // 3 GHz executes 3 Mips
		Computer c = new Computer(computerURI, admissibleFrequencies, processingPower, 1500, // Test
																								// scenario
																								// 1,
																								// frequency
																								// =
																								// 1,5
																								// GHz
				// 3000, // Test scenario 2, frequency = 3 GHz
				1500, // max frequency gap within a processor
				numberOfProcessors, numberOfCores, ComputerServicesInboundPortURI,
				ComputerStaticStateDataInboundPortURI, ComputerDynamicStateDataInboundPortURI);
		this.addDeployedComponent(c);
		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// Create the computer monitor component and connect its to ports
		// with the computer component.
		// --------------------------------------------------------------------
		this.cm = new ComputerMonitor(computerURI, true, ComputerStaticStateDataInboundPortURI,
				ComputerDynamicStateDataInboundPortURI);
		this.addDeployedComponent(this.cm);

		this.ac = new AdmissionController("Admission Control", AdmissionControllerManagementInboundPortURI,
				ApplicationSubmissionInboundPortURI, ApplicationNotificationInboundPortURI,ComputerServicesInboundPortURI);

		this.addDeployedComponent(this.ac);
		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		this.ac.toggleTracing();
		this.ac.toggleLogging();	
		
		this.ap = new ApplicationProvider("Application Provider", ApplicationProviderManagementInboundPortURI,
				ApplicationSubmissionInboundPortURI, ApplicationNotificationInboundPortURI);

		this.addDeployedComponent(this.ap);
		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		this.ap.toggleTracing();
		this.ap.toggleLogging();
		
		
		this.ap.addRequiredInterface(ApplicationProviderManagementI.class) ;
		this.apmop = new ApplicationProviderManagementOutboundPort(this.ap) ;
		this.apmop.publishPort();
		
		this.ac.addRequiredInterface(AdmissionControllerManagementI.class) ;
		this.acmop = new AdmissionControllerManagementOutboundPort(this.ac) ;
		this.acmop.publishPort();
		
		super.deploy();
		
	}
	
	
	
	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub
	
		this.apmop.doConnection(ApplicationProviderManagementInboundPortURI,ApplicationProviderManagementConnector.class.getCanonicalName() );
		this.acmop.doConnection(AdmissionControllerManagementInboundPortURI,AdmissionControllerManagementConnector.class.getCanonicalName() );;
		super.start();

	}

	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		super.execute();
		this.apmop.sendApplication();
		
	}
	
	
	
	@Override
	public void			finalise() throws Exception
	{
		if(this.apmop.connected()) this.apmop.doDisconnection();
		if(this.acmop.connected()) this.acmop.doDisconnection();
		super.finalise();
	}
	
	public static void	main(String[] args)
	{
		// Uncomment next line to execute components in debug mode.
		// AbstractCVM.toggleDebugMode() ;
		try {
			final TestAdmissionController test = new TestAdmissionController() ;
			test.startStandardLifeCycle(1000000L) ;//10000
			Thread.sleep(10000L) ;
	//		 Exit from Java.
			System.exit(0) ;
		   
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}



}

