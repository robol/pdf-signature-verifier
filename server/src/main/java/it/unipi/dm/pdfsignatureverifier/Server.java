package it.unipi.dm.pdfsignatureverifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import static spark.Spark.*;
import spark.Request;
import spark.Response;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.lang.System;
import java.io.ByteArrayInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

public class Server {

  private final static Logger logger = LoggerFactory.getLogger(Server.class);

  private SignatureValidator validator;

  public static void main(String[] args) {
    Server s = new Server();
  }

  public Server() {
    validator = new SignatureValidator(System.getenv("PSV_CERT_PATH"));
    String port_value = System.getenv("PSV_PORT");

    // Setup port to listen on
    logger.info(
            String.format("Starting on port = %s",
                    port_value != null ? port_value : "8081"
            )
    );

    port(port_value != null ? Integer.parseInt(port_value, 10) : 8081);

    Spark.staticFiles.location("/assets");

    get("/", (req, res) -> this.index(req, res));
    get("/validate", (req, res) -> this.validate_home_page(req, res));

    post("/validate",  (req, res) -> {
        res.header("Access-Control-Allow-Origin", "*");
        res.header("Access-Control-Allow-Headers", "*");
        return this.validate(req, res);
    });

    options("/validate", (req, res) -> {
      res.header("Access-Control-Allow-Origin", "*");
      res.header("Access-Control-Allow-Headers", "*");
      return "[]";
    });
  }

  public String index(Request req, Response res) {
    return "This is the PDF Signature Validator server";
  }

  public byte[] validate_home_page(Request req, Response res) {
    ClassLoader loader = getClass().getClassLoader();

    try {
      InputStream templateStream = loader.getResource(
              "templates/validate.html").openStream();
      byte[] template = templateStream.readAllBytes();
      return template;
    }
    catch (Exception e) {
      logger.error("Unable to read template file");
      logger.error(e.toString());
    }

    return "Cannot read template".getBytes(StandardCharsets.UTF_8);
  }

  public String validate(Request req, Response res) {
    ValidationInput input = ValidationInput.fromJSON(req.body());

    if (input == null) {
      logger.error("Invalid request, with no file to check");
      return "[]";
    }

    ByteArrayInputStream stream = new ByteArrayInputStream(input.data);
    List<ValidationResult> result = validator.validate(stream);

    ObjectMapper mapper = new ObjectMapper();

    try {
      return mapper.writeValueAsString(result);
    }
    catch (Exception e) {
      logger.error("Exception while converting the data to JSON");
      return "[]";
    }
  }

}
