package fr.sorbonne_u.datacenter.software.admissioncontroller.test;

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.software.requestdispatcher.test.TestRequestDispatcher;

public class TestAdmissionControl extends AbstractCVM{

	public TestAdmissionControl() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public void deploy() throws Exception {
		// TODO Auto-generated method stub
		super.deploy();

	}


	public static void	main(String[] args)
	{
		// Uncomment next line to execute components in debug mode.
		// AbstractCVM.toggleDebugMode() ;
		try {
			final TestRequestDispatcher trd = new TestRequestDispatcher() ;
			trd.startStandardLifeCycle(10000L) ;//10000
//			final TestAdmissionControl trd = new TestRequestDispatcher() ;
//			trd.startStandardLifeCycle(10000L) ;//10000
			Thread.sleep(50000L) ;//5000
			// Exit from Java.
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
