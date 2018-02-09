package org.celllife.idart.integration.prehmis

import static org.springframework.util.Assert.notNull
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient

import org.celllife.idart.application.patient.PatientProvider
import org.celllife.idart.application.patient.dto.PatientDto
import org.celllife.idart.framework.aspectj.LogLevel
import org.celllife.idart.framework.aspectj.Loggable
import org.celllife.idart.integration.prehmis.builder.PatientBuilder
import org.celllife.idart.integration.prehmis.builder.PrehmisRequestBuilder
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * Implementation of the PREHMIS patient API
 */
@Service class PrehmisPatientProvider implements PatientProvider, InitializingBean {

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
    
    @Autowired
    PatientBuilder patientBuilder

    @Override
	@Loggable(LogLevel.INFO)
    Set<PatientDto> findByIdentifier(String clinicIdentifierValue, String patientIdentifierValue) {

        Set<PatientDto> patients = []

        PrehmisPatientIdentifierType.values().each { PrehmisPatientIdentifierType identifierType ->

            def patient = getPatient(clinicIdentifierValue, patientIdentifierValue, identifierType)
            if (patient != null) {
                patients << patient
            }
        }

        patients
    }

    PatientDto getPatient(String clinicIdentifierValue,
                          String patientIdentifierValue,
                          PrehmisPatientIdentifierType identifierType) {

        String getPatientRequest = PrehmisRequestBuilder.buildGetPatientRequest(
                xmlnsPreh: prehmisNamespace,
                username: prehmisUsername,
                password: prehmisPassword,
                applicationKey: prehmisApplicationKey,
                facilityCode: clinicIdentifierValue,
                patientIdentifierValue: patientIdentifierValue,
                patientIdentifierType: identifierType.toString().toLowerCase()
        )

        RESTClient prehmisRestClient = new RESTClient(prehmisEndpointUrl)
        def getPatientResponse = prehmisRestClient.post(
                body: getPatientRequest,
                contentType: ContentType.XML,
                requestContentType: ContentType.XML,
                headers: [
                        SOAPAction: prehmisEndpointBaseUrl + "/getPatient"
                ]
        )

        return patientBuilder.buildIdartPatient(getPatientResponse.data)
    }

    @Override
    void afterPropertiesSet() throws Exception {

        notNull(prehmisEndpointUrl)

        notNull(prehmisUsername)

        notNull(prehmisPassword)

        notNull(prehmisApplicationKey)

    }

}
