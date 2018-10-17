package fr.sorbonne_u.datacenter.software.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.software.interfaces.ApplicationSubmissionI;

public class ApplicationSubmissionConnector extends AbstractConnector implements ApplicationSubmissionI {

    @Override
    public String[] submitApplication( int nbVM ) throws Exception {
        return ( ( ApplicationSubmissionI ) this.offering ).submitApplication( nbVM );
    }

}