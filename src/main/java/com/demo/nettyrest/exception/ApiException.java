package com.demo.nettyrest.exception;

public class ApiException extends Exception {
  private int errNo;

  public int getErrNo() {
    return errNo;
  }

  public ApiException() {
    super();
  }

  public ApiException(int errNo) {
    super(StatusCode.getMessage(errNo));
    this.errNo = errNo;
  }

  public ApiException(int errNo, String errMsg) {
    super(errMsg);
    this.errNo = errNo;
  }

  public ApiException(int errNo, String errMsg, Throwable cause) {
    super(errMsg, cause);
    this.errNo = errNo;
  }
}
