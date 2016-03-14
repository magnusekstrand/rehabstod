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

/* jshint maxlen: false, unused: false */
var rhsMessages = {
    'sv': {

        'common.logout': 'Logga ut',
        'common.yes': 'Ja',
        'common.no': 'Nej',
        'common.yes.caps': 'JA',
        'common.no.caps': 'NEJ',
        'common.ok': 'OK',
        'common.cancel': 'Avbryt',
        'common.goback': 'Tillbaka',
        'common.save': 'Spara',
        'common.change': 'Ändra',
        'common.print': 'Skriv ut',
        'common.close': 'Stäng',
        'common.date': 'Datum',
        'common.reset': 'Återställ',

        'common.label.loading': 'Laddar',


        // General form errors

        // Header

        // Start
        'label.start.header': 'Rehabstöd startsida',

        // Sjukfall Start
        // Sjukfall Start Läkare
        'label.sjukfall.start.lakare.header' : 'Pågående sjukfall på ',
        'label.sjukfall.start.lakare.selectionpanel.header' : 'Mina pågående sjukfall på ',
        'label.sjukfall.start.lakare.selectionpanel.body' : 'Som läkare kan du ta del av de sjukfall där du själv utfärdat det nuvarande intyget.<br><br>' +
                                                    'När du klickar på "Visa mina sjukfall" nedan kommer du att få se dina pågående sjukfall för den vårdenhet du har loggat in på. För varje sjukfall visas patientens personuppgifter, diagnos, sjukskrivningstid och sjukskrivningsgrad. ' +
                                                    'Om du har tillgång till flera vårdenheter kan du se dina pågående sjukfall för en annan vårdenhet genom att byta vårdenhet i sidhuvudet. ' +
                                                    '<br><br>Informationen som visas loggas enligt Patientdatalagen (PDL).',
        'label.sjukfall.start.lakare.selectionpanel.urval.button' : 'Visa mina sjukfall',

        // Sjukfall Start Rehab
        'label.sjukfall.start.rehab.header' : 'Pågående sjukfall på ',
        'label.sjukfall.start.rehab.selectionpanel.header' : 'Samtliga pågående sjukfall på ',
        'label.sjukfall.start.rehab.selectionpanel.body' : 'Som rehabkoordinator kan du ta del av samtliga pågående sjukfall på vårdenheten.<br><br>' +
                                                    'När du klickar på "Visa alla sjukfall" nedan kommer du att få se alla pågående sjukfall för den vårdenhet du har loggat in på. ' +
                                                    'För varje sjukfall visas patientens personuppgifter, diagnos, sjukskrivningstid, sjukskrivningsgrad och läkare. ' +
                                                    'Om du har tillgång till flera vårdenheter kan du se pågående sjukfall för en annan vårdenhet genom att byta vårdenhet i sidhuvudet.' +
                                                    '<br><br>Informationen som visas loggas enligt Patientdatalagen (PDL).',
        'label.sjukfall.start.rehab.selectionpanel.urval.button' : 'Visa alla sjukfall',



        // Sjukfall Stat
        'label.sjukfall.start.lakare.statheader' : 'Översikt - mina pågående sjukfall på ',
        'label.sjukfall.start.rehab.statheader' : 'Översikt - alla pågående sjukfall på ',

        'label.sjukfall.stat.ongoing.lakare' : 'Antal pågående sjukfall på enheten',
        'label.sjukfall.stat.gender.lakare' : 'Könsfördelning',
        'label.sjukfall.stat.ongoing.rehab' : 'Antal pågående sjukfall på enheten',
        'label.sjukfall.stat.gender.rehab' : 'Könsfördelning',
        'label.sjukfall.stat.male' : 'Män',
        'label.sjukfall.stat.female' : 'Kvinnor',

        // Sjukfall Result
        'label.sjukfall.result.lakare.header': 'Mina sjukfall',
        'label.sjukfall.result.lakare.subheader': ' - Endast de pågående sjukfall där jag utfärdat det nuvarande intyget',

        'label.sjukfall.result.rehab.header': 'Alla sjukfall',
        'label.sjukfall.result.rehab.subheader': ' - Samtliga pågående sjukfall på enheten ',

        'label.sjukfall.result.back': 'Tillbaka till översiktssidan',

        // Filter
        'label.filter.show' : 'Visa sökfilter',
        'label.filter.hide' : 'Dölj sökfilter',
        'label.filter.diagnos' : 'Välj diagnos',
        'label.filter.lakare' : 'Välj läkare',
        'label.filter.langd' : 'Välj sjukskrivningslängd',
        'label.filter.filter' : 'Fritextfilter',
        'label.filter.filter.placeholder' : 'Hitta sjukfall som innehåller...',
        'label.filter.allselected' : 'Alla valda',

        // Settings
        'label.settings.header' : 'Antal dagar mellan intyg',
        'label.settings.help' : 'Ställ in det antal dagar det maximalt får vara mellan två intyg för att de ska räknas som samma sjukfall.',
        'label.settings.info' : 'Max antal dagar uppehåll mellan intyg är satt till:',

        'label.settings.modal.body' : 'Välj hur många dagars uppehåll det maximalt får vara mellan två intyg för att de ska räknas som samma sjukfall.',
        'label.settings.modal.label' : 'Välj max antal dagars uppehåll mellan intygen',



        //
        'label.gender.male': 'Man',
        'label.gender.female': 'Kvinna',
        'label.gender.undefined': '-',

        // Table (NOTE: the parts after label.table.column must match sjukfallmodels json, as we use this to get sortorder descriptions)
        'label.table.column.patient.id': 'Person&shy;nummer',
        'label.table.column.patient.namn': 'Namn',
        'label.table.column.patient.konshow': 'Kön',
        'label.table.column.diagnos.intygsvarde': 'Diagnos',
        'label.table.column.diagnos.help': 'Huvuddiagnos i nuvarande intyg. För muspekaren över koden för att se vilken diagnos den motsvarar.',
        'label.table.column.start': 'Startdatum',
        'label.table.column.start.help': 'Datum då sjukfallet började på ${enhet}. Alla intyg för samma patient som följer på varandra med max ${glapp} dagars uppehåll räknas till samma sjukfall. Max antal dagars uppehåll mellan intyg kan ställas in i filtret.',
        'label.table.column.slut': 'Slutdatum',
        'label.table.column.slut.help': 'Slutdatum för sjukfallet, dvs. den sista dagen då det finns ett giltigt intyg.',
        'label.table.column.aktivgrad': 'Sjukskrivnings&shy;grad',
        'label.table.column.aktivgrad.help': 'Sjukskrivningsgrad i nuvarande intyg. Om det innehåller flera grader anges de ordnade i tidsföljd med markering av den just nu gällande graden.',
        'label.table.column.lakare.namn': 'Läkare',
        'label.table.column.lakare.namn.help': 'Läkaren som utfärdat nuvarande intyg.',
        'label.table.column.dagar': 'Sjukskrivnings&shy;längd',
        'label.table.column.dagar.help': 'Sjukfallets totala längd i dagar, från startdatum till slutdatum. Eventuella dagar mellan intyg räknas inte in.',

        'label.table.no-result': 'Det finns inga pågående sjukfall på ',
        'label.table.no-filter-result': 'Inga sjukfall matchade filtreringen.',

        'label.table.number.of.rows' : 'Visar',
        'label.table.number.of.rows.of' : 'av',
        'label.table.column.sort.desc': 'Fallande',
        'label.table.column.sort.asc': 'Stigande',




        // Export
        'label.export.button' : 'Exportera',
        'label.export.pdf' : 'Exportera sida till PDF',
        'label.export.excel' : 'Exportera sida till Excel'


    },
    'en': {
        'common.ok': 'OK',
        'common.cancel': 'Cancel'
    }
};
