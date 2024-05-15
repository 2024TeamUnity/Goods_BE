package com.unity.goods.domain.point.exception;

import com.unity.goods.global.exception.CustomException;
import com.unity.goods.global.exception.ErrorCode;

public class PointException extends CustomException {

  public PointException(ErrorCode errorCode) {super(errorCode);}
}
