//package fr.sorbonne_u.datacenterclient.tests;
//
//import fr.sorbonne_u.components.cvm.AbstractCVM;
//import fr.sorbonne_u.datacenter.software.admissioncontroller.AdmissionController;
//import fr.sorbonne_u.datacenter.software.admissioncontroller.connectors.AdmissionControllerManagementConnector;
//import fr.sorbonne_u.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
//import fr.sorbonne_u.datacenter.software.admissioncontroller.ports.AdmissionControllerManagementOutboundPort;
//import fr.sorbonne_u.datacenterclient.applicationprovider.ApplicationProvider;
//import fr.sorbonne_u.datacenterclient.applicationprovider.connectors.ApplicationProviderManagementConnector;
//import fr.sorbonne_u.datacenterclient.applicationprovider.interfaces.ApplicationProviderManagementI;
//import fr.sorbonne_u.datacenterclient.applicationprovider.ports.ApplicationProviderManagementOutboundPort;
//
//
//public class TestAPAndCA extends AbstractCVM {
//
//	public static final String AdmissionControllerManagementInboundPortURI = "ac-mip";
//	public static final String ApplicationProviderManagementInboundPortURI = "ap-mip";
//	public static final String ApplicationSubmissionInboundPortURI = "asip";
//	public static final String ApplicationNotificationInboundPortURI = "anip";
//	
//	protected ApplicationProviderManagementOutboundPort apmop;
//	protected AdmissionControllerManagementOutboundPort acmop;
//
//	protected ApplicationProvider ap;
//	protected AdmissionController ac;
//	
//
//	public TestAPAndCA() throws Exception {
//		super();
//		// TODO Auto-generated constructor stub
//	}
//
//	@Override
//	public void deploy() throws Exception {
//		// TODO Auto-generated method stub
//
//		this.ap = new ApplicationProvider("app provider", ApplicationProviderManagementInboundPortURI,
//				ApplicationSubmissionInboundPortURI, ApplicationNotificationInboundPortURI);
//
//		this.addDeployedComponent(this.ap);
//		// Toggle on tracing and logging in the application virtual machine to
//		// follow the execution of individual requests.
//		this.ap.toggleTracing();
//		this.ap.toggleLogging();
//		
//		this.ac = new AdmissionController("admission control", AdmissionControllerManagementInboundPortURI,
//				ApplicationSubmissionInboundPortURI, ApplicationNotificationInboundPortURI);
//
//		this.addDeployedComponent(this.ac);
//		// Toggle on tracing and logging in the application virtual machine to
//		// follow the execution of individual requests.
//		this.ac.toggleTracing();
//		this.ac.toggleLogging();
//		
//		this.ap.addRequiredInterface(ApplicationProviderManagementI.class) ;
//		this.apmop = new ApplicationProviderManagementOutboundPort(this.ap) ;
//		this.apmop.publishPort();
//		
//		this.ac.addRequiredInterface(AdmissionControllerManagementI.class) ;
//		this.acmop = new AdmissionControllerManagementOutboundPort(this.ac) ;
//		this.acmop.publishPort();
//	
//		super.deploy();
//		
//	}
//	
//	@Override
//	public void start() throws Exception {
//		// TODO Auto-generated method stub
//	
//		this.apmop.doConnection(ApplicationProviderManagementInboundPortURI,ApplicationProviderManagementConnector.class.getCanonicalName() );
//		this.acmop.doConnection(AdmissionControllerManagementInboundPortURI,AdmissionControllerManagementConnector.class.getCanonicalName() );;
//		super.start();
//
//	}
//
//	@Override
//	public void execute() throws Exception {
//		// TODO Auto-generated method stub
//		super.execute();
//		this.apmop.sendApplication();
//		
//	}
//	
//	@Override
//	public void			finalise() throws Exception
//	{
//		this.apmop.doDisconnection();
//		this.acmop.doDisconnection();
//		super.finalise();
//	}
//
//	public static void	main(String[] args)
//	{
//		// Uncomment next line to execute components in debug mode.
//		// AbstractCVM.toggleDebugMode() ;
//		try {
//			final TestAPAndCA test = new TestAPAndCA() ;
//			test.startStandardLifeCycle(10000L) ;//10000
//			Thread.sleep(5000L) ;
//			// Exit from Java.
//			System.exit(0) ;
//		} catch (Exception e) {
//			throw new RuntimeException(e) ;
//		}
//	}
//
//}
