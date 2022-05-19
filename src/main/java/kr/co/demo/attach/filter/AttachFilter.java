package kr.co.demo.attach.filter;

import java.io.File;
import java.util.Map;

/**
 * Class Name : AttachFilter.java
 * Description : 첨부파일
 * Writer : lee.j
 */


public interface AttachFilter {
    void config(Map<String, Object> context);

    void doFilter(AttachFilterChain chain, File file) throws Exception;
}
