package org.aion4j.avm.idea.maven;

import com.intellij.openapi.components.ServiceManager;
import org.aion4j.avm.idea.service.AvmApplicationCacheService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenArchetype;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.*;

public class AVMArcheTypeUtil {

    //List of archetypes
    private final static String ARCHETYPES_URL = "https://bloxbean.github.io/aion4j-release/archetypes";

    public static void updateArcheTypeCache() {
        AvmApplicationCacheService applicationCacheService = ServiceManager.getService(AvmApplicationCacheService.class);

        if (applicationCacheService != null) {
            Properties props = AVMArcheTypeUtil.loadArchetypesFromUrl();
            if (props != null) {
                Map<String, String> propMap = new HashMap(props);
                applicationCacheService.updateArchetypes(propMap);
            }
        }
    }

    @NotNull
    public static List<MavenArchetype> getMavenArchetypesFromMap(Map<String, String> archeTypes) {
        List<MavenArchetype> archeTypeList = new ArrayList<>();
        for (Map.Entry<String, String> entry : archeTypes.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (key == null) continue;

            try {
                StringTokenizer st = new StringTokenizer(key, ",");

                if (st.countTokens() != 3)
                    continue;

                String gid = st.nextToken();
                String aid = st.nextToken();
                String version = st.nextToken();

                MavenArchetype archetype = new MavenArchetype(gid, aid, version, null, value);
                archeTypeList.add(archetype);
            } catch (Exception e) {

            }
        }
        return archeTypeList;
    }

    public static Properties loadArchetypesFromUrl() {

        Reader reader = null;
        try {
            URL url = new URL(ARCHETYPES_URL);
            InputStream in = url.openStream();
            reader = new InputStreamReader(in, "UTF-8"); // for example

            Properties prop = new Properties();
            prop.load(reader);

            return prop;

        } catch (Exception e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {

                }
            }
        }
    }
}
