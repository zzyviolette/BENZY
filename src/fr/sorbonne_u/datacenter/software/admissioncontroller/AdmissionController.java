package fr.sorbonne_u.datacenter.software.admissioncontroller;


import java.util.ArrayList;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.dymaniccomponentcreator.connectors.DynamicComponentCreationConnector;
import fr.sorbonne_u.datacenter.dymaniccomponentcreator.interfaces.DynamicComponentCreationI;
import fr.sorbonne_u.datacenter.dymaniccomponentcreator.ports.DynamicComponentCreationOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
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

	protected int nbCorePerAVM = 2;
	
	protected String acURI;
	
	protected String acmipURI;
	
	protected String asipURI;
	
	protected String anipURI;
	
    protected String[]                        csipURIList;
	
	protected int[]                           nbAvailableCoresPerComputer;
	
	protected String dccipURI;
	
	protected AdmissionControllerManagementInboundPort acmip;
	
	protected AdmissionControllerManagementOutboundPort acmop;

	protected ApplicationSubmissionInboundPort asip;

	protected ApplicationNotificationInboundPort anip;
	
	protected DynamicComponentCreationOutboundPort dccop;
	
	protected ComputerServicesOutboundPort[]	csopList ;

	protected int sumCreatedVM = 0;
	
	protected int sumApp = 0;
	
	


	public AdmissionController(String acURI, String acmipURI, String asipURI, String anipURI,String[] csipURIList,int[] nbAvailableCoresPerComputer ,String dccipURI) throws Exception {
		super(1, 1);

		// Preconditions
		assert acURI != null;
		assert acmipURI != null;
		assert asipURI != null;
		assert anipURI != null;
		assert csipURIList.length>0;
		assert nbAvailableCoresPerComputer.length>0;
		assert dccipURI != null;

		this.acURI = acURI;
		this.acmipURI = acmipURI;
		this.anipURI = anipURI;
		this.asipURI = asipURI;
		this.csipURIList = csipURIList;
		this.nbAvailableCoresPerComputer = nbAvailableCoresPerComputer;
		this.dccipURI = dccipURI;
		

		this.addOfferedInterface(AdmissionControllerManagementI.class);
		this.acmip = new AdmissionControllerManagementInboundPort(acmipURI, this);
		this.addPort(this.acmip);
		this.acmip.publishPort();
		
		this.addRequiredInterface(AdmissionControllerManagementI.class) ;
		this.acmop = new AdmissionControllerManagementOutboundPort(this) ;
		this.addPort(this.acmop);
		this.acmop.publishPort();

		// les ports pour la connexion avec application provider
		this.addOfferedInterface(ApplicationSubmissionI.class);
		this.asip = new ApplicationSubmissionInboundPort(this.asipURI, this);
		this.addPort(this.asip);
		this.asip.publishPort();

		this.addOfferedInterface(ApplicationSubmissionI.class);
		this.anip = new ApplicationNotificationInboundPort(this.anipURI, this);
		this.addPort(this.anip);
		this.anip.publishPort();
		
		csopList = new ComputerServicesOutboundPort[this.csipURIList.length];	
		for(int i =0; i< this.csipURIList.length;i++){
		   ComputerServicesOutboundPort csop = new ComputerServicesOutboundPort(this) ;
		   addPort(csop) ;
		   csop.publishPort();
		   csopList[i]=csop;
		}
		
		// les ports pour la connexion avec dynamique creator
		addRequiredInterface(DynamicComponentCreationI.class);
		this.dccop = new DynamicComponentCreationOutboundPort(this);
		addPort(this.dccop);
		dccop.publishPort();


	}

	@Override
	public void start() throws ComponentStartException {
		// TODO Auto-generated method stub
		super.start();
		try {
			for(int i =0; i< this.csipURIList.length;i++){
				this.doPortConnection(
				this.csopList[i].getPortURI(),
				this.csipURIList[i],
				ComputerServicesConnector.class.getCanonicalName()) ;
				}
			this.acmop.doConnection(this.acmipURI, AdmissionControllerManagementConnector.class.getCanonicalName() );
			this.dccop.doConnection(this.dccipURI, DynamicComponentCreationConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}

	}

	@Override
	public void finalise() throws Exception {
		for(int i =0; i< this.csipURIList.length;i++){
			if(this.csopList[i].connected()) this.csopList[i].doDisconnection();
			}
		if(this.acmop.connected()) this.acmop.doDisconnection();
		if(this.dccop.connected()) this.dccop.doDisconnection();
		super.finalise();
	}
	

	
	public int findRessource(int nbCores){
		int index = -1;
		for (int i = 0; i < this.nbAvailableCoresPerComputer.length; i++) {
			if (this.nbAvailableCoresPerComputer[i] >= nbCores) {
				return i;
			}
		}
		return index;
	}
	

	public String[] submitApplication(int nbVm) throws Exception {
		//check source
		
		this.logMessage(this.acURI + " recevoit une demande de " + nbVm +" VM");
		this.logMessage("check the ressource>>>>>>>>>>>>>>");
		int index = findRessource(nbVm*nbCorePerAVM);
		if(index==-1){
			this.logMessage(this.acURI + " refuse cette demande");
			return null;
		}
		this.nbAvailableCoresPerComputer[index] = this.nbAvailableCoresPerComputer[index] - nbVm*nbCorePerAVM;
		this.logMessage(this.acURI + " accept cette demande");
		
		ArrayList<String> vmURIList = new ArrayList<String>();
		ArrayList<String> avmipURIList = new ArrayList<>();
		ArrayList<String> avmrnipURIList = new ArrayList<>();
		ArrayList<String> avmrsipURIList = new ArrayList<>();

		for (int i = 0; i < nbVm; i++) {
			//creer les uris de avms pour cette application
			vmURIList.add(createVMURI("vm"));
			avmipURIList.add(createVMURI("avmip"));
			avmrsipURIList.add(createVMURI("avmsrip"));
			avmrnipURIList.add(createVMURI("avmrnip"));
			
			Object[] argumentsAVM = {vmURIList.get(i),
					avmipURIList.get(i), 
					avmrsipURIList.get(i), 
					avmrnipURIList.get(i)};
	
			this.dccop.createComponent(ApplicationVM.class.getCanonicalName(),
					argumentsAVM);	
			this.sumCreatedVM++;
		}
		
		//creer les uris de request dispathcher
		String rdURI = "ap"+ sumApp + "-rd";
		String rdmipURI = createURI("rdmip");
		String rdrsipURI = createURI("rdrsip");
		String rdrnipURI = createURI("rdrnip");
		
		//creer le dispather 
		Object[] argumentsDispatcher = {rdURI, 
				rdmipURI,rdrsipURI, rdrnipURI, 
				vmURIList, 
				avmrsipURIList, 
				avmrnipURIList};
		this.dccop.createComponent(RequestDispatcher.class.getCanonicalName(),
				argumentsDispatcher);	
		
		//creer le integrator
		Object[] argumentsIntegrator = {
				this.csopList[index],avmipURIList,rdmipURI};
		
		this.dccop.createComponent(Integrator2.class.getCanonicalName(),
				argumentsIntegrator);
		
		//augmente le nombre de application
		this.sumApp ++;	
		
		//retourner les uris de ports de dispathcher pour que le request generator fait la connection
		String[] rdportURI = new String[2];
		rdportURI[0] = rdrsipURI;
		rdportURI[1] = rdrnipURI;
		
		return rdportURI;	
	}
	

	public void notifyRequestGeneratorCreated() throws Exception {
		// TODO Auto-generated method stub
			this.dccop.startComponent();
			this.dccop.executeComponent();
	}
	
	private String createURI(String portType){
		return portType + this.sumApp ;
	}
	
	private String createVMURI(String portType){
		return "ap" + this.sumApp +"-"+ portType + this.sumCreatedVM;
	}
	

}

