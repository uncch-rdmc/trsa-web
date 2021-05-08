package edu.harvard.iq.dataverse.ingest;

/**
 *
 * @author asone
 */
public class IngestException extends RuntimeException {

    public IngestException(String message, Throwable cause) {
        super(message, cause);
    }

    public IngestException(String message) {
        super(message);
    }


}
