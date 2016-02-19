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
package se.inera.intyg.rehabstod.service.sjukfall.ruleengine.util;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import se.inera.intyg.rehabstod.service.sjukfall.ruleengine.SortableIntygsData;

/**
 * Calulates the effective length of all arbetsformaga intervals contained in the SortableIntygsData's, making sure to
 * merge overlapping intervals first.
 * Created by marced on 19/02/16.
 */
public final class SjukfallLangdCalculator {

    private SjukfallLangdCalculator() {
    }

    public static int getEffectiveNumberOfSickDays(List<SortableIntygsData> intygsUnderlag) {
        // Sanity check
        if (intygsUnderlag == null || intygsUnderlag.isEmpty()) {
            return 0;
        }

        // Extract all Arbetsformaga grader-intervals from all sortableIntygsdata's to a list of LocalDateInterals
        List<LocalDateInterval> allIntervals = intygsUnderlag.stream().map(sid -> sid.getArbetsformaga().getFormaga()).flatMap(l -> l.stream())
                .map(formaga -> new LocalDateInterval(formaga.getStartdatum(), formaga.getSlutdatum())).collect(Collectors.toList());

        // apply merge algorithm
        List<LocalDateInterval> mergedIntervals = mergeIntervals(allIntervals);

        // calculate sum of total length of remaining intervals
        int totalDays = (int) mergedIntervals.stream().mapToLong(i -> i.getDurationInDays()).sum();

        return totalDays;
    }

    static List<LocalDateInterval> mergeIntervals(List<LocalDateInterval> allIntervals) {

        if (allIntervals.isEmpty()) {
            return allIntervals;
        }
        // 1. Sort them from lowest starting time to highest starting time
        allIntervals.sort((i1, i2) -> i1.getStartDate().compareTo(i2.getStartDate()));
        Stack<LocalDateInterval> stack = new Stack<>();

        stack.push(allIntervals.get(0));
        for (int i = 1; i < allIntervals.size(); i++) {
            // get interval from stack top
            LocalDateInterval top = stack.peek();

            // if current interval is not overlapping with stack top, push it to the stack
            LocalDateInterval current = allIntervals.get(i);
            if (top.getEndDate().isBefore(current.getStartDate())) {
                stack.push(current);

                // Otherwise update the ending time of top if ending of current
                // interval is more
            } else if (top.getEndDate().isBefore(current.getEndDate())) {
                top.setEndDate(current.getEndDate());
                stack.pop();
                stack.push(top);
            }
        }
        return stack;
    }

}