package kr.co.demo.core.domain;

import kr.co.demo.common.Base;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;


@Alias("board")
@Data
@EqualsAndHashCode(callSuper=false)
public class Board extends Base implements Serializable {

  /** 인덱스 **/
  private Long idx;

  /** 게시판 분류 **/
  private String divisionCode;

  /** 게시판 제목 **/
  private String title;

  /** 게시판 공지 여부 **/
  private String topYn;

  /** 게시판 내용 **/
  private String content;

  /** 게시판 링크 **/
  private Object url;

  /** 게시판 첨부파일 **/
  private Object attach;

  /** 게시판 조회수 **/
  private String views;

  /** 사용 여부 **/
  private String useYn;

  /** 삭제 여부 **/
  private String delYn;

  /** 게시판 생성자 **/
  private String createdBy;

  /** 게시판 생성일 **/
  private String createdDate;

  /** 게시판 수정자 **/
  private String modifiedBy;

  /** 게시판 수정일 **/
  private String modifiedDate;

  /** ip **/
  private String ip;

}
