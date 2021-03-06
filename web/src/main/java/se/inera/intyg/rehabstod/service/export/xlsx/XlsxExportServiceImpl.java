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
package se.inera.intyg.rehabstod.service.export.xlsx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;
import se.inera.intyg.rehabstod.auth.RehabstodUser;
import se.inera.intyg.rehabstod.auth.RehabstodUserPreferences.Preference;
import se.inera.intyg.rehabstod.common.util.YearMonthDateFormatter;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.export.BaseExportService;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.SjukfallEnhet;

/**
 * Created by eriklupander on 2016-02-23.
 */
@Service
public class XlsxExportServiceImpl extends BaseExportService implements XlsxExportService {

    public static final String HELVETICA = "Helvetica";

    private static final String SHEET_TITLE_SJUKFALL = "Sjukfall";
    private static final int FILTER_SPACING = 3;
    private static final int FILTER_HEADLINE_COLUMN = 1;
    private static final int FILTER_VALUE_COLUMN = 2;
    private static final int FILTER_END_COLUMN_SPAN = 6;

    private XSSFCellStyle boldStyle;
    private XSSFCellStyle filterMainHeaderStyle;
    private XSSFCellStyle filterHeaderStyle;
    private XSSFCellStyle filterTextStyle;
    private XSSFCellStyle stripedDarker;
    private XSSFCellStyle stripedLighter;
    private XSSFFont boldFont16;
    private XSSFFont defaultFont12;
    private XSSFFont boldFont12;
    private XSSFFont defaultFont11;
    private XSSFFont boldFont11;
    private String[] headers;

    // api

    @Override
    @PrometheusTimeMethod
    public byte[] export(List<SjukfallEnhet> sjukfallList, PrintSjukfallRequest req, RehabstodUser user, int total)
            throws IOException {

        headers = initHeaders(req, user.getUrval(), isSrsFeatureActive(user));
        XSSFWorkbook wb = new XSSFWorkbook();
        setupFonts(wb);
        XSSFSheet sheet = wb.createSheet(SHEET_TITLE_SJUKFALL);

        int rowNumber = 0;

        // CHECKSTYLE:OFF MagicNumber
        // Start with 2 empty rows to make space for filter
        for (; rowNumber < 2; rowNumber++) {
            sheet.createRow(rowNumber);
        }

        addFilterMainHeader(sheet, rowNumber++, VALDA_FILTER);

        addFilterHeader(sheet, rowNumber++, FILTER_TITLE_FRITEXTFILTER, notEmpty(req) ? req.getFritext() : "-");
        addFilterHeader(sheet, rowNumber++, FILTER_TITLE_VISAPATIENTUPPGIFTER, req.isShowPatientId() ? " Ja" : " Nej");
        addFilterHeader(sheet, rowNumber++, FILTER_TITLE_VALD_ALDER,
                req.getAldersIntervall().getMin() + " - " + req.getAldersIntervall().getMax() + " år");
        rowNumber = addDiagnosKapitel(sheet, rowNumber++, FILTER_TITLE_VALDA_DIAGNOSER, req.getDiagnosGrupper()); // NOSONAR
        addFilterHeader(sheet, rowNumber++, FILTER_TITLE_VALD_SLUTDATUM, getFilterDate(req.getSlutdatumIntervall()));
        addFilterHeader(sheet, rowNumber++, FILTER_TITLE_VALD_SJUKSKRIVNINGSLANGD,
                req.getLangdIntervall().getMin() + " - " + req.getLangdIntervall().getMax() + " dagar");
        rowNumber = addLakareList(sheet, rowNumber++, FILTER_TITLE_VALDA_LAKARE, req.getLakare(), user); // NOSONAR

        // Inställningar
        String maxGlapp = user.getPreferences().get(Preference.MAX_ANTAL_DAGAR_MELLAN_INTYG);
        addFilterMainHeader(sheet, rowNumber++, H2_SJUKFALLSINSTALLNING);
        addFilterHeader(sheet, rowNumber++, MAXANTAL_DAGAR_UPPEHALL_MELLAN_INTYG, maxGlapp + " dagar");
        rowNumber += FILTER_SPACING;

        // Sortering
        addFilterMainHeader(sheet, rowNumber++, VALD_SORTERING_PA_TABELLEN);
        addFilterHeader(sheet, rowNumber++, SORTERING_KOLUMN, req.getSortering().getKolumn());
        addFilterHeader(sheet, rowNumber++, SORTERING_RIKTNING, req.getSortering().getOrder());
        rowNumber += FILTER_SPACING;
        addFilterMainHeader(sheet, rowNumber++, ANTAL_VISAR_ANTAL_PAGAENDE_SJUKFALL);
        addFilterHeader(sheet, rowNumber++, ANTAL_EXPORTEN_VISAR, String.valueOf(sjukfallList.size()));
        addFilterHeader(sheet, rowNumber++, user.getUrval() == Urval.ISSUED_BY_ME ? ANTAL_TOTALT_MINA : ANTAL_TOTALT_PA_ENHETEN,
                String.valueOf(total));

        rowNumber += 3;
        addTableHeaderRows(sheet, rowNumber++);
        addDataRows(sheet, rowNumber, sjukfallList, user.getUrval(), req.isShowPatientId(), isSrsFeatureActive(user));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        return baos.toByteArray();
    }

