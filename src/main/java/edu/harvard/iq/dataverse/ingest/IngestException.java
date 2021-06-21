package edu.harvard.iq.dataverse.ingest;

import javax.ejb.ApplicationException;

/**
 *
 * @author asone
 */
@ApplicationException
public class IngestException extends RuntimeException {

    public IngestException(String message, Throwable cause) {
        super(message, cause);
    }

    public IngestException(String message) {
        super(message);
    }


}
