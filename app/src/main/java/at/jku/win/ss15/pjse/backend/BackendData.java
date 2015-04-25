package at.jku.win.ss15.pjse.backend;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BackendData implements Serializable {

    public List<Entry> entries;
    public List<Category> categories;
    public List<BudgetChangedListener.LogItem> logItems;

    public String export() {
        return exportBackendData(this);
    }

    public static String exportBackendData(BackendData backendData) {
        Gson gson = new Gson();
        return gson.toJson(backendData);
    }

    public static BackendData importBackendData(String exported) {
        Gson gson = new Gson();
        return gson.fromJson(exported, BackendData.class);
    }

    public void fillDataProvider(DataProvider provider) throws DataProvider.DataProviderException {
        provider.reset();
        for (Category c : categories)
            provider.addCategory(c);
        for (Entry e : entries)
            provider.addEntry(e);
    }

    public static BackendData fromBackend(DataProvider dataProvider, BudgetChangedListener listener) throws DataProvider.DataProviderException {
        BackendData data = new BackendData();
        data.categories = dataProvider.getAllCategories();
        data.entries = new ArrayList<>();
        for (Category category : data.categories)
            data.entries.addAll(dataProvider.getAllEntries(category));
        data.categories = Collections.unmodifiableList(data.categories);
        data.entries = Collections.unmodifiableList(data.entries);
        try {
            data.logItems = Collections.unmodifiableList(listener.getAllChanges());
        } catch (UnsupportedOperationException uoe) {
            data.logItems = null;
        }
        return data;
    }
}
