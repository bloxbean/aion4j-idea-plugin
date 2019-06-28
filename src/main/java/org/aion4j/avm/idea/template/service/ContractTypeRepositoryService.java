/*
 * Copyright (c) 2019 BloxBean Project
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

package org.aion4j.avm.idea.template.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ContractTypeRepositoryService {

    private final static Logger log = Logger.getInstance(ContractTypeRepositoryService.class);

    private final String[] ctrMetas = {"ats-metadata.json", "ownable-metadata.json"};

    private List<ContractType> contractTypes = new ArrayList();
    private boolean initialized;

    public void init() {

        for(String metadata: ctrMetas) {
            initialize(metadata);
        }
        initialized = true;
    }

    private void initialize(String metadata) {

        try {
            URL url = this.getClass().getResource("/contracts/" + metadata);
            ObjectMapper mapper = new ObjectMapper();
            ContractType  contractType = mapper.readValue(url, ContractType.class);

            if(contractType != null)
                contractTypes.add(contractType);
        } catch (Exception e) {
            log.error("Error reading ACM contract template metadata", e);
        }
    }

    public List<ContractType> getContractTypes() {
        return contractTypes;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public ContractType getContractType(String id) {
        for(ContractType contractType: contractTypes) {
            if(id.equals(contractType.getId())) {
                return contractType;
            }
        }
        return null;
    }
}
