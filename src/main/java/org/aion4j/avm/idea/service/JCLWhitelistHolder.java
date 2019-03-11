package org.aion4j.avm.idea.service;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;
import java.util.*;

public class JCLWhitelistHolder {

    private final static String CACHE_FILE = "aion4jJclwhitelist.cache";
    private static final Logger log = Logger.getInstance(JCLWhitelistHolder.class);

    private Map<String, Map<String, List<MethodDescriptor>>> jclWhitelist;
    private Map<String, Map<String, List<MethodDescriptor>>> jclWhitelistCache;

    public JCLWhitelistHolder() {
        jclWhitelist = new HashMap<>();
        jclWhitelistCache = new HashMap<>();

    }

    public void loadFromCache(Project project) {

        if(log.isDebugEnabled()) {
            log.debug("Try to load from cache meanwhile >>>>>>>>>>>>>");
        }

        jclWhitelistCache = readFromCache(getCacheFolder(project));
        if(jclWhitelistCache == null)
            jclWhitelistCache = new HashMap<>();

    }

    public void init(Project project, String jsonStr) {

        jclWhitelist.clear();
        parseAndLoad(jclWhitelist, jsonStr);

        if(log.isDebugEnabled()) {
            log.debug("JCL whitelist >>> " + jsonStr);
        }
        writeToCache(getCacheFolder(project), jsonStr);

    }

    private String getCacheFolder(Project project) {
        VirtualFile ideaDir = project.getProjectFile().getParent();
        return ideaDir.getCanonicalPath();
    }

    private void parseAndLoad(Map<String, Map<String, List<MethodDescriptor>>> map, String jsonStr) {
        JsonObject object = Json.parse(jsonStr).asObject();
        List<String> classNames = object.names();

        for (String className : classNames) {
            JsonArray methodArray = object.get(className).asArray();

            List<MethodDescriptor> methods = new ArrayList<>();

            map.put(className, new HashMap<>()); //add classname entry
            Iterator<JsonValue> it = methodArray.iterator();
            while (it.hasNext()) {
                JsonObject methodObj = it.next().asObject();

                Iterator<JsonValue> paramIt = methodObj.get("parameters").asArray().iterator();
                List<String> params = new ArrayList<>();
                paramIt.forEachRemaining(param -> params.add(param.asString()));

                String methodName = methodObj.get("name").asString();
                List<MethodDescriptor> methodList = map.get(className).get(methodName);

                if (methodList == null) {
                    methodList = new ArrayList<>();
                    map.get(className).put(methodName, methodList);
                }

                MethodDescriptor methodDescriptor = new MethodDescriptor(methodObj.get("name").asString(),
                        params, methodObj.get("isStatic").asBoolean());
                methodList.add(methodDescriptor);
            }
        }
    }

    public boolean isClassPresent(String clazz) {
        return isFromCache() ? jclWhitelistCache.containsKey(clazz) : jclWhitelist.containsKey(clazz);
    }

    public List<MethodDescriptor> getMethods(String clazz, String methodName) {
        if (isClassPresent(clazz))
            return isFromCache() ? jclWhitelistCache.get(clazz).getOrDefault(methodName, Collections.EMPTY_LIST) :
                    jclWhitelist.get(clazz).getOrDefault(methodName, Collections.EMPTY_LIST);
        else
            return Collections.EMPTY_LIST;
    }

    public int size() {
        return isFromCache() ? jclWhitelistCache.size() : jclWhitelist.size();
    }

    private boolean isFromCache() {
        return jclWhitelist.size() == 0 ? true : false;
    }

    private synchronized void writeToCache(String folder, String json) {

        try(ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(new File(folder + File.separator + CACHE_FILE)))) {
            outputStream.writeObject(jclWhitelist);
        } catch (IOException e) {
            log.error("Error writing to cache >> ", e);
        }
    }

    private synchronized Map<String, Map<String, List<MethodDescriptor>>> readFromCache(String folder) {
        FileInputStream fileInputStream = null;
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(folder + File.separator + CACHE_FILE)))) {

            return (Map<String, Map<String, List<MethodDescriptor>>>)inputStream.readObject();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            if(log.isDebugEnabled()) {
                log.debug("Error reading from cache >> ", e);
            }
            return null;
        }
    }


}
