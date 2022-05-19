package kr.co.demo.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.demo.core.domain.Attach;
import kr.co.demo.core.domain.Board;
import kr.co.demo.core.repository.BoardRepository;
import kr.co.demo.security.JwtTokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class Name : BoardService.java
 * Description : 게시판 서비스 클래스
 * Modification Information
 *
 * Generated : Source Builder
 */

@Service
public class BoardService {

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private AttachService attachService;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  /**
   * 게시판 카운트 조회
   * @return 조건에 해당하는 게시판 카운트
   */
  @Transactional(readOnly = true)
  public int getBoardCount(Board board) {
    return boardRepository.selectBoardCount(board);
  }

  /**
   * 게시판 리스트 조회
   * @return 조건에 해당하는 게시판 리스트
   */
  @Transactional(readOnly = true)
  public Map<String, Object> getBoardList(String divisionCode, int fromNo, int dataPerPage, String searchType, String searchValue) throws Exception {
    Map<String, Object> result = new HashMap<>();
    List<Map<String, Object>> list = new ArrayList<>();

    Board board = new Board();
    board.setDivisionCode(divisionCode);
    int totalCount = getBoardCount(board);

    if ( totalCount != 0 ) {
      board.setOrderKey("BOARD_CREATED_DATE");
      board.setOrderValue("DESC");
      board.setFromNo(fromNo);
      board.setDataPerPage(dataPerPage);

      Map<String, Object> search = new HashMap<>();
      search.put("search", searchType);
      board.setSearchInfo(search);
      board.setSearchValue(searchValue);

      List<Board> boardList = boardRepository.selectBoardList(board);

      Map<String, Object> item;
      JSONParser parser = new JSONParser();
      for ( Board bItem : boardList ) {
        item = new HashMap<>();
        item.put("idx", bItem.getIdx());
        item.put("title", !StringUtils.isAnyBlank(bItem.getTitle()) ? bItem.getTitle() : null);
        item.put("url", bItem.getUrl() != null ? parser.parse(bItem.getUrl().toString()) : null);
        item.put("attach", bItem.getAttach() != null ? parser.parse(bItem.getAttach().toString()) : null);
        item.put("createdDate", bItem.getCreatedDate());
        list.add(item);
      }
    }

    result.put("list", list);
    result.put("total", totalCount);
    return result;
  }

  /**
   * 게시판 상세 조회
   * @return 조건에 해당하는 게시판 리스트
   */
  @Transactional(readOnly = true)
  public Map<String, Object> getBoard(String divisionCode, Long idx) throws Exception {
    Board board = new Board();
    board.setDivisionCode(divisionCode);
    board.setIdx(idx);

    board = boardRepository.selectBoard(board);

    Map<String, Object> item = new HashMap<>();
    if ( board != null ) {
      JSONParser parser = new JSONParser();
      item.put("idx", board.getIdx() != 0 ? board.getIdx() : null);
      item.put("title", !StringUtils.isAnyBlank(board.getTitle()) ? board.getTitle() : null);
      item.put("url", board.getUrl() != null ? parser.parse(board.getUrl().toString()) : null);
      item.put("attach", board.getAttach() != null ? parser.parse(board.getAttach().toString()) : null);
      item.put("createdDate", !StringUtils.isAnyBlank(board.getCreatedDate()) ? board.getCreatedDate() : null);
    }

    return item;
  }

