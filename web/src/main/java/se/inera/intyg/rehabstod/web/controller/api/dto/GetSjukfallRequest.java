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
package se.inera.intyg.rehabstod.web.controller.api.dto;

import se.inera.intyg.rehabstod.web.model.LangdIntervall;
import se.inera.intyg.rehabstod.web.model.Sortering;

import java.util.List;

/**
 * Created by Magnus Ekstrand on 03/02/16.
 */
public class GetSjukfallRequest {

    private Sortering sortering;
    private LangdIntervall langdIntervall;

    private int maxIntygsGlapp;

    private List<String> lakare;
    private List<String> diagnosGrupper;

    private String fritext;

    /** The sole constructor **/
    public GetSjukfallRequest() {
    }

    public Sortering getSortering() {
        return sortering;
    }

    public void setSortering(Sortering sortering) {
        this.sortering = sortering;
    }

    public LangdIntervall getLangdIntervall() {
        return langdIntervall;
    }

    public void setLangdIntervall(LangdIntervall langdIntervall) {
        this.langdIntervall = langdIntervall;
    }

    public int getMaxIntygsGlapp() {
        return maxIntygsGlapp;
    }

    public void setMaxIntygsGlapp(int maxIntygsGlapp) {
        this.maxIntygsGlapp = maxIntygsGlapp;
    }

    public List<String> getLakare() {
        return lakare;
    }

    public void setLakare(List<String> lakare) {
        this.lakare = lakare;
    }

    public List<String> getDiagnosGrupper() {
        return diagnosGrupper;
    }

    public void setDiagnosGrupper(List<String> diagnosGrupper) {
        this.diagnosGrupper = diagnosGrupper;
    }

    public String getFritext() {
        return fritext;
    }

    public void setFritext(String fritext) {
        this.fritext = fritext;
    }
}