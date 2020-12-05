package it.unipi.dm.pdfsignatureverifier;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.security.PdfPKCS7;
import java.io.File;
import java.security.cert.Certificate;
import com.itextpdf.text.pdf.security.CertificateVerification;
import com.itextpdf.text.pdf.security.VerificationException;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.LinkedList;
import java.io.InputStream;
import java.io.FileInputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignatureValidator {

  private BouncyCastleProvider provider;
  private KeyStore ks = null;
  private final Logger logger = LoggerFactory.getLogger(SignatureValidator.class);

  /**
   * Validate signatures in PDF files.
   *
   * @param cert_path is a folder containing additional certificates that should
   *                  be trusted by the validator. If cert_path is null, then no
   *                  additional certificates are loaded.
   */
  public SignatureValidator(String cert_path) {
    provider = new BouncyCastleProvider();
    Security.addProvider(provider);

    if (initializeKeystore()) {
      try {
        loadCertificates(cert_path);
      } catch (Exception e) {
        logger.error("An error was encountered while loading the custom certificates");
        System.out.println(e);
      }
    }
  }

  private boolean initializeKeystore() {
    if (ks == null) {
      try {
        ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
      } catch (Exception e) {
        logger.error("Cannot open the certificate store");
        return false;
      }
    }

    return true;
  }

  private void loadCertificates(String path) throws Exception {
    if (path == null) {
      return;
    }
    else {
      logger.info(String.format("Loading custom certificate from %s",  path));
    }

    File certFolder = new File(path);
    String[] files = certFolder.list();

    CertificateFactory fact = CertificateFactory.getInstance("X.509");

    for (File f : certFolder.listFiles()) {
      if (f.getName().endsWith(".pem")) {
        logger.info("Loading certificate: " + f.getName());
        InputStream crt_stream = new FileInputStream(f);
        Certificate crt = fact.generateCertificate(crt_stream);
        ks.setCertificateEntry("psv-custom-" + f, crt);
      }
    }
  }

  public List<ValidationResult> validate(InputStream stream) {
    PdfReader reader;
    List<ValidationResult> results = new LinkedList<ValidationResult>();

    try {
      reader = new PdfReader(stream);
    }
    catch(Exception e) {
      logger.error("Error while parsing the PDF file");
      return results;
    }

    AcroFields acroFields = reader.getAcroFields();
    List<String> signatureNames = acroFields.getSignatureNames();

    for (String name : signatureNames) {
      ValidationResult result = new ValidationResult();
      PdfDictionary d = acroFields.getSignatureDictionary(name);

      PdfPKCS7 pk = acroFields.verifySignature(name);
      result.date = pk.getSignDate();
      result.name = pk.getSignName();
      Certificate pkc[] = pk.getCertificates();

      result.valid = true;
      for (Certificate c : pkc) {
        List<VerificationException> errors =
          CertificateVerification.verifyCertificates(pkc, ks, result.date);
        result.valid = result.valid && (errors.size() == 0);
      }

      results.add(result);
    }

    return results;
  }

}
