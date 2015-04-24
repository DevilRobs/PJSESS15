package at.jku.win.ss15.pjse.backend;

import org.joda.time.Duration;
import org.joda.time.DurationFieldType;
import org.joda.time.Period;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

/**
 * A utility class for several operations related to {@code Entry} as well as {@link java.util.List}s containing these objects.
 */
public class Entries {
    /**
     * Calculates the available budget for a given {@link Category} within a certain time span
     *
     * @param c        the category for which the available budget should be calculated
     * @param provider a {@link DataProvider} used for fetching relevant entries
     * @param from     the date defining the beginning of the time span
     * @param to       the date defining the ending of the time span
     * @return NULL, if the category has no budget (NULL). Otherwise the rest budget
     * will be returned which may also be a negative value.
     * @throws java.sql.SQLException if an error occurs during retrieving necessary data from the the database
     */
    public static BigDecimal availableBudget(Category c, DataProvider provider, Date from, Date to) throws SQLException {
        if (c.getBudget() == null)
            return null;
        List<Entry> entries = ensureListIsModifyable(provider.getAllEntries(c));
        entries = onceify(entries, from, to);
        BigDecimal total = new BigDecimal(0);
        for (Entry e : entries) {
            total = total.add(e.getValue());
        }
        BigDecimal result = c.getBudget().add(total);
        return result;
    }

    /**
     * Makes sure that the given list can be worked with. If not, a new List will be returned.
     *
     * @param list to be checked whether it can be modified.
     * @param <T>
     * @return the same list as the parameter if no exception is raised during the process.
     * Otherwise a new empty {@link java.util.ArrayList} will be returned.
     */
    public static <T> List<T> ensureListIsModifyable(List<T> list) {
        try {
            list.add(null);
            return list;
        } catch (Exception e) {
            return new ArrayList<>(list);
        }
    }

    /**
     * Filters all entries in the specific range from a certain list. All entries,
     * except those which occur multiple times, are put into a new {@link java.util.List}.
     * Entries occurring repeatedly will added to the list depending on how often
     * the entry occurs in the give time span.
     *
     * @param entries a {@link java.util.List} of type {@link Entry} to be checked for repeatedly occurrence.
     * @param from    the date defining the beginning of the time span
     * @param to      the date defining the ending of the time span
     * @return a {@link java.util.List} of type {@link Entry}
     */
    public static List<Entry> onceify(List<Entry> entries, Date from, Date to) {
        List<Entry> entryList = new ArrayList<>(entries.size());
        for (Entry e : entries) {
            if (e.getIntervalType() == Entry.IntervalType.Once) {
                if (isInRange(e.getStartTime(), from, to))
                    entryList.add(e);
            } else {
                final Date endTime = (e.getIntervalEndTime() != null && to.getTime() > e.getIntervalEndTime().getTime() ? e.getIntervalEndTime() : to);
                final DurationFieldType temporalUnit = e.getIntervalType() == Entry.IntervalType.Daily ? DurationFieldType.days() : DurationFieldType.months();
                if (isInRange(e.getStartTime(), from, to)) {
                    List<Entry> oncified = internalOncify(e.setParent(e), to, e.getStartTime(), e.getEndTime(), temporalUnit, e.getInterval());
                    entryList.addAll(oncified);
                } else if (isInRange(endTime, from, to)) {
                    //Set start time, keep original as parent!
                    Period p = new Period(e.getStartTime().getTime(), from.getTime());
                    long l = p.get(temporalUnit);
                    Calendar c = Calendar.getInstance();
                    c.setTime(e.getStartTime());
                    int field = temporalUnit == DurationFieldType.days() ? Calendar.DAY_OF_MONTH : Calendar.MONTH;
                    c.add(field, (int) (l * e.getInterval()));
                    while (c.getTime().getTime() <= from.getTime())
                        c.add(field, (int) e.getInterval());
                    Date newStartTime = c.getTime();
                    if (isInRange(newStartTime, from, to)) {
                        Date currentEndTime = e.getEndTime();
                        if (currentEndTime != null) {
                            Duration d = new Duration(e.getStartTime().getTime(), newStartTime.getTime());
                            currentEndTime = new Date(currentEndTime.getTime() + d.getMillis());
                        }

                        List<Entry> oncified = internalOncify(e.setParent(e), to, newStartTime, currentEndTime, temporalUnit, e.getInterval());
                        entryList.addAll(oncified);
                    }
                }
            }
        }
        return entryList;
    }

    private static List<Entry> internalOncify(Entry originalWithParentSelf, Date totalEndTime, Date currentStartTime, Date currentEndTime, DurationFieldType intervalUnit, int interval) {
        if (currentStartTime.getTime() > totalEndTime.getTime())
            return new LinkedList<>();
        Entry entry = originalWithParentSelf.setInterval(Entry.IntervalType.Once, -1, null);
        entry = entry.setEndTime(currentEndTime).setStartTime(currentStartTime);
        //Prepare recursive call
        Calendar c = Calendar.getInstance();
        c.setTime(currentStartTime);
        int field = intervalUnit == DurationFieldType.days() ? Calendar.DAY_OF_MONTH : Calendar.MONTH;
        c.add(field, interval);
        currentStartTime = c.getTime();
        if (currentEndTime != null) {
            c.setTime(currentEndTime);
            c.add(field, interval);
            currentEndTime = c.getTime();
        }
        List<Entry> list = internalOncify(originalWithParentSelf, totalEndTime, currentStartTime, currentEndTime, intervalUnit, interval);
        list.add(entry);
        return list;
    }

    /**
     * Checks the whether the input time is between the specified beginning and end date.
     *
     * @param input the date to be checked
     * @param from  the date marking the beginning of the time span
     * @param to    the date marking the ending of the time span
     * @return {@code true},  if the input date is between the beginning and ending date,
     * or the input date equals one of them.<br>
     * {@code false}, if the input date is not within the time span of {@code from} until {@code to}.
     */
    private static boolean isInRange(Date input, Date from, Date to) {
        return input.getTime() <= to.getTime() && from.getTime() <= input.getTime();
    }

    /**
     * A comperator which orders the elements according to their start time
     */
    public static class EntryTimeComperator implements Comparator<Entry> {
        private EntryTimeComperator() {
        }

        private static Comparator<Entry> instance = null;

        /**
         * Returns an instance of this class
         *
         * @return An instance of  EntryTimeComperator
         */
        public static Comparator<Entry> getInstance() {
            if (instance == null) {
                instance = new EntryTimeComperator();
            }
            return instance;
        }

        @Override
        public int compare(Entry o1, Entry o2) {
            return o1.getStartTime().compareTo(o2.getStartTime());
        }
    }
}
