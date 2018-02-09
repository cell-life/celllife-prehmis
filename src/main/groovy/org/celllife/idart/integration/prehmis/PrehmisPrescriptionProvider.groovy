package org.celllife.idart.integration.prehmis

import static org.celllife.idart.common.IdentifiableType.*
import static org.celllife.idart.common.Identifiers.getIdentifierValue
import static org.celllife.idart.common.Identifiers.newIdentifier
import static org.celllife.idart.common.Identifiers.newIdentifiers
import static org.celllife.idart.common.PartClassificationType.ATC
import static org.celllife.idart.common.Systems.*
import static org.celllife.idart.domain.part.PartClassificationApplications.getClassificationCode
import static org.celllife.idart.integration.prehmis.builder.PrehmisRequestBuilder.buildApiLoginRequest
import static org.celllife.idart.integration.prehmis.builder.PrehmisRequestBuilder.buildDeletePrescriptionRequest
import static org.celllife.idart.integration.prehmis.builder.PrehmisRequestBuilder.buildStorePrescriptionRequest
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient

import java.text.SimpleDateFormat

import javax.inject.Inject
import javax.inject.Named

import org.celllife.idart.application.prescription.PrescriptionNotDeletedException
import org.celllife.idart.application.prescription.PrescriptionNotSavedException
import org.celllife.idart.application.prescription.PrescriptionProvider
import org.celllife.idart.domain.encounter.EncounterService
import org.celllife.idart.domain.eventerror.EventError
import org.celllife.idart.domain.eventerror.EventErrorService
import org.celllife.idart.domain.identifiable.IdentifiableService
import org.celllife.idart.domain.part.PartService
import org.celllife.idart.domain.patient.PatientService
import org.celllife.idart.domain.person.PersonService
import org.celllife.idart.domain.practitioner.PractitionerService
import org.celllife.idart.domain.prescribedmedication.PrescribedMedicationService
import org.celllife.idart.domain.prescription.Prescription
import org.celllife.idart.domain.prescription.PrescriptionEvent
import org.celllife.idart.domain.prescription.PrescriptionService
import org.celllife.idart.domain.product.Medication
import org.celllife.idart.domain.product.ProductService
import org.celllife.idart.framework.aspectj.LogLevel
import org.celllife.idart.framework.aspectj.Loggable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * PREHMIS implementation of the PrescriptionProvider related events
 */
@Service @Named class PrehmisPrescriptionProvider implements PrescriptionProvider {

    static final Logger LOGGER = LoggerFactory.getLogger(PrehmisPrescriptionProvider)

    @Value('${prehmis.namespace}')
    String prehmisNamespace

    @Value('${prehmis.endpoint.baseUrl}')
    String prehmisEndpointBaseUrl

    @Value('${prehmis.endpoint.url}')
    String prehmisEndpointUrl

    @Value('${prehmis.username}')
    String prehmisUsername

    @Value('${prehmis.password}')
    String prehmisPassword

    @Value('${prehmis.applicationKey}')
    String prehmisApplicationKey

    @Inject IdentifiableService identifiableService

    @Inject EncounterService encounterService

    @Inject PatientService patientService

    @Inject PersonService personService

    @Inject PractitionerService practitionerService

    @Inject PrescribedMedicationService prescribedMedicationService

    @Inject ProductService productService

    @Inject PartService partService

    @Inject PrescriptionService prescriptionService
    
    @Inject EventErrorService eventErrorService;

    @Override
    @Loggable(LogLevel.INFO)
    @Transactional
    void processEvent(PrescriptionEvent prescriptionEvent) {
        if (prescriptionEvent.type == PrescriptionEvent.EventType.SAVED) {
            save(prescriptionEvent)
        } else if (prescriptionEvent.type == PrescriptionEvent.EventType.DELETED) {
            delete(prescriptionEvent)
        } else {
            String errorMessage = "Could not process PrescriptionEvent with type "+prescriptionEvent.type
            LOGGER.warn(errorMessage)
            saveEventError(errorMessage, prescriptionEvent)
        }
    }

