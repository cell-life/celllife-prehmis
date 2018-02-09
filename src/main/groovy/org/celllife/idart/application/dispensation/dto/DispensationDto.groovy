package org.celllife.idart.application.dispensation.dto

import org.celllife.idart.common.FacilityId
import org.celllife.idart.common.Identifier

/**
 */
class DispensationDto implements Serializable {

    /**
     * Identified by
     */
    Set<Identifier> identifiers = [] as Set

    /**
     * Dispensed to
     *
     * Although the patient could be inferred from the prescription, this is put here explicitly because the dispense
     * action may take place without a prescription.
     */
    Set<Identifier> patient = [] as Set

    /**
     * Dispensed by
     */
    Set<Identifier> dispenser = [] as Set

    /**
     * Dispensed at
     */
    Set<Identifier> facility = [] as Set

    /**
     * Handed over during
     */
    Date handedOver

    /**
     * Contains
     */
    Set<DispensedMedicationDto> dispensedMedications = [] as Set
}
