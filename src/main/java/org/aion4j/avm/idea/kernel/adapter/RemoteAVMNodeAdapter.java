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

package org.aion4j.avm.idea.kernel.adapter;

import com.intellij.openapi.diagnostic.Logger;
import kong.unirest.*;
import kong.unirest.json.JSONObject;
import org.aion4j.avm.idea.kernel.exception.KernelException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RemoteAVMNodeAdapter {
    private final static Logger log = Logger.getInstance(RemoteAVMNodeAdapter.class);

    private String web3RpcUrl;

    public RemoteAVMNodeAdapter(String web3RpcUrl) {
        this.web3RpcUrl = web3RpcUrl;
    }

    public BigInteger getBalance(String address) {
        if (address == null || address.isEmpty()) {
            return null;
        }

        String balanceInHex = getBalanceFromKernel(address);

        if (balanceInHex != null && !balanceInHex.isEmpty()) {
            if (balanceInHex.startsWith("0x"))
                balanceInHex = balanceInHex.substring(2);

            BigInteger balance = new BigInteger(balanceInHex, 16);
            return balance;

        } else {
            return null;
        }
    }

    private String getBalanceFromKernel(String address) {

        try {
            JSONObject jo = getJsonHeader("eth_getBalance");

            List<String> params = new ArrayList();
            params.add(address);
            params.add("latest");

            jo.put("params", params);

            if(log.isDebugEnabled())
                log.info("Web3Rpc request data: \n" + jo.toString());

            HttpResponse<JsonNode> jsonResponse = getHttpRequest()
                    .body(jo)
                    .asJson();

            JsonNode jsonNode = jsonResponse.getBody();

            if(log.isDebugEnabled())
                log.debug("Get status code >>> " + jsonResponse.getStatus());

            if(jsonNode == null)
                return null;

            log.info("Response from Aion kernel: \n " + jsonNode.getObject().toString(2));

            JSONObject jsonObject = jsonNode.getObject();

            String error = getError(jsonObject);

            if(error == null) {
                return jsonObject.optString("result");
            } else {
                throw new KernelException("get-balance failed. Reason: " + error);
            }

        } catch (UnirestException e) {
            throw new KernelException("Web3Rpc call failed for get balance", e);
        }
    }

    private String getError(JSONObject jsonObject) {
        JSONObject error = jsonObject.optJSONObject("error");

        if(error != null)
            return error.toString();
        else
            return null;
    }

    private JSONObject getJsonHeader(String method) {
        JSONObject jo = new JSONObject();
        jo.put("id", generateRandomId());
        jo.put("jsonrpc", "2.0");
        jo.put("method", method);
        return jo;
    }

    private String generateRandomId() {
        Random rand = new Random();
        int randInt = rand.nextInt(1000000);
        return String.valueOf(randInt);
    }

    private HttpRequestWithBody getHttpRequest() {
        return Unirest.post(web3RpcUrl)
                .header("accept", "application/json")
                .header("Content-Type", "application/json");

        //.queryString("apiKey", "123")
    }

    public static void main(String[] args) {
        RemoteAVMNodeAdapter adapter = new RemoteAVMNodeAdapter("https://aion.api.nodesmith.io/v1/mastery/jsonrpc?apiKey=c065185feb5144529397de3d540e2fe4");
        BigInteger balance = adapter.getBalance("0xa092de3423a1e77f4c5f8500564e3601759143b7c0e652a7012d35eb67b283ca");
        System.out.println(balance);
    }
}
