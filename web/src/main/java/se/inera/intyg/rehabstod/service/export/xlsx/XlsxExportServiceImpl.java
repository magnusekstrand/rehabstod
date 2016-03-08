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
package se.inera.intyg.rehabstod.service.export.xlsx;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.rehabstod.service.Urval;
import se.inera.intyg.rehabstod.service.export.BaseExportService;
import se.inera.intyg.rehabstod.service.user.UserService;
import se.inera.intyg.rehabstod.web.controller.api.dto.PrintSjukfallRequest;
import se.inera.intyg.rehabstod.web.model.InternalSjukfall;
import se.inera.intyg.rehabstod.web.model.Patient;
import se.inera.intyg.rehabstod.web.model.Sjukfall;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by eriklupander on 2016-02-23.
 */
@Service
public class XlsxExportServiceImpl extends BaseExportService implements XlsxExportService {

    private static final String SHEET_TITLE_SJUKFALL = "Sjukfall";
    private static final int FILTER_SPACING = 3;

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

    private static final String[] HEADERS = new String[] { TABLEHEADER_NR, TABLEHEADER_PERSONNUMMER, TABLEHEADER_NAMN, TABLEHEADER_KON,
            TABLEHEADER_NUVARANDE_DIAGNOS,
            TABLEHEADER_STARTDATUM, TABLEHEADER_SLUTDATUM, TABLEHEADER_SJUKSKRIVNINGSLANGD, TABLEHEADER_SJUKSKRIVNINGSGRAD,
            TABLEHEADER_NUVARANDE_LAKARE };

    private static final int FILTER_HEADLINE_COLUMN = 1;
    private static final int FILTER_VALUE_COLUMN = 2;
    private static final int FILTER_END_COLUMN_SPAN = 6;

    @Autowired
    UserService userService;

    @Override
    public byte[] export(List<InternalSjukfall> sjukfallList, PrintSjukfallRequest req, Urval urval, int total) throws IOException {

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
        addFilterHeader(sheet, rowNumber++, FILTER_TITLE_VALD_SJUKSKRIVNINGSLANGD,
                req.getLangdIntervall().getMin() + " - " + req.getLangdIntervall().getMax() + " dagar");
        rowNumber = addLakareList(sheet, rowNumber++, FILTER_TITLE_VALDA_LAKARE, req.getLakare(), urval);
        rowNumber = addDiagnosKapitel(sheet, rowNumber++, FILTER_TITLE_VALDA_DIAGNOSER, req.getDiagnosGrupper());
        addFilterHeader(sheet, rowNumber++, FILTER_TITLE_FRITEXTFILTER, notEmpty(req) ? req.getFritext() : "-");
        addFilterMainHeader(sheet, rowNumber++, H2_SJUKFALLSINSTALLNING);
        addFilterHeader(sheet, rowNumber++, MAXANTAL_DAGAR_UPPEHALL_MELLAN_INTYG, req.getMaxIntygsGlapp() + " dagar");
        rowNumber += FILTER_SPACING;
        addFilterMainHeader(sheet, rowNumber++, VALD_SORTERING_PA_TABELLEN);
        addFilterHeader(sheet, rowNumber++, SORTERING_KOLUMN, req.getSortering().getKolumn());
        addFilterHeader(sheet, rowNumber++, SORTERING_RIKTNING, req.getSortering().getOrder());
        rowNumber += FILTER_SPACING;
        addFilterMainHeader(sheet, rowNumber++, ANTAL_VISAR_ANTAL_PAGAENDE_SJUKFALL);
        addFilterHeader(sheet, rowNumber++, ANTAL_EXPORTEN_VISAR, String.valueOf(sjukfallList.size()));
        addFilterHeader(sheet, rowNumber++, urval == Urval.ISSUED_BY_ME ? ANTAL_TOTALT_MINA : ANTAL_TOTALT_PA_ENHETEN, String.valueOf(total));

        rowNumber += 3;
        addTableHeaderRows(sheet, rowNumber++, urval);
        addDataRows(sheet, rowNumber, sjukfallList, urval);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        return baos.toByteArray();
    }

