package jp.spring.mvc.support;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 1/30/2017.
 */
public class MultipartFiles implements Iterable<MultipartFile>{

    List<MultipartFile> multipartFiles;

    public MultipartFiles(List<MultipartFile> multipartFiles) {
        this.multipartFiles = multipartFiles;
    }

    public int size() {
        return multipartFiles.size();
    }

    public List<MultipartFile> getAll() {
        return multipartFiles;
    }

    @Override
    public Iterator<MultipartFile> iterator() {
        return multipartFiles.iterator();
    }
}
