package at.jku.win.ss15.pjse.backend;



import java.sql.SQLException;
import java.util.List;

/**
 * The <code> DataProvider </code> provides operations for database access, as well as setting up the database itself.
 */
public interface DataProvider {
    /**
     * Initialises the database.
     *
     * @throws java.sql.SQLException if raised during database access
     */
    void initDB() throws SQLException;

    /**
     * Gets all the saved categories.
     *
     * @return a {@link java.util.List} of type {@link at.jku.win.ss15.pjse.backend.Category}
     * @throws java.sql.SQLException if raised during database operation
     */
    List<Category> getAllCategories() throws SQLException;

    /**
     * Gets all entries for a specific category.
     *
     * @param categoryName the {@link at.jku.win.ss15.pjse.backend.Category} from which the entries should be retrieved from
     * @return a {@link java.util.List} containing the entries
     * @throws java.sql.SQLException if raised during database access
     */
    List<Entry> getAllEntries(String categoryName) throws SQLException;

    /**
     * Gets a specific category, identified by a name.
     *
     * @param name the name of the {@link Category}
     * @return the category identified by a string
     * @throws java.sql.SQLException if raised during a database access
     */
    Category getCategory(String name) throws SQLException;

    /**
     * Gets all entries from a specific category.
     *
     * @param category the {@link Category} containing the entries
     * @return the entries in the specified category
     * @throws java.sql.SQLException
     */
    List<Entry> getAllEntries(Category category) throws SQLException;

    /**
     * Adds a new {@link at.jku.win.ss15.pjse.backend.BudgetChangedListener} .
     *
     * @param l
     * @throws java.sql.SQLException if raised during database access
     */
    void addBudgetChangedListener(BudgetChangedListener l) throws SQLException;

    /**
     * Removes a new {@link BudgetChangedListener} .
     *
     * @param l
     * @throws java.sql.SQLException if raised during database access
     */
    void removeBudgetChangedListener(BudgetChangedListener l) throws SQLException;

    /**
     * Adds a {@link at.jku.win.ss15.pjse.backend.Category} to the database.
     *
     * @param c the category to be added
     * @throws java.sql.SQLException if raised during database operation
     */
    void addCategory(Category c) throws SQLException;

    /**
     * Removes a {@link at.jku.win.ss15.pjse.backend.Category} from the database
     *
     * @param c the category to be removed
     * @throws java.sql.SQLException if raised during database operation
     */
    void removeCategory(Category c) throws SQLException;

    /**
     * Removes a {@link at.jku.win.ss15.pjse.backend.Category} from the database, identified by a String.
     *
     * @param categoryName the category's name
     * @throws java.sql.SQLException - if raised during database operation
     */
    void removeCategory(String categoryName) throws SQLException;

    /**
     * Updates a {@link at.jku.win.ss15.pjse.backend.Category} in the database.
     *
     * @param c the category to be updated
     * @throws java.sql.SQLException if raised during database operation
     */
    void updateCategory(Category c) throws SQLException;

    /**
     * Adds a new {@link Entry} to the database.
     *
     * @param e the entry to be added
     * @throws java.sql.SQLException if raised during database operation
     */
    void addEntry(Entry e) throws SQLException;

    /**
     * Removes an {@link Entry} from the database.
     *
     * @param e the entry to be removed
     * @throws java.sql.SQLException
     */
    void removeEntry(Entry e) throws SQLException;

    /**
     * Updates a specific {@link Entry}.<br>
     * Default: adds the new entry and deletes the old.
     *
     * @param oldEntry the entry to be updated
     * @param newEntry the updated entry to be written in the database
     * @throws java.sql.SQLException if raised during database operation
     */
    void updateEntry(Entry oldEntry, Entry newEntry) throws SQLException;

}