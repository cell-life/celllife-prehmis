package org.celllife.idart.integration.prehmis

import org.celllife.idart.integration.prehmis.builder.PractitionerBuilder
import org.celllife.idart.integration.prehmis.builder.PrehmisRequestBuilder
import static org.springframework.util.Assert.notNull
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient

import org.celllife.idart.application.practitioner.PractitionerProvider
import org.celllife.idart.application.practitioner.dto.PractitionerDto
import org.celllife.idart.framework.aspectj.LogLevel
import org.celllife.idart.framework.aspectj.Loggable
import org.celllife.idart.integration.prehmis.builder.PractitionerBuilder
import org.celllife.idart.integration.prehmis.builder.PrehmisRequestBuilder
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * Provides helpful services used when connecting to PREHMIS and retrieving patients
 */
@Service class PrehmisPractitionerProvider implements PractitionerProvider, InitializingBean {

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
    PractitionerBuilder practitionerBuilder

    @Override
	@Loggable(LogLevel.INFO)
    Set<PractitionerDto> findAll(String clinicIdValue) {

        String getPractitionerListRequest = PrehmisRequestBuilder.buildGetPractitionerListRequest(
                xmlnsPreh: prehmisNamespace,
                username: prehmisUsername,
                password: prehmisPassword,
                facilityCode: clinicIdValue,
                applicationKey: prehmisApplicationKey
        )

        RESTClient prehmisRestClient = new RESTClient(prehmisEndpointUrl)
        def getPractionerListResponse = prehmisRestClient.post(
                body: getPractitionerListRequest,
                contentType: ContentType.XML,
                requestContentType: ContentType.XML,
                headers: [
                        SOAPAction: prehmisEndpointBaseUrl + "/getPractitionerList"
                ]
        )

        return practitionerBuilder.buildPractitioners(getPractionerListResponse)
    }

    @Override
    void afterPropertiesSet() throws Exception {

        notNull(prehmisEndpointUrl)

        notNull(prehmisUsername)

        notNull(prehmisPassword)

        notNull(prehmisApplicationKey)
    }
}
