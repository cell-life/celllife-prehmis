package org.celllife.idart.integration.prehmis

import static org.celllife.idart.common.IdentifiableType.*
import static org.celllife.idart.common.Identifiers.getIdentifierValue
import static org.celllife.idart.common.Identifiers.newIdentifiers
import static org.celllife.idart.common.PartClassificationType.ATC
import static org.celllife.idart.common.Systems.*
import static org.celllife.idart.domain.part.PartClassificationApplications.getClassificationCode
import static org.celllife.idart.integration.prehmis.builder.PrehmisRequestBuilder.buildApiLoginRequest
import static org.celllife.idart.integration.prehmis.builder.PrehmisRequestBuilder.buildDeleteDispensationRequest
import static org.celllife.idart.integration.prehmis.builder.PrehmisRequestBuilder.buildStoreDispensationRequest
import static org.celllife.idart.integration.prehmis.builder.PrehmisRequestBuilder.buildUpdateDispensationRequest
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient

import java.text.SimpleDateFormat

import org.celllife.idart.application.dispensation.DispensationNotDeletedException
import org.celllife.idart.application.dispensation.DispensationNotSavedException
import org.celllife.idart.application.dispensation.DispensationProvider
import org.celllife.idart.application.dispensation.dto.DispensationDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value

/**
 * The PREHMIS implementation for the DispensationProvider related events
 */
class PrehmisDispensationProvider implements DispensationProvider {

    static final Logger LOGGER = LoggerFactory.getLogger(PrehmisDispensationProvider)

    String SOAP_NAMESPACE = 'http://schemas.xmlsoap.org/soap/envelope/'
	
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
    
    @Value('${prehmis.facilityCode}')
    String prehmisFacilityCode;

    void save(DispensationDto dispensation) {

        def prehmisRestClient = new RESTClient(prehmisEndpointUrl)
        
        def storeDispensationRequest = buildStoreDispensationRequest(dispensation)
        
        try {
            apiLogin(prehmisRestClient)

            def storeDispensationResponse = prehmisRestClient.post(
                    body: storeDispensationRequest,
                    contentType: ContentType.XML,
                    requestContentType: ContentType.XML,
                    headers: [
                        SOAPAction: prehmisEndpointBaseUrl + "/storeDispensation"
                    ]
                    )

            def result = storeDispensationResponse.data

            LOGGER.info("PREHMIS response: "+result)
            if (!result.equals("Dispensation saved")) {
                LOGGER.error("Unable to store dispensation '" + dispensation + "' on PREHMIS. Error: "+result);
                throw new DispensationNotSavedException("Error while storing dispensation on PREHMIS. Error: " + result)
            }

        } catch (Exception e) {
            LOGGER.error("Unable to communicate with PREHMIS while trying to store dispensation '" + dispensation + "'.",e);
            throw new DispensationNotSavedException("Error while storing dispensation on PREHMIS. Error: " + e.message, e)
        }
    }

    void update(DispensationEvent dispensationEvent) {
        
        def prehmisFacilityIdentifier = getFacilityIdentifiable(dispensationEvent)
        if (prehmisFacilityIdentifier == null) {
            return
        }

        def prehmisRestClient = new RESTClient(prehmisEndpointUrl)
        def updateDispensationRequest
        def updateDispensationResponse

        try {
            updateDispensationRequest = buildUpdateDispensationRequest(prehmisFacilityIdentifier, dispensationEvent.dispensation)
        } catch (Exception e) {
            String errorMessage = "Unable to create updatedispensation request for dispensation '"+dispensationEvent.dispensation.id+"'. Error: "+e.message
            saveEventError(errorMessage, dispensationEvent)
            throw new DispensationNotSavedException(errorMessage, e)
        }

        try {
            apiLogin(prehmisRestClient, prehmisFacilityIdentifier)

            updateDispensationResponse = prehmisRestClient.post(
                    body: updateDispensationRequest,
                    contentType: ContentType.XML,
                    requestContentType: ContentType.XML,
                    headers: [
                        SOAPAction: prehmisEndpointBaseUrl + "/updateDispensation"
                    ]
                    )
        } catch (Exception e) {
            String errorMessage = "Unable to communicate with PREHMIS while trying to update dispensation '"+dispensationEvent.dispensation.id+"'. Error: "+e.message
            saveEventError(errorMessage, dispensationEvent)
            throw new DispensationNotSavedException(errorMessage, e)
        }

        def result = updateDispensationResponse.data
        LOGGER.info("PREHMIS response: "+result)
        if (!result.equals("Dispensation updated")) {
            String errorMessage = "Unable to store dispensation '"+dispensationEvent.dispensation.id+"' on PREHMIS. Error: "+result
            saveEventError(errorMessage, dispensationEvent)
            throw new DispensationNotSavedException(errorMessage)
        }
    }        
        
