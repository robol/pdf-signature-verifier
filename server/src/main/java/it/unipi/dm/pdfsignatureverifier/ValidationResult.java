package it.unipi.dm.pdfsignatureverifier;

import java.util.Calendar;

public class ValidationResult {

  /**
   * This field contains the name associated with the PDF signature
   */
  public String name;

  /**
   * The timestamp for the signature
   */
  public Calendar date;

  /**
   * A boolean value indicating if the signature could be verified or not. If this is false,
   * then the correct root certificate might be missing from the keystore.
   */
  public boolean valid;

}
