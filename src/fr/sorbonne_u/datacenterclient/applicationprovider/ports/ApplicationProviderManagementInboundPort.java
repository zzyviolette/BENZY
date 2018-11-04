package fr.sorbonne_u.datacenterclient.applicationprovider.ports;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenterclient.applicationprovider.ApplicationProvider;
import fr.sorbonne_u.datacenterclient.applicationprovider.interfaces.ApplicationProviderManagementI;

public class ApplicationProviderManagementInboundPort extends AbstractInboundPort
		implements ApplicationProviderManagementI {

	private static final long serialVersionUID = 1L;

	public ApplicationProviderManagementInboundPort(ComponentI owner) throws Exception {
		super(ApplicationProviderManagementI.class, owner);

	}

	public ApplicationProviderManagementInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, ApplicationProviderManagementI.class, owner);
	}

	@Override
	public void sendApplication(int nbVM) throws Exception {

		this.getOwner().handleRequestSync(new AbstractComponent.AbstractService<Void>() {

			@Override
			public Void call() throws Exception {
				((ApplicationProvider) this.getOwner()).sendApplication(nbVM);
				return null;

			}
		});

	}

	@Override
	public void stopApplication() throws Exception {
		
		this.getOwner().handleRequestAsync(new AbstractComponent.AbstractService<Void>() {

			@Override
			public Void call() throws Exception {
				((ApplicationProvider) this.getOwner()).stopApplication();
				return null;

			}
		});

	}

}