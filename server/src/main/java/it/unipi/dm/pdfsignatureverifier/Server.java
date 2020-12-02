package it.unipi.dm.pdfsignatureverifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import static spark.Spark.*;
import spark.Request;
import spark.Response;
import java.util.List;
import java.lang.System;
import java.io.InputStream;
import javax.servlet.http.Part;
import javax.servlet.MultipartConfigElement;



public class Server {

    private static SignatureValidator validator;

    public static void main(String[] args) {
        validator = new SignatureValidator(System.getenv("PSV_CERT_PATH"));

        get("/", (req, res) -> Server.index(req, res));
        post("/validate", (req, res) -> Server.validate(req, res));
    }

    public static String index(Request req, Response res) {
      return "This is the PDF Signature Validator server";
    }

    public static String validate(Request req, Response res) {
      // String file = req.body();
      // String file = req.queryParams("file");
      InputStream file;
      try {
        req.raw().setAttribute("org.eclipse.jetty.multipartConfig",
            new MultipartConfigElement("/tmp", 100000000, 100000000, 1024));

        file = req.raw().getPart("file").getInputStream();
      }
      catch (Exception e) {
        System.out.println(e);
        return "[]";
      }

      if (file == null) {
        return "Invalid request";
      }

      ObjectMapper mapper = new ObjectMapper();

      List<ValidationResult> result = validator.validate(file);

      try {
        return mapper.writeValueAsString(result);
      }
      catch (Exception e) {
        return "Failed";
      }
    }

}