    private String[] initHeaders(PrintSjukfallRequest req, Urval urval, boolean showSrs) {
        List<String> tempHeaders = new ArrayList<>();
        tempHeaders.add(TABLEHEADER_NR);
        if (req.isShowPatientId()) {
            tempHeaders.add(TABLEHEADER_PERSONNUMMER);
            tempHeaders.add(TABLEHEADER_ALDER);
            tempHeaders.add(TABLEHEADER_NAMN);
        } else {
            tempHeaders.add(TABLEHEADER_ALDER);
        }
        tempHeaders.add(TABLEHEADER_KON);
        tempHeaders.add(TABLEHEADER_NUVARANDE_DIAGNOS);
        tempHeaders.add(TABLEHEADER_STARTDATUM);
        tempHeaders.add(TABLEHEADER_SLUTDATUM);
        tempHeaders.add(TABLEHEADER_SJUKSKRIVNINGSLANGD);
        tempHeaders.add(TABLEHEADER_ANTAL);
        tempHeaders.add(TABLEHEADER_SJUKSKRIVNINGSGRAD);
        if (urval != Urval.ISSUED_BY_ME) {
            tempHeaders.add(TABLEHEADER_NUVARANDE_LAKARE);
        }

        if (showSrs) {
            tempHeaders.add(TABLEHEADER_SRS_RISK);
        }
        return tempHeaders.toArray(new String[tempHeaders.size()]);
    }

    // private scope

    private int addLakareList(XSSFSheet sheet, int currentRowNumber, String filterTitle, List<String> lakareList, RehabstodUser user) {
        int rowNumber = currentRowNumber;
        if (Urval.ISSUED_BY_ME == user.getUrval()) {
            addFilterHeader(sheet, rowNumber++, filterTitle, user.getNamn());
            return rowNumber;
        }
        if (lakareList == null || lakareList.isEmpty()) {
            addFilterHeader(sheet, rowNumber++, filterTitle, SELECTION_VALUE_ALLA);
            return rowNumber;
        }

        for (int i = 0; i < lakareList.size(); i++) {
            String lakare = lakareList.get(i);
            addFilterHeader(sheet, rowNumber++, i == 0 ? filterTitle : "", lakare);
        }
        return rowNumber;
    }

    private int addDiagnosKapitel(XSSFSheet sheet, int currentRowNumber, String filterTitle, List<String> diagnosGrupper) {
        int rowNumber = currentRowNumber;

        if (diagnosGrupper == null || diagnosGrupper.isEmpty()) {
            addFilterHeader(sheet, rowNumber++, filterTitle, SELECTION_VALUE_ALLA);
            return rowNumber;
        }

        for (int i = 0; i < diagnosGrupper.size(); i++) {
            String diagnosKapitel = diagnosGrupper.get(i);
            addFilterHeaderWithRichTextValue(sheet, rowNumber++, i == 0 ? filterTitle : "", diagnosKapitelFormat(diagnosKapitel));
        }
        return rowNumber;
    }

    private XSSFRichTextString diagnosKapitelFormat(String diagnosKapitelId) {

        StringBuilder buf = new StringBuilder();

        buf.append(diagnosKapitelId).append(StringUtils.isNotEmpty(diagnosKapitelId) ? ": " : "");
        // Add the description text for the code
        buf.append(diagnosKapitelService.getDiagnosKapitel(diagnosKapitelId).getName());

        XSSFRichTextString richTextString = new XSSFRichTextString();
        richTextString.setString(buf.toString());
        richTextString.applyFont(defaultFont12);

        richTextString.applyFont(0, diagnosKapitelId.length(), boldFont12);
        return richTextString;
    }

