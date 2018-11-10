package fr.sorbonne_u.datacenterclient.tests;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.dymaniccomponentcreator.DynamicComponentCreator;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.admissioncontroller.AdmissionController;
import fr.sorbonne_u.datacenterclient.applicationprovider.ApplicationProvider;
import fr.sorbonne_u.datacenterclient.applicationprovider.connectors.ApplicationProviderManagementConnector;
import fr.sorbonne_u.datacenterclient.applicationprovider.interfaces.ApplicationProviderManagementI;
import fr.sorbonne_u.datacenterclient.applicationprovider.ports.ApplicationProviderManagementOutboundPort;



public class TestAdmissionController2Computer extends AbstractCVM {
	


	protected AdmissionController                   ac;

	protected DynamicComponentCreator               dcc;
    
	protected String[]                       csipURIList;
	
	protected int[]                           nbAvailableCoresPerComputer;
	
	protected ArrayList<ApplicationProviderManagementOutboundPort>    apmopList;
	
	protected int sumComputer = 2;
	
	protected int sumApp = 2;
	
	
	
	public TestAdmissionController2Computer() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public void deploy() throws Exception {
		// TODO Auto-generated method stub
		Processor.DEBUG = true ;
        
		csipURIList = new String[sumComputer];
		nbAvailableCoresPerComputer = new int[sumComputer];
		for(int i =0; i< sumComputer; i++){
            /**senario accept**/
			int numberOfProcessors = 2;
			int numberOfCores = 2;
//			  /**senario refuse 1 demande de app**/
//			//le premier computer a 2 cores au total , le deuxieme computer a 4 cores au total
//			int numberOfProcessors = 2;
//			int numberOfCores = (i+1);
			Set<Integer> admissibleFrequencies = new HashSet<Integer>();
			admissibleFrequencies.add(1500); // Cores can run at 1,5 GHz
			admissibleFrequencies.add(3000); // and at 3 GHz
			Map<Integer, Integer> processingPower = new HashMap<Integer, Integer>();
			processingPower.put(1500, 1500000); // 1,5 GHz executes 1,5 Mips
			processingPower.put(3000, 3000000); // 3 GHz executes 3 Mips
			Computer c = new Computer("computer"+i, admissibleFrequencies, processingPower, 1500, // Test
																									// scenario
																									// 1,
																									// frequency
																									// =
																									// 1,5
																									// GHz
					// 3000, // Test scenario 2, frequency = 3 GHz
					1500, // max frequency gap within a processor
					numberOfProcessors, numberOfCores, "csip"+i,
					"cssdip"+i, "cdsdip"+i);
		    csipURIList[i] = "csip"+i;
		    nbAvailableCoresPerComputer[i] = numberOfProcessors * numberOfCores;
			this.addDeployedComponent(c);
			
		}
		// --------------------------------------------------------------------
		// Create and deploy a computer component with its 2 processors and
		// each with 2 cores.
		// --------------------------------------------------------------------
		
		
		// --------------------------------------------------------------------

		// --------------------------------------------------------------------
		// Create the computer monitor component and connect its to ports
		// with the computer component.
		// --------------------------------------------------------------------
//		this.cm = new ComputerMonitor(computerURI, true, cssdipURI,
//				cdsdipURI);
//		this.addDeployedComponent(this.cm);
		
	    
		/*
		 * Creation du dynamic component creator
		 */
		this.dcc = new DynamicComponentCreator("dccipURI");
		this.addDeployedComponent(dcc);

		this.ac = new AdmissionController("Admission Controller", "acmipURI",
				"asipURI", "anipURI",csipURIList,nbAvailableCoresPerComputer,"dccipURI");

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
			this.apmopList.get(i).sendApplication(2);
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
			final TestAdmissionController2Computer test = new TestAdmissionController2Computer() ;
			test.startStandardLifeCycle(100000L) ;//10000
			Thread.sleep(10000L) ;//10000
	//		 Exit from Java.
			System.exit(0) ;
		   
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}



}

