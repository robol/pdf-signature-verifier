package it.unipi.dm.pdfsignatureverifier;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ValidationInput {

  /**
   *  This field contains the content of the PDF file
   */
  public byte[] data;

  public static ValidationInput fromJSON(String json) {
    ObjectMapper mapper = new ObjectMapper();

    try {
      ValidationInput input = mapper.readValue(json, ValidationInput.class);
      return input;
    }
    catch (Exception e) {
      return null;
    }
  }

}
