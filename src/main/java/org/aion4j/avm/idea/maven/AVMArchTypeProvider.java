package org.aion4j.avm.idea.maven;

import org.aion4j.avm.idea.misc.PluginConfig;
import org.jetbrains.idea.maven.indices.MavenArchetypesProvider;
import org.jetbrains.idea.maven.model.MavenArchetype;
import com.intellij.openapi.diagnostic.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AVMArchTypeProvider implements MavenArchetypesProvider {
    private final static Logger log = Logger.getInstance(AVMArchTypeProvider.class);

    private final String AVM_ARCHTYPE_VERSION = "0.9";

    @Override
    public Collection<MavenArchetype> getArchetypes() {

        String version = readArchTypeVersion();
        List<MavenArchetype> archetypes = new ArrayList<>();

        MavenArchetype archetype = new MavenArchetype("org.aion4j", "avm-archetype", version,
                null, "AVM Smart Contract Archetype");

        archetypes.add(archetype);

        return archetypes;
    }

    private String readArchTypeVersion() {
        try {
            return PluginConfig.getOrUpdateVersionIfRequired(AVM_ARCHTYPE_VERSION);
        } catch (Exception e) {
            if(log.isDebugEnabled()) {
                log.debug("Error reading AVM Arch type version", e);
            }

            return AVM_ARCHTYPE_VERSION;
        }
    }
}
