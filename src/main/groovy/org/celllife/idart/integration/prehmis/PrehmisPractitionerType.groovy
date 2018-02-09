package org.celllife.idart.integration.prehmis

import org.celllife.idart.common.PractitionerType

/**
 * User: Kevin W. Sewell
 * Date: 2013-04-25
 * Time: 15h22
 */
enum PrehmisPractitionerType {

    ADMINISTRATION_ASISTANT("Administration Asistant", PractitionerType.ADMINISTRATION_ASISTANT),
    AGENCY_DOCTORS("Agency Doctors", PractitionerType.AGENCY_DOCTORS),
    AGENCY_NURSES("Agency Nurses", PractitionerType.AGENCY_NURSES),
    AGENCY_PHARMACIST("Agency Pharmacist", PractitionerType.AGENCY_PHARMACIST),
    AGENCY_STUDENT_NURSES("Agency Student Nurses", PractitionerType.AGENCY_STUDENT_NURSES),
    CHIEF_PROFESSIONAL_NURSE("Chief Professional Nurse", PractitionerType.CHIEF_PROFESSIONAL_NURSE),
    CLINICAL_MEDICAL_OFFICER("Clinical Medical Officer", PractitionerType.CLINICAL_MEDICAL_OFFICER),
    CLINICAL_NURSE_PRACTITIONER("Clinical Nurse Practitioner", PractitionerType.CLINICAL_NURSE_PRACTITIONER),
    CLINICAL_NURSING_ASSISTANT("Clinical Nursing Assistant", PractitionerType.CLINICAL_NURSING_ASSISTANT),
    CLINIC_ASSISTANT("Clinic Assistant", PractitionerType.CLINIC_ASSISTANT),
    CLINIC_MANAGER("Clinic Manager", PractitionerType.CLINIC_MANAGER),
    COMMUNITY_IMCI_MA_AFRIKA_TIKKUN("Community IMCI,Ma-AfrikaTikkun", PractitionerType.COMMUNITY_IMCI_MA_AFRIKA_TIKKUN),
    COMMUNITY_SERVICE_PSYCHIATRIST("Community Service Psychiatrist", PractitionerType.COMMUNITY_SERVICE_PSYCHIATRIST),
    COUNSELLOR("Counsellor", PractitionerType.COUNSELLOR),
    DENTIST("Dentist", PractitionerType.DENTIST),
    DIAGNOSTIC_RADIOGRAPHER("Diagnostic Radiographer", PractitionerType.DIAGNOSTIC_RADIOGRAPHER),
    DIETICIAN("Dietician", PractitionerType.DIETICIAN),
    ENROLLED_NURSE("Enrolled Nurse", PractitionerType.ENROLLED_NURSE),
    ENROLLED_NURSING_ASSISTANT("Enrolled Nursing Assistant", PractitionerType.ENROLLED_NURSING_ASSISTANT),
    HIV_TB_COUNSELOR("HIV/TB Counselor", PractitionerType.HIV_TB_COUNSELOR),
    MEDICAL_OFFICER("Medical Officer", PractitionerType.MEDICAL_OFFICER),
    MIDWIFE("Midwife", PractitionerType.MIDWIFE),
    NGO("NGO", PractitionerType.NGO),
    NUTRITIONIST("Nutritionist", PractitionerType.NUTRITIONIST),
    OCCUPATIONAL__THERAPIST("Occupational  Therapist", PractitionerType.OCCUPATIONAL__THERAPIST),
    ORTHOPEADIC("Orthopeadic", PractitionerType.ORTHOPEADIC),
    PHARMACIST_ASSISTANT("Pharmacist Assistant", PractitionerType.PHARMACIST_ASSISTANT),
    PHARMACIST("Pharmacist", PractitionerType.PHARMACIST),
    POST_BASIC_PHARMACIST_ASSISTANT_PBPA("Post Basic Pharmacist Assistant (PBPA)", PractitionerType.POST_BASIC_PHARMACIST_ASSISTANT_PBPA),
    PROFESSIONAL_NURSE("Professional Nurse", PractitionerType.PROFESSIONAL_NURSE),
    PSYCHOLOGIST("Psychologist", PractitionerType.PSYCHOLOGIST),
    RECEPTIONIST_DARKROOM_ASST("Receptionist/ Darkroom Asst", PractitionerType.RECEPTIONIST_DARKROOM_ASST),
    SENIOR_CLINIC_ASSISTANT("Senior Clinic Assistant", PractitionerType.SENIOR_CLINIC_ASSISTANT),
    SENIOR_ENROLLED_NURSE("Senior Enrolled Nurse", PractitionerType.SENIOR_ENROLLED_NURSE),
    SENIOR_ENROLLED_NURSING_ASSIST("Senior Enrolled Nursing Assist", PractitionerType.SENIOR_ENROLLED_NURSING_ASSIST),
    SENIOR_NURSING_ASSISTANT("Senior Nursing Assistant", PractitionerType.SENIOR_NURSING_ASSISTANT),
    SENIOR_PROFESSIONAL_NURSE("Senior Professional Nurse", PractitionerType.SENIOR_PROFESSIONAL_NURSE),
    SENIOR_STAFF_NURSE("Senior Staff Nurse", PractitionerType.SENIOR_STAFF_NURSE),
    SENIOR_THERAPIST("Senior Therapist", PractitionerType.SENIOR_THERAPIST),
    SOCIAL_WORKER("Social Worker", PractitionerType.SOCIAL_WORKER),
    STAFF_NURSE("Staff Nurse", PractitionerType.STAFF_NURSE),
    STUDENT_NURSE("Student Nurse", PractitionerType.STUDENT_NURSE),
    SUBSTANCE_ABUSE_SCREENING_CLERK("Substance Abuse Screening Clerk", PractitionerType.SUBSTANCE_ABUSE_SCREENING_CLERK),
    SUPP_DIAGNOSTIC_RADIOGRAPHER("Supp Diagnostic Radiographer", PractitionerType.SUPP_DIAGNOSTIC_RADIOGRAPHER),
    THERAPIST_SUPERVISOR("Therapist Supervisor", PractitionerType.THERAPIST_SUPERVISOR),
    THERAPIST("Therapist", PractitionerType.THERAPIST),
    TREATMENT_SUPPORTER_TB_CARE("Treatment Supporter - TB Care", PractitionerType.TREATMENT_SUPPORTER_TB_CARE),
    UNASSIGNED("Unassigned", PractitionerType.UNASSIGNED),
    UNKNOWN("Unknown", PractitionerType.UNKNOWN)

    final String name

    final PractitionerType practitionerType

    PrehmisPractitionerType(String name, PractitionerType practitionerType) {
        this.name = name
        this.practitionerType = practitionerType
    }

    static getPractitionerType(String name) {

        for (prehmisPractitionerType in values()) {
            if (prehmisPractitionerType.name.equals(name) ) {
                return prehmisPractitionerType.practitionerType
            }
        }

        return null
    }
}

