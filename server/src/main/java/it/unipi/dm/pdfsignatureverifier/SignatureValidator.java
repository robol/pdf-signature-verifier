package it.unipi.dm.pdfsignatureverifier;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.security.PdfPKCS7;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import com.itextpdf.text.pdf.security.CertificateVerification;
import com.itextpdf.text.pdf.security.VerificationException;
import java.security.KeyStore;
import java.util.Calendar;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.LinkedList;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignatureValidator {

  private BouncyCastleProvider provider;
  private KeyStore ks;
  private final Logger logger = LoggerFactory.getLogger(SignatureValidator.class);

  public SignatureValidator(String cert_path) {
    provider = new BouncyCastleProvider();
    Security.addProvider(provider);

    loadCertificate(cert_path);
  }

  private void loadCertificate(String path) {
    try {
      ks = KeyStore.getInstance(KeyStore.getDefaultType());
      ks.load(null, null);
    } catch (Exception e) {
      logger.error("Cannot open the certificate store");
      return;
    }

    if (path == null) {
      return;
    }
    else {
      logger.info(String.format("Loading custom certificate: %s",  path));
    }

    CertificateFactory fact;
    try {
      fact = CertificateFactory.getInstance("X.509");
    } catch (Exception e) {
      logger.error("Unable to create the Certificate Factor");
      return;
    }

    InputStream crt_stream;
    try {
      crt_stream = new FileInputStream(path);
    } catch (Exception e) {
      logger.error(String.format("Unable to load the certificate: %s", path));
      return;
    }

    Certificate crt;
    try {
      crt = fact.generateCertificate(crt_stream);
    } catch (Exception e) {
      logger.error(String.format("Unable to generate a certificate from %s", path));
      return;
    }

    try {
      ks.setCertificateEntry("unipi", crt);
    } catch (Exception e) {
      logger.error("Unable to store new certificate into the KeyStore");
      return;
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
      result.valid = false;

      PdfDictionary d = acroFields.getSignatureDictionary(name);

      for (PdfName key : d.getKeys()) {
        if (key.equals(PdfName.NAME)) {
          result.name = d.get(key).toString();
        }
        if (key.equals(PdfName.M)) {
          result.date = d.get(key).toString();
        }
      }

      PdfPKCS7 pk = acroFields.verifySignature(name);
      Calendar cal = pk.getSignDate();
      Certificate pkc[] = pk.getCertificates();

      result.valid = true;
      for (Certificate c : pkc) {
        List<VerificationException> errors =
          CertificateVerification.verifyCertificates(pkc, ks, cal);
        result.valid = result.valid && (errors.size() == 0);
      }

      results.add(result);
    }

    return results;
  }

}
