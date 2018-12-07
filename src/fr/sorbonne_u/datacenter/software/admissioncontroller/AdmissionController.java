package fr.sorbonne_u.datacenter.software.admissioncontroller;


import java.util.ArrayList;
import java.util.Random;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.dymaniccomponentcreator.connectors.DynamicComponentCreationConnector;
import fr.sorbonne_u.datacenter.dymaniccomponentcreator.interfaces.DynamicComponentCreationI;
import fr.sorbonne_u.datacenter.dymaniccomponentcreator.ports.DynamicComponentCreationOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
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
import fr.sorbonne_u.datacenter.software.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import fr.sorbonne_u.datacenter.software.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;

public class AdmissionController extends AbstractComponent implements AdmissionControllerManagementI {

	
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
	
	protected ArrayList<RequestDispatcherManagementOutboundPort>	rdmopList ;
	
	protected Computer[] computersList;

	protected int sumCreatedVM = 0;
	
	protected int demandeApp = 0;
	
	


	public AdmissionController(String acURI, String acmipURI, String asipURI, String anipURI,Computer[] computersList, String[] csipURIList,String dccipURI) throws Exception {
		super(1, 1);

		// Preconditions
		assert acURI != null;
		assert acmipURI != null;
		assert asipURI != null;
		assert anipURI != null;
		assert csipURIList.length>0;
		assert dccipURI != null;
		assert computersList.length>0;

		this.acURI = acURI;
		this.acmipURI = acmipURI;
		this.anipURI = anipURI;
		this.asipURI = asipURI;
		this.csipURIList = csipURIList;
		this.dccipURI = dccipURI;
		this.computersList = computersList;
		

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
		   ComputerServicesOutboundPort csop = new ComputerServicesOutboundPort(computersList[i]) ;
		   addPort(csop) ;
		   csop.publishPort();
		   csopList[i]=csop;
		}
		
		rdmopList = new ArrayList<RequestDispatcherManagementOutboundPort>();
		
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
			for(int i = 0; i< this.csipURIList.length;i++){
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
		for(int i =0; i< this.csopList.length;i++){
			if(this.csopList[i].connected()) this.csopList[i].doDisconnection();
			}
		for(int i = 0; i< this.rdmopList.size();i++){
			if(this.rdmopList.get(i).connected()) this.rdmopList.get(i).doDisconnection();
			}
		if(this.acmop.connected()) this.acmop.doDisconnection();
		if(this.dccop.connected()) this.dccop.doDisconnection();
	}
	
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
		
			for (int i = 0; i <  this.csopList.length;i++ ) { 
				System.out.println("ca for csop");
				if (this.csopList[i].isPublished()) this.csopList[i].unpublishPort();					
			}
			for(int i = 0; i< this.rdmopList.size();i++){
				System.out.println("ca for rdmop");
				if(this.rdmopList.get(i).isPublished()) this.rdmopList.get(i).unpublishPort();
				}
			System.out.println("ca for acmop");
			if (this.acmop.isPublished()) this.acmop.unpublishPort();
			System.out.println("ca for dccop");
			if (this.dccop.isPublished()) this.dccop.unpublishPort();
			System.out.println("fin");
			
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}

	}
		

	public String[] submitApplication(int nbVm) throws Exception {
			//augmente le nombre de application
			this.demandeApp ++;	
			
			//check source
			this.logMessage(this.acURI + " recevoit une demande de " + nbVm +" VM");		
		
			int[] corePerAVM = new int[nbVm];
			for (int i = 0; i< nbVm; i++) {
				Random rn = new Random();
				int range = 3;
				int randomNum =  rn.nextInt(range) + 1;
				corePerAVM[i]=randomNum;
				this.logMessage( "VM " + (i+1) + " demande " + randomNum + " Cores");
			}
			
			this.logMessage("check the ressource>>>>>>>>>>>>>>");
			 ArrayList<AllocatedCore[]> allocatedCores = findRessouces(corePerAVM,nbVm);
			 if(allocatedCores == null) {
				 this.logMessage(this.acURI + " refuse cette demande");
				 return null;
				 
			 }
			
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
			String rdURI = "ap"+ demandeApp + "-rd";
			String rdmipURI = createURI("rdmip");
			String rdrsipURI = createURI("rdrsip");
			String rdrnipURI = createURI("rdrnip");
			
			//creer le dispather 
			Object[] argumentsDispatcher = {rdURI, 
					rdmipURI,rdrsipURI, rdrnipURI};
			this.dccop.createComponent(RequestDispatcher.class.getCanonicalName(),
					argumentsDispatcher);	
			
			
			RequestDispatcherManagementOutboundPort rdmop = new RequestDispatcherManagementOutboundPort(this) ;
			addPort(rdmop) ;
			rdmop.publishPort();
			rdmopList.add(rdmop);
			
			this.doPortConnection(
			rdmop.getPortURI(),
			rdmipURI,
			RequestDispatcherManagementConnector.class.getCanonicalName()) ;
			
			for(int i = 0; i<vmURIList.size();i++){
				rdmop.connectVm(vmURIList.get(i), avmrsipURIList.get(i),avmrnipURIList.get(i),avmipURIList.get(i));
				rdmop.allocatedCore(vmURIList.get(i), allocatedCores.get(i));
			}
			
			//retourner les uris de ports de dispathcher pour que le request generator fait la connection
			String[] rdportURI = new String[2];
			rdportURI[0] = rdrsipURI;
			rdportURI[1] = rdrnipURI;
			
			return rdportURI;	

		}
		
	
	
	public  ArrayList<AllocatedCore[]> findRessouces (int[] corePerAVM , int numberOfAVMs) throws Exception {
	
			ArrayList<AllocatedCore[]> allocatedCores = new ArrayList<AllocatedCore[]>();

			ArrayList<ComputerServicesOutboundPort> csopListTemps = new ArrayList<ComputerServicesOutboundPort>();
				
				for (int i = 0; i < numberOfAVMs; i++) {

					AllocatedCore[] allocatedCoresAVM;

					for(ComputerServicesOutboundPort csop : this.csopList) {
						allocatedCoresAVM = csop.allocateCores(corePerAVM[i]) ;

						if (allocatedCoresAVM.length == corePerAVM[i]) {
							allocatedCores.add(allocatedCoresAVM);
							csopListTemps.add(csop);
							break;
						}

						Computer computer = (Computer) csop.getOwner();
						computer.releaseCores(allocatedCoresAVM);
					}
				}
				
				if (allocatedCores.size() == numberOfAVMs) {
					return allocatedCores;
				}

				for(int i = 0; i< allocatedCores.size(); i++) {
					Computer computer = (Computer) csopListTemps.get(i).getOwner();
					computer.releaseCores(allocatedCores.get(i));
				}

				return null;
			}

		
	public  void notifyRequestGeneratorCreated() throws Exception {
		// TODO Auto-generated method stub
		
			this.dccop.startComponent();
			this.dccop.executeComponent();
	}
	
	private String createURI(String portType){
		return portType + this.demandeApp ;
	}
	
	private String createVMURI(String portType){
		return "ap" + this.demandeApp +"-"+ portType + this.sumCreatedVM;
	}
	

}