    void save(PrescriptionEvent prescriptionEvent) {

        def prehmisFacilityIdentifier = getFacilityIdentifiable(prescriptionEvent)
        if (prehmisFacilityIdentifier == null) {
            return
        }

        def prehmisRestClient = new RESTClient(prehmisEndpointUrl)
        
        def storePrescriptionRequest
        try {
            storePrescriptionRequest = buildStorePrescriptionRequest(prehmisFacilityIdentifier, prescriptionEvent.prescription)
        } catch (Exception e) {
            String errorMessage = "Unable to create PREHMIS storeprescription request for prescription '"+prescriptionEvent.prescription.id+"'. Error: "+e.message
            saveEventError(errorMessage, prescriptionEvent)
            throw new PrescriptionNotDeletedException(errorMessage, e)
        }
        
        def storePrescriptionResponse
        try {
            apiLogin(prehmisRestClient, prehmisFacilityIdentifier)

            storePrescriptionResponse = prehmisRestClient.post(
                    body: storePrescriptionRequest,
                    contentType: ContentType.XML,
                    requestContentType: ContentType.XML,
                    headers: [
                        SOAPAction: prehmisEndpointBaseUrl + "/storePrescription"
                    ]
                    )
        } catch (Exception e) {
            throw new PrescriptionNotSavedException("Unable to communicate with PREHMIS while trying to store prescription '"+prescriptionEvent.prescription.id+"'. Error: "+e.message, e)
        }

        def result = storePrescriptionResponse.data
        LOGGER.info("PREHMIS response: "+result)
        if (!result.equals("Prescription saved")) {
            String errorMessage = "Unable to store prescription '"+prescriptionEvent.prescription.id+"' on PREHMIS. Error: "+result
            saveEventError(errorMessage, prescriptionEvent)
            throw new PrescriptionNotSavedException(errorMessage)
        }
    }

    void delete(PrescriptionEvent prescriptionEvent) {
        def prehmisFacilityIdentifier = getFacilityIdentifiable(prescriptionEvent)
        if (prehmisFacilityIdentifier == null) {
            return
        }

        def prehmisRestClient = new RESTClient(prehmisEndpointUrl)
        def deletePrescriptionRequest
        def deletePrescriptionResponse
        
        try {
            deletePrescriptionRequest = buildDeletePrescriptionRequest(prehmisFacilityIdentifier, prescriptionEvent.prescription)
        } catch (Exception e) {
            String errorMessage = "Unable to create deleteprescription request for prescription '"+prescriptionEvent.prescription.id+"'. Error: "+e.message
            saveEventError(errorMessage, prescriptionEvent)
            throw new PrescriptionNotDeletedException(errorMessage, e)
        }

        try {
            apiLogin(prehmisRestClient, prehmisFacilityIdentifier)

            deletePrescriptionResponse = prehmisRestClient.post(
                    body: deletePrescriptionRequest,
                    contentType: ContentType.XML,
                    requestContentType: ContentType.XML,
                    headers: [
                        SOAPAction: prehmisEndpointBaseUrl + "/deletePrescription"
                    ]
                    )
        } catch (Exception e) {
            String errorMessage = "Unable to communicate with PREHMIS while trying to delete prescription '"+prescriptionEvent.prescription.id+"'. Error: "+e.message 
            saveEventError(errorMessage, prescriptionEvent)
            throw new PrescriptionNotDeletedException(errorMessage, e)
        }

        def result = deletePrescriptionResponse.data
        LOGGER.info("PREHMIS response: "+result)
        if (!result.equals("Prescription deleted")) {
            String errorMessage = "Unable to delete prescription '"+prescriptionEvent.prescription.id+"' on PREHMIS. Error: "+result
            saveEventError(errorMessage, prescriptionEvent)
            throw new PrescriptionNotDeletedException(errorMessage)
        }
    }

    def saveEventError(String message, PrescriptionEvent event) {
        EventError eventError = new EventError();
        eventError.with {
            datetime = new Date()
            retryCount = 0
            errorMessage = message
            eventType = EventError.EventType.PRESCRIPTION_EVENT
            eventUuid = event.uuid
        }
        eventError.setUnserializedEventObject(event)
        eventErrorService.save(eventError)
    }

    String getFacilityIdentifiable(PrescriptionEvent prescriptionEvent) {
        def encounter = encounterService.findByEncounterId(prescriptionEvent.prescription.encounter)

        def facilityIdentifiable =
                identifiableService.resolveIdentifiable(FACILITY, newIdentifiers(encounter.facility.value))

        return getIdentifierValue(facilityIdentifiable.identifiers, PREHMIS.id)
    }

