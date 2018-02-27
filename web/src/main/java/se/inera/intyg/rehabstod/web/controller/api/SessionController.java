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
package se.inera.intyg.rehabstod.web.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/session")
@EnableScheduling
public class SessionController {

    private static final Logger LOG = LoggerFactory.getLogger(SessionController.class);
    private static final long DEFAULT_TIMOUT_SECONDS = 15L;

    private ConcurrentHashMap<String, LocalDateTime> disconnectRequestRegistry = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(value = "/disconnect/init", produces = MediaType.TEXT_PLAIN)
    public ResponseEntity<String> initDisconnect() {
        String jessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        if (!disconnectRequestRegistry.containsKey(jessionId)) {
            disconnectRequestRegistry.put(jessionId, LocalDateTime.now());
            return new ResponseEntity<>("Init added", HttpStatus.OK);
        } else {
            disconnectRequestRegistry.replace(jessionId, LocalDateTime.now());
            return new ResponseEntity<>("Init refreshed", HttpStatus.OK);
        }

    }

    @RequestMapping(value = "/disconnect/cancel", produces = MediaType.TEXT_PLAIN)
    public ResponseEntity<String> cancelDisconnect() {
        String jessionId = RequestContextHolder.currentRequestAttributes().getSessionId();

        if (disconnectRequestRegistry.containsKey(jessionId)) {
            disconnectRequestRegistry.remove(jessionId);
            return new ResponseEntity<>("Cancel OK", HttpStatus.OK);
        }
        return new ResponseEntity<>("Nothing to cancel", HttpStatus.OK);
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void janitor() {
        int count = 0;
        Iterator<Map.Entry<String, LocalDateTime>> i = disconnectRequestRegistry.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, LocalDateTime> entry = i.next();
            if (entry.getValue().isBefore(LocalDateTime.now().minusSeconds(DEFAULT_TIMOUT_SECONDS))) {
                SessionCleaner bean = applicationContext.getBean("sessionCleaner", SessionCleaner.class);
                bean.setSessionId(entry.getKey());
                bean.schedule();
                i.remove();
                count++;
            }
        }

        if (count > 0) {
            LOG.info("Successfully force-expired {} sessions.", count);
        }
    }
}
