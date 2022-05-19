package kr.co.demo.core.repository;

import kr.co.demo.core.domain.Board;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Class Name : BoardRepository.java
 * Description : 게시판 레포지토리 클래스
 * Modification Information

 * Generated : Source Builder
 */

@Repository
public interface BoardRepository {

    /**
    * 게시판 카운트 조회
    * @param board
    * @return int
    */
    int selectBoardCount(Board board);

    /**
    * 게시판 목록 조회
    * @param board
    * @return 조건에 해당하는 게시판 리스트
    */
    List<Board> selectBoardList(Board board);

    /**
     * 게시판 상세 조회
     * @param board
     * @return 조건에 해당하는 게시판 리스트
     */
    Board selectBoard(Board board);

    /**
     * 게시판 등록
     * @param board
     * @return int
     */
    int insertBoard(Board board) throws Exception;

    /**
     * 게시판 등록
     * @param board
     * @return int
     */
    int updateBoard(Board board) throws Exception;

    /**
     * 게시판 삭제
     * @param board
     * @return int
     */
    int deleteBoard(Board board) throws Exception;

}
