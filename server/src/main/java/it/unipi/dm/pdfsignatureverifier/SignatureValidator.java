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

public class SignatureValidator {

  private BouncyCastleProvider provider;
  private KeyStore ks;

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
      System.out.println("Cannot open the certificate store");
      return;
    }

    if (path == null) {
      return;
    }
    else {
      System.out.println("Loading custom certificate: " + path);
    }

    CertificateFactory fact;
    try {
      fact = CertificateFactory.getInstance("X.509");
    } catch (Exception e) {
      System.out.println("Unable to create the Certificate Factor");
      return;
    }

    InputStream crt_stream;
    try {
      crt_stream = new FileInputStream(path);
    } catch (Exception e) {
      System.out.println("Unable to load cert.pem");
      return;
    }

    Certificate crt;
    try {
      crt = fact.generateCertificate(crt_stream);
    } catch (Exception e) {
      System.out.println("Unable to generate certificate from cert.pem");
      return;
    }

    try {
      ks.setCertificateEntry("unipi", crt);
    } catch (Exception e) {
      System.out.println("Unable to store new certificate into the KeyStore");
      return;
    }
  }

  public List<ValidationResult> validate(InputStream stream) {
    PdfReader reader;
    List<ValidationResult> results = new LinkedList<ValidationResult>();

    // System.out.println(file);

    try {
      // FileInputStream stream = new FileInputStream(file);
      // String data = stream.read();
      // InputStream stream = new ByteArrayInputStream(file.getBytes());
      reader = new PdfReader(stream);
    }
    catch(Exception e) {
      System.out.printf("Error reading the given file\n");
      // System.out.print(file);
      System.out.println(e);
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
