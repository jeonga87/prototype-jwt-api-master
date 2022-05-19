package kr.co.demo.config;

import kr.co.demo.attach.policy.DirectoryPathPolicy;
import kr.co.demo.attach.policy.impl.DateDirectoryPathPolicy;
import kr.co.demo.config.properties.FileUploadProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

/**
 * Class Name : FileUploadConfiguration.java
 * Description : 첨부파일 업로드 config
 * Writer : lee.j
 */

@Configuration
public class FileUploadConfiguration {

  @Inject
  private FileUploadProperties fileUploadProperties;

  @Bean
  public DirectoryPathPolicy dateDirectoryPathPolicy() {
    DateDirectoryPathPolicy policy = new DateDirectoryPathPolicy(
        DateDirectoryPathPolicy.DATE_SUBDIR_TYPE.YYYY_MM,
        fileUploadProperties.getSaveDir(),
        fileUploadProperties.getTempDir()
    );
    return policy;
  }
}
