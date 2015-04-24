package at.jku.win.ss15.pjse.backend;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The <code>Entry</code> class is used to create expenditures and add them to a certain category.
 * An <code>Entry</code> can occur repeatedly and the location where the entry was/should added
 * can be stored as well.
 */
public class Entry implements Serializable {
    private BigDecimal value; //(10,2)
    private String purpose, category;
    private Float locLat, locLng;
    private int interval;
    private IntervalType intervalType;
    private Date startTime, endTime, intervalEndTime;
    private Entry parent;

    /**
     * Do not use this constructor
     */
    @Deprecated
    public Entry() {
    }

    /**
     * An entry can be added to a {@link Category}, which can change the budget (left) for the category.
     *
     * @param value     the budget value for this entry
     * @param startTime the date when the entry starts
     * @param c         the category the entry belongs to
     */
    public Entry(BigDecimal value, Date startTime, Category c) {
        this(value, startTime, c.getName());
    }

    /**
     * An entry can be added to a {@link Category}, which can change the budget (or what is left of it) for the category.
     *
     * @param value     the budget value for this entry
     * @param startTime the date when the entry starts
     * @param category  the name of the category to which the entry should be added
     */
    public Entry(BigDecimal value, Date startTime, String category) {
        if (value == null)
            throw new NullPointerException("value must not be NULL");
        if (startTime == null)
            throw new NullPointerException("startTime must not be NULL");
        if (category == null)
            throw new NullPointerException("category must not be NULL");
        this.value = value;
        this.startTime = startTime;
        this.category = category;
        intervalType = IntervalType.Once;
        interval = -1;
    }

    public Entry getParent() {
        return parent;
    }

    /**
     * @param parent Gets the parent element, if exists
     * @return null if no parent element exists or an entry
     */
    public Entry setParent(Entry parent) {
        Entry e = new Entry(this);
        e.parent = parent;
        return e;
    }

    private Entry(Entry e) {
        value = e.value;
        purpose = e.purpose;
        locLat = e.locLat;
        locLng = e.locLng;
        interval = e.interval;
        intervalType = e.intervalType;
        startTime = e.startTime;
        endTime = e.endTime;
        intervalEndTime = e.intervalEndTime;
        category = e.category;
        parent = e.parent;
    }

    /**
     * Gets the name of the {@link Category}.
     *
     * @return the category's name
     */
    public String getCategoryName() {
        return category;
    }

    /**
     * Creates a new entry and adds it to the specified category.
     *
     * @param category the category's name the entry should be added to
     * @return the created entry
     */
    public Entry setCategoryName(String category) {
        Entry e = new Entry(this);
        e.category = category;
        return e;
    }

    /**
     * Creates a new entry and adds it to the specified category. Delegates to {@link at.jku.win.ss15.pjse.backend.Entry#setCategoryName}
     *
     * @param category the category the entry should be added to
     * @return the created entry
     */
    public Entry setCategory(Category category) {
        return setCategoryName(category.getName());
    }

    /**
     * Gets the event's end time.
     *
     * @return the end time of the event
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * Gets the start time of the event.
     *
     * @return the start time of the event
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * The last execution time of an interval.
     *
     * @return the time of the interval's last execution
     */
    public Date getIntervalEndTime() {
        return intervalEndTime;
    }

    /**
     * It is recommended, if the event should be visible, when the day starts,
     * that the startTime is the actual time, when the event happens.
     *
     * @param startTime the time when the entry should start
     * @return the new entry with the specified start time
     */
    public Entry setStartTime(Date startTime) {
        if (value == null)
            throw new NullPointerException("startTime must not be NULL");
        Entry newEntry = new Entry(this);
        newEntry.startTime = startTime;
        return newEntry;
    }

    /**
     * Sets the end time for a entry.
     *
     * @param endTime the time when an entry should end
     * @return the new entry with the specified end time
     */
    public Entry setEndTime(Date endTime) {
        Entry newEntry = new Entry(this);
        newEntry.endTime = endTime;
        return newEntry;
    }

    /**
     * Gets the value of the entry.
     *
     * @return the entry's value
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Sets the value of an entry.
     *
     * @param value the value to be set
     * @return the new entry with the specified value
     */
    public Entry setValue(BigDecimal value) {
        if (value == null)
            throw new NullPointerException("value must not be NULL");
        Entry newEntry = new Entry(this);
        newEntry.value = value;
        return newEntry;
    }

