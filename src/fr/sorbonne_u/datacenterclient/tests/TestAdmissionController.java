package fr.sorbonne_u.datacenterclient.tests;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.admissioncontroller.AdmissionController;
import fr.sorbonne_u.datacenterclient.applicationprovider.ApplicationProvider;
import fr.sorbonne_u.datacenterclient.applicationprovider.connectors.ApplicationProviderManagementConnector;
import fr.sorbonne_u.datacenterclient.applicationprovider.interfaces.ApplicationProviderManagementI;
import fr.sorbonne_u.datacenterclient.applicationprovider.ports.ApplicationProviderManagementOutboundPort;

public class TestAdmissionController extends AbstractCVM {
	


	protected AdmissionController                   ac;
	protected ComputerMonitor						cm ;
		
	protected ArrayList<ApplicationProviderManagementOutboundPort>    apmopList;

	
	protected int sumComputer = 0;
	
	protected int sumApp = 3;
	
	
	
	public TestAdmissionController() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}
	
	private String createComputerURI(String portType){
		return portType +  sumComputer;
	}
	


	@Override
	public void deploy() throws Exception {
		// TODO Auto-generated method stub
		Processor.DEBUG = true ;

		// --------------------------------------------------------------------
		// Create and deploy a computer component with its 2 processors and
		// each with 2 cores.
		// --------------------------------------------------------------------
		String computerURI= createComputerURI("computer");
		String csipURI= createComputerURI("csip");
		String cssdipURI= createComputerURI("cssdip");
		String cdsdipURI= createComputerURI("cdsdip");
		int numberOfProcessors = 6;
		int numberOfCores = 2;
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
				numberOfProcessors, numberOfCores, csipURI,
				cssdipURI, cdsdipURI);
		this.addDeployedComponent(c);
		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// Create the computer monitor component and connect its to ports
		// with the computer component.
		// --------------------------------------------------------------------
		this.cm = new ComputerMonitor(computerURI, true, cssdipURI,
				cdsdipURI);
		this.addDeployedComponent(this.cm);
		
		sumComputer++;
	    

		this.ac = new AdmissionController("Admission Controller", "acmipURI",
				"asipURI", "anipURI",csipURI);

		this.addDeployedComponent(this.ac);
		// Toggle on tracing and logging in the application virtual machine to
		// follow the execution of individual requests.
		this.ac.toggleTracing();
		this.ac.toggleLogging();
		
		
		this.apmopList = new ArrayList<>();
		
		for(int i = 0; i< sumApp; i++){
			ApplicationProvider ap = new ApplicationProvider("ap"+i, "apmip" + i,
					"asipURI", "anipURI");
			this.addDeployedComponent(ap);
			ap.toggleTracing();
			ap.toggleLogging();
			
			ap.addRequiredInterface(ApplicationProviderManagementI.class) ;
			ApplicationProviderManagementOutboundPort apmop = new ApplicationProviderManagementOutboundPort("apmop"+i,ap) ;
			apmop.publishPort();
			this.apmopList.add(apmop);
		}
				
		super.deploy();
		
	}
	
	
	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub
	
		for(int i = 0; i< this.apmopList.size(); i++){
			System.out.println(this.apmopList.size());
			this.apmopList.get(i).doConnection("apmip" + i,ApplicationProviderManagementConnector.class.getCanonicalName() );
		}
		super.start();

	}

	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		super.execute();
		for(int i =0 ; i<this.apmopList.size(); i++){
			this.apmopList.get(i).sendApplication((i+1));
		}
		
	}
	
	
	
	@Override
	public void			finalise() throws Exception
	{
		for(int i =0 ; i<this.apmopList.size(); i++){
			if(this.apmopList.get(i).connected()) this.apmopList.get(i).doDisconnection();
		}
		super.finalise();
	}
	
	public static void	main(String[] args)
	{
		// Uncomment next line to execute components in debug mode.
		// AbstractCVM.toggleDebugMode() ;
		try {
			final TestAdmissionController test = new TestAdmissionController() ;
			test.startStandardLifeCycle(10000L) ;//10000
			Thread.sleep(10000L) ;//10000
	//		 Exit from Java.
			System.exit(0) ;
		   
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}



}

