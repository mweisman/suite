package org.opengeo.data.importer;

import com.thoughtworks.xstream.XStream;
import com.vividsolutions.jts.geom.Geometry;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.config.util.XStreamPersister;
import org.geoserver.config.util.XStreamPersister.CRSConverter;
import org.geoserver.config.util.XStreamPersisterFactory;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureStore;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.util.logging.Logging;
import org.opengeo.data.importer.ImportTask.State;
import org.opengeo.data.importer.bdb.BDBImportStore;
import org.opengeo.data.importer.transform.RasterTransformChain;
import org.opengeo.data.importer.transform.ReprojectTransform;
import org.opengeo.data.importer.transform.TransformChain;
import org.opengeo.data.importer.transform.VectorTransformChain;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Primary controller/facade of the import subsystem.
 * 
 * @author Justin Deoliveira, OpenGeo
 *
 */
public class Importer implements InitializingBean, DisposableBean {

    static Logger LOGGER = Logging.getLogger(Importer.class);

    /** catalog */
    Catalog catalog;

    /** import context storage */
    ImportStore contextStore;

    /** style generator */
    StyleGenerator styleGen;

    /** job queue */
    JobQueue jobs = new JobQueue();
    
    ConcurrentHashMap<Long,ImportItem> currentlyProcessing = new ConcurrentHashMap<Long, ImportItem>();

    public Importer(Catalog catalog) {
        this.catalog = catalog;
        this.contextStore = new BDBImportStore(this);
        this.styleGen = new StyleGenerator(catalog);
    }

    public ImportStore getStore() {
        return contextStore;
    }

    public ImportItem getCurrentlyProcessingItem(long contextId) {
        return currentlyProcessing.get(new Long(contextId));
    }

