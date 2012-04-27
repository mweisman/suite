package org.opengeo.data.importer.web;

import org.geoserver.catalog.CoverageStoreInfo;

public class CoverageStoreEditPage extends
        org.geoserver.web.data.store.CoverageStoreEditPage {

    public CoverageStoreEditPage(CoverageStoreInfo store) {
        super(store);
    }

    @Override
    protected void doSaveStore(CoverageStoreInfo info) {
        if (info.getId() != null) {
            super.doSaveStore(info);
        }
        //do nothing, not part of catalog yet
    }
}
