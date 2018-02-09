package org.celllife.idart.application.dispensation

import org.celllife.idart.application.dispensation.dto.DispensationDto


/**
 * Provides the functionality to manage a dispensation event (save or delete).
 * This class is triggered via a JMS topic (see spring-jms.xml - both in the source and test META-INF resources)
 */
public interface DispensationProvider {

    void save(DispensationDto dispensation)
    void delete(DispensationDto dispensation)

}