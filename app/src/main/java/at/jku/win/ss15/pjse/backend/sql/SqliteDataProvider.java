package at.jku.win.ss15.pjse.backend.sql;


//import com.se.cashmanagement.app.at.jku.win.ss15.pjse.backend.RessourceHelper;

import java.io.File;

/**
 * The <code>SqliteDataProvider</code> provides various methods returning SQL Strings for
 * adding to, deleting from and updating the database. These Strings can be used in
 * {@link java.sql.Statement}<code>s</code> or {@link java.sql.PreparedStatement}<code>s</code>. If additional
 * parameters are required then the parameters will be listed in the documentation of
 * the respective method.
 */
public class SqliteDataProvider extends AbstractSqlDataProvider {

    private static SqliteDataProvider instance = null;

    /**
     * Gets a the SqliteDataProvider to be used for database modification.
     *
     * @param dataBaseLocation the directory location of the database
     * @return a <code>SqliteDataProvider</code> which can be used for database access
     */
    public static SqliteDataProvider getSqlDataProvider(File dataBaseLocation) {
        if (instance == null)
            instance = new SqliteDataProvider(dataBaseLocation);
        return instance;
    }

    private final String dataBaseLocation;

    private SqliteDataProvider(File dataBaseLocation) {
        this.dataBaseLocation = dataBaseLocation.toString();
    }

    /**
     * Gets the database driver needed for database access.
     *
     * @return the database driver as a String
     */
    @Override
    protected String getDriverName() {
        return "org.sqldroid.SQLDroidDriver";
    }

    /**
     * Gets the name of the connection needed to establish a connection to the database
     *
     * @return the URL needed for connecting to the database
     */
    @Override
    protected String getJdbcConnectionUrl() {
        return "jdbc:sqlite:" + dataBaseLocation;
    }

    /**
     * Gets a SQL script which can be used for setting up the database.
     *
     * @return the initialisation script of the database
     */
    @Override
    protected String getInitDBSql() {
        return "create table if not exists Category(\n" +
                "name text primary key," +
                "budget DECIMAL(10,2)," +
                "currency character(3) not null" +
                ");" +
                "create table if not exists Entry(" +
                "value DECIMAL(10,2) not null," +
                "purpose text," +
                "startTime character(23) not null," +
                "endTime character(23)," +
                "locLng real," +
                "locLat real," +
                "interval integer not null," +
                "intervalType character(1) check(intervalType ='M' or intervalType ='D' OR intervalType is null)," +
                "intervalEndTime character(23)," +
                "category text references Category(name) on delete cascade" +
                ");" +
                "create index if not exists entry_category on Entry(category);";
    }

    /**
     * Gets a String which can be used for requesting all categories from the database.
     *
     * @return a SQL String for retrieving all categories
     */
    @Override
    protected String getAllCategoriesSql() {
        return "select name,budget,currency from Category";
    }

    /**
     * Gets a SQL String that can be used to add new entries to the database.<br>
     * The parameter list for the {@link java.sql.PreparedStatement} is as follows:
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
     * @return a SQL String to be used for adding a new {@link com.se.cashmanagement.app.backend.Entry} to the database
     */
    @Override
    protected String getAddEntriesSql() {
        return "insert into Entry(value,purpose,startTime,endTime,locLng,locLat,interval,intervalType,intervalEndTime,category) VALUES(?,?,?,?,?,?,?,?,?,?)";
    }

    /**
     * Gets a SQL String to be used for retrieving every {@link com.se.cashmanagement.app.backend.Entry}
     * from a specified {@link com.se.cashmanagement.app.backend.Category}. <br>
     * The parameter category has to be inserted in the {@link java.sql.PreparedStatement}:
     * <ul>
     * <li>category</li>
     * </ul>
     *
     * @return a SQL String for getting all entries from a category
     */
    @Override
    protected String getAllEntriesSql() {
        return "select value,purpose,startTime,endTime,locLng,locLat,interval,intervalType,intervalEndTime,category from Entry where category = ?";
    }

    /**
     * Gets a String that adds a new {@link com.se.cashmanagement.app.backend.Category} to the database.
     * The required parameters for the category are:
     * <ol>
     * <li>name</li>
     * <li>budget</li>
     * <li>currency</li>
     * </ol>
     *
     * @return
     */
    @Override
    protected String getAddCategorySql() {
        return "INSERT into Category(name,budget,currency) VALUES(?,?,?)";
    }

    /**
     * Gets a SQL String to be used for deleting a certain category from the database.<br>
     * The parameter for the {@link java.sql.PreparedStatement} is the name of the category:
     * <ul>
     * <li>name</li>
     * </ul>
     *
     * @return a SQL String to be used for deleting a {@link com.se.cashmanagement.app.backend.Category}
     */
    @Override
    protected String getDeleteCategorySql() {
        return "delete from category where name = ?";
    }

    /**
     * Gets a SQL String which can be used for updating a {@link com.se.cashmanagement.app.backend.Category}.<br>
     * The parameters are:
     * <ol>
     * <li>budget (the new budget)</li>
     * <li>category-name (to be updated)</li>
     * </ol>
     *
     * @return a SQL String to be used for updating a Category in the database
     */
    @Override
    protected String getUpdateCategorySql() {
        return "update category set budget=? where name = ?";
    }

    /**
     * Gets a SQL String for deleting an {@link com.se.cashmanagement.app.backend.Entry} from
     * a {@link com.se.cashmanagement.app.backend.Category} in the database.<br>
     * The parameters for the {@link java.sql.PreparedStatement} are as follows:
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
     * @return a SQL String to be used for deleting an <code>Entry</code>
     */
    @Override
    protected String getDeleteEntrySql() {
        return "delete from entry where value = ? AND (? is null OR  purpose =?) AND startTime = ? AND (? is null OR  endTime =?)" +
                "AND (? is null OR  locLng =?) AND (? is null OR  locLat =?) AND interval = ? AND (? is null OR  intervalType =?)" +
                "AND (? is null OR  intervalEndTime =?) AND category =?";
    }

}
