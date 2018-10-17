package fr.sorbonne_u.datacenter.software.requestdispatcher.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.sorbonne_u.datacenter.software.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.datacenterclient.tests.Integrator;
import fr.sorbonne_u.datacenterclient.tests.TestRequestGenerator;

public class TestRequestDispatcher extends AbstractCVM{

	
	// ------------------------------------------------------------------------
		// Constants and instance variables
		// ------------------------------------------------------------------------

		// Predefined URI of the different ports visible at the component assembly
		// level.
		public static final String	ComputerServicesInboundPortURI = "cs-ibp" ;
		public static final String	ComputerStaticStateDataInboundPortURI = "css-dip" ;
		public static final String	ComputerDynamicStateDataInboundPortURI = "cds-dip" ;
		
		public static final String	ApplicationVMManagementInboundPortURI = "avm-ibp" ;
		
		public static final String	RG_RequestSubmissionInboundPortURI = "rg-rsibp" ;
		public static final String	RG_RequestNotificationInboundPortURI = "rg-rnibp" ;
		
		public static final String	VM_RequestSubmissionInboundPortURI = "vm-rsibp" ;
		public static final String	VM_RequestNotificationInboundPortURI = "vm-rnibp" ;
		
		public static final String	RequestGeneratorManagementInboundPortURI = "rgmip" ;		
		public static final String	RequestDispatcherManagementInboundPortURI = "rdmip" ;
		

		/** 	Computer monitor component.										*/
		protected ComputerMonitor						cm ;
		/** 	Application virtual machine component.							*/
		protected ApplicationVM							vm ;
		/** 	Request generator component.										*/
		protected RequestGenerator						rg ;
		/** Integrator component.											*/
		protected Integrator								integ ;
		protected RequestDispatcher     rd;

		// ------------------------------------------------------------------------
		// Component virtual machine constructors
		// ------------------------------------------------------------------------
		public TestRequestDispatcher() throws Exception {
			super();
			// TODO Auto-generated constructor stub
		}

		// ------------------------------------------------------------------------
		// Component virtual machine methods
		// ------------------------------------------------------------------------

		@Override
		public void			deploy() throws Exception
		{
			Processor.DEBUG = true ;

			// --------------------------------------------------------------------
			// Create and deploy a computer component with its 2 processors and
			// each with 2 cores.
			// --------------------------------------------------------------------
			String computerURI = "computer0" ;
			int numberOfProcessors = 2 ;
			int numberOfCores = 2 ;
			Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
			admissibleFrequencies.add(1500) ;	// Cores can run at 1,5 GHz
			admissibleFrequencies.add(3000) ;	// and at 3 GHz
			Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
			processingPower.put(1500, 1500000) ;	// 1,5 GHz executes 1,5 Mips
			processingPower.put(3000, 3000000) ;	// 3 GHz executes 3 Mips
			Computer c = new Computer(
								computerURI,
								admissibleFrequencies,
								processingPower,  
								1500,		// Test scenario 1, frequency = 1,5 GHz
								// 3000,	// Test scenario 2, frequency = 3 GHz
								1500,		// max frequency gap within a processor
								numberOfProcessors,
								numberOfCores,
								ComputerServicesInboundPortURI,
								ComputerStaticStateDataInboundPortURI,
								ComputerDynamicStateDataInboundPortURI) ;
			this.addDeployedComponent(c) ;
			// --------------------------------------------------------------------

			// --------------------------------------------------------------------
			// Create the computer monitor component and connect its to ports
			// with the computer component.
			// --------------------------------------------------------------------
			this.cm = new ComputerMonitor(computerURI,
										 true,
										 ComputerStaticStateDataInboundPortURI,
										 ComputerDynamicStateDataInboundPortURI) ;
			this.addDeployedComponent(this.cm) ;
			// --------------------------------------------------------------------

			// --------------------------------------------------------------------
			// Create an Application VM component
			// --------------------------------------------------------------------
			this.vm = new ApplicationVM("vm0",	// application vm component URI
									    ApplicationVMManagementInboundPortURI,
									    VM_RequestSubmissionInboundPortURI,
									    VM_RequestNotificationInboundPortURI) ;
			this.addDeployedComponent(this.vm) ;
			// Toggle on tracing and logging in the application virtual machine to
			// follow the execution of individual requests.
			this.vm.toggleTracing() ;
			this.vm.toggleLogging() ;
			// --------------------------------------------------------------------

			// --------------------------------------------------------------------
			// Creating the request generator component.
			// --------------------------------------------------------------------
			this.rg = new RequestGenerator(
						"rg",			// generator component URI
						500.0,			// mean time between two requests 500
						6000000000L,	// mean number of instructions in requests
						RequestGeneratorManagementInboundPortURI,
						RG_RequestSubmissionInboundPortURI,
						RG_RequestNotificationInboundPortURI) ;
			this.addDeployedComponent(rg) ;

			// Toggle on tracing and logging in the request generator to
			// follow the submission and end of execution notification of
			// individual requests.
			this.rg.toggleTracing() ;
			this.rg.toggleLogging() ;
			// --------------------------------------------------------------------

			this.rd= new RequestDispatcher("rd",RequestDispatcherManagementInboundPortURI,
					RG_RequestSubmissionInboundPortURI,
					RG_RequestNotificationInboundPortURI,
					VM_RequestSubmissionInboundPortURI,
					VM_RequestNotificationInboundPortURI);
			
			this.addDeployedComponent(rd) ;

			// Toggle on tracing and logging in the request generator to
			// follow the submission and end of execution notification of
			// individual requests.
			this.rd.toggleTracing() ;
			this.rd.toggleLogging() ;
			// --------------------------------------------------------------------
			// Creating the integrator component.
			// --------------------------------------------------------------------
			this.integ = new Integrator(
								ComputerServicesInboundPortURI,
								ApplicationVMManagementInboundPortURI,
								RequestGeneratorManagementInboundPortURI) ;
			this.addDeployedComponent(this.integ) ;
			// --------------------------------------------------------------------

			// complete the deployment at the component virtual machine level.
			super.deploy();
		}

		// ------------------------------------------------------------------------
		// Test scenarios and main execution.
		// ------------------------------------------------------------------------

		/**
		 * execute the test application.
		 * 
		 * @param args	command line arguments, disregarded here.
		 */
		public static void	main(String[] args)
		{
			// Uncomment next line to execute components in debug mode.
			// AbstractCVM.toggleDebugMode() ;
			try {
				final TestRequestDispatcher trd = new TestRequestDispatcher() ;
				trd.startStandardLifeCycle(10000L) ;//10000
				Thread.sleep(50000L) ;//5000
				// Exit from Java.
				System.exit(0) ;
			} catch (Exception e) {
				throw new RuntimeException(e) ;
			}
		}




}
