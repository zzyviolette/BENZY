package fr.sorbonne_u.datacenterclient.tests;

import java.util.ArrayList;
import java.util.Random;

import org.omg.CORBA.INTERNAL;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.sorbonne_u.datacenter.software.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import fr.sorbonne_u.datacenter.software.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;
import fr.sorbonne_u.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

/**
 * The class <code>Integrator</code> plays the role of an overall supervisor
 * for the data center example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>Created on : 2018-09-21</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */

//This integrator is for CA
public class				Integrator2
extends		AbstractComponent
{
	protected ArrayList<String>							avmipURIList ;
	protected String                                    rdmipURI;
	protected ArrayList<AllocatedCore[]>                allocatedCores;

	/** Port connected to the AVM component to allocate it cores.			*/
	protected ArrayList<ApplicationVMManagementOutboundPort> avmopList;
	

	//csop is for allocation cores
	public				Integrator2(
		ArrayList<AllocatedCore[]> allocatedCores,
		ArrayList<String> avmipURIList
		) throws Exception
	{
		super(1, 0) ;

		assert	allocatedCores.size() > 0  && avmipURIList.size()>0;

		this.avmipURIList = new ArrayList<>(avmipURIList);
        this.allocatedCores = allocatedCores;
        
		this.addRequiredInterface(ComputerServicesI.class) ;
		this.addRequiredInterface(RequestGeneratorManagementI.class) ;
		this.addRequiredInterface(ApplicationVMManagementI.class) ;

		this.avmopList = new ArrayList<ApplicationVMManagementOutboundPort>();
		for(int i = 0; i<this.avmipURIList.size(); i++){
			ApplicationVMManagementOutboundPort avmop = new ApplicationVMManagementOutboundPort(this);
			this.avmopList.add(avmop);
			this.addPort(avmop);
			this.avmopList.get(i).publishPort();			
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;
		try {
			
			for(int i = 0; i<this.avmipURIList.size(); i++){
				this.doPortConnection(
						this.avmopList.get(i).getPortURI(),
						avmipURIList.get(i),
						ApplicationVMManagementConnector.class.getCanonicalName()) ;
			}
			
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public void			execute() throws Exception
	{
		super.execute() ;
		for (int i = 0; i< this.avmopList.size(); i++) {
			this.avmopList.get(i).allocateCores(this.allocatedCores.get(i));
		}

	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		for (int i = 0; i< this.avmopList.size(); i++) { 
			if(this.avmopList.get(i).connected()) this.doPortDisconnection(this.avmopList.get(i).getPortURI());
		}
		
		super.finalise();
		System.out.println("integr fin");
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			for (int i = 0; i< this.avmopList.size(); i++) { 
				if(this.avmopList.get(i).isPublished()) this.avmopList.get(i).unpublishPort();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
		System.out.println("integr shut");
	}

	
}
