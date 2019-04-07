package org.aion4j.avm.idea.maven;

import org.aion4j.avm.idea.misc.PluginConfig;
import org.jetbrains.idea.maven.indices.MavenArchetypesProvider;
import org.jetbrains.idea.maven.model.MavenArchetype;
import com.intellij.openapi.diagnostic.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AVMArchTypeProvider implements MavenArchetypesProvider {
    private final static Logger log = Logger.getInstance(AVMArchTypeProvider.class);

    private final static String LATEST_RELEASE_VERSION_URL = "https://bloxbean.github.io/aion4j-release/avm-archetype";
    private final String AVM_ARCHTYPE_VERSION = "0.11";

    @Override
    public Collection<MavenArchetype> getArchetypes() {

        String version = readArchTypeVersion();
        List<MavenArchetype> archetypes = new ArrayList<>();

        MavenArchetype archetype = new MavenArchetype("org.aion4j", "avm-archetype", version,
                null, "AVM Smart Contract Archetype");

        archetypes.add(archetype);

        try {
            String latestGitVersion = readLatestReleaseVersionFromGitHub();
            if (latestGitVersion != null && !latestGitVersion.equals(AVM_ARCHTYPE_VERSION)) {
                MavenArchetype latestArcheType = new MavenArchetype("org.aion4j", "avm-archetype", latestGitVersion,
                        null, "AVM Smart Contract Archetype");
                archetypes.add(0,latestArcheType);
            }
        } catch (Exception e) {}

        return archetypes;
    }

    private String readArchTypeVersion() {
        return AVM_ARCHTYPE_VERSION;
//        try {
//            return PluginConfig.getOrUpdateVersionIfRequired(AVM_ARCHTYPE_VERSION);
//        } catch (Exception e) {
//            if(log.isDebugEnabled()) {
//                log.debug("Error reading AVM Arch type version", e);
//            }
//
//            return AVM_ARCHTYPE_VERSION;
//        }
    }

    private String readLatestReleaseVersionFromGitHub() {
        BufferedReader in = null;
        try {
            URL gitUrl = new URL(LATEST_RELEASE_VERSION_URL);
            URLConnection yc = gitUrl.openConnection();
            yc.setConnectTimeout(2000);
            yc.setReadTimeout(2000);
            in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
            String inputLine = in.readLine();

            return inputLine != null? inputLine.trim() : null;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (Exception e) {

            }
        }
    }
}
