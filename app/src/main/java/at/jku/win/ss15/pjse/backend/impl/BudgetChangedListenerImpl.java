package at.jku.win.ss15.pjse.backend.impl;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import at.jku.win.ss15.pjse.backend.BudgetChangedListener;
import at.jku.win.ss15.pjse.backend.DataProvider;

/**
 * Created by Florian on 25.04.2015.
 */
public class BudgetChangedListenerImpl implements BudgetChangedListener {

    private SharedPreferences settings;

    private SharedPreferences.Editor getEditor() {
        return settings.edit();
    }

    public DataProvider addBudgetChangedListenerIfNoneAdded(DataProvider dataProvider, Activity a) {
        dataProvider.addBudgetChangedListener(getInstance(a));
        return dataProvider;
    }

    public DataProvider addBudgetChangedListenerIfNoneAdded(DataProvider dataProvider, Context context) {
        dataProvider.addBudgetChangedListener(getInstance(context));
        return dataProvider;
    }

    public DataProvider addBudgetChangedListenerIfNoneAdded(DataProvider dataProvider, SharedPreferences sharedPreferences) {
        dataProvider.addBudgetChangedListener(getInstance(sharedPreferences));
        return dataProvider;
    }

    private static BudgetChangedListener instance = null;

    public static BudgetChangedListener getInstance(SharedPreferences preferences) {
        if (instance == null)
            instance = new BudgetChangedListenerImpl(preferences);
        return instance;
    }

    public static BudgetChangedListener getInstance(Context context) {
        if (instance == null)
            instance = new BudgetChangedListenerImpl(context);
        return instance;
    }

    public static BudgetChangedListener getInstance(Activity activity) {
        return getInstance(activity.getApplicationContext());
    }

    public BudgetChangedListenerImpl(Context context) {
        this(context.getSharedPreferences("categoryBudgetChanged", 0));
    }

    public BudgetChangedListenerImpl(SharedPreferences preferences) {
        settings = preferences;
    }

    @Override
    public void budgetChanged(String category, Date time, BigDecimal from, BigDecimal to) {
        LogItem item = new LogItem();
        item.setBugget(to);
        item.setCategoryName(category);
        item.setDate(time);
        // getEditor().putString(Long.toString(time.getTime()), to.toString()).apply();
    }

    /**
     * This method is rather expensive
     *
     * @return
     */
    @Override
    public List<LogItem> getAllChanges() throws UnsupportedOperationException {
        Map<String, ?> all = settings.getAll();
        List<LogItem> logItems = new ArrayList<>();
        String s;
        for (Object o : all.values()) {
            if (!(o instanceof String))
                throw new UnsupportedOperationException(String.format("o should be of type %s but is %s. Error in log save!", String.class, o.getClass()));
            try {
                logItems.add(GsonHelper.deseralizeObject((String) o, LogItem.class));
            } catch (IOException | ClassNotFoundException e) {
                throw new UnsupportedOperationException("Log is untidy!", e);
            }
        }
        return Collections.unmodifiableList(logItems);
    }
}
