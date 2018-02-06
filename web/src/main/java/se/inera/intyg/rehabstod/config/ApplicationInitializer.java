/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.config;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.DispatcherServlet;
import se.inera.intyg.infra.integration.pu.cache.PuCacheConfiguration;
import se.inera.intyg.rehabstod.integration.it.config.IntygstjanstIntegrationClientConfiguration;
import se.inera.intyg.rehabstod.integration.it.config.IntygstjanstIntegrationConfiguration;
import se.inera.intyg.rehabstod.integration.it.stub.IntygstjanstIntegrationStubConfiguration;
import se.inera.intyg.rehabstod.integration.srs.config.SRSIntegrationClientConfiguration;
import se.inera.intyg.rehabstod.integration.srs.config.SRSIntegrationConfiguration;
import se.inera.intyg.rehabstod.integration.srs.stub.SRSIntegrationStubConfiguration;
import se.inera.intyg.rehabstod.persistence.config.PersistenceConfigDev;
import se.inera.intyg.rehabstod.persistence.config.PersistenceConfigJndi;
import se.inera.intyg.rehabstod.web.filters.SessionTimeoutFilter;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import static se.inera.intyg.rehabstod.web.controller.api.SessionStatusController.SESSION_STATUS_CHECK_URI;
import static se.inera.intyg.rehabstod.web.controller.api.SessionStatusController.SESSION_STATUS_EXTEND;
import static se.inera.intyg.rehabstod.web.controller.api.SessionStatusController.SESSION_STATUS_REQUEST_MAPPING;

public class ApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(javax.servlet.ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();

        appContext.register(ApplicationConfig.class, HsaConfiguration.class,
                PuConfiguration.class, PuCacheConfiguration.class,
                ServiceConfig.class, IntygstjanstIntegrationConfiguration.class,
                IntygstjanstIntegrationClientConfiguration.class,
                IntygstjanstIntegrationStubConfiguration.class,
                SRSIntegrationConfiguration.class,
                SRSIntegrationClientConfiguration.class,
                SRSIntegrationStubConfiguration.class,
                JmsConfig.class, NTjPPingConfig.class, SecurityConfig.class,
                SjukfallConfig.class, EmployeeNameCacheConfig.class, DynamicLinkConfig.class, PersistenceConfigJndi.class,
                PersistenceConfigDev.class);

        servletContext.addListener(new ContextLoaderListener(appContext));

        AnnotationConfigWebApplicationContext webConfig = new AnnotationConfigWebApplicationContext();
        webConfig.register(WebConfig.class);
        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(webConfig));
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");

        // Session Timeout filter
        FilterRegistration.Dynamic sessionTimeoutFilter = servletContext.addFilter("sessionTimeoutFilter", SessionTimeoutFilter.class);
        sessionTimeoutFilter.addMappingForUrlPatterns(null, false, "/*");
        sessionTimeoutFilter.setInitParameter("getSessionStatusUri", SESSION_STATUS_CHECK_URI);

        // Spring security filter
        FilterRegistration.Dynamic springSecurityFilterChain = servletContext.addFilter("springSecurityFilterChain",
                DelegatingFilterProxy.class);
        springSecurityFilterChain.addMappingForUrlPatterns(null, false, "/*");

        // unitSelectedAssurance filter
        FilterRegistration.Dynamic unitSelectedAssuranceFilter = servletContext.addFilter("unitSelectedAssuranceFilter",
                DelegatingFilterProxy.class);
        unitSelectedAssuranceFilter.setInitParameter("targetFilterLifecycle", "true");
        unitSelectedAssuranceFilter.addMappingForUrlPatterns(null, false, "/api/*");
        unitSelectedAssuranceFilter.setInitParameter("ignoredUrls", "/api/config,/api/user,/api/user/andraenhet");

        // pdlConsentGiven filter
        FilterRegistration.Dynamic pdlConsentGivenAssuranceFilter = servletContext.addFilter("pdlConsentGivenAssuranceFilter",
                DelegatingFilterProxy.class);
        pdlConsentGivenAssuranceFilter.setInitParameter("targetFilterLifecycle", "true");
        pdlConsentGivenAssuranceFilter.addMappingForUrlPatterns(null, false, "/api/*");
        pdlConsentGivenAssuranceFilter.setInitParameter("ignoredUrls",
                SESSION_STATUS_CHECK_URI + "," + SESSION_STATUS_REQUEST_MAPPING + SESSION_STATUS_EXTEND
                        + ",/api/config,/api/user,/api/user/giveconsent,/api/sjukfall/summary,/api/stub");

        FilterRegistration.Dynamic characterEncodingFilter = servletContext.addFilter("characterEncodingFilter",
                CharacterEncodingFilter.class);
        characterEncodingFilter.addMappingForUrlPatterns(null, false, "/*");
        characterEncodingFilter.setInitParameter("encoding", "UTF-8");
        characterEncodingFilter.setInitParameter("forceEncoding", "true");

        // NOTE: The characterEncoding filter must run before the hiddenHttpMethodFilter, otherwise the setEncoding will
        // be done to late, as the hiddenHttpMethodFilter internally uses request.getParameter which will parse the
        // parameters using a default encoding which may not be UTF-8 in e.g in tomcat it's ISO-8859-1.
        FilterRegistration.Dynamic hiddenHttpMethodFilter = servletContext.addFilter("hiddenHttpMethodFilter",
                HiddenHttpMethodFilter.class);
        hiddenHttpMethodFilter.addMappingForUrlPatterns(null, false, "/*");

        // CXF services filter
        ServletRegistration.Dynamic cxfServlet = servletContext.addServlet("services", new CXFServlet());
        cxfServlet.setLoadOnStartup(1);
        cxfServlet.addMapping("/services/*");

        // Listeners for session audit logging
        servletContext.addListener(new HttpSessionEventPublisher());
        servletContext.addListener(new RequestContextListener());
    }

}
