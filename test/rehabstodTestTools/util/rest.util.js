/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

/**
 * Created by BESA on 2015-11-17.
 */
/*globals*/
'use strict';
var restClient = require('./restclient.util.js');
var env = require('./../environment.js').envConfig;

module.exports = {
    login: function(userJson) {

        // login with doctor Jan Nilsson if none else is specified
        var user = userJson || {
            'fornamn': 'Emma',
            'efternamn': 'Nilsson',
            'hsaId': 'TSTNMT2321000156-105R',
            'enhetId': 'TSTNMT2321000156-105N',
            'lakare': true,
            'forskrivarKod': '2481632'
        }; 

        var options = {
            url: 'fake',
            method: 'POST',
            body: 'userJsonDisplay=' + JSON.stringify(user)
        };
        return restClient.run(options, 'urlenc');
    },
    getUser: function(id) {
        var options = {
            url: 'api/user',
            method: 'GET'
        };
        return restClient.run(options, 'json', env.REHABSTOD_URL);
    }
};
