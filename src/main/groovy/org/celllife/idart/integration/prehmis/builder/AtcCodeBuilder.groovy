package org.celllife.idart.integration.prehmis.builder

import static org.celllife.idart.common.Identifiers.newIdentifier
import static org.celllife.idart.common.Identifiers.newIdentifiers
import static org.celllife.idart.common.Systems.PREHMIS

import org.celllife.idart.application.part.dto.AtcCodeDto
import org.celllife.idart.application.person.dto.PersonDto
import org.celllife.idart.application.practitioner.dto.PractitionerDto
import org.celllife.idart.common.Systems
import org.celllife.idart.integration.prehmis.PrehmisPractitionerType

/**
 * Converts the PREHMIS drug list into the AtcCodeDto objects
 */
interface AtcCodeBuilder {

	Set<AtcCodeDto> buildAtcCodes(getDrugListResponse);
	AtcCodeDto buildAtcCode(prehmisDrug);

}
