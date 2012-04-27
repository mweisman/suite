package org.opengeo.data.importer.web;

import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.web.data.store.DataAccessEditPage;

public class DataStoreEditPage extends DataAccessEditPage {

    public DataStoreEditPage(DataStoreInfo store) {
        super(store);
    }

    @Override
    protected void doSaveStore(DataStoreInfo info) {
        if (info.getId() != null) {
            super.doSaveStore(info);
        }

        //do nothing, not part of catalog yet
    }
}
