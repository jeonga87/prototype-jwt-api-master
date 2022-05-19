package kr.co.demo.util;

import lombok.Data;
import lombok.ToString;

/**
 * Class Name : ApiResponseMessage.java
 * Description : json message
 * Writer : lee.j
 */


@Data
@ToString
public class ApiResponseMessage {

  // Error Message to USER
  private String errorMessage;
  // Error Code
  private String errorCode;

  public ApiResponseMessage() {}

  public ApiResponseMessage(String errorCode, String errorMessage) {
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }
}
