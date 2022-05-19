package kr.co.demo.config.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Class Name : FileUploadProperties.java
 * Description : 첨부파일 업로드
 * Writer : lee.j
 */

@Component
@Data
@EqualsAndHashCode(callSuper=false)
@ConfigurationProperties(prefix = "demo.file-upload")
public class FileUploadProperties {

    private String tempDir;
    private String saveDir;

    private Map<String, String> filterMap;
}
