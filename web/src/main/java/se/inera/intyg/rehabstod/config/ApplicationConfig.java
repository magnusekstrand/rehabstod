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

import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.apache.cxf.Bus;
import org.apache.cxf.feature.LoggingFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import se.inera.intyg.infra.monitoring.MonitoringConfiguration;
import se.inera.intyg.infra.security.filter.PrincipalUpdatedFilter;
import se.inera.intyg.rehabstod.service.diagnos.DiagnosFactory;
import se.inera.intyg.rehabstod.web.filters.PdlConsentGivenAssuranceFilter;
import se.inera.intyg.rehabstod.web.filters.UnitSelectedAssuranceFilter;

@Configuration
@EnableTransactionManagement
@PropertySource({ "classpath:default.properties",
        "file:${config.file}",
        "file:${credentials.file}",
        "classpath:version.properties" })
@ImportResource({ "classpath:META-INF/cxf/cxf.xml", "classpath:securityContext.xml" })
@Import(MonitoringConfiguration.class)
public class ApplicationConfig implements TransactionManagementConfigurer {

    @Autowired
    private Bus bus;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @PostConstruct
    public Bus init() {
        bus.setFeatures(new ArrayList<>(Arrays.asList(loggingFeature())));
        return bus;
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("version");
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }

    @Bean
    public UnitSelectedAssuranceFilter unitSelectedAssuranceFilter() {
        return new UnitSelectedAssuranceFilter();
    }

    @Bean
    public PdlConsentGivenAssuranceFilter pdlConsentGivenAssuranceFilter() {
        return new PdlConsentGivenAssuranceFilter();
    }

    @Bean
    public PrincipalUpdatedFilter principalUpdatedFilter() {
        return new PrincipalUpdatedFilter();
    }

    @Bean(name = "jacksonJsonProvider")
    public JacksonJaxbJsonProvider jacksonJsonProvider() {

        return new JacksonJaxbJsonProvider();
    }

    @Bean
    public DiagnosFactory diagnosFactory() {
        return new DiagnosFactory();
    }

    @Bean
    public LoggingFeature loggingFeature() {
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);
        return loggingFeature;
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager;
    }
}
