package kr.co.demo.config;

import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class Name : WebConfigurer.java
 * Description : web config
 * Writer : lee.j
 */

@Configuration
public class WebConfigurer implements ServletContextInitializer, WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException { }

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
        mappings.add("html", "text/html;charset=utf-8"); // IE issue
        mappings.add("json", "text/html;charset=utf-8"); // CloudFoundry issue
        factory.setMimeMappings(mappings);
    }

    /**
     * spring boot - web.xml 대신 아래와 같이 변경됨
     * filter 설정
     * */
    @Bean
    public FilterRegistrationBean characterEncodingFilter() {
        FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
        filterRegBean.setFilter(new CharacterEncodingFilter());
        List<String> urlPatterns = new ArrayList<>();
        urlPatterns.add("/*");
        filterRegBean.setUrlPatterns(urlPatterns);
        filterRegBean.setOrder(-200);
        filterRegBean.setAsyncSupported(true);
        filterRegBean.addInitParameter("encoding", "UTF-8");
        return filterRegBean;
    }

    @Bean
    public ErrorPageFilter errorPageFilter() {
    	return new ErrorPageFilter();
    }

    @Bean
    public FilterRegistrationBean disableSptringBootErrorFilter(ErrorPageFilter filter){
    	FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
    	filterRegistrationBean.setFilter(filter);
    	filterRegistrationBean.setEnabled(false);
    	return filterRegistrationBean;
    }
}
