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

/*global
browser
*/
'use strict';

exports.config = {
    // baseUrl: process.env.WEBCERT_URL,
    allScriptsTimeout: 30000,
    // seleniumAddress: 'http://127.0.0.1:4444/wd/hub',
    framework: 'custom',

    // path relative to the current config file
    frameworkPath: require.resolve('protractor-cucumber-framework'),
    specs: [
        'features/*.feature'
    ],
    capabilities: {
        browserName: 'firefox',
        version: '',
        platform: 'ANY'
    },
    lmaxSessions: 1,
    cucumberOpts: {
        format: ['json:./acceptance/report/acc_results.json', 'pretty'],
        require: ['features/steps/**/*.js', 'features/support/**/*.js']
    },
    onPrepare: function() {

        //http://chaijs.com/
        global.chai = require('chai');

        //https://github.com/domenic/chai-as-promised/
        global.chaiAsPromised = require('chai-as-promised');
        global.chai.use(global.chaiAsPromised);

        global.expect = global.chai.expect;
        global.should = global.chai.should();

        var rehTestTools = require('rehabstod-testtools');

        global.rehTestTools = rehTestTools;
        global.testdata = rehTestTools.testdata;
        global.pages = rehTestTools.pages;

        browser.ignoreSynchronization = false;
        browser.baseUrl = process.env.WEBCERT_URL;

        //Set window size
        browser.manage().window().setSize(1600, 1000);
        global.user = {};
        //Strunta i om servern inte kan bekräfta dess identitet
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = '0';
    }
};