    private int addLakareList(XSSFSheet sheet, int currentRowNumber, String filterTitle, List<String> lakareList, Urval urval) {
        int rowNumber = currentRowNumber;
        if (Urval.ISSUED_BY_ME == urval) {
            addFilterHeader(sheet, rowNumber++, filterTitle, userService.getUser().getNamn());
            return rowNumber;
        }
        if (lakareList == null || lakareList.size() == 0) {
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

        if (diagnosGrupper == null || diagnosGrupper.size() == 0) {
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

    private void addTableHeaderRows(XSSFSheet sheet, int rowIndex, Urval urval) {

        XSSFRow row = sheet.createRow(rowIndex);

        for (int a = 0; a < HEADERS.length; a++) {
            // Not too elegant, but if we have ISSUED_BY_ME urval, we don't render the last column.
            if (!(urval == Urval.ISSUED_BY_ME && a == HEADERS.length - 1)) {
                createHeaderCell(row, a, HEADERS[a]);
            }
        }
    }

    private void addDataRows(XSSFSheet sheet, int rowIndex, List<InternalSjukfall> sjukfallList, Urval urval) {
        for (int a = 0; a < sjukfallList.size(); a++) {
            XSSFRow row = sheet.createRow(rowIndex + a);
            Sjukfall sf = sjukfallList.get(a).getSjukfall();

            int colIndex = 0;
            createDataCell(row, colIndex++, "" + (a + 1));
            createRichTextDataCell(row, colIndex++, buildPersonnummerAndAgeRichText(sf.getPatient()));
            createDataCell(row, colIndex++, sf.getPatient().getNamn());
            createDataCell(row, colIndex++, sf.getPatient().getKon().getDescription());
            createDataCell(row, colIndex++, sf.getDiagnos().getKod());
            createDataCell(row, colIndex++, ISODateTimeFormat.yearMonthDay().print(sf.getStart()));
            createDataCell(row, colIndex++, ISODateTimeFormat.yearMonthDay().print(sf.getSlut()));
            createDataCell(row, colIndex++, String.format(FORMAT_ANTALA_DAGAR, sf.getDagar()) + String.format(FORMAT_ANTAL_INTYG, sf.getIntyg()));
            createRichTextDataCell(row, colIndex++, buildGraderRichText(sf));
            if (urval != Urval.ISSUED_BY_ME) {
                createDataCell(row, colIndex, sf.getLakare().getNamn());
            }
        }
        for (int a = 0; a < HEADERS.length; a++) {
            sheet.autoSizeColumn(a);
        }
        // Makes sure the "namn" column isn't excessively wide due to the filter.
        sheet.setColumnWidth(2, 7000);
    }

    private XSSFRichTextString buildPersonnummerAndAgeRichText(Patient patient) {
        String value = patient.getId() + String.format(FORMAT_ALDER_PARANTESER, patient.getAlder());
        XSSFRichTextString richTextString = new XSSFRichTextString();
        richTextString.setString(value);
        richTextString.applyFont(defaultFont11);

        return richTextString;
    }

    private XSSFRichTextString buildGraderRichText(Sjukfall sf) {

        if (sf.getGrader() == null || sf.getGrader().size() == 0) {
            return new XSSFRichTextString();
        }

        StringBuilder buf = new StringBuilder();
        Pair aktivIndicies = null;
        int currentIndex = 0;
        for (Integer grad : sf.getGrader()) {

            // Store indicies for the aktiv grad so we can make its text bold later.
            if (grad == sf.getAktivGrad()) {
                aktivIndicies = new Pair(currentIndex, currentIndex + ("" + grad + "%").length());
            }
            buf.append("").append(grad).append("% ");
            currentIndex = buf.length();
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
        boldFont16 = buildFont(wb, 16, "Helvetica", true, true);
        boldFont12 = buildFont(wb, 12, "Helvetica", true, false);
        defaultFont12 = buildFont(wb, 12, "Helvetica", false, false);
        boldFont11 = buildFont(wb, 11, "Helvetica", true, false);
        defaultFont11 = buildFont(wb, 11, "Helvetica", false, false);

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
