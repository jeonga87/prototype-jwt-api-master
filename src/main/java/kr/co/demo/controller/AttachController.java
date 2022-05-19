package kr.co.demo.controller;

import kr.co.demo.attach.domain.ReferenceTypeRegistry;
import kr.co.demo.attach.filter.AttachFilterChain;
import kr.co.demo.config.properties.FileUploadProperties;
import kr.co.demo.core.domain.Attach;
import kr.co.demo.core.service.AttachService;
import kr.co.demo.util.WebUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Class Name : AttachController.java
 * Description : 첨부파일 컨트롤러 클래스
 * Modification Information
 * Generated : Source Builder
 */

@RestController
public class AttachController extends BaseFormController {

  private final Logger log = LoggerFactory.getLogger(AttachController.class);

  @Inject
  private AttachService attachService;

  @Inject
  private FileUploadProperties fileUploadProperties;

  /**
   * GET /attach/view/{attachIdx} : 이미지 보기
   *
   * @param attachIdx 첨부파일 번호
   * @param request
   * @param response
   * @throws Exception
   */
  @GetMapping("/attach/view/{attachIdx}")
  public void view( @PathVariable("attachIdx") Long attachIdx, HttpServletRequest request, HttpServletResponse response ) throws Exception {
    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

    if (!WebUtil.needFreshResponse(request, dateFormat)) {
      response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
      return;
    }

    Attach attach = attachService.getAttach(attachIdx);

    if(attach == null) {
      return;
    }

    File f = new File(fileUploadProperties.getSaveDir() + attach.getSavedDir() + attach.getSavedName());

    if(!f.exists()) {
      return;
    }

    FileInputStream fin = null;
    FileChannel inputChannel = null;
    WritableByteChannel outputChannel = null;

    WebUtil.setCacheHeader(response, dateFormat);

    response.setContentType(attach.getFileType());
    response.setContentLength( (int)f.length() );

    WebUtil.writeFile(response, f, fin, inputChannel, outputChannel);
  }

  /**
   * POST /attach/insert/{refType}/{mapCode} : 파일 업로드
   * @param refType   참조타입코드 - 예) 상품 = product
   * @param mapCode   세부타입코드 - 예) 섬네일 이미지 : thumbnail
   * @param file
   * @return
   * @throws Exception
   */
  @PostMapping("/attach/insert/{refType}/{mapCode}")
  public ResponseEntity<Map<String, Object>> insertAttach( HttpServletRequest request,
                                                           @PathVariable("refType") String refType,
                                                           @PathVariable("mapCode") String mapCode,
                                                           @RequestParam(value = "file") MultipartFile file ) throws Exception {

    AttachFilterChain filterChain = new AttachFilterChain();
    Attach attach = attachService.uploadFile(request, file, ReferenceTypeRegistry.get(refType.toUpperCase()), mapCode, filterChain);

    boolean isSuccess = StringUtils.isNotEmpty(attach.getIdx().toString());

    Map<String, Object> resultMap = new HashMap<>();
    if ( isSuccess ) {
      resultMap.put("idx", attach.getIdx());
      resultMap.put("name", attach.getDisplayName());
    }

    return ResponseEntity.ok(resultMap);
  }
}