    void delete(DispensationDto dispensation) {
        def prehmisFacilityIdentifier = getFacilityIdentifiable(dispensationEvent)
        if (prehmisFacilityIdentifier == null) {
            return
        }

        def prehmisRestClient = new RESTClient(prehmisEndpointUrl)
        def deleteDispensationRequest
        def deleteDispensationResponse
        
        try {
            deleteDispensationRequest = buildDeleteDispensationRequest(prehmisFacilityIdentifier, dispensationEvent.dispensation)
        } catch (Exception e) {
            String errorMessage = "Unable to create deletedispensation request for dispensation '"+dispensationEvent.dispensation.id+"'. Error: "+e.message
            saveEventError(errorMessage, dispensationEvent)
            throw new DispensationNotDeletedException(errorMessage, e)
        }

        try {
            apiLogin(prehmisRestClient, prehmisFacilityIdentifier)

            deleteDispensationResponse = prehmisRestClient.post(
                    body: deleteDispensationRequest,
                    contentType: ContentType.XML,
                    requestContentType: ContentType.XML,
                    headers: [
                        SOAPAction: prehmisEndpointBaseUrl + "/deleteDispensation"
                    ]
                    )
        } catch (Exception e) {
            String errorMessage = "Unable to communicate with PREHMIS while trying to delete dispensation '"+dispensationEvent.dispensation.id+"'. Error: "+e.message
            saveEventError(errorMessage, dispensationEvent)
            throw new DispensationNotDeletedException(errorMessage, e)
        }

        def result = deleteDispensationResponse.data
        LOGGER.info("PREHMIS response: "+result)
        if (!result.equals("Dispensation deleted")) {
            String errorMessage = "Unable to delete dispensation '"+dispensationEvent.dispensation.id+"' on PREHMIS. Error: "+result
            saveEventError(errorMessage, dispensationEvent)
            throw new DispensationNotDeletedException(errorMessage)
        }
    }
    
    String buildStoreDispensationRequest(DispensationDto dispensation) {

        String storeDispensationRequest = buildStoreDispensationRequest([
            xmlnsPreh: prehmisNamespace,
            username: prehmisUsername,
            password: prehmisPassword,
            applicationKey: prehmisApplicationKey,
            facilityCode: prehmisFacilityCode,
            dispensation: getPrehmisDispensationMap(prehmisFacilityCode, dispensation, true)
        ])

        storeDispensationRequest
    }

    String buildUpdateDispensationRequest(String facilityCode, DispensationDto dispensation) {
        
        String updateDispensationRequest = buildUpdateDispensationRequest([
            xmlnsPreh: prehmisNamespace,
            username: prehmisUsername,
            password: prehmisPassword,
            applicationKey: prehmisApplicationKey,
            facilityCode: facilityCode,
            dispensation: getPrehmisDispensationMap(facilityCode, dispensation, true)
        ])

        updateDispensationRequest
    }
        
    String buildDeleteDispensationRequest(String facilityCode, DispensationDto dispensation) {

        String deleteDispensationRequest = buildDeleteDispensationRequest([
            xmlnsPreh: prehmisNamespace,
            username: prehmisUsername,
            password: prehmisPassword,
            applicationKey: prehmisApplicationKey,
            facilityCode: facilityCode,
            dispensation: getPrehmisDispensationMap(facilityCode, dispensation, false)
        ])

        deleteDispensationRequest
    }
    
    Map<String, Object> getPrehmisDispensationMap(String facilityCode, DispensationDto dispensation, boolean includeDrugs) {
        def prehmisDispensation = [:]

        dispensation.with {

            def dispensationIdentifiable = identifiableService
                    .resolveIdentifiable(PRESCRIBED_MEDICATION, newIdentifiers(dispensation..id.value))

            prehmisDispensation.id = getIdentifierValue(dispensationIdentifiable.identifiers, IDART_WEB.id)

            def patient = patientService.findByPatientId(dispensation.patient)
            def patientIdentifiable = identifiableService
                    .resolveIdentifiable(PATIENT, newIdentifiers(patient.id.value))

            prehmisDispensation.prehmisPatientId = getIdentifierValue(patientIdentifiable?.identifiers, PREHMIS.id)
            prehmisDispensation.pgwcPatientNumber = getIdentifierValue(patientIdentifiable?.identifiers, PGWC.id)

            def person = personService.findByPersonId(patient.person)
            def personIdentifiable = identifiableService
                    .resolveIdentifiable(PERSON, newIdentifiers(person.id.value))

            prehmisDispensation.patientSaIdNumber = getIdentifierValue(personIdentifiable?.identifiers, SA_IDENTITY_NUMBER.id)

            def dispenser = practitionerService.findByPractitionerId(dispensation.dispenser)
            def dispenserIdentifiable = identifiableService
                    .resolveIdentifiable(PRACTITIONER, newIdentifiers(dispenser.id.value))

            prehmisDispensation.practitionerCode = getIdentifierValue(dispenserIdentifiable?.identifiers, PREHMIS.id)

            prehmisDispensation.dispensationDate = toPrehmisDate(handedOver)

            if (dispensedMedications.size() != 0) {

                prehmisDispensation.dispensedMedications = dispensedMedications.collect { dispensedMedication ->

                    if (prehmisDispensation.prescription == null) {

                        def prescriptionId = prescriptionDataWarehouse
                                .findByPrescribedMedication(dispensedMedication.authorizingPrescribedMedication)

                        prehmisDispensation.prescription = prescriptionId.value

                    }

                    if (prehmisDispensation.supplyDuration == null) {
                        prehmisDispensation.supplyDuration = dispensedMedication.expectedSupplyDuration?.value
                    }
                    
                    if (includeDrugs) {
                        def medication = productService.findByProductId(dispensedMedication.medication)
                        def drug = partService.findByPartId((medication as Medication).drug)
    
                        [
                                atcCode: getClassificationCode(drug.classifications, ATC),
                                quantity: dispensedMedication.quantity?.value,
                        ]
                    }
                }
            }
        }

        prehmisDispensation
    }

    void apiLogin(RESTClient prehmisRestClient) throws Exception {
        def apiLoginRequest = buildApiLoginRequest([
            xmlnsPreh: prehmisNamespace,
            username: prehmisUsername,
            password: prehmisPassword,
            applicationKey: prehmisApplicationKey,
            facilityCode: prehmisFacilityCode,
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

    static toPrehmisDate(Date date) {

        if (date == null) {
            return null
        }

        new SimpleDateFormat("yyyy-MM-dd").format(date)
    }
}
