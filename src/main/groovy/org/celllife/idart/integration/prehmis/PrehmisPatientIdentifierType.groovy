package org.celllife.idart.integration.prehmis

import org.celllife.idart.common.SystemId
import org.celllife.idart.common.Systems

/**
 * User: Kevin W. Sewell
 * Date: 2013-04-25
 * Time: 15h22
 */
enum PrehmisPatientIdentifierType {

    PGWC(Systems.PGWC.id, "PGWC Patient Number"),

    PREHMIS(Systems.PREHMIS.id, "PREHMIS ID"),

    SAID(Systems.SA_IDENTITY_NUMBER.id, "National ID Number"),

    PASSPORT(Systems.SA_PASSPART_NUMBER.id, "Passport Number")

    final SystemId system

    final String description

    PrehmisPatientIdentifierType(SystemId system, String description) {
        this.system = system
        this.description = description
    }

    String getDescription() {
        return this.description
    }

    SystemId getSystem() {
        return this.system
    }
}
