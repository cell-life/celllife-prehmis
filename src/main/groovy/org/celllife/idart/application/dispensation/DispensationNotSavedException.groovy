package org.celllife.idart.application.dispensation

/**
 * User: Kevin W. Sewell
 * Date: 2013-09-19
 * Time: 17h42
 */
class DispensationNotSavedException extends RuntimeException {

    DispensationNotSavedException(String message) {
        super(message)
    }
    
    DispensationNotSavedException(String message, Exception e) {
        super(message, e)
    }
}
