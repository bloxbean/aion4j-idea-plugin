package org.aion4j.avm.idea.service;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;

import java.util.HashMap;
import java.util.Map;

@State(name="aion4j-application", reloadable = true, storages = @com.intellij.openapi.components.Storage("aion4j-application.xml"))
public class AvmApplicationCacheService implements PersistentStateComponent<AvmApplicationCacheService.State> {

    public static class State {
        public Map<String, String> archeTypes = new HashMap<>();  //org.aion4j:avm-archetype:0.17=Avm Archetype
        public long archeTypeLastUpdatedTime;
    }

    public AvmApplicationCacheService.State state;

    public AvmApplicationCacheService.State getState() {
        if(state == null)
            return new AvmApplicationCacheService.State();
        else
            return state;
    }

    public Map<String, String> getAllArchetypes() {
        return getState().archeTypes;
    }

    public void updateArchetypes(Map<String, String> archeTypes) {
        if(state == null)
            state = this.getState();

        getState().archeTypes.clear();
        getState().archeTypes.putAll(archeTypes);
        getState().archeTypeLastUpdatedTime = System.currentTimeMillis();
    }

    public long getArchetypeLastUpdateTime()
    {
        return getState().archeTypeLastUpdatedTime;
    }

    public void loadState(AvmApplicationCacheService.State state) {
        this.state = state;
    }

}
