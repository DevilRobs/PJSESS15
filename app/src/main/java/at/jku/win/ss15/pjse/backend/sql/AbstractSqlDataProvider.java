package at.jku.win.ss15.pjse.backend.sql;


//import at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.BudgetChangedListener;
//import at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.Category;
//import at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.DataProvider;
//import at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.Entry;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import at.jku.win.ss15.pjse.backend.BudgetChangedListener;
import at.jku.win.ss15.pjse.backend.Category;
import at.jku.win.ss15.pjse.backend.Currency;
import at.jku.win.ss15.pjse.backend.DataProvider;
import at.jku.win.ss15.pjse.backend.Entry;

public abstract class AbstractSqlDataProvider implements DataProvider {
    /**
     * Gets the driver's name.
     *
     * @return the name of the driver's class
     */
    protected abstract String getDriverName();

    /**
     * A String used as JDBC connection URL.
     *
     * @return the valid JDBC connection URL
     */
    protected abstract String getJdbcConnectionUrl();

    /**
     * Gets a JDBC connection. If supported, Auto-commit is set to false.
     *
     * @return A JDBC-connection to the database
     * @throws java.sql.SQLException  If a problem occured during connecting to the database
     * @throws ClassNotFoundException If the JDBC driver could not be loaded
     */
    protected final Connection getConnection() throws SQLException {
        try {
            Class.forName(getDriverName());
        } catch (ClassNotFoundException cnfe) {
            throw new SQLException("JDBC-Driver was not found", cnfe);
        }

        Connection c = DriverManager.getConnection(getJdbcConnectionUrl());
        try {
            c.setAutoCommit(false);
        } catch (SQLException e) {
        }
        return c;
    }

