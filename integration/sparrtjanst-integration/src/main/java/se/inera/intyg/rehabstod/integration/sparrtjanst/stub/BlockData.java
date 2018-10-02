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
package se.inera.intyg.rehabstod.integration.sparrtjanst.stub;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by marced on 2018-10-01.
 */
public class BlockData {
    // Maybe add vg/ve to?
    String personId;
    LocalDate blockFrom;
    LocalDate blockTo;

    public BlockData(String personId, LocalDate blockFrom, LocalDate blockTo) {
        this.personId = personId;
        this.blockFrom = blockFrom;
        this.blockTo = blockTo;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public LocalDate getBlockFrom() {
        return blockFrom;
    }

    public void setBlockFrom(LocalDate blockFrom) {
        this.blockFrom = blockFrom;
    }

    public LocalDate getBlockTo() {
        return blockTo;
    }

    public void setBlockTo(LocalDate blockTo) {
        this.blockTo = blockTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BlockData blockData = (BlockData) o;
        return Objects.equals(personId, blockData.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personId);
    }
}
