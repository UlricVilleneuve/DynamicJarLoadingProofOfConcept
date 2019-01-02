package main.java;

import com.ProofOfConcept.ServiceContracts.IApplicationModule;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ServiceLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class main {
    public static void main(String[] args) {
        String folderPath = "/home/ulric/IdeaProjects/ProofOfConceptDynamicJarLoading/src/main/java/Modules";

        final File folder = new File(folderPath);

        var jarFiles = loadJarFiles(folder);

        Enumeration<JarEntry> entries;
        try (JarFile jarFile = new JarFile(jarFiles.get(0))) {
            entries = jarFile.entries();
            URL[] urls = new URL[]{new URL("jar:file:" + jarFiles.get(0).getAbsolutePath() + "!/")};

            URLClassLoader cl = URLClassLoader.newInstance(urls);

            /*while(entries.hasMoreElements()) {
                JarEntry je2 = entries.nextElement();
                String className2 = je2.getName();
            }*/

            while (entries.hasMoreElements()) {
                JarEntry je = entries.nextElement();
                if (je.isDirectory() || !je.getName().endsWith(".class")) {
                    continue;
                }
                // -6 because of .class
                String className = je.getName().substring(0, je.getName().length() - 6);
                className = className.replace('/', '.');
                Class c = cl.loadClass(className);
                if(IApplicationModule.class.isAssignableFrom(c))
                {
                    System.out.println(String.format("Found class inheriting IApplicationModule : %s", c.toString()));
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            URL myJarFile = new URL("jar", "", String.format("file:%s", jarFiles.get(0).getName()));
            URL[] myJars = {myJarFile};
            URLClassLoader child = new URLClassLoader (myJars , main.class.getClassLoader());
            Class classToLoad = Class.forName ("com.ProofOfConcept.ServiceContracts.IApplicationModule", true, child);

            ServiceLoader<IApplicationModule> loader = ServiceLoader.load(IApplicationModule.class);
            ServiceLoader<IApplicationModule> loader1 = ServiceLoader.load(classToLoad, child);
            var test = loader1;
            for (IApplicationModule module : loader) {
                System.out.println("Found class.");
                System.out.println(module.getClass());
                module.Load();
            }

            Method method = classToLoad.getDeclaredMethod ("Load");
            Object instance = classToLoad.newInstance ();
            Object result = method.invoke (instance);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static List<File> loadJarFiles(final File folder) {
        var jarFiles = new ArrayList<File>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                loadJarFiles(fileEntry);
            } else {
                if(GetExtension(fileEntry).equals("jar"))
                {
                    jarFiles.add(fileEntry);
                }
            }
        }
        return jarFiles;
    }

    private static String GetExtension(final File file)
    {
        String extension = "";

        int i = file.getName().lastIndexOf('.');
        if (i > 0) {
            extension = file.getName().substring(i+1);
        }

        return extension;
    }
}
