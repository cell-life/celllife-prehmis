package org.celllife.idart.integration.prehmis.builder

import org.celllife.idart.application.patient.dto.PatientDto
import org.celllife.idart.application.person.dto.PersonDto
import org.celllife.idart.common.Systems
import org.celllife.idart.domain.contactmechanism.MobileTelephoneNumber
import org.celllife.idart.integration.prehmis.PrehmisGender
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import java.text.SimpleDateFormat

import static org.celllife.idart.common.Identifiers.newIdentifier

@Service
class PrehmisPatientBuilder implements PatientBuilder {

    String soapNamesapce = 'http://schemas.xmlsoap.org/soap/envelope/'

    @Value('${prehmis.namespace}')
    String prehmisNamespace

    def DATA_OF_BIRTH_FORMAT = 'yyyy-MM-dd'

    @Override
    PatientDto buildIdartPatient(envelope) {
        
        envelope.declareNamespace(soap: soapNamesapce, prehmis: prehmisNamespace)

        def prehmisPatient = envelope.'soap:Body'.'prehmis:getPatientResponse'.result

        PatientDto patient = new PatientDto()
        PersonDto person = new PersonDto()

        String prehmisId = prehmisPatient.id.text()
        if (prehmisId == null || prehmisId.empty) {
            return null
        }

        patient.identifiers << newIdentifier(Systems.PREHMIS.id, prehmisId)

        String pgwcPatientNumber = prehmisPatient.pgwc_patient_number.text()
        if (pgwcPatientNumber != null && !pgwcPatientNumber.empty) {
            patient.identifiers << newIdentifier(Systems.PGWC.id, pgwcPatientNumber)
        }

        String saId = prehmisPatient.sa_id_number.text()
        if (saId != null && !saId.empty) {
            patient.identifiers << newIdentifier(Systems.SA_IDENTITY_NUMBER.id, saId)
        }

        person.firstName = prehmisPatient.first_name.text()

        person.lastName = prehmisPatient.last_name.text()

        person.birthDate = getDate(prehmisPatient.date_of_birth.text())

        person.gender = PrehmisGender.findByPrehmisCode(prehmisPatient.gender.text())

        person.addContactMechanism(getMobileTelephoneNumber(prehmisPatient.cellphone_number.text()))

        patient.person = person

        patient
    }

    Date getDate(String dateOfBirth) {
        if (dateOfBirth == null || dateOfBirth.empty) {
            return null
        }

        new SimpleDateFormat(DATA_OF_BIRTH_FORMAT).parse(dateOfBirth)
    }

    MobileTelephoneNumber getMobileTelephoneNumber(String mobileNumber) {

        if (mobileNumber == null || mobileNumber.trim().empty) {
            return null
        }

        mobileNumber.replaceAll(" ", "")

        // International Format
        if (mobileNumber.length().equals(11)) {
            return [countryCode: mobileNumber.substring(0, 2), contactNumber: "0" + mobileNumber.substring(2)]
        }

        return new MobileTelephoneNumber(countryCode: "27", contactNumber: mobileNumber)
    }
}
