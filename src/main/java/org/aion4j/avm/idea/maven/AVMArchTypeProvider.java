package org.aion4j.avm.idea.maven;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressManager;
import org.aion4j.avm.idea.misc.PluginConfig;
import org.aion4j.avm.idea.service.AvmApplicationCacheService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.indices.MavenArchetypesProvider;
import org.jetbrains.idea.maven.model.MavenArchetype;
import com.intellij.openapi.diagnostic.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class AVMArchTypeProvider implements MavenArchetypesProvider {
    private final static Logger log = Logger.getInstance(AVMArchTypeProvider.class);

    private final String AVM_ARCHTYPE_VERSION = "0.19";

    @Override
    public Collection<MavenArchetype> getArchetypes() {

        String version = readArchTypeVersion();
        List<MavenArchetype> archetypes = new ArrayList<>();

        MavenArchetype archetype = new MavenArchetype("org.aion4j", "avm-archetype", version,
                null, "AVM Smart Contract Archetype");

        archetypes.add(archetype);

        try {
            List<MavenArchetype> otherArcheTypes = getArcheTypesFromRepo();
            archetypes.addAll(otherArcheTypes);
        } catch (Exception e) {
            log.debug("Error fetching archetypes: ", e);
        }

        return archetypes;
    }

    private String readArchTypeVersion() {
        return AVM_ARCHTYPE_VERSION;
    }

    public List<MavenArchetype> getArcheTypesFromRepo() {

        AvmApplicationCacheService applicationCacheService = ServiceManager.getService(AvmApplicationCacheService.class);

        if(applicationCacheService != null) {

            long lastUpdatedTime = applicationCacheService.getArchetypeLastUpdateTime();
            long delay = System.currentTimeMillis() - lastUpdatedTime;

            if(delay > 1296000000) { //15 days

                if(lastUpdatedTime == 0) { //first time so fetch it
                    AVMArcheTypeUtil.updateArcheTypeCache();
                } else {
                    ApplicationManager.getApplication().invokeLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    AVMArcheTypeUtil.updateArcheTypeCache();
                                }
                            },
                            ModalityState.any()
                    );
                }

            }

            applicationCacheService = ServiceManager.getService(AvmApplicationCacheService.class);
            if(applicationCacheService == null)
                return Collections.EMPTY_LIST;

            Map<String, String> archeTypes = applicationCacheService.getAllArchetypes();

            if(archeTypes != null && archeTypes.size() != 0) {
                return AVMArcheTypeUtil.getMavenArchetypesFromMap(archeTypes);
            } else
                return Collections.EMPTY_LIST;
        } else
            return Collections.EMPTY_LIST;
    }

}
