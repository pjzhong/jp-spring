package jp.spring.ioc.beans.io.resources;

import jp.spring.ioc.beans.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 1/8/2017.
 * 这个类的设计，有点问题。现在这里标记下，以后在回来修改
 */
public class FileResource implements Resource {

    public final File file;

    public final String className;

    public FileResource(File file, String className) {
        this.file = file;
        this.className = className;
    }

    public File getFile() {
        return this.file;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileResource that = (FileResource) o;

        return file != null ? file.equals(that.file) : that.file == null;

    }

    @Override
    public int hashCode() {
        return file != null ? file.hashCode() : 0;
    }
}
