package org.celllife.idart.integration.prehmis.builder

import static org.celllife.idart.common.Identifiers.newIdentifier
import static org.celllife.idart.common.Identifiers.newIdentifiers
import static org.celllife.idart.common.Systems.PREHMIS

import org.celllife.idart.application.person.dto.PersonDto
import org.celllife.idart.application.practitioner.dto.PractitionerDto
import org.celllife.idart.common.Systems
import org.celllife.idart.integration.prehmis.PrehmisPractitionerType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service


@Service
class PrehmisPractitionerBuilder implements PractitionerBuilder {

    String soapNamespace = 'http://schemas.xmlsoap.org/soap/envelope/'

    @Value('${prehmis.namespace}')
    String prehmisNamespace

    @Override
    Set<PractitionerDto> buildPractitioners(getPractionerListResponse) {

        def envelope = getPractionerListResponse.data
        envelope.declareNamespace(soap: soapNamespace, prehmis: prehmisNamespace)

        def practitioners = envelope.'soap:Body'.'prehmis:getPractitionerListResponse'.result.item
                .collect { buildPractitioner(it) }

        def result = practitioners.findAll { it != null }

        result
    }

    @Override
    PractitionerDto buildPractitioner(prehmisPractitioner) {

        PractitionerDto practitioner = new PractitionerDto()

        String prehmisPractitionerCode = prehmisPractitioner.practitioner_code.text()
        if (prehmisPractitionerCode == null || prehmisPractitionerCode.empty) {
            return null
        }

        String prehmisPractitionerType = prehmisPractitioner.practitioner_type.text()
        if (prehmisPractitionerType != null && !prehmisPractitionerType.empty) {
            practitioner.type = PrehmisPractitionerType.getPractitionerType(prehmisPractitionerType)
        }

        practitioner.identifiers = newIdentifiers(PREHMIS.id, prehmisPractitionerCode)

        PersonDto person = new PersonDto()

        String firstName = prehmisPractitioner.first_name.text()
        if (firstName != null && !firstName.empty) {
            person.firstName = firstName
        }

        String lastName = prehmisPractitioner.last_name.text()
        if (lastName != null && !lastName.empty) {
            person.lastName = lastName
        }

        practitioner.person = person

        practitioner
    }
}
