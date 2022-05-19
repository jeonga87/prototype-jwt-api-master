package kr.co.demo.core.repository;

import kr.co.demo.core.domain.Attach;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface AttachRepository {

    /**
     * 첨부파일 카운트 조회
     * @param param 조회 조건
     * @return int
     */
    int listAttachCnt(Map<String, Object> param);

    /**
     * 첨부파일 목록 조회
     * @param param 조회 조건
     * @return List<Attach>
     */
    List<Attach> listAttach(Map<String, Object> param);

    /**
     * param에 포함되지 않는 첨부파일 idx 목록 조회
     * @param param 조회 조건
     * @return List<Attach>
     */
    List<Attach> listAttachNotInIndex(Map<String, Object> param);

    /**
     * 첨부파일 조회
     * @param param 조회 조건
     * @return Attach
     */
    Attach getAttach(Map<String, Object> param);

    /**
     * 첨부파일 등록
     * @param attach
     * @return int
     */
    int insertAttach(Attach attach);

    /**
     * 첨부파일 수정
     * @param attach
     * @return int
     */
    int updateAttach(Attach attach);

    /**
     * 첨부파일 RefKey 수정
     * @param attach
     * @return int
     */
    int updateAttachRefKey(Attach attach);

    /**
     * 첨부파일 삭제 처리
     * @param idx
     * @return int
     */
    int deleteAttach(@Param("idx") Long idx);

}
