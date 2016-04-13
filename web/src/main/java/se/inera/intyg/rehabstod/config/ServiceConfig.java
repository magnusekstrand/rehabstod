/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.rehabstod.config;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.support.ServletContextAttributeExporter;
import se.inera.intyg.rehabstod.service.monitoring.HealthCheckService;
import se.inera.intyg.rehabstod.service.monitoring.PingForConfigurationResponderImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pebe on 2015-09-07.
 */
@Configuration
@ComponentScan("se.inera.intyg.rehabstod.service, se.inera.intyg.rehabstod.auth, se.inera.intyg.rehabstod.common.service")
@EnableScheduling
public class ServiceConfig {

    @Autowired
    HealthCheckService healtCheckService;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ServletContextAttributeExporter contextAttributes() {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put("healthcheck", healtCheckService);
        final ServletContextAttributeExporter exporter = new ServletContextAttributeExporter();
        exporter.setAttributes(attributes);
        return exporter;
    }

    @Bean
    public PingForConfigurationResponderImpl pingForConfigurationResponder() {
        return new PingForConfigurationResponderImpl();
    }

    @Bean
    public EndpointImpl pingForConfigurationEndpoint() {
        Bus bus = (Bus) applicationContext.getBean(Bus.DEFAULT_BUS_ID);
        Object implementor = pingForConfigurationResponder();
        EndpointImpl endpoint = new EndpointImpl(bus, implementor);
        endpoint.publish("/ping-for-configuration");
        return endpoint;
    }

}
