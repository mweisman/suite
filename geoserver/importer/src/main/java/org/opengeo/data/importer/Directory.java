package org.opengeo.data.importer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.geoserver.data.util.IOUtils;
import org.geotools.util.logging.Logging;
import org.h2.store.fs.FileObjectOutputStream;

public class Directory extends FileData {

    private static final Logger LOGGER = Logging.getLogger(Directory.class);
    
    private static final long serialVersionUID = 1L;

    /**
     * list of files contained in directory
     */
    List<FileData> files = new ArrayList<FileData>();
    
    public Directory(File file) {
        super(file);
    }

    public static Directory createNew(File parent) throws IOException {
        File directory = File.createTempFile("tmp", "", parent);
        if (!directory.delete() || !directory.mkdir()) throw new IOException("Error creating temp directory at " + directory.getAbsolutePath());
        return new Directory(directory);
    }

    public static Directory createFromArchive(File archive) throws IOException {
        VFSWorker vfs = new VFSWorker();
        if (!vfs.canHandle(archive)) {
            throw new IOException(archive.getPath() + " is not a recognizable  format");
        }

        String basename = FilenameUtils.getBaseName(archive.getName());
        File dir = new File(archive.getParentFile(), basename);
        int i = 0;
        while (dir.exists()) {
            dir = new File(archive.getParentFile(), basename + i++);
        }
        vfs.extractTo(archive, dir);
        return new Directory(dir);
    }

    public File getFile() {
        return file;
    }

    public List<FileData> getFiles() {
        return files;
    }

    public void unpack(File file) throws IOException {
        //if the file is an archive, unpack it
        VFSWorker vfs = new VFSWorker();
        if (vfs.canHandle(file)) {
            LOGGER.fine("unpacking " + file.getAbsolutePath() + " to " + this.file.getAbsolutePath());
            vfs.extractTo(file, this.file);

            LOGGER.fine("deleting " + file.getAbsolutePath());
            file.delete();
        }
    }
    
    public File getChild(String name) {
        return new File(this.file,name);
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public void prepare() throws IOException {
        files = new ArrayList<FileData>();

        //recursively search for spatial files, maintain a queue of directories to recurse into
        LinkedList<File> q = new LinkedList<File>();
        q.add(file);

        while(!q.isEmpty()) {
            File dir = q.poll();

            //get all the regular (non directory) files
            Set<File> all = new LinkedHashSet<File>(Arrays.asList(dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return !new File(dir, name).isDirectory();
                }
            })));

            //scan all the files looking for spatial ones
            for (File f : dir.listFiles()) {
                if (f.isHidden()) {
                    all.remove(f);
                    continue;
                }
                if (f.isDirectory()) {
                    // @hacky - ignore __MACOSX
                    // this could probably be dealt with in a better way elsewhere
                    // like by having Directory ignore the contents since they
                    // are all hidden files anyway
                    if (!"__MACOSX".equals(f.getName())) {
                        Directory d = new Directory(f);
                        d.prepare();

                        files.add(d);
                    }
                    //q.push(f);
                    continue;
                }

                //special case for .aux files, they are metadata but get picked up as readable 
                // by the erdas imagine reader...just ignore them for now 
                if ("aux".equalsIgnoreCase(FilenameUtils.getExtension(f.getName()))) {
                    continue;
                }

                //determine if this is a spatial format or not
                DataFormat format = DataFormat.lookup(f);

                if (format != null) {
                    SpatialFile sf = new SpatialFile(f);
                    sf.setFormat(format);

                    //gather up the related files
                    sf.prepare();

                    files.add(sf);

                    all.removeAll(sf.allFiles());
                }
            }

            //take any left overs and add them as unspatial/unrecognized
            for (File f : all) {
                files.add(new ASpatialFile(f));
            }
        }

        format = format();
//        //process ignored for files that should be grouped with the spatial files
//        for (DataFile df : files) {
//            SpatialFile sf = (SpatialFile) df;
//            String base = FilenameUtils.getBaseName(sf.getFile().getName());
//            for (Iterator<File> i = ignored.iterator(); i.hasNext(); ) {
//                File f = i.next();
//                if (base.equals(FilenameUtils.getBaseName(f.getName()))) {
//                    //.prj file?
//                    if ("prj".equalsIgnoreCase(FilenameUtils.getExtension(f.getName()))) {
//                        sf.setPrjFile(f);
//                    }
//                    else {
//                        sf.getSuppFiles().add(f);
//                    }
//                    i.remove();
//                }
//            }
//        }
//        
//        //take any left overs and add them as unspatial/unrecognized
//        for (File f : ignored) {
//            files.add(new ASpatialFile(f));
//        }
//        
//        return files;
//        
//        for (DataFile f : files()) {
//            f.prepare();
//        }
    }

    public List<Directory> flatten() {
        List<Directory> flat = new ArrayList<Directory>();

        LinkedList<Directory> q = new LinkedList<Directory>();
        q.addLast(this);
        while(!q.isEmpty()) {
            Directory dir = q.removeFirst();
            flat.add(dir);

            for (Iterator<FileData> it = dir.getFiles().iterator(); it.hasNext(); ) {
                FileData f = it.next();
                if (f instanceof Directory) {
                    Directory d = (Directory) f;
                    it.remove();
                    q.addLast(d);
                }
            }
        }

        return flat;
    }

