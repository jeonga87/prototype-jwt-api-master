package kr.co.demo.attach.policy;

/**
 * Class Name : DirectoryPathPolicy.java
 * Description : 첨부파일
 * Writer : lee.j
 */

public interface DirectoryPathPolicy {
    String getTempDir();
    String getSaveDir();
    String getSubDir();
}
