package org.opengeo.data.importer;

import java.io.IOException;
import java.io.Serializable;

/**
 * Base class for all types of data sources that can be imported.
 * 
 * @author Justin Deoliveira, OpenGeo
 *
 */

public abstract class ImportData implements Serializable{

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * the format for this data
     */
    DataFormat format;
    
    String charsetEncoding;

    public String getCharsetEncoding() {
        return charsetEncoding;
    }

    public void setCharsetEncoding(String charsetEncoding) {
        this.charsetEncoding = charsetEncoding;
    }
    
    public DataFormat getFormat() {
        return format;
    }

    public void setFormat(DataFormat format) {
        this.format = format;
    }

    /**
     * Generates a name for this data.
     */
    public abstract String getName();

    /**
     * Runs any initial checks against the data preparing for import. 
     */
    public void prepare() throws IOException {
    }
    
    public void cleanup() throws IOException {
        // do nothing
    }
    
    /**
     * A dummy transfer object to hold properties but has no functionality.
     */
    public static class TransferObject extends ImportData {

        @Override
        public String getName() {
            return null;
        }

        @Override
        public void cleanup() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void prepare() throws IOException {
            throw new UnsupportedOperationException();
        }
    
    }

    public void reattach() {
    }
}
