package com.vivek.odatav4_sample;

import java.io.File;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;

import org.apache.catalina.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Compression;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.Http2;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.SslStoreProvider;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.server.Jsp;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

import com.sap.cloud.sdk.service.prov.v4.rt.core.web.ODataApplicationInitializer;
import com.sap.cloud.sdk.service.prov.v4.rt.core.web.ODataServlet;

@Configuration
@PropertySource(value = "classpath:application.properties")
public class ZAppConfig {

	/*
	 * @Bean ServletWebServerFactory servletWebServerFactory() {
	 * 
	 * TomcatServletWebServerFactory tcf = new TomcatServletWebServerFactory();
	 * tcf.setContextPath(""); return tcf; }
	 */

	/*@Bean
	public ServletContextInitializer initializer() {
		return new ServletContextInitializer() {

			@Override
			public void onStartup(ServletContext servletContext) throws ServletException {
				servletContext.setInitParameter("package", "com.vivek.odatav4_sample");
			}
		};
	}*/

	@Bean
	public ODataServlet getODataServlet() {
		return new ODataServlet();
	}

	@Bean
	public ServletRegistrationBean<ODataServlet> ODataServletRegistration() {
		ServletRegistrationBean<ODataServlet> registration = new ServletRegistrationBean<ODataServlet>(
				getODataServlet(), "/odata/v4/*");
		registration.setLoadOnStartup(1);
		return registration;
	}

}
