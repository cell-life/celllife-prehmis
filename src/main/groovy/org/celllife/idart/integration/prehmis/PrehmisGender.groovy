package org.celllife.idart.integration.prehmis

import org.celllife.idart.common.Gender

/**
 * User: Kevin W. Sewell
 * Date: 2013-04-26
 * Time: 12h22
 */
enum PrehmisGender {

    MALE("male", Gender.MALE),
    FEMALE("female", Gender.FEMALE)

    final String prehmisCode

    final Gender gender

    PrehmisGender(String prehmisCode, Gender gender) {

        this.prehmisCode = prehmisCode
        this.gender = gender
    }

    static Gender findByPrehmisCode(String prehmisCode) {
        for (PrehmisGender prehmisToIdartGenderMapping : values()) {
            if (prehmisToIdartGenderMapping.prehmisCode.equalsIgnoreCase(prehmisCode)) {
                return prehmisToIdartGenderMapping.gender
            }
        }
        return null
    }

}
