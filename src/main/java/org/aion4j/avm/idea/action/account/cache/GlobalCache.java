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

package org.aion4j.avm.idea.action.account.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import org.aion4j.avm.idea.action.account.model.AccountCache;

import javax.crypto.SecretKey;
import java.io.*;
import java.util.Properties;

/**
 * This class is taken from aion4j-avm-helper project
 */
public class GlobalCache {
    private final static Logger log = Logger.getInstance(GlobalCache.class);
    public static String ACCOUNT_CACHE = ".aion4j.account.conf";

    //props in key file
    private final static String SECRET_KEY = "secret-key";
    private final static String PROTECTION_MODE = "protection-mode";

    private final String targetFolder;
    private ObjectMapper objectMapper;

    public GlobalCache(String targetFolder) {
        this.targetFolder = targetFolder;
        this.objectMapper = new ObjectMapper();
    }

    public AccountCache getAccountCache() {
        File file = getAccountCacheFile();
        File keyFile = getAccountCacheKeyFile();

        if(!file.exists())
            return new AccountCache();

        if(keyFile.exists()) {
            SecretKey secretKey = getSecretKeyFromFile();
            if(secretKey == null) {
                return new AccountCache();
            } else {
                try {
                    FileEncrypterDecrypter fileEncrypterDecrypter = new FileEncrypterDecrypter(secretKey);
                    String encContent = fileEncrypterDecrypter.decrypt(file);
                    return readAccountCacheFromJson(encContent);
                } catch (Exception e) {
                    log.warn("Account cache could not be read", e);
                    return new AccountCache();
                }
            }
        } else { //If not encrypted. Just to support older version and migration
            AccountCache accountCache = readAccountCacheFromFile(file);

            return accountCache;
        }
    }

    private AccountCache readAccountCacheFromJson(String content) {
        AccountCache accountCache = null;

        try {
            accountCache = objectMapper.readValue(content, AccountCache.class);
        } catch (Exception e) {
            accountCache = new AccountCache();
            //e.printStackTrace();
            log.warn("Could not read from account cache: " + e.getMessage());
            if(log.isDebugEnabled()) {
                log.error("Could not read from account cache", e);
            }
        }
        return accountCache;
    }

    private AccountCache readAccountCacheFromFile(File file) {
        AccountCache accountCache = null;

        try {
            accountCache = objectMapper.readValue(file, AccountCache.class);
        } catch (Exception e) {
            accountCache = new AccountCache();
            //e.printStackTrace();
            log.warn("Could not read from account cache: " + e.getMessage());
            if(log.isDebugEnabled()) {
                log.error("Could not read from account cache", e);
            }
        }
        return accountCache;
    }


    private SecretKey getSecretKeyFromFile() {
        try {
            File keyFile = getAccountCacheKeyFile();
            if (!keyFile.exists()) {
                return null;
            }

            Properties props = readKeyProperties(keyFile);
            String secretKey = (String) props.get(SECRET_KEY);

            if (secretKey != null && !secretKey.isEmpty()) {
                return FileEncrypterDecrypter.getSecretKeyFromEncodedKey(secretKey);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.warn("Invalid secret key.");
            log.debug("Invalid key content", e);
            return null;
        }
    }

    private File getAccountCacheFile() {
        return new File(targetFolder, ACCOUNT_CACHE);
    }

    private File getAccountCacheKeyFile() {
        return new File(targetFolder, ACCOUNT_CACHE + ".key");
    }

    private Properties readKeyProperties(File file) {
        InputStream input = null;

        try {

            input = new FileInputStream(file);

            Properties properties = new Properties();
            properties.load(input);

            return properties;

        } catch (Exception io) {
            io.printStackTrace();
            return new Properties();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
