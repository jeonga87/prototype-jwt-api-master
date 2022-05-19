package kr.co.demo.controller;

import kr.co.demo.core.service.BoardService;
import kr.co.demo.util.ApiResponseMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Class Name : ExampleController.java
 * Description : 예제 컨트롤러 클래스
 * Modification Information
 */

@RestController
public class ExampleController extends BaseFormController {

  private static final Logger log = LoggerFactory.getLogger(ExampleController.class);

  @Autowired
  private BoardService boardService;

  /**
  * 게시판 리스트
  * @param
  * @return
  */
  @GetMapping("/example/{divisionCode}/list")
  public ResponseEntity<Map<String, Object>> list( @PathVariable String divisionCode,
                                                   @RequestParam (value = "fromNo", required = false, defaultValue = "1") int fromNo,
                                                   @RequestParam (value = "dataPerPage", required = false, defaultValue = "10") int dataPerPage,
                                                   @RequestParam (value = "searchType", required = false, defaultValue = "") String searchType,
                                                   @RequestParam (value = "searchValue", required = false, defaultValue = "") String searchValue ) throws Exception {
    return ResponseEntity.ok(boardService.getBoardList(divisionCode, fromNo, dataPerPage, searchType, searchValue));
  }

  /**
   * 게시판 상세
   * @param
   * @return
   */
  @GetMapping("/example/{divisionCode}/detail/{idx}")
  public ResponseEntity<Map<String, Object>> detail( @PathVariable String divisionCode,
                                                     @PathVariable int idx ) throws Exception {

    if ( idx == 0 || StringUtils.isAnyBlank(idx + "") ) {
      return new ResponseEntity(new ApiResponseMessage("605", "bad request"), HttpStatus.BAD_REQUEST);
    }

    Map<String, Object> result = boardService.getBoard(divisionCode, Long.valueOf(idx));
    if ( result.size() == 0 ) {
      return new ResponseEntity(new ApiResponseMessage("608", "return fail"), HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.ok(result);
  }

  /**
   * 게시판 입력 프로세스
   * @param
   * @return
   */
  @PostMapping("/example/{divisionCode}/insert")
  public ResponseEntity<Map<String, Object>> insertList( HttpServletRequest request,
                                                         @PathVariable String divisionCode,
                                                         @RequestBody Map<String, Object> map ) throws Exception {
    Map<String, Object> result = boardService.insertBoard(request, divisionCode, map);
    if ( result.size() == 0 ) {
      return new ResponseEntity(new ApiResponseMessage("608", "return fail"), HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.ok(result);
  }

  /**
   * 게시판 수정 프로세스
   * @param
   * @return
   */
  @PutMapping("/example/{divisionCode}/modify/{idx}")
  public ResponseEntity<Map<String, Object>> updateList( HttpServletRequest request,
                                                         @PathVariable String divisionCode,
                                                         @PathVariable int idx,
                                                         @RequestBody Map<String, Object> map ) throws Exception {
    if ( idx == 0 || StringUtils.isAnyBlank(idx + "") ) {
      return new ResponseEntity(new ApiResponseMessage("605", "bad request"), HttpStatus.BAD_REQUEST);
    }

    Map<String, Object> result = boardService.updateBoard(request, divisionCode, Long.valueOf(idx), map);
    if ( result.size() == 0 ) {
      return new ResponseEntity(new ApiResponseMessage("608", "return fail"), HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.ok(result);
  }

  /**
   * 게시판 삭제 프로세스
   * @param
   * @return
   */
  @DeleteMapping("/example/{divisionCode}/delete/{idx}")
  public ResponseEntity<Map<String, Object>> deleteList( HttpServletRequest request,
                                                         @PathVariable String divisionCode,
                                                         @PathVariable int idx ) throws Exception {
    if ( idx == 0 || StringUtils.isAnyBlank(idx + "") ) {
      return new ResponseEntity(new ApiResponseMessage("605", "bad request"), HttpStatus.BAD_REQUEST);
    }

    Map<String, Object> result = boardService.deleteBoard(request, divisionCode, Long.valueOf(idx));
    if ( result.size() == 0 ) {
      return new ResponseEntity(new ApiResponseMessage("608", "return fail"), HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.ok(result);
  }
}
