package jp.spring.orm.utils;

import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class PackageUtils {

    /**
     *
     * @param pkgName 支持多folder, 以";"分隔
     * @param annotate
     * @return
     * @throws Exception*/
    public static <A extends Annotation> Set<Class<?>> findClazzsByAnnotation(String pkgName, Class<A> annotate) {
        if(StringUtils.isEmpty(pkgName)) {
            return null;
        }

        Set<Class<?>> list = new LinkedHashSet<Class<?>>();
        Set<Class<?>> clazzs = null;

        String[] pkgs = pkgName.split("\\s*;\\s*");
        for(String pkg : pkgs) {
            if(!pkg.isEmpty()) {
               clazzs = getClazzs(pkg);
               for(Class clazz : clazzs) {
                   if(clazz.isAnnotation()) {
                       continue;
                   }
                   if(JpUtils.isAnnotated(clazz, annotate)) {
                       list.add(clazz);
                   }
               }
            }
         }

        return list;
    }

    /**
     * Get all class from this package
     * @param pkg
     */
    public static Set<Class<?>> getClazzs(String pkg) {
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        boolean recursive = true;
        String pkgDirName = pkg.replace(".", "/");
        try {
            URL url = PackageUtils.class.getClassLoader().getResource(pkgDirName);
            if(null == url) { return classes; }

            String protocol = url.getProtocol();
            if("file".equals(protocol)) { // 如果是以文件的形式保存在服务器上
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8"); // 获取包的物理路径
                findClazzsByFile(pkg, filePath, recursive, classes);
            } else if ("jar".equals(protocol)) { // if is a jar file
                JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                findClazzsByJar(pkg, jar, recursive, classes);
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
    public static void findClazzsByFile(String pkgName, String pkgPath, final boolean recursive, Set<Class<?>> clazzs) {
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
                findClazzsByFile(pkgName + "." + file.getName(), file.getAbsolutePath(), recursive, clazzs);
            } else if(file.getName().endsWith(".class")) {
                className = file.getName();
                className = className.substring(0, className.length() - 6); // 去掉后面的".class"
                                                                       // 获取真正的类名
                clazz = getClazz(pkgName, className);
                if(null != clazz) {
                    clazzs.add(clazz);
                }
            }
        }
    }

    /**
     * @param pkgName example: com.jp.example
     * */
    public static void findClazzsByJar(String pkgName, JarFile jar, final boolean recursive, Set<Class<?>> clazzs) {
        String packageDirName = pkgName.replace(".", "/");

        Enumeration<JarEntry> jarEntries = jar.entries();
        JarEntry jarEntry;
        String name, className;
        Class<?> clazz;
        while(jarEntries.hasMoreElements()) {
            jarEntry = jarEntries.nextElement();
            name = jarEntry.getName();
            if(name.charAt(0) == '/') {
                name = name.substring(1);
            }
            if(name.startsWith(packageDirName)) {
                int index = name.lastIndexOf('/');
                if(index > -1) { // endWith '/' mean that is a package
                    pkgName = name.substring(0, index).replace('/', '.'); // get the packageName,
                                                                          // replace all '/' with '.'
                }
                if((index) > -1 || recursive) {
                    if(name.endsWith(".class") && !jarEntry.isDirectory()) {
                        className = name.substring(pkgName.length() + 1, name.length() - 6);// 去掉后面的".class"
                                                                                           // 获取真正的类名
                        clazz = getClazz(pkgName, className);
                        if(null != clazz) {
                            clazzs.add(clazz);
                        }
                    }
                }
            }
        }
    }

    public static Class<?> getClazz(String pkgName, String clazzName) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(pkgName + "." + clazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
