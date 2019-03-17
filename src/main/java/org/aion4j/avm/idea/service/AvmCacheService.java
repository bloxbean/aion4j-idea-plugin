package org.aion4j.avm.idea.service;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import org.aion4j.avm.idea.action.InvokeParam;
import org.aion4j.avm.idea.misc.AvmTypeHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@State(name="aion4j-cache", reloadable = true, storages = @com.intellij.openapi.components.Storage("aion4j-cache.xml"))
public class AvmCacheService implements PersistentStateComponent<AvmCacheService.State> {
    private static int MAX_CACHE_ENTRY = 30;

    public static class State {
        public Map<String, List<String>> methodArgs = new HashMap<>();
    }

    public State state;

    public State getState() {
        if(state == null)
            return new State();
        else
            return state;
    }

    public String encodeMethod(PsiMethod method) {
        StringBuilder sb = new StringBuilder();

        String name = method.getName();
        sb.append(name);
        sb.append("-");
        for (PsiParameter param : method.getParameterList().getParameters()) {
            String type = param.getType().getCanonicalText();
            sb.append(type);
            sb.append(",");
        }

        return sb.toString();
    }

    public List<String> getArgsFromCache(@NotNull PsiMethod method) {
        String encMethod = encodeMethod(method);
        return getState().methodArgs.get(encMethod);
    }

    public void updateArgsToCache(@NotNull PsiMethod method, List<String> args) {
        String encMethod = encodeMethod(method);

        if(state == null)
            state = this.getState();

        if(state.methodArgs.size() >= MAX_CACHE_ENTRY) {
            state.methodArgs.clear();
        }

        state.methodArgs.put(encMethod, args);
    }

    public void loadState(State state) {
        this.state = state;
    }
}