  /**
   * 게시판 등록
   * @return int
   */
  @Transactional
  public Map<String, Object> insertBoard(HttpServletRequest request, String divisionCode, Map<String, Object> map) throws Exception {
    String cspCode = jwtTokenUtil.getCurrentUser(request);

    ObjectMapper mapper = new ObjectMapper();
    Board board = new Board();
    board.setDivisionCode(divisionCode);
    board.setTitle(map.get("title") != null && !StringUtils.isAnyBlank(map.get("title").toString()) ? map.get("title").toString() : null);
    board.setUrl(map.get("url") != null ? mapper.writeValueAsString(map.get("url")) : null);
    board.setAttach(map.get("attach") != null ? mapper.writeValueAsString(map.get("attach")) : null);
    board.setCreatedBy(cspCode);
    boardRepository.insertBoard(board);

    //첨부파일 관련 작업
    Map<String, Object> param = new HashMap<>();
    List<Map<String, Object>> attachList = (List<Map<String, Object>>) map.get("attach");
    if ( attachList != null && attachList.size() != 0 ) {
      List<Long> idxList = new ArrayList<>();
      Attach at = new Attach();
      at.setRefKey(board.getIdx().toString());
      at.setCreatedBy(cspCode);

      for ( Map<String, Object> attach : attachList ) {
        Long attachIdx = Long.parseLong(attach.get("idx").toString());
        idxList.add(attachIdx);

        //attach refKey를 boardIdx로 update
        at.setIdx(attachIdx);
        attachService.updateAttachRefKey(at);
      }
      param.put("idxList", idxList);
    }
    //board에 해당 안되는 attach 삭제
    param.put("refType", "board");
    param.put("refKey", "0");
    param.put("refMapCode", "csImage");
    param.put("createdBy", cspCode);
    List<Attach> delList = attachService.listAttachNotInIndex(param);
    if ( delList != null ) {
      attachService.deleteAttachPermanently(delList);
    }

    return getBoard(divisionCode, board.getIdx());
  }

  /**
   * 게시판 수정
   * @return int
   */
  @Transactional
  public Map<String, Object> updateBoard(HttpServletRequest request, String divisionCode, Long idx, Map<String, Object> map) throws Exception {
    String cspCode = jwtTokenUtil.getCurrentUser(request);

    ObjectMapper mapper = new ObjectMapper();
    Board board = new Board();
    board.setIdx(idx);
    board.setDivisionCode(divisionCode);
    board.setTitle(map.get("title") != null && !StringUtils.isAnyBlank(map.get("title").toString()) ? map.get("title").toString() : null);
    board.setUrl(map.get("url") != null ? mapper.writeValueAsString(map.get("url")) : null);
    board.setAttach(map.get("attach") != null ? mapper.writeValueAsString(map.get("attach")) : null);
    board.setModifiedBy(cspCode);
    boardRepository.updateBoard(board);

    //첨부파일 관련 작업
    Map<String, Object> param = new HashMap<>();
    List<Map<String, Object>> attachList = (List<Map<String, Object>>) map.get("attach");
    if ( attachList != null && attachList.size() != 0 ) {
      List<Long> idxList = new ArrayList<>();
      Attach at = new Attach();
      at.setRefKey(board.getIdx().toString());
      at.setCreatedBy(cspCode);
      for ( Map<String, Object> attach : attachList ) {
        Long attachIdx = Long.parseLong(attach.get("idx").toString());
        idxList.add(attachIdx);

        //attach refKey를 boardIdx로 update
        at.setIdx(attachIdx);
        attachService.updateAttachRefKey(at);
      }
      param.put("idxList", idxList);
    }
    //board에 해당 안되는 attach 삭제
    param.put("refType", "board");
    param.put("refMapCode", "csImage");
    param.put("createdBy", cspCode);
    for ( int i = 0; i < 2; i++) {
      param.put("refKey", i == 0 ? "0" : idx);
      List<Attach> delList = attachService.listAttachNotInIndex(param);
      if ( delList != null ) {
        attachService.deleteAttachPermanently(delList);
      }
    }

    return getBoard(divisionCode, board.getIdx());
  }

  /**
   * 게시판 삭제
   * @return int
   */
  @Transactional
  public Map<String, Object> deleteBoard(HttpServletRequest request, String divisionCode, Long idx) throws Exception {
    Map<String, Object> result = getBoard(divisionCode, idx);

    //첨부파일 삭제
    Map<String, Object> param = new HashMap<>();
    param.put("refKey", idx);
    List<Attach> delList = attachService.listAttach(param);
    if ( delList != null ) {
      attachService.deleteAttachPermanently(delList);
    }

    //게시글 삭제
    Board board = new Board();
    board.setDivisionCode(divisionCode);
    board.setIdx(idx);
    board.setModifiedBy(jwtTokenUtil.getCurrentUser(request));
    boardRepository.deleteBoard(board);

    return result;
  }
}