    private final static String dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";
    private final static SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);

    private String localDateTimeToString(Date date) {
        if (date == null)
            return null;
        return dateFormatter.format(date);
    }

    private Date stringToLocalDateTime(String dateString) {
        if (dateString == null)
            return null;
        try {
            return dateFormatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Initialises the database.
     *
     * @throws java.sql.SQLException if raised during database operation
     */
    @Override
    public final void initDB() throws SQLException {
        String sql = getInitDBSql();
        try (Connection c = getConnection();
             Statement s = c.createStatement()) {
            s.executeUpdate(sql);
            c.commit();
        }
    }

    /**
     * Retrieves all { at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.Category}
     * objects from the database and returns them as a {@link java.util.List}.
     *
     * @return all categories as a <code>List</code>
     * @throws java.sql.SQLException if raised during database access
     */
    @Override
    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new LinkedList<>();
        String name, currency;
        BigDecimal budget;
        try (Connection c = getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(getAllCategoriesSql())) {
            while (r.next()) {
                name = r.getString("name");
                currency = r.getString("currency");
                budget = r.getBigDecimal("budget");
                if (r.wasNull())
                    budget = null;
                Category category = new Category(name, Currency.valueOf(currency));
                category.setBudget(budget);
                categories.add(category);
            }
            c.commit();
        }

        return Collections.unmodifiableList(categories);
    }

    /**
     * Retrieves all { at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.Entry}
     * objects from the database and returns them as a {@link java.util.List}.
     *
     * @return all entries as a <code>List</code>
     * @throws java.sql.SQLException if raised during database access
     */
    @Override
    public final List<Entry> getAllEntries(String categoryName) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(getAllEntriesSql())) {
            p.setString(1, categoryName);
            try (ResultSet r = p.executeQuery()) {
                List<Entry> list = getEntriesFromResultSet(r);
                c.commit();
                return list;
            }
        }
    }

    /**
     * Gets a { at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.Category} with the specified name from the database.
     *
     * @param name the name of the {@link Category}
     * @return the category specified by the name
     * @throws java.sql.SQLException if raised during database access
     */
    @Override
    public final Category getCategory(String name) throws SQLException {
        String currency;
        BigDecimal budget;
        try (Connection c = getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(getAllCategoriesSql())) {
            if (r.next()) {
                name = r.getString("name");
                currency = r.getString("currency");
                budget = r.getBigDecimal("budget");
                if (r.wasNull())
                    budget = null;
                Category category = new Category(name, Currency.valueOf(currency));
                category.setBudget(budget);
                c.commit();
                return category;
            } else {
                c.commit();
                return null;
            }
        }
    }

    @Override
    public List<Entry> getAllEntries(Category category) throws SQLException {
        return getAllEntries(category.getName());
    }

    /**
     * Retrieves every { at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.Entry} from a
     * <code>JDBC</code> {@link java.sql.ResultSet} and adds it to a {@link java.util.List}.
     *
     * @param r the <code>ResultSet</code> to get the <code>entries</code> from
     * @return the <code>List</code> containing all <code>entries</code> from the <code>ResultSet</code>
     * @throws java.sql.SQLException if raised during database access
     */
    private final List<Entry> getEntriesFromResultSet(ResultSet r) throws SQLException {
        List<Entry> entries = new LinkedList<>();
        BigDecimal value;
        String purpose, startTime, endTime, intervalEndTime, category, intervalType;
        Float locLng, locLat;
        int interval;
        while (r.next()) {
            value = r.getBigDecimal(1);
            purpose = r.getString(2);
            if (r.wasNull())
                purpose = null;
            startTime = r.getString(3);
            endTime = r.getString(4);
            if (r.wasNull())
                endTime = null;
            locLng = r.getFloat(5);
            if (r.wasNull())
                locLng = null;
            locLat = r.getFloat(6);
            if (r.wasNull())
                locLat = null;
            interval = r.getInt(7);
            intervalType = r.getString(8);
            if (r.wasNull())
                intervalType = null;
            intervalEndTime = r.getString(9);
            if (r.wasNull())
                intervalEndTime = null;
            category = r.getString(10);

            Entry e = new Entry(value, stringToLocalDateTime(startTime), category);
            e = e.setPurpose(purpose).setEndTime(stringToLocalDateTime(endTime)).setLngLat(locLng, locLat);
            Entry.IntervalType type = Entry.IntervalType.Once;
            if (intervalType != null) {
                if ("M".equalsIgnoreCase(intervalType))
                    type = Entry.IntervalType.Monthly;
                else
                    type = Entry.IntervalType.Daily;
            }
            e = e.setInterval(type, interval, stringToLocalDateTime(intervalEndTime));
            entries.add(e);
        }
        return Collections.unmodifiableList(entries);
    }

    List<BudgetChangedListener> listeners = new ArrayList<>(1);

    /**
     * Adds a { at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.BudgetChangedListener}.
     *
     * @param l the BudgetChangedListener to be added
     */
    @Override
    public final void addBudgetChangedListener(BudgetChangedListener l) {
        listeners.add(l);
    }

    /**
     * Removes a { at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.BudgetChangedListener}.
     *
     * @param l the BudgetChangedListener to be removed
     */
    @Override
    public final void removeBudgetChangedListener(BudgetChangedListener l) {
        listeners.remove(l);
    }

    /**
     * Adds a { at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.Category} to the database.
     *
     * @param c the category to be added
     * @throws java.sql.SQLException if raised during database operation
     */
    @Override
    public final void addCategory(Category c) throws SQLException {
        try (Connection con = getConnection();
             PreparedStatement p = con.prepareStatement(getAddCategorySql())) {
            p.setString(1, c.getName());
            p.setBigDecimal(2, c.getBudget());
            p.setString(3, c.getCurrency().toString());
            p.executeUpdate();
            con.commit();
        }
    }

    @Override
    public void removeCategory(Category c) throws SQLException {
        removeCategory(c.getName());
    }

    /**
     * Removes a specific { at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.Category} from the database.
     *
     * @param categoryName the category's name
     * @throws java.sql.SQLException if raised during database operation
     */
    @Override
    public final void removeCategory(String categoryName) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(getDeleteCategorySql())) {
            p.setString(1, categoryName);
            p.executeUpdate();
            c.commit();
        }
    }

    /**
     * Updates a specific category in the database.
     *
     * @param c the { at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.Category} to be updated
     * @throws java.sql.SQLException if raised during database operation.
     */
    @Override
    public final void updateCategory(Category c) throws SQLException {
        try (Connection con = getConnection();
             PreparedStatement p = con.prepareStatement(getUpdateCategorySql())) {
            p.setBigDecimal(1, c.getBudget());
            p.setString(2, c.getName());
            p.executeUpdate();
            con.commit();
        }
    }

    /**
     * Adds an { at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.Entry} to the database.
     *
     * @param e the entry to be added
     * @throws java.sql.SQLException if raised during database operation
     */
    @Override
    public final void addEntry(Entry e) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(getAddEntriesSql())) {
            p.setBigDecimal(1, e.getValue());
            p.setString(2, e.getPurpose());
            p.setString(3, localDateTimeToString(e.getStartTime()));
            p.setString(4, localDateTimeToString(e.getEndTime()));
            p.setObject(5, e.getLocLng(), Types.FLOAT);
            p.setObject(6, e.getLocLat(), Types.FLOAT);
            p.setInt(7, e.getInterval());
            p.setString(8, getSQLStringFromIntervalType(e.getIntervalType()));
            p.setString(9, localDateTimeToString(e.getIntervalEndTime()));
            p.setString(10, e.getCategoryName());
            p.executeUpdate();
            c.commit();
        }
    }

    private String getSQLStringFromIntervalType(Entry.IntervalType type) {
        if (type == Entry.IntervalType.Once) return null;
        else {
            String s = null;
            switch (type) {
                case Monthly:
                    s = "M";
                    break;
                default:
                    s = "D";
                    break;
            }
            return s;
        }
    }

    /**
     * Removes an { at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.Entry} from the database.
     *
     * @param e the entry to be removed
     * @throws java.sql.SQLException if raised during database operation
     */
    @Override
    public final void removeEntry(Entry e) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(getDeleteEntrySql())) {
            p.setBigDecimal(1, e.getValue());
            p.setString(2, e.getPurpose());
            p.setString(3, e.getPurpose());
            p.setString(4, localDateTimeToString(e.getStartTime()));
            p.setString(5, localDateTimeToString(e.getEndTime()));
            p.setString(6, localDateTimeToString(e.getEndTime()));
            p.setObject(7, e.getLocLng(), Types.FLOAT);
            p.setObject(8, e.getLocLng(), Types.FLOAT);
            p.setObject(9, e.getLocLat(), Types.FLOAT);
            p.setObject(10, e.getLocLat(), Types.FLOAT);
            p.setInt(11, e.getInterval());
            p.setString(12, getSQLStringFromIntervalType(e.getIntervalType()));
            p.setString(13, getSQLStringFromIntervalType(e.getIntervalType()));
            p.setString(14, localDateTimeToString(e.getIntervalEndTime()));
            p.setString(15, localDateTimeToString(e.getIntervalEndTime()));
            p.setString(16, e.getCategoryName());
            p.executeUpdate();
            c.commit();
        }
    }

    @Override
    public void updateEntry(Entry oldEntry, Entry newEntry) throws SQLException {
        addEntry(newEntry);
        try {
            removeEntry(oldEntry);
        } catch (SQLException e) {
            try {
                removeEntry(newEntry);
            } catch (SQLException e1) {
                e.addSuppressed(e1);
            }
            throw e;
        }
    }

    /**
     * Triggers the event of the budget listener.
     *
     * @param dateTime the timestamp of the change event
     * @param from     the current budget value
     * @param to       the new budget value to set
     */
    protected final void fireBudgetChangedListener(Date dateTime, BigDecimal from, BigDecimal to) {
        for (BudgetChangedListener l : listeners)
            l.budgetChanged(dateTime, from, to);
    }

    /**
     * Gets the SQL String for database initialisation.
     *
     * @return the database initialisation String
     */
    protected abstract String getInitDBSql();

    /**
     * Retrieves all the categories with their column name and the appropriate data type.
     *
     * @return a valid SQL query which returns the 3 columns, "name","budget","currency" in the appropriate data type
     */
    protected abstract String getAllCategoriesSql();

    /**
     * Gets a String which can add an new entry to the database.<br>
     * The fields are in the following order:
     * <ol>
     * <li>value</li>
     * <li>purpose</li>
     * <li>startTime</li>
     * <li>endTime</li>
     * <li>locLng</li>
     * <li>locLat</li>
     * <li>interval</li>
     * <li>intervalType</li>
     * <li>intervalEndTime</li>
     * <li>category</li>
     * </ol>
     *
     * @return A JDBC {@link java.sql.PreparedStatement} String which can add an Entry
     */
    protected abstract String getAddEntriesSql();

    /**
     * Gets a String which can retrieve all entries from the database.
     * An additional JDBC parameter for the categories is required in the where clause <br>
     * The fields are in the following order:
     * <ol>
     * <li>value</li>
     * <li>purpose</li>
     * <li>startTime</li>
     * <li>endTime</li>
     * <li>locLng</li>
     * <li>locLat</li>
     * <li>interval</li>
     * <li>intervalType</li>
     * <li>intervalEndTime</li>
     * <li>category</li>
     * </ol>
     *
     * @return
     */
    protected abstract String getAllEntriesSql();

    /**
     * Gets a String which can be used for adding a { at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.Category} to the database.<br>
     * The fields are in the following order:
     * <ol>
     * <li>name</li>
     * <li>budget</li>
     * <li>currency</li>
     * </ol>
     *
     * @return a String which can add categories to the database
     */
    protected abstract String getAddCategorySql();

    /**
     * Gets a String which can be used for deleting a { at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.Category}.
     *
     * @return a valid prepared statement Sql String with 1 JDBC argument (name)
     */
    protected abstract String getDeleteCategorySql();

    /**
     * Gets a String which can be used for updating a { at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.Category}.<br>
     * The parameter list for the Statement is as follows:
     * <ol>
     * <li>budget</li>
     * <li>name</li>
     * </ol>
     *
     * @return a valid {@link java.sql.PreparedStatement}-SQL-String with parameters as mentioned above
     */
    protected abstract String getUpdateCategorySql();

    /**
     * Gets a String which can be used for deleting an { at.jku.win.pjsess15.at.jku.win.ss15.pjse.backend.Entry} from the database.<br>
     * The order of the fields in the where-clause is as follows:
     * <ol>
     * <li>value</li>
     * <li>purpose</li>
     * <li>purpose</li>
     * <li>startTime</li>
     * <li>endTime</li>
     * <li>locLng</li>
     * <li>locLng</li>
     * <li>locLat</li>
     * <li>LocLat</li>
     * <li>interval</li>
     * <li>intervalType</li>
     * <li>intervalType</li>
     * <li>intervalEndTime</li>
     * <li>intervalEndTime</li>
     * <li>category</li>
     * </ol>
     *
     * @return a String which can be used for deleting an <code>Entry</code>
     */
    protected abstract String getDeleteEntrySql();
}