//    public List<DataFile> files() throws IOException {
//        LinkedList<DataFile> files = new LinkedList<DataFile>();
//        
//        LinkedList<File> ignored = new LinkedList<File>();
//
//        LinkedList<File> q = new LinkedList<File>();
//        q.add(file);
//
//        while(!q.isEmpty()) {
//            File f = q.poll();
//
//            if (f.isDirectory()) {
//                q.addAll(Arrays.asList(f.listFiles()));
//                continue;
//            }
//
//            //determine if this is a spatial format or not
//            DataFormat format = DataFormat.lookup(f);
//
//            if (format != null) {
//                SpatialFile file = new SpatialFile(f);
//                file.setFormat(format);
//                files.add(file);
//            }
//            else {
//                ignored.add(f);
//            }
//        }
//        
//        //process ignored for files that should be grouped with the spatial files
//        for (DataFile df : files) {
//            SpatialFile sf = (SpatialFile) df;
//            String base = FilenameUtils.getBaseName(sf.getFile().getName());
//            for (Iterator<File> i = ignored.iterator(); i.hasNext(); ) {
//                File f = i.next();
//                if (base.equals(FilenameUtils.getBaseName(f.getName()))) {
//                    //.prj file?
//                    if ("prj".equalsIgnoreCase(FilenameUtils.getExtension(f.getName()))) {
//                        sf.setPrjFile(f);
//                    }
//                    else {
//                        sf.getSuppFiles().add(f);
//                    }
//                    i.remove();
//                }
//            }
//        }
//        
//        //take any left overs and add them as unspatial/unrecognized
//        for (File f : ignored) {
//            files.add(new ASpatialFile(f));
//        }
//        
//        return files;
//    }

    /**
     * Returns the data format of the files in the directory iff all the files are of the same 
     * format, if they are not this returns null.
     */
    public DataFormat format() throws IOException {
        if (files.isEmpty()) {
            return null;
        }

        FileData file = files.get(0);
        DataFormat format = file.getFormat();
        for (int i = 1; i < files.size(); i++) {
            FileData other = files.get(i);
            if (format != null && !format.equals(other.getFormat())) {
                return null;
            }
            if (format == null && other.getFormat() != null) {
                return null;
            }
        }

        return format;
    }

    public Directory filter(List<FileData> files) {
        return new Filtered(file, files);
    }

    @Override
    public String toString() {
        return file.getPath();
    }

    public void accept(String childName, InputStream in) throws IOException {
        File dest = getChild(childName);
        
        IOUtils.copy(in, dest);

        try {
            unpack(dest);
        } catch (IOException ioe) {
            // problably should delete on error
            LOGGER.warning("Possible invalid file uploaded to " + dest.getAbsolutePath());
            throw ioe;
        }
    }

    public void accept(FileItem item) throws Exception {
        File dest = getChild(item.getName());
        item.write(dest);

        try {
            unpack(dest);
        } 
        catch (IOException e) {
            // problably should delete on error
            LOGGER.warning("Possible invalid file uploaded to " + dest.getAbsolutePath());
            throw e;
        }
    }
    
    public void archive(File output) throws IOException {
        File archiveDir = output.getAbsoluteFile().getParentFile();
        String outputName = output.getName().replace(".zip","");
        int id = 0;
        while (output.exists()) {
            output = new File(archiveDir, outputName + id + ".zip");
            id++;
        }
        ZipOutputStream zout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(output)));
        Exception error = null;

        // don't call zout.close in finally block, if an error occurs and the zip
        // file is empty by chance, the second error will mask the first
        try {
            IOUtils.zipDirectory(file, zout, null);
        } catch (Exception ex) {
            error = ex;
            try {
                zout.close();
            } catch (Exception ex2) {
                // nothing, we're totally aborting
            }
            output.delete();
            if (ex instanceof IOException) throw (IOException) ex;
            throw (IOException) new IOException("Error archiving").initCause(ex);
        } 
        
        // if we get here, the zip is properly written
        try {
            zout.close();
        } finally {
            cleanup();
        }
    }

    @Override
    public void cleanup() throws IOException {
        File[] files = file.listFiles();
        if (files != null) {
            for (File f: files) {
                if (f.isDirectory()) {
                    new Directory(f).cleanup();
                } else {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine("Deleting file " + f.getAbsolutePath());
                    }
                    f.delete();
                }
            }
        }
        super.cleanup();
    }
    
    static class Filtered extends Directory {

        List<FileData> filter;

        public Filtered(File file, List<FileData> filter) {
            super(file);
            this.filter = filter;
        }

        @Override
        public void prepare() throws IOException {
            super.prepare();

            files.retainAll(filter);
            format = format();
        }
    }
}
