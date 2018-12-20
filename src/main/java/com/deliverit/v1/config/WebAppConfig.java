package com.deliverit.v1.config;

import com.atlassian.oai.validator.springmvc.OpenApiValidationFilter;
import javax.servlet.Filter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Web App Config.
 */
public class WebAppConfig extends AbstractAnnotationConfigDispatcherServletInitializer {

  @Override
  protected Class<?>[] getRootConfigClasses() {
    return new Class[]{Application.class};
  }

  @Override
  protected Class<?>[] getServletConfigClasses() {
    return null;
  }

  @Override
  protected String[] getServletMappings() {
    return new String[]{"/"};
  }

  @Override
  protected Filter[] getServletFilters() {
    return new Filter[] { new OpenApiValidationFilter(
        true, // enable request validation
        true  // enable response validation
    ) };
  }
}
