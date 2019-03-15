package org.aion4j.avm.idea.framework;

import com.intellij.openapi.diagnostic.Logger;

import java.io.*;

public class FrameworkInitializer {

    private final static String AVM_RESOURCE_FOLDER = "/lib/avm";
    private final static String AVM_JAR = "avm.jar";
    public final static String AVM_API_JAR = "org-aion-avm-api.jar";
    public final static String AVM_USERLIB_JAR = "org-aion-avm-userlib.jar";
    private final static String VERSION_FILE = "version";

    private String projectFolder;
    private String pluginFolder;
    private Logger log;

    public FrameworkInitializer(String projectFolder, String pluginPath) {
       this.projectFolder = projectFolder;
       this.pluginFolder = pluginPath + File.separatorChar + "lib";
    }

    public String getAvmLibDir() {
        return projectFolder + File.separatorChar + "lib";
    }


    public void execute() throws AvmFrameworkException {

        String libFolderPath = getAvmLibDir();
        File libFolder = new File(libFolderPath);

        if(!libFolder.exists()) {
            libFolder.mkdirs();

            if(!libFolder.exists()) {
                throw new AvmFrameworkException("Unable to create library folder %s. "
                        + "Please check the directory permission and try again." + libFolderPath, null);
            }
        }

        //only copy version file if the libraries are copied from bundled version
        boolean bundledVersion = false;
        if (!checkIfLibExists(AVM_API_JAR)) {
            log.info(String
                    .format("%s doesn't exist. Copying the default %s to %s folder.", AVM_API_JAR,
                            AVM_API_JAR, getAvmLibDir()));
            copyLibJar(AVM_API_JAR, pluginFolder + "/" + AVM_API_JAR,
                    getAvmLibDir());
            bundledVersion = true;
        }

        if (!checkIfLibExists(AVM_USERLIB_JAR)) {
            log.info(String
                    .format("%s doesn't exist. Copying the default %s to %s folder.", AVM_USERLIB_JAR,
                            AVM_USERLIB_JAR, getAvmLibDir()));
            copyLibJar(AVM_USERLIB_JAR, pluginFolder + "/" + AVM_USERLIB_JAR,
                    getAvmLibDir());
        }

//        if (!checkIfLibExists(VERSION_FILE) && bundledVersion) {
//            log.info(String
//                    .format("%s doesn't exist. Copying the default %s to %s folder.", VERSION_FILE,
//                            VERSION_FILE, getAvmLibDir()));
//            copyLibJar(VERSION_FILE, pluginFolder + "/" + AVM_API_JAR + "/" + VERSION_FILE,
//                    getAvmLibDir());
//        }
    }

    private boolean checkIfLibExists(String libFileName) {
        File libFile = new File(getAvmLibDir(), libFileName);

        return libFile.exists();
    }

    private String copyLibJar(String jarName, String jarFilePath, String destFolder)
            throws AvmFrameworkException {

        if (jarFilePath == null) {
            return null;
        }

        // Grab the file name
        String[] chopped = jarFilePath.split("\\/");
        String fileName = chopped[chopped.length - 1];

        // See if we already have the file
        if (checkIfLibExists(fileName)) {
            return null;
        }

        InputStream fileStream = null;
        OutputStream out = null;
        try {
            // Read the file we're looking for
            fileStream = new FileInputStream(new File(jarFilePath));//LocalAvmNode.class.getResourceAsStream(jarFilePath);

            if (fileStream == null) {
                throw new RuntimeException(String.format("%s is not found in the plugin jar. ", jarFilePath));
                //return null;
            }

            File targetFile
                    = new File(destFolder, fileName);

            out = new FileOutputStream(targetFile);

            // Write the file to the temp file
            byte[] buffer = new byte[1024];
            int len = fileStream.read(buffer);
            while (len != -1) {
                out.write(buffer, 0, len);
                len = fileStream.read(buffer);
            }

            // Return the path of this sweet new file
            return targetFile.getAbsolutePath();

        } catch (IOException e) {
            throw new AvmFrameworkException("Error copying " + jarName + "to " + destFolder, e);
        } finally {
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }
        }
    }
}
