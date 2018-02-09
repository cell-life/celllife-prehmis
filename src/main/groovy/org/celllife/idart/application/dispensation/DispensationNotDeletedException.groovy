package org.celllife.idart.application.dispensation

/**
 * Exception that indicates that it was not possible to delete a dispensation
 */
class DispensationNotDeletedException extends RuntimeException {

    DispensationNotDeletedException(String message) {
        super(message)
    }
    
    DispensationNotDeletedException(String message, Exception e) {
        super(message, e)
    }
}
