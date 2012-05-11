package org.opengeo.data.importer;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.h2.H2DataStoreFactory;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;


public class ImporterDataTest extends ImporterTestSupport {

    public void testImportShapefile() throws Exception {
        File dir = unpack("shape/archsites_epsg_prj.zip");
        
        ImportContext context = 
                importer.createContext(new SpatialFile(new File(dir, "archsites.shp")));
        assertEquals(1, context.getTasks().size());
        
        ImportTask task = context.getTasks().get(0);
        assertEquals(ImportTask.State.READY, task.getState());
        assertEquals(1, task.getItems().size());
        
        ImportItem item = task.getItems().get(0);
        assertEquals(ImportItem.State.READY, item.getState());
        assertEquals("archsites", item.getLayer().getResource().getName());

        importer.run(context);
        
        Catalog cat = getCatalog();
        assertNotNull(cat.getLayerByName("archsites"));

        assertEquals(ImportTask.State.COMPLETE, task.getState());
        assertEquals(ImportItem.State.COMPLETE, item.getState());

        runChecks("archsites");
    }

    public void testImportShapefilesWithExtraFiles() throws Exception {
        File dir = tmpDir();
        unpack("shape/archsites_epsg_prj.zip", dir);
        
        // make some 'extra' files
        new File(dir,"archsites.sbn").createNewFile();
        new File(dir,"archsites.sbx").createNewFile();
        new File(dir,"archsites.shp.xml").createNewFile();
        
        ImportContext context = importer.createContext(new Directory(dir));
        
        assertEquals(1, context.getTasks().size());
        
        ImportTask task = context.getTasks().get(0);
        assertEquals(ImportTask.State.READY, task.getState());
        
        assertEquals(ImportItem.State.READY, task.getItems().get(0).getState());
        assertEquals("archsites", task.getItems().get(0).getLayer().getResource().getName());
    }
    
    public void testImportShapefiles() throws Exception {
        File dir = tmpDir();
        unpack("shape/archsites_epsg_prj.zip", dir);
        unpack("shape/bugsites_esri_prj.tar.gz", dir);
        
        ImportContext context = importer.createContext(new Directory(dir));
        assertEquals(1, context.getTasks().size());
        
        ImportTask task = context.getTasks().get(0);
        assertEquals(ImportTask.State.READY, task.getState());
        assertEquals(2, task.getItems().size());
        
        assertEquals(ImportItem.State.READY, task.getItems().get(0).getState());
        assertEquals("archsites", task.getItems().get(0).getLayer().getResource().getName());

        assertEquals(ImportItem.State.READY, task.getItems().get(1).getState());
        assertEquals("bugsites", task.getItems().get(1).getLayer().getResource().getName());

        importer.run(context);
        
        Catalog cat = getCatalog();
        assertNotNull(cat.getLayerByName("archsites"));
        assertNotNull(cat.getLayerByName("bugsites"));
        
        assertEquals(ImportTask.State.COMPLETE, task.getState());
        assertEquals(ImportItem.State.COMPLETE, task.getItems().get(0).getState());
        assertEquals(ImportItem.State.COMPLETE, task.getItems().get(1).getState());
        
        runChecks("archsites");
        runChecks("bugsites");
    }

    public void testImportShapefilesWithError() throws Exception {
        File dir = tmpDir();
        unpack("shape/archsites_no_crs.zip", dir);
        unpack("shape/bugsites_esri_prj.tar.gz", dir);

        ImportContext context = importer.createContext(new Directory(dir));
        assertEquals(1, context.getTasks().size());

        ImportTask task = context.getTasks().get(0);
        assertEquals(ImportTask.State.INCOMPLETE, task.getState());
        assertEquals(2, task.getItems().size());

        assertEquals(ImportItem.State.NO_CRS, task.getItems().get(0).getState());
        assertEquals("archsites", task.getItems().get(0).getLayer().getResource().getName());

        assertEquals(ImportItem.State.READY, task.getItems().get(1).getState());
        assertEquals("bugsites", task.getItems().get(1).getLayer().getResource().getName());

        importer.run(context);

        Catalog cat = getCatalog();
        assertNull(cat.getLayerByName("archsites"));
        assertNotNull(cat.getLayerByName("bugsites"));

        assertEquals(ImportTask.State.INCOMPLETE, task.getState());
        assertEquals(ImportItem.State.NO_CRS, task.getItems().get(0).getState());
        assertEquals(ImportItem.State.COMPLETE, task.getItems().get(1).getState());

        runChecks("bugsites");
    }
 
