package fr.sorbonne_u.datacenter.software.admissioncontroller;



import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
import fr.sorbonne_u.datacenter.software.admissioncontroller.ports.AdmissionControllerManagementInboundPort;
import fr.sorbonne_u.datacenter.software.connectors.ApplicationNotificationConnector;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationSubmissionI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.ports.ApplicationNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.ApplicationSubmissionInboundPort;


public class AdmissionController extends AbstractComponent implements AdmissionControllerManagementI {
	
	protected String							acURI ;
	
	protected AdmissionControllerManagementInboundPort acmip;

	protected ApplicationSubmissionInboundPort	asip ;
	
	protected ApplicationNotificationOutboundPort
											anop ;
	protected String							anipURI ;
	

	public AdmissionController(String acURI,
			String acmip,
			String asip,
			String anip) throws Exception{
		super(1, 1);
		
		// Preconditions
				assert	acURI != null ;
				assert	acmip != null ;
				assert	asip != null ;
				assert	anip != null ;

				this.acURI = acURI ;
				this.anipURI = anip;
				

				this.addOfferedInterface(AdmissionControllerManagementI.class);
				this.acmip = new AdmissionControllerManagementInboundPort(acmip, this);
				this.addPort(this.acmip);
				this.acmip.publishPort();

				this.addOfferedInterface(ApplicationSubmissionI.class);
				this.asip = new ApplicationSubmissionInboundPort(asip, this);
				this.addPort(this.asip);
				this.asip.publishPort();


				this.addRequiredInterface(ApplicationNotificationI.class);
				this.anop = new ApplicationNotificationOutboundPort(this);
				this.addPort(this.anop);
				this.anop.publishPort();
		
	}
	
	

	@Override
	public void start() throws ComponentStartException {
		// TODO Auto-generated method stub
		super.start();
		try {

			//connect to rg
			this.doPortConnection(
					this.anop.getPortURI(),
					anipURI,
					ApplicationNotificationConnector.class.getCanonicalName()) ;
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
		
	}

	
	@Override
	public void			finalise() throws Exception
	{
		this.doPortDisconnection(this.anop.getPortURI()) ;
		super.finalise() ;
	}


	public void acceptSubmitApplicationAndNotify(final RequestI r) throws Exception {
		// TODO Auto-generated method stub
//		System.out.println( this.acURI + " receive the request " + r.getRequestURI() );
		this.logMessage( this.acURI + " recevoir la demande" + r.getRequestURI() );
		//faire l'allocation
		Thread.sleep(1000L);
		this.logMessage( "verifier les ressources " );
		this.logMessage( "creer le requestGenerator, le distributeur et la machine virtuel" );
		Thread.sleep(2000L);
		this.logMessage( this.acURI + " termine et envoye la notification a l'application " );
		Thread.sleep(1000L);
		this.anop.notifyRequestGeneratorCreated(r);

	}

}
