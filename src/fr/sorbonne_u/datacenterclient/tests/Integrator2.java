package fr.sorbonne_u.datacenterclient.tests;

import java.util.ArrayList;

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
public class				Integrator2
extends		AbstractComponent
{
	protected String									rmipURI ;
	protected String									csipURI ;
	protected ArrayList<String>							avmipURIList ;
	protected String                                    rdmipURI;

	/** Port connected to the computer component to access its services.	*/
	protected ComputerServicesOutboundPort			csop ;
	/** Port connected to the AVM component to allocate it cores.			*/
	protected ArrayList<ApplicationVMManagementOutboundPort> avmopList;
	
	protected RequestDispatcherManagementOutboundPort rdmop;

	public				Integrator2(
		String csipURI,
		ArrayList<String> avmipURIList,
		String rdmipURI
		) throws Exception
	{
		super(1, 0) ;

		assert	csipURI != null && rdmipURI != null && avmipURIList.size()>0;

		this.rdmipURI = rdmipURI;
		this.csipURI = csipURI;
		this.avmipURIList = new ArrayList<>(avmipURIList);

		this.addRequiredInterface(ComputerServicesI.class) ;
		this.addRequiredInterface(RequestGeneratorManagementI.class) ;
		this.addRequiredInterface(ApplicationVMManagementI.class) ;

		this.csop = new ComputerServicesOutboundPort(this) ;
		this.addPort(this.csop) ;
		this.csop.publishPort() ;

		this.rdmop = new RequestDispatcherManagementOutboundPort(this) ;
		this.addPort(this.rdmop) ;
		this.rdmop.publishPort() ;

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
			this.doPortConnection(
				this.csop.getPortURI(),
				this.csipURI,
				ComputerServicesConnector.class.getCanonicalName()) ;
			
			for(int i = 0; i<this.avmipURIList.size(); i++){
				this.doPortConnection(
						this.avmopList.get(i).getPortURI(),
						avmipURIList.get(i),
						ApplicationVMManagementConnector.class.getCanonicalName()) ;
			}
			
			this.doPortConnection(
					this.rdmop.getPortURI(),
					rdmipURI,
					RequestDispatcherManagementConnector.class.getCanonicalName()) ;
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

	}
	
	public void allocateCores(int nbCores) throws Exception{
		for (int i = 0; i< this.avmopList.size(); i++) {
			AllocatedCore[] ac = this.csop.allocateCores(nbCores);
			this.avmopList.get(i).allocateCores(ac);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		this.doPortDisconnection(this.csop.getPortURI()) ;
		this.doPortDisconnection(this.rdmop.getPortURI()) ;
		for (int i = 0; i< this.avmopList.size(); i++) { 
			this.doPortDisconnection(this.avmopList.get(i).getPortURI());
		}
		
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.csop.unpublishPort() ;
			this.rdmop.unpublishPort() ;
			for (int i = 0; i< this.avmopList.size(); i++) { 
				this.avmopList.get(i).unpublishPort();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdownNow()
	 */
	@Override
	public void			shutdownNow() throws ComponentShutdownException
	{
		try {
			this.csop.unpublishPort() ;
			this.rdmop.unpublishPort() ;
			for (int i = 0; i< this.avmopList.size(); i++) { 
				this.avmopList.get(i).unpublishPort();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdownNow();
	}
}
