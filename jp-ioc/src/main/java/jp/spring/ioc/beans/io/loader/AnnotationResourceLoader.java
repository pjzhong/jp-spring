package jp.spring.ioc.beans.io.loader;

import jp.spring.ioc.beans.io.Resource;
import jp.spring.ioc.beans.io.ResourceLoader;
import jp.spring.ioc.beans.io.resources.FileResource;
import jp.spring.ioc.util.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Administrator on 1/8/2017.
 */
public class AnnotationResourceLoader implements ResourceLoader {

    @Override
    public Resource[] getResource(String location) {
        return findClassFile(location);
    }

    /**
     *
     * @param pkgName 支持多folder, 以";"分隔
     * @return
     * @throws Exception*/
    protected static FileResource[] findClassFile(String pkgName) {
        if(StringUtils.isEmpty(pkgName)) {
            return null;
        }

        Set<FileResource> list = new LinkedHashSet<FileResource>();
        Set<FileResource> classFiles = null;

        String[] pkgs = pkgName.split("\\s*;\\s*");
        for(String pkg : pkgs) {
            if(!pkg.isEmpty()) {
                classFiles = getClassFile(pkg);
                list.addAll(classFiles);
            }
        }

        return list.toArray(new FileResource[list.size()]);
    }

    /**
     * Get all class from this package
     * @param pkg
     */
    protected static Set<FileResource> getClassFile(String pkg) {
        Set<FileResource> classes = new LinkedHashSet<FileResource>();
        boolean recursive = true;
        String pkgDirName = pkg.replace(".", "/");
        try {
            URL url = AnnotationResourceLoader.class.getClassLoader().getResource(pkgDirName);
            if(null == url) { return classes; }

            String protocol = url.getProtocol();
            if("file".equals(protocol)) { // 如果是以文件的形式保存在服务器上
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8"); // 获取包的物理路径
                findClazzsByFile(pkg, filePath, recursive, classes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * pkgName look like this —— com.zjp.pkg
     * pkgPath look like this —— com/zjp/pkg
     */
    protected static void findClazzsByFile(String pkgName, String pkgPath, final boolean recursive, Set<FileResource> classFiles) {
        File dir = new File(pkgPath);
        if(!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });

        String className;
        Class<?> clazz;
        for(File file : files) {
            if(file.isDirectory()) {
                findClazzsByFile(pkgName + "." + file.getName(), file.getAbsolutePath(), recursive, classFiles);
            } else {
                 className = file.getName();
                 className = className.substring(0, className.length() - 6); // ".class".length = 6
                 className = pkgName + "." + className;
                classFiles.add(new FileResource(file, className));
            }
        }
    }
}
