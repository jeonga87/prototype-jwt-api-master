package kr.co.demo.attach.domain;

import com.google.common.collect.ImmutableMap;
import kr.co.demo.attach.filter.impl.MaxSizeExceptionFilter;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class Name : ReferenceTypeRegistry.java
 * Description : 첨부파일용 ReferenceType 설정
 * Writer : lee.j
 */

@Getter
public class ReferenceTypeRegistry implements Serializable {
  private static Map<String, ReferenceType> refTypeMap = new HashMap<>();

  public static ReferenceType get(String type) {
    return refTypeMap.get(type);
  }

  /** 게시판 */
  public static ReferenceType BOARD = new DefaultReferenceType() {
    @Override
    public String getTypeName() {
      return "board";
    }
  };
  static {
    try {
      BOARD.addMapCode("csImage")
        // 용량제한
        .addFilter(
          MaxSizeExceptionFilter.class,
          ImmutableMap.<String, Object>of(MaxSizeExceptionFilter.PARAM_MAX_SIZE, 52428800l)
        );
    } catch (Exception e) {
      e.printStackTrace();
    }
    refTypeMap.put("BOARD", BOARD);
  }
}