    void apiLogin(RESTClient prehmisRestClient, String prehmisFacilityIdentifier) throws Exception {
        def apiLoginRequest = buildApiLoginRequest([
            xmlnsPreh: prehmisNamespace,
            username: prehmisUsername,
            password: prehmisPassword,
            applicationKey: prehmisApplicationKey,
            facilityCode: prehmisFacilityIdentifier,
        ])

        prehmisRestClient.post(
                body: apiLoginRequest,
                contentType: ContentType.XML,
                requestContentType: ContentType.XML,
                headers: [
                    SOAPAction: prehmisEndpointBaseUrl + "/apiLogin"
                ]
                )
    }

    String buildStorePrescriptionRequest(String facilityCode, Prescription prescription) {

        String storePrescriptionRequest = buildStorePrescriptionRequest([
            xmlnsPreh: prehmisNamespace,
            username: prehmisUsername,
            password: prehmisPassword,
            applicationKey: prehmisApplicationKey,
            facilityCode: facilityCode,
            prescription: getPrehmisPrescriptionMap(facilityCode, prescription, true)
        ])

        storePrescriptionRequest
    }

    String buildDeletePrescriptionRequest(String facilityCode, Prescription prescription) {

        String deletePrescriptionRequest = buildDeletePrescriptionRequest([
            xmlnsPreh: prehmisNamespace,
            username: prehmisUsername,
            password: prehmisPassword,
            applicationKey: prehmisApplicationKey,
            facilityCode: facilityCode,
            prescription: getPrehmisPrescriptionMap(facilityCode, prescription, false)
        ])

        deletePrescriptionRequest
    }

    Map<String, Object> getPrehmisPrescriptionMap(String facilityCode, Prescription prescription, boolean includeMedications) {
        def prehmisPrescription = [:]

        prescription.with {

            def prescriptionIdentifiable = identifiableService
                    .resolveIdentifiable(PRESCRIBED_MEDICATION, newIdentifiers(prescription.id.value))

            prehmisPrescription.id = getIdentifierValue(prescriptionIdentifiable.identifiers, IDART_WEB.id)

            def patient = patientService.findByPatientId(prescription.patient)
            def patientIdentifiable = identifiableService
                    .resolveIdentifiable(PATIENT, newIdentifiers(patient.id.value))

            prehmisPrescription.prehmisPatientId = getIdentifierValue(patientIdentifiable?.identifiers, PREHMIS.id)
            prehmisPrescription.pgwcPatientNumber = getIdentifierValue(patientIdentifiable?.identifiers, PGWC.id)

            def person = personService.findByPersonId(patient.person)
            def personIdentifiable = identifiableService
                    .resolveIdentifiable(PERSON, newIdentifiers(person.id.value))

            prehmisPrescription.patientSaIdNumber = getIdentifierValue(personIdentifiable?.identifiers, SA_IDENTITY_NUMBER.id)

            def prescriber = practitionerService.findByPractitionerId(prescription.prescriber)
            def prescriberIdentifiable = identifiableService
                    .resolveIdentifiable(PRACTITIONER, newIdentifiers(prescriber.id.value))

            prehmisPrescription.practitionerCode = getIdentifierValue(prescriberIdentifiable?.identifiers, PREHMIS.id)

            prehmisPrescription.prescriptionDate = toPrehmisDate(dateWritten)

            if (includeMedications && prescribedMedications.size() != 0) {

                prehmisPrescription.prescribedMedications = prescribedMedications.collect { prescribedMedicationId ->

                    def prescribedMedication =
                            prescribedMedicationService.findByPrescribedMedicationId(prescribedMedicationId)

                    if (prehmisPrescription.endDate == null) {
                        prehmisPrescription.endDate = toPrehmisDate(prescribedMedication.valid?.thruDate)
                    }

                    if (prehmisPrescription.duration == null) {
                        prehmisPrescription.duration = prescribedMedication.expectedSupplyDuration?.value
                    }

                    if (prehmisPrescription.changeReason == null) {
                        prehmisPrescription.changeReason = prescribedMedication.reasonForPrescribing
                    }

                    def medication = productService.findByProductId(prescribedMedication.medication)

                    def drug = partService.findByPartId((medication as Medication).drug)

                    [
                        atcCode: getClassificationCode(drug.classifications, ATC),
                        amountPerTime: prescribedMedication.dosageInstruction?.doseQuantity?.value,
                        timesPerDay: prescribedMedication.dosageInstruction?.timing?.repeat?.frequency
                    ]
                }
            }
        }
        prehmisPrescription
    }

    static toPrehmisDate(Date date) {

        if (date == null) {
            return null
        }

        new SimpleDateFormat("yyyy-MM-dd").format(date)
    }
}
