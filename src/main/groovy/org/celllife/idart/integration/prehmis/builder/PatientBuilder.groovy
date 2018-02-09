package org.celllife.idart.integration.prehmis.builder

import static org.celllife.idart.common.Identifiers.newIdentifier

import org.celllife.idart.application.patient.dto.PatientDto

/**
 * Will build any patient related object given a response from e.g. PREHMIS
 */
interface PatientBuilder {

    PatientDto buildIdartPatient(envelope)
}
