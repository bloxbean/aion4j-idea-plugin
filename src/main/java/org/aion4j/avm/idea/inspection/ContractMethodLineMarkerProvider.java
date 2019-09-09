/*
 * Copyright (c) 2019 Aion4j Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.aion4j.avm.idea.inspection;

import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import org.aion4j.avm.idea.misc.AvmIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContractMethodLineMarkerProvider extends RunLineMarkerContributor {
    @Nullable
    @Override
    public Info getInfo(@NotNull PsiElement element) {
        if(element instanceof PsiMethod) {

            PsiMethod method = (PsiMethod)element;
            PsiAnnotation callableAnnotation = method.getAnnotation("org.aion.avm.tooling.abi.Callable");

            if (callableAnnotation != null) {
               // LineMarkerInfo lineMarkerInfo = new LineMarkerInfo();
                ActionManager am = ActionManager.getInstance();
                AnAction localCall = am.getAction("Avm.local.gutter.LocalCallAction");
                AnAction debugAction = am.getAction("Avm.local.gutter.Debug");
                AnAction remoteCall = am.getAction("Avm.remote.gutter.CallMethodAction");
                AnAction remoteTxn = am.getAction("Avm.remote.gutter.ContractTxnAction");

                return new RunLineMarkerContributor.Info(AvmIcons.CALLABLE_GUTTER_ICON,
                        (psiElement -> ((PsiMethod)psiElement).getName()), localCall, debugAction, remoteCall, remoteTxn);
            }
        }

        return null;
    }
}
