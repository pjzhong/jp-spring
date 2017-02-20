package jp.spring.web.handler.impl;

import jp.spring.ioc.beans.factory.annotation.Value;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;
import jp.spring.web.handler.MultipartResolver;
import jp.spring.web.support.MultiPartRequest;
import jp.spring.web.support.MultipartFile;
import jp.spring.web.support.MultipartFiles;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 1/30/2017.
 * 负责处理文件上传的情况
 */
@Component
public class DefaultMultipartResolver implements MultipartResolver {

    @Value("upload.size")
    private Integer uploadSize = 4 * 1024 * 1024; //4M;
    private boolean isInitialized = false;
    private ServletFileUpload fileUpload;

    public void init(ServletContext servletContext) {
        if(isInitialized) {
           return;
        }
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        fileUpload = new ServletFileUpload(new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, repository));
        fileUpload.setFileSizeMax(uploadSize);
        isInitialized = true;
    }

    @Override
    public boolean isMultiPart(HttpServletRequest request) {
        return ServletFileUpload.isMultipartContent(request);
    }

    @Override
    public MultiPartRequest resolveMultipart(HttpServletRequest request) {
        init(request.getServletContext());
        MultiPartRequest multiPartRequest = new MultiPartRequest(request);
        MultipartFiles multipartFiles = null;
        //for field and files
        Map<String, String[]> multipartFieldMap = new HashMap<String, String[]>();
        List<MultipartFile> multiParts = new ArrayList<>();

        List<FileItem> fileItemList;
        try {
            fileItemList = fileUpload.parseRequest(request);
        } catch (FileUploadException e) {
            throw new RuntimeException(e);
        }

        for(FileItem fileItem : fileItemList) {
            if(fileItem.isFormField()) {
                String name = fileItem.getFieldName();
                String value = fileItem.getString();

                String[] curParam = multipartFieldMap.get(name);
                if(multipartFieldMap.get(name) == null) {
                    multipartFieldMap.put(name, new String[] {value});
                } else {
                    curParam = StringUtils.addStringToArray(curParam, value);
                    multipartFieldMap.put(name, curParam);
                }
            } else {
                MultipartFile multipartFile = new MultipartFile(fileItem);
                multiParts.add(multipartFile);
            }
        }

        if(!JpUtils.isEmpty(multiParts)) {
            multipartFiles = new MultipartFiles(multiParts);
        }

        multiPartRequest.setMultipartFiles(multipartFiles);
        multiPartRequest.setMultipartParameterMap(multipartFieldMap);
        return multiPartRequest;
    }

    public void setUploadSize(Integer uploadSize) {
        this.uploadSize = uploadSize * 1024 * 1024;
    }
}
