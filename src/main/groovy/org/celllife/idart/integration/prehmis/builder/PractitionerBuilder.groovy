package org.celllife.idart.integration.prehmis.builder

import org.celllife.idart.application.person.dto.PersonDto
import org.celllife.idart.application.practitioner.dto.PractitionerDto
import org.celllife.idart.common.Systems
import org.celllife.idart.integration.prehmis.PrehmisPractitionerType

import static org.celllife.idart.common.Identifiers.newIdentifiers
import static org.celllife.idart.common.Systems.PREHMIS
import static org.celllife.idart.common.Identifiers.newIdentifier

/**
 * Everything you need to build a practitioner entity given data (e.g. from PREHMIS)
 */
interface PractitionerBuilder {

    Set<PractitionerDto> buildPractitioners(getPractionerListResponse);

    PractitionerDto buildPractitioner(prehmisPractitioner)
}