    public void testImportUnknownFile() throws Exception {
        File dir = unpack("gml/states_wfs11.xml.gz");

        ImportContext context = importer.createContext(new Directory(dir)); 
        assertEquals(1, context.getTasks().size());

        ImportTask task = context.getTasks().get(0);
        assertEquals(ImportTask.State.INCOMPLETE, task.getState());
        assertNull(task.getData().getFormat());

    }

    public void testImportDatabase() throws Exception {
        File dir = unpack("h2/cookbook.zip");

        Map params = new HashMap();
        params.put(H2DataStoreFactory.DBTYPE.key, "h2");
        params.put(H2DataStoreFactory.DATABASE.key, new File(dir, "cookbook").getAbsolutePath());
     
        ImportContext context = importer.createContext(new Database(params));
        assertEquals(1, context.getTasks().size());

        ImportTask task = context.getTasks().get(0);
        assertEquals(ImportTask.State.READY, task.getState());
        
        assertEquals(3, task.getItems().size());

        assertEquals(ImportItem.State.READY, task.getItems().get(0).getState());
        assertEquals(ImportItem.State.READY, task.getItems().get(1).getState());
        assertEquals(ImportItem.State.READY, task.getItems().get(2).getState());

        Catalog cat = getCatalog();
        assertNull(cat.getDataStoreByName(cat.getDefaultWorkspace(), "cookbook"));
        assertNull(cat.getLayerByName("point"));
        assertNull(cat.getLayerByName("line"));
        assertNull(cat.getLayerByName("polygon"));

        importer.run(context);
        assertEquals(ImportTask.State.COMPLETE, task.getState());
        assertEquals(ImportItem.State.COMPLETE, task.getItems().get(0).getState());
        assertEquals(ImportItem.State.COMPLETE, task.getItems().get(1).getState());
        assertEquals(ImportItem.State.COMPLETE, task.getItems().get(2).getState());

        assertNotNull(cat.getDataStoreByName(cat.getDefaultWorkspace(), "cookbook"));

        DataStoreInfo ds = cat.getDataStoreByName(cat.getDefaultWorkspace(), "cookbook");
        assertNotNull(cat.getFeatureTypeByDataStore(ds, "point"));
        assertNotNull(cat.getFeatureTypeByDataStore(ds, "line"));
        assertNotNull(cat.getFeatureTypeByDataStore(ds, "polygon"));
        assertNotNull(cat.getLayerByName("point"));
        assertNotNull(cat.getLayerByName("line"));
        assertNotNull(cat.getLayerByName("polygon"));

        runChecks("point");
        runChecks("line");
        runChecks("polygon");
    }
//    
////    public void testImportGML() throws Exception {
////        File dir = unpack("gml/states_wfs11.gml.gz");
////        
////        Import imp = importer.newImport();
////        imp.setSource(new SpatialFile(new File(dir, "states_wfs11.gml"), new GMLFormat()));
////
////        importer.prepare(imp);
////        assertEquals(ImportStatus.READY, imp.getStatus());
////        assertEquals(1, imp.getLayers().size());
////        assertEquals(LayerStatus.READY, imp.getLayers().get(0).getStatus());
////
////        importer.run(imp);
////        
////        //converting gml leaves us without a crs and without bounds
////        assertTrue(imp.getLayers(LayerStatus.COMPLETED).isEmpty());
////        assertEquals(1, imp.getLayers().size());
////        assertEquals(LayerStatus.NO_CRS, imp.getLayers().get(0).getStatus());
////        imp.getLayers().get(0).setCRS(CRS.decode("EPSG:4326"));
////        
////        importer.run(imp);
////        runChecks("states");
////    }
//    
//    public void testImportGMLWithPrjFile() throws Exception {
//        File dir = unpack("gml/states_wfs11_prj.zip");
//        
//        Import imp = importer.newImport();
//        imp.setSource(new Directory(dir));
//
//        importer.prepare(imp);
//        assertEquals(ImportStatus.READY, imp.getStatus());
//        assertEquals(1, imp.getLayers().size());
//        assertEquals(LayerStatus.READY, imp.getLayers().get(0).getStatus());
//
//        importer.run(imp);
//        
//        runChecks("states");
//    }
//

