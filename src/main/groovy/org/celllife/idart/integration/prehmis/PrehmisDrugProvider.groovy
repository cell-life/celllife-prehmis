package org.celllife.idart.integration.prehmis

import org.celllife.idart.integration.prehmis.builder.PrehmisRequestBuilder
import static org.springframework.util.Assert.notNull
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient

import org.celllife.idart.application.part.DrugProvider
import org.celllife.idart.framework.aspectj.LogLevel
import org.celllife.idart.framework.aspectj.Loggable
import org.celllife.idart.integration.prehmis.builder.AtcCodeBuilder
import org.celllife.idart.integration.prehmis.builder.PrehmisRequestBuilder
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * PREMIS implementation of the DrugProvider interface. Used to retrieve a druglist from PREHMIS
 * in order to synch ATC codes with iDART.
 */
@Service class PrehmisDrugProvider implements DrugProvider, InitializingBean {

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

    RESTClient prehmisRestClient

    @Autowired
    AtcCodeBuilder atcCodeBuilder

	@Loggable(LogLevel.INFO)
    def findAll(String clinicIdValue) {

        String getDrugListRequest = PrehmisRequestBuilder.buildGetDrugListRequest(
                xmlnsPreh: prehmisNamespace,
                username: prehmisUsername,
                password: prehmisPassword,
                applicationKey: prehmisApplicationKey,
                facilityCode: clinicIdValue
        )

        def getDrugResponse = prehmisRestClient.post(
                body: getDrugListRequest,
                contentType: ContentType.XML,
                requestContentType: ContentType.XML,
                headers: [
                        SOAPAction: prehmisEndpointBaseUrl + "/getDrugList"
                ]
        )

        return atcCodeBuilder.buildAtcCodes(getDrugResponse)
    }

    @Override
    void afterPropertiesSet() throws Exception {

        notNull(prehmisEndpointUrl)

        notNull(prehmisUsername)

        notNull(prehmisPassword)

        notNull(prehmisApplicationKey)

        prehmisRestClient = new RESTClient(prehmisEndpointUrl)

    }

}
