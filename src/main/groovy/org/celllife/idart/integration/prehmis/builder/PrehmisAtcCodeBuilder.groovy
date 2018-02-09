package org.celllife.idart.integration.prehmis.builder

import static org.celllife.idart.common.Identifiers.newIdentifier
import static org.celllife.idart.common.Identifiers.newIdentifiers
import static org.celllife.idart.common.Systems.PREHMIS

import org.celllife.idart.application.part.dto.AtcCodeDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * Converts the PREHMIS drug list into the AtcCodeDto objects
 */
@Service
class PrehmisAtcCodeBuilder implements AtcCodeBuilder {

	String soapNamespace = 'http://schemas.xmlsoap.org/soap/envelope/'

    @Value('${prehmis.namespace}')
	String prehmisNamespace

    @Override
	Set<AtcCodeDto> buildAtcCodes(getDrugListResponse) {

		def envelope = getDrugListResponse.data
		envelope.declareNamespace(soap: soapNamespace, prehmis: prehmisNamespace)

		def drugs = envelope.'soap:Body'.'prehmis:getDrugListResponse'.result.item
				.collect { buildAtcCode(it) }

		def result = drugs.findAll { it != null }

		result
	}

    @Override
	AtcCodeDto buildAtcCode(prehmisDrug) {

		AtcCodeDto atcDrug = new AtcCodeDto()

		// FIXME: should be more fancy to match the rest of this system, but I think it's overkill at the moment

		String drugName = prehmisDrug.drug_name.text()
		if (drugName == null || !drugName.empty) {
			atcDrug.drugName = drugName
		}

		String atcCode = prehmisDrug.atc_code.text()
		if (atcCode != null && !atcCode.empty) {
			atcDrug.atcCode = atcCode
		}

		atcDrug
	}
}