    public void afterPropertiesSet() throws Exception {
        this.contextStore.init();
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public ImportContext getContext(long id) {
        return contextStore.get(id);
    }

    public Iterator<ImportContext> getContexts() {
        return contextStore.allNonCompleteImports();
    }

    public Iterator<ImportContext> getContextsByUser(String user) {
        return contextStore.importsByUser(user);
    }
    
    public Iterator<ImportContext> getAllContexts() {
        return contextStore.iterator();
    }

    public ImportContext createContext(ImportData data, WorkspaceInfo targetWorkspace) throws IOException {
        return createContext(data, targetWorkspace, null);
    }

    public ImportContext createContext(ImportData data, StoreInfo targetStore) throws IOException {
        return createContext(data, null, targetStore); 
    }

    public ImportContext createContext(ImportData data) throws IOException {
        return createContext(data, null, null); 
    }
    
    public ImportContext createContext(ImportData data, WorkspaceInfo targetWorkspace, 
        StoreInfo targetStore) throws IOException {
        ImportContext context = new ImportContext();
        context.setData(data);

        if (targetWorkspace == null && targetStore != null) {
            targetWorkspace = targetStore.getWorkspace();
        }
        if (targetWorkspace == null) {
            targetWorkspace = catalog.getDefaultWorkspace();
        }
        context.setTargetWorkspace(targetWorkspace);
        context.setTargetStore(targetStore);

        init(context);
        contextStore.add(context);
        return context;
    }

    public List<ImportTask> update(ImportContext context, ImportData data) throws IOException {
        List<ImportTask> tasks = init(context, data);
        
        prep(context);
        changed(context);

        return tasks;
    }

    public void init(ImportContext context) throws IOException {
        //context.getTasks().clear();
        ImportData data = context.getData();
        if (data != null) {
            init(context, data); 
        }
    }

    List<ImportTask> init(ImportContext context, ImportData data) throws IOException {
        if (data == null) {
            return Collections.EMPTY_LIST;
        }
        data.prepare();

        List<ImportTask> tasks = new ArrayList();
        StoreInfo targetStore = context.getTargetStore();
        if (data instanceof FileData) {
            if (data instanceof Directory) {
                //flatten out the directory into itself and all sub directories and process in order
                for (Directory dir : ((Directory) data).flatten()) {
                    //ignore empty directories
                    if (dir.getFiles().isEmpty()) continue;

                    //group the contents of the directory by format
                    Map<DataFormat,List<FileData>> map = new HashMap<DataFormat,List<FileData>>();
                    for (FileData f : dir.getFiles()) {
                        DataFormat format = f.getFormat();
                        List<FileData> files = map.get(format);
                        if (files == null) {
                            files = new ArrayList<FileData>();
                            map.put(format, files);
                        }
                        files.add(f);
                    }

                    // if no target store specified group the directory into pieces that can be 
                    // processed as a single task
                    if (targetStore == null) {

                        //create a task for each "format" if that format can handle a directory
                        for (DataFormat format: new ArrayList<DataFormat>(map.keySet())) {
                            if (format != null && format.canRead(dir)) {
                                List<FileData> files = map.get(format);
                                if (files.size() == 1) {
                                    //use the file directly
                                    tasks.add(createTask(files.get(0), context, null));
                                }
                                else {
                                    tasks.add(createTask(dir.filter(files), context, null));
                                }
                                
                                map.remove(format);
                            }
                        }
    
                        //handle the left overs, each file gets its own task
                        for (List<FileData> files : map.values()) {
                            for (FileData file : files) {
                                tasks.add(createTask(file, context, null));
                            }
                        }

                    }
                    else {
                        for (DataFormat format: new ArrayList<DataFormat>(map.keySet())) {
                            List<FileData> files = map.get(format);
                            if (files.size() == 1) {
                                //use the file directly
                                tasks.add(createTask(files.get(0), context, targetStore));
                            }
                            else {
                                tasks.add(createTask(dir.filter(files), context, targetStore));
                            }
                        }
                        //for (FileData file : dir.getFiles()) {
                        //    createTask(file, context, targetStore);
                        //}
                    }
                }
            }
            else {
                //single file case
                tasks.add(createTask((FileData) data, context, targetStore));
            }
        }
        else if (data instanceof Table) {
        }
        else if (data instanceof Database) {
            Database db = (Database) data;
        
            //if no target store specified do direct import
            if (targetStore == null) {
                //create one task for entire database
                tasks.add(createTask(db, context, null));
            }
            else {
                //one by one import, create task for each table
                for (Table t : db.getTables()) {
                    tasks.add(createTask(t, context, targetStore));
                }
            }
        }

        prep(context);
        return tasks;
    }

    ImportTask createTask(ImportData data, ImportContext context, StoreInfo targetStore) {
        // @revisit - why was this temporary
        //if (data instanceof ASpatialFile) return; // this is temporary
        ImportTask task = new ImportTask(data);
        task.setStore(targetStore);
        task.setDirect(targetStore == null);
        context.addTask(task);
        return task;
    }

    public void prep(ImportContext context) throws IOException {
        boolean ready = true;
        for (ImportTask task : context.getTasks()) {
            ImportData data = task.getData();
            data.prepare();

            DataFormat format = data.getFormat();

            if (format == null) {
                //set state to unknown format
                task.setState(State.INCOMPLETE);
                continue;
            }

            if (task.getStore() == null) {
                StoreInfo store = format.createStore(data, context.getTargetWorkspace(), catalog);
                if (store == null) {
                    //can't do a direct import with this format, use the default store in catalog
                    // if configured (and if this a vector layer)
                    if (format instanceof VectorFormat) {
                        store = lookupDefaultStore();
                    }

                    if (store == null) {
                        //unable to import, no direct import and no default store
                        task.setState(State.INCOMPLETE);
                        continue;
                    }
                }

                // @todo revisit - thought this was needed or shapefile errors were occuring
                task.setDirect(true);
                task.setStore(store);
            }
            else {
                //check for a mismatch between store and format
                if ((format instanceof GridFormat && task.getStore() instanceof DataStoreInfo) || 
                    (format instanceof VectorFormat && task.getStore() instanceof CoverageStoreInfo)) {
                    task.setStore(null);
                    task.setState(State.INCOMPLETE);
                    continue;
                }
            }

            if (task.getItems().isEmpty()) {
                //ask the format for the list of "resources" to import
                for (ImportItem item : format.list(data, catalog)) {
                    task.addItem(item);

                    item.setTransform(format instanceof VectorFormat 
                        ? new VectorTransformChain() : new RasterTransformChain());

                    LayerInfo layer = item.getLayer();

                    ResourceInfo resource = layer.getResource();

                    //initialize resource references
                    resource.setStore(task.getStore());
                    resource.setNamespace(
                        catalog.getNamespaceByPrefix(task.getStore().getWorkspace().getName()));

                    //assign a default style to the layer if not already done 
                    if (layer.getDefaultStyle() == null) {
                        StyleInfo style = null;
                        if (resource instanceof FeatureTypeInfo) {
                            //since this resource is still detached from the catalog we can't call
                            // through to get it's underlying resource, so we depend on the "native"
                            // type provided from the format
                            FeatureType featureType = 
                                (FeatureType) item.getMetadata().get(FeatureType.class);
                            if (featureType != null) {
                                style = styleGen.createStyle((FeatureTypeInfo) resource, featureType);
                            }

                        }
                        else if (resource instanceof CoverageInfo) {
                            style = styleGen.createStyle((CoverageInfo) resource);
                        }
                        else {
                            //hmmm....
                        }
                        layer.setDefaultStyle(style);
                    }
                }
            }

            ready = prep(task) && ready;
        }
        if (ready) {
            context.setState(ImportContext.State.READY);
        }
        else {
            context.setState(ImportContext.State.INCOMPLETE);
        }

        //only save if not a new context
        if (context.getId() != null) {
            contextStore.save(context);
        }
    }

    boolean prep(ImportTask task) {
        //sanity check all the resources
        boolean ready = true;
        for (ImportItem item : task.getItems()) {
            ready = prep(item) && ready;
        }

        if (ready) {
            task.setState(State.READY);
        }
        else {
            task.setState(State.INCOMPLETE);
        }
        return ready;
    }

    boolean prep(ImportItem item) {
        if (item.getState() == ImportItem.State.COMPLETE) {
            return true;
        }

        LayerInfo l = item.getLayer();
        ResourceInfo r = l.getResource();

        //srs
        if (r.getSRS() == null) {
            item.setState(ImportItem.State.NO_CRS);
            return false;
        }

        //bounds
        if (r.getNativeBoundingBox() == null) {
            item.setState(ImportItem.State.NO_BOUNDS);
            return false;
        }
        
        item.setState(ImportItem.State.READY);
        return true;
    }

    public void run(ImportContext context) throws IOException {
        run(context, ImportFilter.ALL);
    }

    public void run(ImportContext context, ImportFilter filter) throws IOException {
        context.setState(ImportContext.State.RUNNING);

        for (ImportTask task : context.getTasks()) {
            if (!filter.include(task)) {
                continue;
            }

            run(task, filter);
        }

        context.updateState();
        contextStore.save(context);
    }

    void run(ImportTask task) throws IOException {
        run(task, ImportFilter.ALL);
    }

    void run(ImportTask task, ImportFilter filter) throws IOException {
        if (task.getState() == ImportTask.State.COMPLETE) {
            return;
        }
        task.setState(ImportTask.State.RUNNING);

        if (task.isDirect()) {
            //direct import, simply add configured store and layers to catalog
            doDirectImport(task, filter);
        }
        else {
            //indirect import, read data from the source and into the target datastore 
            doIndirectImport(task, filter);
        }

        //check if the task is complete, ie all items are complete
        task.updateState();
        
        if (task.getState() == ImportTask.State.COMPLETE && !task.isDirect()) {
            Directory directory = null;
            if ( task.getData() instanceof Directory) {
                directory = (Directory) task.getData();
            } else if ( task.getData() instanceof SpatialFile ) {
                directory = new Directory( ((SpatialFile) task.getData()).getFile().getParentFile() );
            }
            if (directory != null) {
                try {
                    directory.archive(getArchiveFile(task));
                } catch (Exception ioe) {
                    ioe.printStackTrace();
                    // this is not a critical operation, so don't make the whole thing fail
                    LOGGER.log(Level.WARNING, "Error archiving", ioe);
                }
            }
        }
    }
    
    public File getArchiveFile(ImportTask task) throws IOException {
        String archiveName = "import-" + task.getContext().getId() + "-" + task.getId() + "-" + task.getData().getName() + ".zip";
        File dir = getCatalog().getResourceLoader().findOrCreateDirectory("uploads","archives");
        return new File(dir, archiveName);
    }
    
    public void changed(ImportContext context) {
        context.updated();
        contextStore.save(context);
    }

    public void changed(ImportItem item)  {
        changed(item.getTask());
    }

    public void changed(ImportTask task)  {
        prep(task);
        changed(task.getContext());
    }

    public Long runAsync(final ImportContext context, final ImportFilter filter) {
        return jobs.submit(new Callable<ImportContext>() {
            public ImportContext call() throws Exception {
                run(context, filter);
                return context;
            }
        });
    }

    public Future<ImportContext> getFuture(Long job) {
        return (Future<ImportContext>) jobs.getFuture(job);
    }

    /* 
     * an import that involves consuming a data source directly
     */
    void doDirectImport(ImportTask task, ImportFilter filter) throws IOException {
        //TODO: this needs to be transactional in case of errors along the way

        //add the store, may have been added in a previous iteration of this task
        if (task.getStore().getId() == null) {
            StoreInfo store = task.getStore();

            //ensure a unique name
            store.setName(findUniqueStoreName(task.getStore()));
            
            //ensure a namespace connection parameter set matching workspace/namespace
            if (!store.getConnectionParameters().containsKey("namespace")) {
                WorkspaceInfo ws = task.getContext().getTargetWorkspace();
                NamespaceInfo ns = catalog.getNamespaceByPrefix(ws.getName());
                if (ns != null) {
                    store.getConnectionParameters().put("namespace", ns.getURI());
                }
            }
            catalog.add(task.getStore());
        }

        //add the individual resources
        for (ImportItem item : task.getItems()) {
            if (item.getState() != ImportItem.State.READY) {
                continue;
            }
            if (!filter.include(item)) {
                continue;
            }

            item.setState(ImportItem.State.RUNNING);

            //set up transform chain
            TransformChain tx = (TransformChain) item.getTransform();
            
            //apply pre transform
            if (!doPreTransform(item, task.getData(), tx)) {
                continue;
            }

            addToCatalog(item, task);

            //apply pre transform
            if (!doPostTransform(item, task.getData(), tx)) {
                continue;
            }

            item.setState(ImportItem.State.COMPLETE);
        }
    }

    /* 
     * an import that involves reading from the datastore and writing into a specified target store
     */
    void doIndirectImport(ImportTask task, ImportFilter filter) throws IOException {
        if (!task.getStore().isEnabled()) {
            task.getStore().setEnabled(true);
        }
        // @todo This needs to be transactional and probably should be extracted to a class for clarity
        for (ImportItem item : task.getItems()) {
            if (item.getState() != ImportItem.State.READY) {
                continue;
            }
            if (!filter.include(item)) {
                continue;
            }

            item.setState(ImportItem.State.RUNNING);

            //setup transform chain
            TransformChain tx = item.getTransform();

            //pre transform
            if (!doPreTransform(item, task.getData(), tx)) {
                continue;
            }

            DataFormat format = task.getData().getFormat();
            if (format instanceof VectorFormat) {
                try {
                    currentlyProcessing.put(task.getContext().getId(), item);
                    loadIntoDataStore(item, (DataStoreInfo)task.getStore(), (VectorFormat) format, (VectorTransformChain) tx);

                    //JD: not sure what the rationale is here... ask IS
                    //if (task.getUpdateMode() == null) {
                    if (item.updateMode() == null) {
                        addToCatalog(item, task);
                    }
                }
                catch(Exception e) {
                    LOGGER.log(Level.SEVERE, "Error occured during import", e);
                    item.setError(e);
                    item.setState(ImportItem.State.ERROR);
                    continue;
                } finally {
                    currentlyProcessing.remove(task.getContext().getId());
                }
            }
            else {
                throw new UnsupportedOperationException("Indirect raster import not yet supported");
            }

            if (!doPostTransform(item, task.getData(), tx)) {
                continue;
            }

            item.setState(ImportItem.State.COMPLETE);
        }
    }

    boolean doPreTransform(ImportItem item, ImportData data, TransformChain tx) {
        try {
            tx.pre(item, data);
        } 
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occured during pre transform", e);
            item.setError(e);
            item.setState(ImportItem.State.ERROR);
            return false;
        }
        return true;
    }