    public void testImportIntoDatabase() throws Exception {
        Catalog cat = getCatalog();

        DataStoreInfo ds = createH2DataStore(cat.getDefaultWorkspace().getName(), "spearfish");

        File dir = tmpDir();
        unpack("shape/archsites_epsg_prj.zip", dir);
        unpack("shape/bugsites_esri_prj.tar.gz", dir);

        ImportContext context = importer.createContext(new Directory(dir), ds);
        assertEquals(1, context.getTasks().size());

        assertEquals(2, context.getTasks().get(0).getItems().size());
        //assertEquals(1, context.getTasks().get(1).getItems().size());

        assertEquals(ImportTask.State.READY, context.getTasks().get(0).getState());
        //assertEquals(ImportTask.State.READY, context.getTasks().get(1).getState());
        
        ImportItem item1 = context.getTasks().get(0).getItems().get(0);
        assertEquals(ImportItem.State.READY, item1.getState());
        
        ImportItem item2 = context.getTasks().get(0).getItems().get(1);
        assertEquals(ImportItem.State.READY, item2.getState());
        
        // cannot ensure ordering of items
        HashSet resources = new HashSet();
        resources.add(item1.getLayer().getResource().getName());
        resources.add(item2.getLayer().getResource().getName());
        assertTrue(resources.contains("bugsites"));
        assertTrue(resources.contains("archsites"));

        importer.run(context);

        assertEquals(ImportItem.State.COMPLETE, item1.getState());
        assertEquals(ImportItem.State.COMPLETE, item2.getState());

        assertNotNull(cat.getLayerByName("archsites"));
        assertNotNull(cat.getLayerByName("bugsites"));

        assertNotNull(cat.getFeatureTypeByDataStore(ds, "archsites"));
        assertNotNull(cat.getFeatureTypeByDataStore(ds, "bugsites"));

        runChecks("archsites");
        runChecks("bugsites");
    }
    
    public void testImportIntoDatabaseWithEncoding() throws Exception {
        Catalog cat = getCatalog();

        DataStoreInfo ds = createH2DataStore(cat.getDefaultWorkspace().getName(), "ming"); 

        File dir = tmpDir();
        unpack("shape/ming_time.zip", dir);

        ImportContext context = importer.createContext(new Directory(dir), ds);
        assertEquals(1, context.getTasks().size());
        assertEquals(1, context.getTasks().get(0).getItems().size());

        context.getTasks().get(0).getData().setCharsetEncoding("UTF-8");
        importer.run(context);
        
        FeatureTypeInfo info = (FeatureTypeInfo) context.getTasks().get(0).getItems().get(0).getLayer().getResource();
        FeatureSource<? extends FeatureType, ? extends Feature> fs = info.getFeatureSource(null, null);
        FeatureCollection<? extends FeatureType, ? extends Feature> features = fs.getFeatures();
        FeatureIterator<? extends Feature> it = features.features();
        assertTrue(it.hasNext());
        SimpleFeature next = (SimpleFeature) it.next();
        // let's test some attributes to see if they were digested properly
        String type_ch = (String) next.getAttribute("type_ch");
        assertEquals("卫",type_ch);
        String name_ch = (String) next.getAttribute("name_ch");
        assertEquals("杭州前卫",name_ch);
        
        it.close();
    }
    
