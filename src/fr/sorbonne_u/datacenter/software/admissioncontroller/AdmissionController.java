package fr.sorbonne_u.datacenter.software.admissioncontroller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.software.admissioncontroller.connectors.AdmissionControllerManagementConnector;
import fr.sorbonne_u.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
import fr.sorbonne_u.datacenter.software.admissioncontroller.ports.AdmissionControllerManagementInboundPort;
import fr.sorbonne_u.datacenter.software.admissioncontroller.ports.AdmissionControllerManagementOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.ApplicationNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.ApplicationSubmissionInboundPort;
import fr.sorbonne_u.datacenter.software.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.datacenterclient.tests.Integrator2;

public class AdmissionController extends AbstractComponent implements AdmissionControllerManagementI {

	protected String acURI;
	
	protected String acmipURI;
	
	protected String asipURI;
	
	protected String anipURI;
	
	protected String csipURI;
	
	protected AdmissionControllerManagementInboundPort acmip;
	
	protected AdmissionControllerManagementOutboundPort acmop;

	protected ApplicationSubmissionInboundPort asip;

	protected ApplicationNotificationInboundPort anip;
	
	
	protected Map<String, RequestDispatcher> rdMap = new HashMap<>();

	protected int sumCreatedVM = 0;
	
	protected int sumApp = 0;
	

	public AdmissionController(String acURI, String acmipURI, String asipURI, String anipURI,String csipURI) throws Exception {
		super(1, 1);

		// Preconditions
		assert acURI != null;
		assert acmip != null;
		assert asipURI != null;
		assert anipURI != null;
		assert csipURI !=null;

		this.acURI = acURI;
		this.acmipURI = acmipURI;
		this.anipURI = anipURI;
		this.asipURI = asipURI;
		this.csipURI = csipURI;

		// connect to application provider
		this.addOfferedInterface(AdmissionControllerManagementI.class);
		this.acmip = new AdmissionControllerManagementInboundPort(acmipURI, this);
		this.addPort(this.acmip);
		this.acmip.publishPort();
		
		this.addRequiredInterface(AdmissionControllerManagementI.class) ;
		this.acmop = new AdmissionControllerManagementOutboundPort(this) ;
		this.addPort(this.acmop);
		this.acmop.publishPort();

		this.addOfferedInterface(ApplicationSubmissionI.class);
		this.asip = new ApplicationSubmissionInboundPort(this.asipURI, this);
		this.addPort(this.asip);
		this.asip.publishPort();

		this.addOfferedInterface(ApplicationSubmissionI.class);
		this.anip = new ApplicationNotificationInboundPort(this.anipURI, this);
		this.addPort(this.anip);
		this.anip.publishPort();


	}

	@Override
	public void start() throws ComponentStartException {
		// TODO Auto-generated method stub
		super.start();
		try {
			this.acmop.doConnection(this.acmipURI,AdmissionControllerManagementConnector.class.getCanonicalName() );
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}

	}

	@Override
	public void finalise() throws Exception {
		if(this.acmop.connected()) this.acmop.doDisconnection();
		super.finalise();
	}
	


	public String[] submitApplication(String apURI,int nbVm) throws Exception {
		
		ArrayList<String> vmURIList = new ArrayList<String>();
		ArrayList<ApplicationVM> vmList = new ArrayList<>();
		ArrayList<String> avmipURIList = new ArrayList<>();
		ArrayList<String> avmrnipURIList = new ArrayList<>();
		ArrayList<String> avmrsipURIList = new ArrayList<>();
		// TODO Auto-generated method stub
		String[] ports = new String[2];
		this.logMessage(this.acURI + "recevoit une demande de " + nbVm +" VM");
		this.logMessage("check the ressource>>>>>>>>>>>>>>");
		Thread.sleep(3000L);
		//create
		for (int i = 0; i < nbVm; i++) {
			vmURIList.add(createVMURI("vm"));
			avmipURIList.add(createVMURI("avmip"));
			avmrsipURIList.add(createVMURI("avmsrip"));
			avmrnipURIList.add(createVMURI("avmrnip"));
			vmList.add(new ApplicationVM(vmURIList.get(i), // application vm component URI
					avmipURIList.get(i), 
					avmrsipURIList.get(i), 
					avmrnipURIList.get(i) ));
			AbstractCVM.getCVM().addDeployedComponent( vmList.get(i) );
			vmList.get(i).toggleTracing();
			vmList.get(i).toggleLogging();
			sumCreatedVM++;
			System.out.println("sumCreatedVM : "+sumCreatedVM);
		}
		
		String rdmip = createURI("rdmip");
		String rdrsip = createURI("rdrsip");
		String rdrnip = createURI("rdrnip");
		
		RequestDispatcher rd = new RequestDispatcher("ap"+ sumApp + "-rd", 
				rdmip,rdrsip, rdrnip, 
				vmURIList, 
				avmrsipURIList, 
				avmrnipURIList);
		AbstractCVM.getCVM().addDeployedComponent( rd );
		rd.toggleTracing();
		rd.toggleLogging();
		rdMap.put(apURI, rd);
		
		Integrator2 integ = new Integrator2(this.csipURI,avmipURIList,rdmip);	
		AbstractCVM.getCVM().addDeployedComponent( integ );
		//allocation and start
		integ.doConnect();
		integ.allocateCores(2);
		
		for (int i = 0; i < nbVm; i++) {
			vmList.get(i).doConnect();
		}
		
		ports[0] = rdrsip;
		ports[1] = rdrnip;
		sumApp ++;
		
		System.out.println("ac : "+apURI + "*************" + nbVm);
		System.out.println("ac : "+ports[0] + "*************" + ports[1]);
		System.out.println("ac : " +sumApp);
		return ports;	
	}
	

	public void notifyRequestGeneratorCreated(String apURI) throws Exception {
		// TODO Auto-generated method stub
		//compelet the connection (rd and rg)
			System.out.println("ac notify : " + apURI);
			rdMap.get(apURI).doConnect();
		
	}
	
	private String createURI(String portType){
		return portType + sumApp ;
	}
	
	private String createVMURI(String portType){
		return "ap" + sumApp +"-"+ portType + sumCreatedVM;
	}
	

}

