package it.unipi.dm.pdfsignatureverifier;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ValidationResult {

  public String name;

  public String date;

  public boolean valid;

  public ValidationResult() {

  }

  public String toJSON() {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(this);
    }
    catch (Exception e) {
      return "{ result: 'Failed' }";
    }
  }

}
