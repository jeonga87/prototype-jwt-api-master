package kr.co.demo.common;

import kr.co.demo.util.ApiResponseMessage;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Class Name : ControllerExceptionHandler.java
 * Exception 예외 발생시 공통 처리 로직
 * Writer : lee.j
 */

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ControllerExceptionHandler {

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseBody
  public ResponseEntity<?> BadRequestException() {
    return new ResponseEntity<>(new ApiResponseMessage("605", "bad request"), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseBody
  public ResponseEntity<?> RequestParameterException() {
    return new ResponseEntity<>(new ApiResponseMessage("605", "bad request"), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ResponseEntity<?> handleException() {
    return new ResponseEntity<>(new ApiResponseMessage("606", "internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
