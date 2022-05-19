package kr.co.demo.core.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import kr.co.demo.attach.domain.MapCode;
import kr.co.demo.attach.domain.ReferenceType;
import kr.co.demo.attach.filter.AttachFilterChain;
import kr.co.demo.attach.policy.DirectoryPathPolicy;
import kr.co.demo.config.CacheConfiguration;
import kr.co.demo.core.domain.Attach;
import kr.co.demo.core.repository.AttachRepository;
import kr.co.demo.security.JwtTokenUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Class Name : AttachService.java
 * Description : 첨부파일 서비스 클래스
 * Modification Information
 *
 * Generated : Source Builder
 */

@Service
public class AttachService {

  private final Logger log = LoggerFactory.getLogger(AttachService.class);

  public static final String FILE_SEP = "/";

  @Autowired
  @Qualifier("dateDirectoryPathPolicy")
  private DirectoryPathPolicy directoryPathPolicy;

  @Inject
  private AttachRepository attachRepository;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  private static Tika tika = new Tika();

  /**
   * 첨부파일 카운트 조회
   * @param param
   * @return
   */
  @Transactional(readOnly = true)
  public int listAttachCnt(Map<String, Object> param) {
    return attachRepository.listAttachCnt(param);
  }

  /**
   * 첨부파일 목록 조회
   * @param param
   * @return
   */
  @Transactional(readOnly = true)
  public List<Attach> listAttach(Map<String, Object> param) {
    return attachRepository.listAttach(param);
  }

  /**
   * param에 포함되지 않는 첨부파일 idx 목록 조회
   * @param param
   * @return
   */
  @Transactional(readOnly = true)
  public List<Attach> listAttachNotInIndex(Map<String, Object> param) {
    return attachRepository.listAttachNotInIndex(param);
  }

  /**
   * 첨부파일 조회
   * @param idx
   * @return Attach
   */
  @Transactional(readOnly = true)
  public Attach getAttach(Long idx) {
    return attachRepository.getAttach(Maps.newHashMap(ImmutableMap.<String, Object>builder().put("idx", idx).build()));
  }

  /**
   * 첨부파일 refKey 수정
   * @param attach
   * @return int
   */
  @Transactional
  public int updateAttachRefKey(Attach attach) throws IOException {
    return attachRepository.updateAttachRefKey(attach);
  }

  /**
   * 첨부파일 업로드
   * @param request, file, refType, mapCode, filterChain
   * @return Attach
   */
  @Transactional
  public Attach uploadFile(HttpServletRequest request, MultipartFile file, ReferenceType refType, String mapCode, AttachFilterChain filterChain) throws Exception {

    // 실제 첨부파일이 저장되는 경로 (기본경로/기능별경로/날짜/)
    String saveDir = directoryPathPolicy.getSaveDir() + FILE_SEP + refType.getTypeName() + FILE_SEP + directoryPathPolicy.getSubDir();
    createDirectoryIfNotExists(saveDir);

    if ( !file.isEmpty() ) {
      try {
        String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
        String fileName = (StringUtils.isNotBlank(mapCode) ? mapCode + "_" : "")
                        + UUID.randomUUID().toString()
                        + "."
                        + extension;
        String uniqueName = getUniqueFileName(saveDir, fileName);
        Path path = Paths.get(saveDir, uniqueName);

        try {
          if ( filterChain == null || filterChain.size() == 0 ) {
            MapCode map = refType.getMapCode(mapCode);
            if ( map == null ) {
                throw new Exception("Unsupported mapCode : " + mapCode);
            }
            refType.getMapCode(mapCode).startFilter(path.toFile());

          } else {
            filterChain.startFilter(path.toFile());
          }
          Files.copy(file.getInputStream(), path);

        } catch(Exception e) {
          log.error(e.getMessage(), e);
          throw new RuntimeException(e.getMessage());
        }

        Attach attach = new Attach();
        attach.setRefType(refType.getTypeName());
        attach.setRefKey("0");
        attach.setRefMapCode(mapCode);
        attach.setOrder(1);
        attach.setDisplayName(file.getOriginalFilename());
        attach.setSavedName(uniqueName);
        attach.setSavedDir(StringUtils.removeStart(saveDir, directoryPathPolicy.getSaveDir()));
        attach.setFileType(file.getContentType());
        attach.setFileSize(file.getSize());
        attach.setCreatedBy(jwtTokenUtil.getCurrentUser(request));
        attachRepository.insertAttach(attach);

        return attach;

      } catch (Exception e) {
        throw new Exception("Failed to upload " + file.getOriginalFilename() + " => " + e.getMessage());
      }

    } else {
      throw new FileNotFoundException("Failed to upload " + file.getOriginalFilename() + " because it was empty");
    }
  }

  @Transactional
  @CacheEvict(CacheConfiguration.ATTACH_IMAGE_VIEW)
  public void clearCache(String refType, String refKey, String mapCode) {
    log.debug("캐시 초기화. refType: " + refType + ", refKey: " + refKey + ", mapCode: " + mapCode);
  }

  /**
   * 첨부파일 파일, DB 모두 삭제
   * @param attaches
   */
  public void deleteAttachPermanently(List<Attach> attaches) {
    for ( Attach attach : attaches ) {
      Attach savedAttach = attachRepository.getAttach(Maps.newHashMap(ImmutableMap.<String, Object>builder().put("idx", attach.getIdx()).build()));
      if ( savedAttach != null && StringUtils.isNoneBlank(savedAttach.getSavedDir()) && StringUtils.isNoneBlank(savedAttach.getSavedName()) ) {
        File t = new File(directoryPathPolicy.getSaveDir() + FILE_SEP + savedAttach.getSavedDir() + savedAttach.getSavedName());
        if(t.exists()) {
          t.delete();
        }
        attachRepository.deleteAttach(attach.getIdx());
      } else {
        throw new InvalidPathException("", "No path specified");
      }
    }
  }

  private void createDirectoryIfNotExists(String saveDir) throws IOException {
    Path path = Paths.get(saveDir);
    //if directory exists?
    if ( !Files.exists(path) ) {
      Files.createDirectories(path);
    }
  }

  private String getUniqueFileName(String path, String fileName) {
    File tmpFile = new File(path + fileName);
    File parentDir = tmpFile.getParentFile();
    int count = 1;
    String extension = FilenameUtils.getExtension(tmpFile.getName());
    String baseName = FilenameUtils.getBaseName(tmpFile.getName());
    String uniqueName = baseName + "_" + count++ + "_." + extension;
    while ( tmpFile.exists() ) {
      tmpFile = new File(parentDir, uniqueName);
    }
    return uniqueName;
  }

}
