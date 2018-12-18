package com.deliverit.v1.config;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.springmvc.OpenApiValidationInterceptor;
import com.atlassian.oai.validator.springmvc.ResettableRequestServletWrapper;
import com.atlassian.oai.validator.springmvc.SpringMVCLevelResolverFactory;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Configuration
public class OpenApiValidationConfig extends WebMvcConfigurerAdapter {

  private final OpenApiValidationInterceptor validationInterceptor;

  /**
   * @param swaggerSchema the {@link Resource} to your OpenAPI / Swagger schema
   */
  @Autowired
  public OpenApiValidationConfig(@Value("classpath:swagger.yaml") final Resource swaggerSchema) throws IOException {
    final OpenApiInteractionValidator openApiInteractionValidator = OpenApiInteractionValidator
        .createFor(swaggerSchema.getURL().getPath())
        .withLevelResolver(SpringMVCLevelResolverFactory.create())
        // the context path of the Spring application differs from the base path in the OpenAPI schema
        .withBasePathOverride("/v1")
        .build();
    validationInterceptor = new OpenApiValidationInterceptor(openApiInteractionValidator);
  }

  @Override
  public void addInterceptors(final InterceptorRegistry registry) {
    registry.addInterceptor(new RequestLoggingInterceptor());
    registry.addInterceptor(validationInterceptor);
  }

  private static class RequestLoggingInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public boolean preHandle(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse,
        final Object handler) throws Exception {
      final String requestLog = String.join(System.lineSeparator(),
          "uri=" + servletRequest.getRequestURI() + "?" + servletRequest.getQueryString(),
          "client=" + servletRequest.getRemoteAddr(),
          "payload=" + getPayload(servletRequest)
      );
      System.out.println("Incoming request: {}" + requestLog);
      LOG.info("Incoming request: {}", requestLog);

      HttpServletRequest requestCacheWrapperObject
          = new ContentCachingRequestWrapper(servletRequest);
      requestCacheWrapperObject.getParameterMap();
      return true;
    }

    private String getPayload(final HttpServletRequest request) throws IOException {
      System.out.println(request);
      if (request instanceof ResettableRequestServletWrapper) {
        final ResettableRequestServletWrapper resettableRequest = (ResettableRequestServletWrapper) request;
        final String requestBody = IOUtils.toString(resettableRequest.getReader());
        // reset the input stream - so it can be read again by the next interceptor / filter
        resettableRequest.resetInputStream();
        return requestBody.substring(0, Math.min(1024, requestBody.length())); // only log the first 1kB
      } else {
        return "[unknown]";
      }
    }
  }
}