    /**
     * Creates a merged span to make all filter sections the same size and style.
     *
     * @param fromColumn
     * @param style
     * @param sheet
     * @param row
     */
    private void createMergedCellFromColumn(int fromColumn, XSSFCellStyle style, XSSFSheet sheet, XSSFRow row) {
        // Create and style cells for the span
        for (int i = fromColumn + 1; i <= FILTER_END_COLUMN_SPAN; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);

        }
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), fromColumn, FILTER_END_COLUMN_SPAN));
    }

    private void addFilterMainHeader(XSSFSheet sheet, int rowIndex, String value) {
        XSSFRow row = sheet.createRow(rowIndex);
        XSSFCell cell = row.createCell(FILTER_HEADLINE_COLUMN);
        cell.setCellValue(value);
        cell.setCellStyle(filterMainHeaderStyle);

        createMergedCellFromColumn(FILTER_HEADLINE_COLUMN, filterMainHeaderStyle, sheet, row);
    }

    private void addFilterHeader(XSSFSheet sheet, int rowIndex, String key, String value) {
        XSSFRow row = buildFilterTitleCell(sheet, rowIndex, key);
        addValueCell(sheet, row, value);
    }

    private void addFilterHeaderWithRichTextValue(XSSFSheet sheet, int rowIndex, String key, XSSFRichTextString value) {
        XSSFRow row = buildFilterTitleCell(sheet, rowIndex, key);
        addRichTextValueCell(sheet, row, value);
    }

    private XSSFRow buildFilterTitleCell(XSSFSheet sheet, int rowIndex, String key) {
        XSSFRow row = sheet.createRow(rowIndex);
        XSSFCell cell = row.createCell(FILTER_HEADLINE_COLUMN);
        cell.setCellStyle(filterHeaderStyle);
        cell.setCellValue(key);
        return row;
    }

    private void addRichTextValueCell(XSSFSheet sheet, XSSFRow row, XSSFRichTextString value) {
        XSSFCell cell2 = row.createCell(FILTER_VALUE_COLUMN);
        cell2.setCellStyle(filterTextStyle);
        cell2.setCellValue(value);
        createMergedCellFromColumn(FILTER_VALUE_COLUMN, filterTextStyle, sheet, row);
    }

    private void addValueCell(XSSFSheet sheet, XSSFRow row, String value) {
        XSSFCell cell2 = row.createCell(FILTER_VALUE_COLUMN);
        cell2.setCellValue(value);
        cell2.setCellStyle(filterTextStyle);
        createMergedCellFromColumn(FILTER_VALUE_COLUMN, filterTextStyle, sheet, row);
    }

    private void addTableHeaderRows(XSSFSheet sheet, int rowIndex) {

        XSSFRow row = sheet.createRow(rowIndex);
        for (int a = 0; a < headers.length; a++) {
            createHeaderCell(row, a, headers[a]);
        }
    }

    private void addDataRows(XSSFSheet sheet, int rowIndex, List<SjukfallEnhet> sjukfallList, Urval urval, boolean showPatientId,
            boolean showSrs) {
        for (int a = 0; a < sjukfallList.size(); a++) {
            XSSFRow row = sheet.createRow(rowIndex + a);
            SjukfallEnhet sf = sjukfallList.get(a);

            int colIndex = 0;
            createDataCell(row, colIndex++, Integer.toString(a + 1));
            if (showPatientId) {
                createRichTextDataCell(row, colIndex++, buildPersonnummerRichText(sf.getPatient()));
            }
            createDataCell(row, colIndex++, Integer.toString(sf.getPatient().getAlder()) + " år");

            if (showPatientId) {
                createDataCell(row, colIndex++, sf.getPatient().getNamn());
            }
            createDataCell(row, colIndex++, sf.getPatient().getKon().getDescription());
            createDataCell(row, colIndex++, getCompoundDiagnoseText(sf));
            createDataCell(row, colIndex++, YearMonthDateFormatter.print(sf.getStart()));
            createDataCell(row, colIndex++, YearMonthDateFormatter.print(sf.getSlut()));
            createDataCell(row, colIndex++, String.format(FORMAT_ANTAL_DAGAR, sf.getDagar()));
            createDataCell(row, colIndex++, Integer.toString(sf.getIntyg()));
            createRichTextDataCell(row, colIndex++, buildGraderRichText(sf));
            if (urval != Urval.ISSUED_BY_ME) {
                createDataCell(row, colIndex++, sf.getLakare().getNamn());
            }

            if (showSrs) {
                createDataCell(row, colIndex++, getRiskKategoriDesc(sf.getRiskSignal()));
            }

        }
        for (int a = 0; a < headers.length; a++) {
            sheet.autoSizeColumn(a);
        }
        // Makes sure the "namn" column isn't excessively wide due to the filter.
        sheet.setColumnWidth(2, 7000);
    }

    private String getCompoundDiagnoseText(SjukfallEnhet sf) {
        StringBuilder b = new StringBuilder();
        b.append(sf.getDiagnos().getKod()).append(" ");
        b.append(sf.getDiagnos().getBeskrivning());
        b.append(diagnoseListToString(sf.getBiDiagnoser()));
        return b.toString();
    }

    private XSSFRichTextString buildPersonnummerRichText(Patient patient) {
        String value = patient.getId();
        XSSFRichTextString richTextString = new XSSFRichTextString();
        richTextString.setString(value);
        richTextString.applyFont(defaultFont11);

        return richTextString;
    }

    private XSSFRichTextString buildGraderRichText(SjukfallEnhet sf) {

        if (sf.getGrader() == null || sf.getGrader().isEmpty()) {
            return new XSSFRichTextString();
        }

        StringBuilder buf = new StringBuilder();
        Pair aktivIndicies = null;
        boolean first = true;
        for (Integer grad : sf.getGrader()) {

            if (!first) {
                buf.append(UNICODE_RIGHT_ARROW_SYMBOL).append(" ");
            }

            int currentIndex = buf.length();
            // Store indicies for the aktiv grad so we can make its text bold later.
            if (grad == sf.getAktivGrad()) {
                aktivIndicies = new Pair(currentIndex, currentIndex + ("" + grad + "%").length());
            }
            buf.append("").append(grad).append("% ");

            first = false;
        }
        buf.setLength(buf.length() - 1);

        XSSFRichTextString richTextString = new XSSFRichTextString();
        richTextString.setString(buf.toString());
        richTextString.applyFont(defaultFont11);

        // Uses stored indicies to make the correct part of the rich string bold.
        if (aktivIndicies != null) {
            richTextString.applyFont(aktivIndicies.getI1(), aktivIndicies.getI2(), boldFont11);
        }
        return richTextString;
    }

    private void createDataCell(XSSFRow row, int colIndex, String value) {
        XSSFCell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(row.getRowNum() % 2 == 0 ? stripedDarker : stripedLighter);
    }

    private void createRichTextDataCell(XSSFRow row, int colIndex, XSSFRichTextString value) {
        XSSFCell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(row.getRowNum() % 2 == 0 ? stripedDarker : stripedLighter);
    }

    private void createHeaderCell(XSSFRow row, int colIndex, String value) {
        XSSFCell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(boldStyle);
    }

    /**
     * Sets up all fonts and cell styles used in the document.
     */
    private void setupFonts(XSSFWorkbook wb) {
        boldFont16 = buildFont(wb, 16, HELVETICA, true, true);
        boldFont12 = buildFont(wb, 12, HELVETICA, true, false);
        defaultFont12 = buildFont(wb, 12, HELVETICA, false, false);
        boldFont11 = buildFont(wb, 11, HELVETICA, true, false);
        defaultFont11 = buildFont(wb, 11, HELVETICA, false, false);

        boldStyle = wb.createCellStyle();
        boldStyle.setFont(boldFont11);
        boldStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(40, 180, 196)));
        boldStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

        stripedDarker = wb.createCellStyle();
        stripedDarker.setFont(defaultFont11);
        stripedDarker.setFillForegroundColor(new XSSFColor(new java.awt.Color(230, 230, 230)));
        stripedDarker.setFillPattern(CellStyle.SOLID_FOREGROUND);

        stripedLighter = wb.createCellStyle();
        stripedLighter.setFont(defaultFont11);
        stripedLighter.setFillForegroundColor(new XSSFColor(new java.awt.Color(244, 244, 244)));
        stripedLighter.setFillPattern(CellStyle.SOLID_FOREGROUND);

        filterTextStyle = wb.createCellStyle();
        filterTextStyle.setFont(defaultFont12);
        filterTextStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(240, 240, 240)));
        filterTextStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        filterTextStyle.setWrapText(true);

        filterHeaderStyle = wb.createCellStyle();
        filterHeaderStyle.setFont(boldFont12);
        filterHeaderStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(240, 240, 240)));
        filterHeaderStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        filterHeaderStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);

        filterMainHeaderStyle = wb.createCellStyle();
        filterMainHeaderStyle.setFont(boldFont16);
        filterMainHeaderStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(240, 240, 240)));
        filterMainHeaderStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

    }

    private XSSFFont buildFont(XSSFWorkbook wb, int heightInPoints, String fontName, boolean bold, boolean underline) {
        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) heightInPoints);
        font.setFontName(fontName);
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(bold);
        font.setBoldweight(bold ? Font.BOLDWEIGHT_BOLD : Font.BOLDWEIGHT_NORMAL);
        font.setUnderline(underline ? XSSFFont.U_SINGLE : XSSFFont.U_NONE);
        return font;
    }

    // CHECKSTYLE:ON MagicNumber

}
