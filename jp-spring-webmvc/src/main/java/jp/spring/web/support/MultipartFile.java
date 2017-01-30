package jp.spring.web.support;

import org.apache.commons.fileupload.FileItem;


/**
 * Created by Administrator on 1/30/2017.
 */
public class MultipartFile {

    private final FileItem fileItem;
    private final long size;

    public MultipartFile(FileItem fileItem) {
        this.fileItem = fileItem;
        this.size = this.fileItem.getSize();
    }

    public FileItem getFileItem() {
        return this.fileItem;
    }

    public String getName()  {
        return this.fileItem.getFieldName();
    }

    public String getOriginalFilename() {
        String filename = this.fileItem.getName();
        if(filename == null) {
            //Should never happen.
            return "";
        }

        //check for Unix-style path
        int pos = filename.lastIndexOf("/");
        if(pos == -1) {
            //check for Windows-style path
            pos = filename.lastIndexOf("\\");
        }
        if(pos != -1) {
            //any sort of path separator found
            return filename.substring(pos + 1);
        } else {
            //plain name
            return filename;
        }
    }

    public String getContentType() {
        return this.fileItem.getContentType();
    }

    public boolean isEmpty() {
        return (this.size == 0);
    }

    public long getSize() {
        return this.size;
    }

}
