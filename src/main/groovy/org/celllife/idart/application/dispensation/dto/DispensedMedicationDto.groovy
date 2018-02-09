package org.celllife.idart.application.dispensation.dto

import org.celllife.idart.common.Duration
import org.celllife.idart.common.Identifier
import org.celllife.idart.common.Period
import org.celllife.idart.common.Quantity
import org.celllife.idart.domain.dosageinstruction.DosageInstruction

/**
 * User: Kevin W. Sewell
 * Date: 2013-09-19
 * Time: 14h10
 */
class DispensedMedicationDto implements Serializable {

    /**
     * Medication
     */
    Set<Identifier> medication

    /**
     * Quantity
     */
    Quantity quantity

    /**
     * Prepared during
     */
    Period prepared

    /**
     * Dosage Instruction
     */
    DosageInstruction dosageInstruction

    /**
     * Expected Supply Duration
     */
    Duration expectedSupplyDuration

    /**
     * Authorized by
     */
    Set<Identifier> authorizingPrescribedMedication

}
