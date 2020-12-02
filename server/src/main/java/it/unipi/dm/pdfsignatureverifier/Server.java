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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

  private final static Logger logger = LoggerFactory.getLogger(Server.class);

  private static SignatureValidator validator;

  public static void main(String[] args) {
    validator = new SignatureValidator(System.getenv("PSV_CERT_PATH"));
    String port_value = System.getenv("PSV_PORT");

    // Setup port to listen on
    logger.info(
      String.format("Starting on port = %s",
        port_value != null ? port_value : "8081"
      )
    );

    port(port_value != null ? Integer.parseInt(port_value, 10) : 8081);

    get("/", (req, res) -> Server.index(req, res));
    post("/validate", (req, res) -> Server.validate(req, res));
  }

  public static String index(Request req, Response res) {
    return "This is the PDF Signature Validator server";
  }

  public static String validate(Request req, Response res) {
    InputStream file;
    try {
      req.raw().setAttribute("org.eclipse.jetty.multipartConfig",
        new MultipartConfigElement("/tmp", 100000000, 100000000, 1024));

      file = req.raw().getPart("file").getInputStream();
    }
    catch (Exception e) {
      logger.error("Error while reading the file");
      return "[]";
    }

    if (file == null) {
      logger.error("Invalid request, with no file to check");
      return "[]";
    }

    ObjectMapper mapper = new ObjectMapper();
    List<ValidationResult> result = validator.validate(file);

    try {
      return mapper.writeValueAsString(result);
    }
    catch (Exception e) {
      logger.error("Exception while convreting the data to JSON");
      return "[]";
    }
  }

}
