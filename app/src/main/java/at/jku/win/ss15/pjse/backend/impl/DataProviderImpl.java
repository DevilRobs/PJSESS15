package at.jku.win.ss15.pjse.backend.impl;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import at.jku.win.ss15.pjse.backend.BudgetChangedListener;
import at.jku.win.ss15.pjse.backend.Category;
import at.jku.win.ss15.pjse.backend.DataProvider;
import at.jku.win.ss15.pjse.backend.Entry;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class DataProviderImpl implements DataProvider {

    private static DataProvider instance = null;
    private final Gson gson = new Gson();

    public static DataProvider getInstance(Activity a) {
        return getInstance(a.getApplicationContext());
    }

    public static DataProvider getInstance(Context context) {
        if (instance == null)
            instance = new DataProviderImpl(context);
        return instance;
    }

    public static DataProvider getInstance(SharedPreferences a, SharedPreferences b, SharedPreferences c) {
        if (instance == null)
            instance = new DataProviderImpl(a, b, c);
        return instance;
    }


    private static final String CATEGORIES = "CATEGORIES";

    private DataProviderImpl(Context a) {
        this(a.getSharedPreferences("cat", 0), a.getSharedPreferences("ent", 0), a.getSharedPreferences("catEnt", 0));
    }

    private DataProviderImpl(SharedPreferences cat, SharedPreferences entries, SharedPreferences catEntRef) {
        catSettings = cat;
        entSettings = entries;
        catEntRelation = catEntRef;
    }

    private final SharedPreferences catSettings, entSettings, catEntRelation;

    private SharedPreferences.Editor getCategoryEditor() {
        return catSettings.edit();
    }

    private SharedPreferences.Editor getEntryEditor() {
        return entSettings.edit();
    }

    private SharedPreferences.Editor getCategoryEntryEditor() {
        return catEntRelation.edit();
    }

    @Override
    public List<Category> getAllCategories() throws DataProviderException {
        List<Category> list = new LinkedList<>();
        Set<String> set = catSettings.getStringSet(CATEGORIES, Collections.<String>emptySet());
        for (String s : set) {
            try {
                list.add(deseralizeObject(catSettings.getString(s, null), Category.class));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public List<Entry> getAllEntries(String categoryName) throws DataProviderException {
        List<Entry> entries = new LinkedList<>();
        Set<String> set = catEntRelation.getStringSet(categoryName, Collections.<String>emptySet());
        for (String s : set) {
            try {
                entries.add(deseralizeObject(entSettings.getString(s, null), Entry.class));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return Collections.unmodifiableList(entries);
    }

    @Override
    public Category getCategory(String name) throws DataProviderException {
        try {
            return deseralizeObject(catSettings.getString(name, null), Category.class);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Entry> getAllEntries(Category category) throws DataProviderException {
        return getAllEntries(category.getName());
    }

    List<BudgetChangedListener> budgetChangedListeners = null;

    @Override
    public void addBudgetChangedListener(BudgetChangedListener l) throws DataProviderException {
        if (budgetChangedListeners == null)
            budgetChangedListeners = new ArrayList<>();
        budgetChangedListeners.add(l);
    }

    @Override
    public void removeBudgetChangedListener(BudgetChangedListener l) throws DataProviderException {
        if (budgetChangedListeners != null)
            budgetChangedListeners.remove(l);
    }

    @Override
    public void addCategory(Category c) throws DataProviderException {
        if (c.getName().equals(CATEGORIES))
            throw new DataProviderException("This name is not supported");
        SharedPreferences.Editor editor = getCategoryEditor();
        try {
            Set<String> allCategories = catSettings.getStringSet(CATEGORIES, null);
            if (allCategories == null) {
                allCategories = new HashSet<>();
            }
            if (allCategories.contains(c.getName()))
                throw new DataProviderException("A category with this name already exists!");

            editor.putString(c.getName(), seralizeObject(c));
            allCategories.add(c.getName());
            editor.putStringSet(CATEGORIES, allCategories);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            editor.apply();
        }
    }


    @Override
    public void removeCategory(Category c) throws DataProviderException {
        removeCategory(c.getName());
    }

    @Override
    public void removeCategory(String categoryName) throws DataProviderException {
        Set<String> set = catSettings.getStringSet(CATEGORIES, Collections.<String>emptySet());
        set.remove(getCategory(categoryName));

        getCategoryEditor().remove(categoryName).putStringSet(CATEGORIES, set).apply();
    }

    @Override
    public void updateCategory(Category c) throws DataProviderException {
        Category old;
        try {
            old = deseralizeObject(catSettings.getString(c.getName(), null), Category.class);
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new DataProviderException("Category not found!", e);
        }
        try {
            getCategoryEditor().putString(c.getName(), seralizeObject(c)).apply();
        } catch (IOException e) {
            throw new DataProviderException("Update could not be performed!");
        }
        BigDecimal bo = old.getBudget(), bn = c.getBudget();
        Date now = new Date(System.currentTimeMillis());
        for (BudgetChangedListener l : budgetChangedListeners)
            l.budgetChanged(now, bo, bn);

    }

    @Override
    public void addEntry(Entry e) throws DataProviderException {
        String id = getIDfrom(e);
        if (entSettings.getString(id, null) != null)
            throw new DataProviderException("This entry already exists in the database");
        try {
            getEntryEditor().putString(id, seralizeObject(e)).apply();
        } catch (IOException e1) {
            throw new DataProviderException("Entry could not be added!", e1);
        }
    }

    @Override
    public void removeEntry(Entry e) throws DataProviderException {
        getEntryEditor().remove(getIDfrom(e)).apply();
    }

    @Override
    public void updateEntry(Entry oldEntry, Entry newEntry) throws DataProviderException {
        removeEntry(oldEntry);
        addEntry(newEntry);
    }

    @Override
    public void reset() throws DataProviderException {
        getEntryEditor().clear().apply();
        getCategoryEditor().clear().apply();
        getCategoryEntryEditor().clear().apply();
    }


    private String seralizeObject(Object o) throws IOException {
       /* ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(o);
        oos.close();
        return os.toString(String.valueOf(StandardCharsets.UTF_8));*/
        return gson.toJson(o);
    }

    private <T> T deseralizeObject(String o, Class<T> tClass) throws IOException, ClassNotFoundException {
        // return new ObjectInputStream(new ByteArrayInputStream(o.getBytes(String.valueOf(StandardCharsets.UTF_8)))).readObject();
        return gson.fromJson(o, tClass);
    }

    private String getIDfrom(Entry e) {
        return Integer.toHexString(e.hashCode());
    }
}