    boolean doPostTransform(ImportItem item, ImportData data, TransformChain tx) {
        try {
            tx.post(item, data);
        } 
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occured during post transform", e);
            item.setError(e);
            item.setState(ImportItem.State.ERROR);
            return false;
        }
        return true;
    }

    void loadIntoDataStore(ImportItem item, DataStoreInfo store, VectorFormat format, 
        VectorTransformChain tx) throws Exception {
        
        FeatureReader reader = format.read(item.getTask().getData(), item);
        SimpleFeatureType featureType = (SimpleFeatureType) reader.getFeatureType();
        DataStore dataStore = (DataStore) store.getDataStore(null);
        String featureTypeName = featureType.getName().getLocalPart();

        UpdateMode updateMode = item.updateMode();
        
        if (updateMode == null) {
            //find a unique type name in the target store
            featureTypeName = findUniqueNativeFeatureTypeName(featureType, store);
            if (!featureTypeName.equals(featureType.getTypeName())) {
                //update the metadata
                item.setOriginalName(featureType.getTypeName());
                item.getLayer().getResource().setName(featureTypeName);
                item.getLayer().getResource().setNativeName(featureTypeName);
                
                //retype
                SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
                typeBuilder.setName(featureTypeName);
                typeBuilder.addAll(featureType.getAttributeDescriptors());
                featureType = typeBuilder.buildFeatureType();
            }

            // @todo HACK remove this at some point when timezone issues are fixed
            // this will force postgis to create timezone w/ timestamp fields
            if (dataStore instanceof JDBCDataStore) {
                JDBCDataStore ds = (JDBCDataStore) dataStore;
                // sniff for postgis (h2 is used in tests and will cause failure if this occurs)
                if (ds.getSqlTypeNameToClassMappings().containsKey("timestamptz")) {
                    ds.getSqlTypeToSqlTypeNameOverrides().put(java.sql.Types.TIMESTAMP, "timestamptz");
                }
            }

            //apply the feature type transform
            featureType = tx.inline(item, dataStore, featureType);

            dataStore.createSchema(featureType);
        } else {
            // @todo what to do if featureType transform is present?
            
            // @todo implement me - need to specify attribute used for id
            if (updateMode == UpdateMode.UPDATE) {
                throw new UnsupportedOperationException("updateMode UPDATE is not supported yet");
            }
        }
            
        Transaction transaction = new DefaultTransaction();
        
        if (updateMode == UpdateMode.REPLACE) {
            
            FeatureStore fs = (FeatureStore) dataStore.getFeatureSource(featureTypeName);
            fs.setTransaction(transaction);
            fs.removeFeatures(Filter.INCLUDE);
        }
        
        // using this exception to throw at the end
        Exception error = null;

        //start writing features
        // @todo ability to collect transformation errors for use in a dry-run (auto-rollback)
        FeatureWriter writer = null;
        
        // @todo need better way to communicate to client
        int skipped = 0;
        int cnt = 0;
        // metrics
        long startTime = System.currentTimeMillis();
        item.clearImportMessages();
        
        item.setTotalToProcess(format.getFeatureCount(item.getTask().getData(), item));
        
        LOGGER.info("begining import");
        try {
            writer = dataStore.getFeatureWriterAppend(featureTypeName, transaction);
            
            while(reader.hasNext()) {
                SimpleFeature feature = (SimpleFeature) reader.next();
                SimpleFeature next = (SimpleFeature) writer.next();
                next.setAttributes(feature.getAttributes());

                // @hack #45678 - mask empty geometry or postgis will complain
                Geometry geom = (Geometry) next.getDefaultGeometry();
                if (geom != null && geom.isEmpty()) {
                    next.setDefaultGeometry(null);
                }
                
                //apply the feature transform
                next = tx.inline(item, dataStore, feature, next);
                
                if (next == null) {
                    skipped++;
                } else {
                    writer.write();
                }
                item.setNumberProcessed(++cnt);
            }
            transaction.commit();
            if (skipped > 0) {
                item.addImportMessage(Level.WARNING,skipped + " features were skipped.");
            }
            LOGGER.info("load to target took " + (System.currentTimeMillis() - startTime));
        } 
        catch (Exception e) {
            error = e;
        } 
        // no finally block, there is too much to do
        
        if (error != null) {
            // all sub exceptions in this catch block should be logged, not thrown
            // as the triggering exception will be thrown

            //failure, rollback transaction
            try {
                transaction.rollback();
            } catch (Exception e1) {
                LOGGER.log(Level.WARNING, "Error rolling back transaction",e1);
            }

            //attempt to drop the type that was created as well
            try {
                dropSchema(dataStore,featureTypeName);
            } catch(Exception e1) {
                LOGGER.log(Level.WARNING, "Error dropping schema in rollback",e1);
            }
        }

        // try to cleanup, but if an error occurs here and one hasn't already been set, set the error
        try {
            transaction.close();
        } catch (Exception e) {
            if (error != null) {
                error = e;
            }
            LOGGER.log(Level.WARNING, "Error closing transaction",e);
        }
        if (writer != null) {
            try {
                writer.close();
            } catch (Exception e) {
                if (error != null) {
                    error = e;
                }
                LOGGER.log(Level.WARNING, "Error closing writer",e);
            }
        }

        // @revisit - when this gets disposed, any following uses seem to
        // have a problem where later users of the dataStore get an NPE 
        // since the dataStore gets cached by the ResourcePool but is in a 
        // closed state???
        
        // do this last in case we have to drop the schema
//        try {
//            dataStore.dispose();
//        } catch (Exception e) {
//            LOGGER.log(Level.WARNING, "Error closing dataStore",e);
//        }
        try {    
            format.dispose(reader, item);
            // @hack catch _all_ Exceptions here - occassionally closing a shapefile
            // seems to result in an IllegalArgumentException related to not
            // holding the lock...
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error closing reader",e);
        }
        
        // finally, throw any error
        if (error != null) {
            throw error;
        }
    }

    StoreInfo lookupDefaultStore() {
        WorkspaceInfo ws = catalog.getDefaultWorkspace();
        if (ws == null) {
            return null;
        }

        return catalog.getDefaultDataStore(ws);
    }

    void addToCatalog(ImportItem item, ImportTask task) throws IOException {
        LayerInfo layer = item.getLayer();
        ResourceInfo resource = layer.getResource();
        resource.setStore(task.getStore());

        //add the resource
        String name = findUniqueResourceName(resource);
        resource.setName(name); 

        //JD: not setting a native name, it should actually already be set by this point and we 
        // don't want to blindly set it to the same name as the resource name, which might have 
        // changed to deal with name clashes
        //resource.setNativeName(name);
        resource.setEnabled(true);
        catalog.add(resource);

        //add the layer (and style)
        if (layer.getDefaultStyle().getId() == null) {
            catalog.add(layer.getDefaultStyle());
        }

        layer.setEnabled(true);
        catalog.add(layer);
    }

    String findUniqueStoreName(StoreInfo store) {
        WorkspaceInfo workspace = store.getWorkspace();

        //TODO: put an upper limit on how many times to try
        String name = store.getName();
        if (catalog.getStoreByName(workspace, store.getName(), StoreInfo.class) != null) {
            int i = 0;
            name += i;
            while (catalog.getStoreByName(workspace, name, StoreInfo.class) != null) {
                name = name.replaceAll(i + "$", String.valueOf(i+1));
                i++;
            }
        }

        return name;
    }
    
    String findUniqueResourceName(ResourceInfo resource) 
        throws IOException {

        //TODO: put an upper limit on how many times to try
        StoreInfo store = resource.getStore();
        NamespaceInfo ns = catalog.getNamespaceByPrefix(store.getWorkspace().getName());
        
        String name = resource.getName();
        if (catalog.getResourceByName(ns, name, ResourceInfo.class) != null) {
            int i = 0;
            name += i;
            while (catalog.getResourceByName(ns, name, ResourceInfo.class) != null) {
                name = name.replaceAll(i + "$", String.valueOf(i+1));
                i++;
            }
        }

        return name;
    }

    String findUniqueNativeFeatureTypeName(FeatureType featureType, DataStoreInfo store) throws IOException {
        DataStore dataStore = (DataStore) store.getDataStore(null);
        String name = featureType.getName().getLocalPart();
        
        //TODO: put an upper limit on how many times to try
        List<String> names = Arrays.asList(dataStore.getTypeNames());
        if (names.contains(name)) {
            int i = 0;
            name += i;
            while(names.contains(name)) {
                name = name.replaceAll(i + "$", String.valueOf(i+1));
                i++;
            }
        }
        
        return name;
    }

    //file location methods
    public File getImportRoot() {
        try {
            return catalog.getResourceLoader().findOrCreateDirectory("imports");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void destroy() throws Exception {
        jobs.shutdown();
        contextStore.destroy();
    }

    public void delete(ImportContext importContext) throws IOException {
        importContext.delete();
        contextStore.remove(importContext);
    }

    private void dropSchema(DataStore ds, String featureTypeName) throws Exception {
        // @todo this needs implementation in geotools
        SimpleFeatureType schema = ds.getSchema(featureTypeName);
        if (schema != null) {
            if (ds instanceof JDBCDataStore) {
                JDBCDataStore dataStore = (JDBCDataStore) ds;
                Connection conn = dataStore.getConnection(Transaction.AUTO_COMMIT);
                Statement st = null;
                try {
                    st = conn.createStatement();
                    st.execute("drop table " + featureTypeName);
                    LOGGER.fine("dropSchema " + featureTypeName + " successful");
                } finally {
                    dataStore.closeSafe(conn);
                    dataStore.closeSafe(st);
                }
            } else {
                LOGGER.warning("Unable to dropSchema " + featureTypeName + " from datastore " + ds.getClass());
            }
        } else {
            LOGGER.warning("Unable to dropSchema " + featureTypeName + " as it does not appear to exist in dataStore");
        }
    }

    public XStreamPersister createXStreamPersister() {
        return initXStreamPersister(new XStreamPersisterFactory().createXMLPersister());
    }
    
    public XStreamPersister initXStreamPersister(XStreamPersister xp) {
        return initXStreamPersister(xp, true);
    }

    public XStreamPersister initXStreamPersister(XStreamPersister xp, boolean encodeByRef) { 
        if (encodeByRef) {
            xp.setEncodeByReference();
        }
        xp.setCatalog(catalog);

        XStream xs = xp.getXStream();

        //ImportContext
        xs.alias("import", ImportContext.class);

        //ImportTask
        xs.alias("task", ImportTask.class);
        xs.omitField(ImportTask.class, "context");

        //ImportItem
        xs.alias("item", ImportItem.class);
        xs.omitField(ImportItem.class, "task");

        //DataFormat
        xs.alias("dataStoreFormat", DataStoreFormat.class);

        //ImportData
        xs.alias("spatialFile", SpatialFile.class);
        xs.alias("database", org.opengeo.data.importer.Database.class);
        xs.alias("table", Table.class);
        xs.omitField(Table.class, "db");

        xs.alias("vectorTransformChain", VectorTransformChain.class);
        xs.registerLocalConverter(ReprojectTransform.class, "source", new CRSConverter());
        xs.registerLocalConverter(ReprojectTransform.class, "target", new CRSConverter());

        xs.registerLocalConverter( ReferencedEnvelope.class, "crs", new CRSConverter() );
        xs.registerLocalConverter( GeneralEnvelope.class, "crs", new CRSConverter() );
        
        return xp;
    }

}