    /**
     * Gets the information what this entry is about.
     *
     * @return the entry's purpose
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * Sets the information about this entry is about.
     *
     * @param purpose the information about this entry
     * @return the new entry with the specified information
     */
    public Entry setPurpose(String purpose) {
        Entry newEntry = new Entry(this);
        newEntry.purpose = purpose;
        return newEntry;
    }

    /**
     * Gets the latitude which can be used for geo-positioning.
     *
     * @return the location's latitude where this entry was created
     */
    public Float getLocLat() {
        return locLat;
    }

    /**
     * Sets the longitude and latitude of this entry for geo-positioning.
     *
     * @param locLng the location's longitude
     * @param locLat the location's latitude
     * @return the entry with the specified longitude and latitude.
     */
    public Entry setLngLat(Float locLng, Float locLat) {
        if (locLat == null ^ locLng == null)
            throw new IllegalArgumentException("Both locLat and locLng either have to be null or have a value");
        Entry newEntry = new Entry(this);
        newEntry.locLat = locLat;
        newEntry.locLng = locLng;
        return newEntry;
    }

    /**
     * Gets the longitude where this entry was created.
     *
     * @return the location's longitude where this entry was created
     */
    public Float getLocLng() {
        return locLng;
    }

    /**
     * Gets the interval for this entry.
     *
     * @return the entry's interval
     */
    public int getInterval() {
        return interval;
    }

    /**
     * A enum used to determine in which kind of intervals a entry is repeated.
     */
    public enum IntervalType {
        Monthly, Daily, Once
    }

    /**
     * Sets interval of an entry.
     *
     * @param type            The type of the interval
     * @param interval        Ignored if interval type is ONCE. The amount of types between repetition.
     * @param intervalEndTime Can be NULL, the end time of the interval
     * @return the new entry with the specified interval
     */
    public Entry setInterval(IntervalType type, int interval, Date intervalEndTime) {
        if (type == null)
            throw new NullPointerException("type must not be NULL");
        if (interval <= 0 && type != IntervalType.Once)
            throw new IllegalArgumentException("Interval must be greater than zero");
        Entry newEntry = new Entry(this);

        newEntry.intervalType = type;
        if (type != IntervalType.Once)
            newEntry.interval = interval;
        else
            newEntry.interval = -1;
        newEntry.intervalEndTime = intervalEndTime;
        return newEntry;
    }

    /**
     * Gets the {@link at.jku.win.ss15.pjse.backend.Entry.IntervalType} of the entry.
     *
     * @return the type of the entry's interval
     */
    public IntervalType getIntervalType() {
        return intervalType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entry entry = (Entry) o;

        if (interval != entry.interval) return false;
        if (endTime != null ? !endTime.equals(entry.endTime) : entry.endTime != null) return false;
        if (intervalEndTime != null ? !intervalEndTime.equals(entry.intervalEndTime) : entry.intervalEndTime != null)
            return false;
        if (intervalType != entry.intervalType) return false;
        if (locLat != null ? !locLat.equals(entry.locLat) : entry.locLat != null) return false;
        if (locLng != null ? !locLng.equals(entry.locLng) : entry.locLng != null) return false;
        if (purpose != null ? !purpose.equals(entry.purpose) : entry.purpose != null) return false;
        if (!startTime.equals(entry.startTime)) return false;
        if (!value.equals(entry.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + (purpose != null ? purpose.hashCode() : 0);
        result = 31 * result + (locLat != null ? locLat.hashCode() : 0);
        result = 31 * result + (locLng != null ? locLng.hashCode() : 0);
        result = 31 * result + interval;
        result = 31 * result + (intervalType != null ? intervalType.hashCode() : 0);
        result = 31 * result + startTime.hashCode();
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (intervalEndTime != null ? intervalEndTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Expenditure: ");
        sb.append(value).append(' ');
        if (purpose != null)
            sb.append("for:\n").append(purpose).append(' ');
        sb.append("\nStart time: ").append(startTime);
        if (endTime != null)
            sb.append("-").append(endTime);
        if (locLng != null && locLat != null)
            sb.append("\n@LNG:").append(locLng).append(" LAT:").append(locLat);
        if (intervalType != IntervalType.Once)
            sb.append("\nrepeating every ").append(interval).append(' ').append(intervalType).append(" until ").append(intervalEndTime);
        return sb.toString();
    }
}
