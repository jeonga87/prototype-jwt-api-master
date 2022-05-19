package kr.co.demo.attach.domain;

import kr.co.demo.core.domain.Attach;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Class Name : AttachBag.java
 * Description : 첨부파일 domain
 * Writer : lee.j
 */

@Data
@EqualsAndHashCode(callSuper=false)
public class AttachBag extends HashMap<String, List<Attach>> implements Serializable {
  public Attach one(String mapCode) {
    List<Attach> attachList = get(mapCode);
    if(attachList != null && attachList.size() > 0) {
      return attachList.get(0);
    }
    return null;
  }
}