    public void testImportIntoDatabaseUpdateModes() throws Exception {
        testImportIntoDatabase();
        
        DataStoreInfo ds = getCatalog().getDataStoreByName("spearfish");
        assertNotNull(ds);
        
        File dir = tmpDir();
        unpack("shape/archsites_epsg_prj.zip", dir);
        unpack("shape/bugsites_esri_prj.tar.gz", dir);
        
        FeatureSource<? extends FeatureType, ? extends Feature> fs = getCatalog().getFeatureTypeByName("archsites").getFeatureSource(null, null);
        int archsitesCount = fs.getCount(Query.ALL);
        fs = getCatalog().getFeatureTypeByName("bugsites").getFeatureSource(null, null);
        int bugsitesCount = fs.getCount(Query.ALL);

        ImportContext context = importer.createContext(new Directory(dir), ds);
        context.getTasks().get(0).getItems().get(0).setUpdateMode(UpdateMode.REPLACE);
        context.getTasks().get(0).getItems().get(1).setUpdateMode(UpdateMode.APPEND);
        
        importer.run(context);
        
        fs = getCatalog().getFeatureTypeByName("archsites").getFeatureSource(null, null);
        int archsitesCount2 = fs.getCount(Query.ALL);
        fs = getCatalog().getFeatureTypeByName("bugsites").getFeatureSource(null, null);
        int bugsitesCount2 = fs.getCount(Query.ALL);
        
        // tasks might not be in same order
        if (context.getTasks().get(0).getItems().get(0).getLayer().getName().equals("archsites")) {
            assertEquals(archsitesCount, archsitesCount2);
            assertEquals(bugsitesCount * 2, bugsitesCount2);
        } else {
            assertEquals(archsitesCount * 2, archsitesCount2);
            assertEquals(bugsitesCount, bugsitesCount2);
        }
    }

    public void testImportGeoTIFF() throws Exception {
        File dir = unpack("geotiff/EmissiveCampania.tif.bz2");
        
        ImportContext context = 
                importer.createContext(new SpatialFile(new File(dir, "EmissiveCampania.tif")));
        assertEquals(1, context.getTasks().size());
        
        ImportTask task = context.getTasks().get(0);
        assertEquals(ImportTask.State.READY, task.getState());
        assertEquals(1, task.getItems().size());
        
        ImportItem item = task.getItems().get(0);
        assertEquals(ImportItem.State.READY, item.getState());
        assertEquals("EmissiveCampania", item.getLayer().getResource().getName());

        importer.run(context);
        
        Catalog cat = getCatalog();
        assertNotNull(cat.getLayerByName("EmissiveCampania"));

        assertEquals(ImportTask.State.COMPLETE, task.getState());
        assertEquals(ImportItem.State.COMPLETE, item.getState());

        runChecks("EmissiveCampania");
    }

//    public void testUnknownFormat() throws Exception {
//        File dir = unpack("gml/states_wfs11.xml.gz");
//        
//        Import imp = importer.newImport();
//        imp.setSource(new SpatialFile(new File(dir, "states_wfs11.xml"), new GMLFormat()));
//
//        importer.prepare(imp);
//        assertEquals(ImportStatus.READY, imp.getStatus());
//        assertEquals(1, imp.getLayers().size());
//        assertEquals(LayerStatus.READY, imp.getLayers().get(0).getStatus());
//
//        importer.run(imp);
//        
//        //converting gml leaves us without a crs and without bounds
//        assertTrue(imp.getCompleted().isEmpty());
//        assertEquals(1, imp.getLayers().size());
//        assertEquals(LayerStatus.NO_CRS, imp.getLayers().get(0).getStatus());
//        imp.getLayers().get(0).setCRS(CRS.decode("EPSG:4326"));
//        
//        importer.run(imp);
//        runChecks("states");
//    }

    public void testImportNameClash() throws Exception {
        File dir = unpack("shape/archsites_epsg_prj.zip");
        
        ImportContext context = 
                importer.createContext(new SpatialFile(new File(dir, "archsites.shp")));
        importer.run(context);
        
        Catalog cat = getCatalog();
        assertNotNull(cat.getLayerByName("archsites"));
        runChecks("archsites");

        context = importer.createContext(new SpatialFile(new File(dir, "archsites.shp")));
        importer.run(context);

        ImportItem i = context.getTasks().get(0).getItems().get(0);
        assertEquals("archsites0", i.getLayer().getName());
        runChecks("archsites0");
    }

    public void testArchiveOnIndirectImport() throws Exception {
        File dir = unpack("shape/archsites_epsg_prj.zip");
        assertTrue(dir.exists());

        DataStoreInfo ds = createH2DataStore(null, "foo");
        
        ImportContext context = 
            importer.createContext(new SpatialFile(new File(dir, "archsites.shp")), ds);
        importer.run(context);
        assertFalse(dir.exists());

        dir = unpack("shape/bugsites_esri_prj.tar.gz");
        assertTrue(dir.exists());
        context = importer.createContext(new SpatialFile(new File(dir, "bugsites.shp")), ds);
        context.setArchive(false);

        importer.run(context);
        assertTrue(dir.exists());
    }
}
