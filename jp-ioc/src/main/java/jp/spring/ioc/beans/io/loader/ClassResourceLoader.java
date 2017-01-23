package jp.spring.ioc.beans.io.loader;

import jp.spring.ioc.beans.io.Resource;
import jp.spring.ioc.beans.io.ResourceLoader;
import jp.spring.ioc.beans.io.resources.ClassResource;
import jp.spring.ioc.util.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Administrator on 1/8/2017.
 */
public class ClassResourceLoader implements ResourceLoader {

    @Override
    public Resource[] getResource(String location) {
        return findClassFile(location);
    }

    /**
     *
     * @param pkgName 支持多folder, 以";"分隔
     * @return
     * @throws Exception*/
    protected static ClassResource[] findClassFile(String pkgName) {
        if(StringUtils.isEmpty(pkgName)) {
            return null;
        }

        Set<ClassResource> list = new LinkedHashSet<ClassResource>();
        Set<ClassResource> classFiles = null;

        String[] pkgs = pkgName.split("\\s*;\\s*");
        for(String pkg : pkgs) {
            if(!pkg.isEmpty()) {
                classFiles = getClassFile(pkg);
                list.addAll(classFiles);
            }
        }

        return list.toArray(new ClassResource[list.size()]);
    }

    /**
     * Get all class from this package
     * @param pkg
     */
    protected static Set<ClassResource> getClassFile(String pkg) {
        Set<ClassResource> classes = new LinkedHashSet<ClassResource>();
        boolean recursive = true;
        String pkgDirName = pkg.replace(".", "/");
        try {
            URL url = ClassResourceLoader.class.getClassLoader().getResource(pkgDirName);
            if(null == url) { return classes; }

            String protocol = url.getProtocol();
            if("file".equals(protocol)) { // 如果是以文件的形式保存在服务器上
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8"); // 获取包的物理路径
                findClazzsByFile(pkg, filePath, recursive, classes);
            } else if ("jar".equals(protocol)) {
                JarFile jar = ((JarURLConnection)url.openConnection()).getJarFile();
                findClassByJar(pkg, jar, recursive, classes);
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
    protected static void findClazzsByFile(String pkgName, String pkgPath, final boolean recursive, Set<ClassResource> classFiles) {
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
                classFiles.add(new ClassResource(file, className));
            }
        }
    }

    public static void findClassByJar(String pkgName, JarFile jar, final boolean recursive, Set<ClassResource> classResource) {
        String packageDirName = pkgName.replace(".", "/");

        Enumeration<JarEntry> jarEntries = jar.entries();
        JarEntry jarEntry;
        String name, className;
        while(jarEntries.hasMoreElements()) {
            jarEntry = jarEntries.nextElement();
            name = jarEntry.getName();
            if(name.charAt(0) == '/') {
                name = name.substring(1);
            }

            if(name.startsWith(packageDirName)) {
                int index = name.lastIndexOf('/');
                if(index > -1) {
                    pkgName = name.substring(0, index).replace('/', '.');
                }

                if((index > -1) || recursive) {
                    if(name.endsWith(".class") && !jarEntry.isDirectory()) {
                        className = name.substring(pkgName.length() + 1, name.length() - 6); // ".class".length = 6
                        className = pkgName + "." + className;
                        classResource.add(new ClassResource(null, className));
                    }
                }
            }
        }
    }
}
