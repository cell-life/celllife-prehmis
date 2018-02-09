package org.celllife.idart.application.dispensation.dto

import org.celllife.idart.application.facility.FacilityApplicationService
import org.celllife.idart.application.patient.PatientApplicationService
import org.celllife.idart.application.practitioner.PractitionerApplicationService
import org.celllife.idart.application.prescribedmedication.PrescribedMedicationApplicationService
import org.celllife.idart.application.product.ProductApplicationService
import org.celllife.idart.domain.dispensation.Dispensation
import org.celllife.idart.domain.dispensation.DispensedMedication

import javax.inject.Inject
import javax.inject.Named

/**
 */
@Named class DispensationDtoAssembler {

    @Inject PatientApplicationService patientApplicationService

    @Inject PractitionerApplicationService practitionerApplicationService

    @Inject ProductApplicationService productApplicationService

    @Inject FacilityApplicationService facilityApplicationService

    @Inject PrescribedMedicationApplicationService prescribedMedicationApplicationService

    Dispensation toDispensation(DispensationDto dispensationDto) {

        def dispensation = new Dispensation()
        dispensation.with {

            patient = patientApplicationService.findByIdentifiers(dispensationDto.patient)

            dispenser = practitionerApplicationService.findByIdentifiers(dispensationDto.dispenser)

            facility = facilityApplicationService.findByIdentifiers(dispensationDto.facility)

            handedOver = dispensationDto.handedOver

            dispensedMedications = dispensationDto.dispensedMedications.collect { dispensedMedicationDto ->
                toDispensedMedication(dispensedMedicationDto)
            }

        }

        dispensation
    }

    DispensedMedication toDispensedMedication(DispensedMedicationDto dispensedMedicationDto) {

        def dispensedMedication = new DispensedMedication()
        dispensedMedication.with {

            medication = productApplicationService.findByIdentifiers(dispensedMedicationDto.medication)

            quantity = dispensedMedicationDto.quantity

            prepared = dispensedMedicationDto.prepared

            dosageInstruction = dispensedMedicationDto.dosageInstruction

            expectedSupplyDuration = dispensedMedicationDto.expectedSupplyDuration

            authorizingPrescribedMedication = prescribedMedicationApplicationService
                    .findByIdentifiers(dispensedMedicationDto.authorizingPrescribedMedication)
        }

        dispensedMedication
    }

    DispensationDto toDispensationDto(Dispensation dispensation) {

        def dispensationDto = new DispensationDto()
        dispensationDto.with {

            patient = patientApplicationService.findByPatientId(dispensation.patient).identifiers

            dispenser = practitionerApplicationService.findByPractitionerId(dispensation.dispenser).identifiers

            facility = facilityApplicationService.findByFacilityId(dispensation.facility).identifiers

            handedOver = dispensation.handedOver



            dispensedMedications = dispensation.dispensedMedications.collect { dispensedMedication ->
                toDispensedMedicationDto(dispensedMedication)
            }

        }

        dispensationDto
    }

    DispensedMedicationDto toDispensedMedicationDto(DispensedMedication dispensedMedication) {

        def dispensedMedicationDto = new DispensedMedicationDto()
        dispensedMedicationDto.with {

            medication = productApplicationService.findByProductId(dispensedMedication.medication).identifiers

            quantity = dispensedMedication.quantity

            prepared = dispensedMedication.prepared

            dosageInstruction = dispensedMedication.dosageInstruction

            expectedSupplyDuration = dispensedMedication.expectedSupplyDuration

            authorizingPrescribedMedication = prescribedMedicationApplicationService
                    .findByPrescribedMedicationId(dispensedMedication.authorizingPrescribedMedication).identifiers
        }

        dispensedMedicationDto
    }
}
