package it.unipi.dm.pdfsignatureverifier;

import java.util.Calendar;
import java.util.Date;

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
   * DN field of the signing certificate
   */
  public String DN;

  /**
   * DN of the issuer of the signing certificate
   */
  public String issuerDN;

  /**
   * Date after which the certificate can be considered valid.
   */
  public Date notBefore;

  /**
   * Date before which the certificate can be considered valid.
   */
  public Date notAfter;

  /**
   * A boolean value indicating if the signature could be verified or not. If this is false,
   * then the correct root certificate might be missing from the keystore.
   */
  public boolean valid;

}
