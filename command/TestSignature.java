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
import java.io.InputStream;
import java.io.FileInputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


public class TestSignature {

    public static void main(String[] args) {
        PdfReader reader;
        
        BouncyCastleProvider provider = new BouncyCastleProvider();
		Security.addProvider(provider);
		
        KeyStore ks;
        
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, null);
        } catch (Exception e) {
            System.out.println("Cannot open the certificate store");
            return;
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
            crt_stream = new FileInputStream("cert.pem");
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

        try {
            reader = new PdfReader(args[0]);
        }
        catch(Exception e) {
            System.out.printf("Error reading the given file: %s\n", args[0]);
            return;
        }

        AcroFields acroFields = reader.getAcroFields();
        List<String> signatureNames = acroFields.getSignatureNames();

        for (String name : signatureNames) {
            PdfDictionary d = acroFields.getSignatureDictionary(name);
            
            // System.out.printf("Signature: %s\n", name);
            for (PdfName key : d.getKeys()) {
                if (key.equals(PdfName.NAME)) {
                    System.out.printf("| Name: %s\n", d.get(key));
                }
                if (key.equals(PdfName.M)) {
                    System.out.printf("| M: %s\n", d.get(key));
                }
            }

            PdfPKCS7 pk = acroFields.verifySignature(name);
            Calendar cal = pk.getSignDate();
            Certificate pkc[] = pk.getCertificates();
            
            for (Certificate c : pkc) {
                try {
                    // Mainly for debugging, in case you want to see the certificate 
                    // X509Certificate dd = (X509Certificate) c;
                    // System.out.println(dd.toString());
                } catch (Exception e) {
                    System.out.println("Failed"); 
                    return;
                }
            }

            List<VerificationException> errors =
                CertificateVerification.verifyCertificates(pkc, ks, cal);

            if (errors.size() == 0)
                System.out.println("| Firma \033[32;1mvalida\033[0m");
            else
                System.out.println("| Firma \033[31;1mnon valida\033[0m");
        }
    }

}
