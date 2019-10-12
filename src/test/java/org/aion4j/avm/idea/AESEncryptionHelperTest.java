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

package org.aion4j.avm.idea;

import org.aion4j.avm.idea.misc.AESEncryptionHelper;
import org.junit.Assert;
import org.junit.Test;

public class AESEncryptionHelperTest {

    @Test
    public void testEncryption() {
        String key = "AFyhdsf#$#1HUbXJhkwei@786&&423";

        AESEncryptionHelper encryptionHelper = new AESEncryptionHelper(key);
        String message = "c284dfd2de46acd652b2d72adf0467d0674c9bc9a7c756596dcb5ce39bdbc20f34458d527f3981728cff45e75b53dafa23a5fb8a85ddef65e051332e4980acfc";
        String encryptedMessage = encryptionHelper.encrypt(message);

        System.out.println(encryptedMessage);

        String decryptedMessage = encryptionHelper.decrypt(encryptedMessage);
        System.out.println(decryptedMessage);

        Assert.assertEquals(message, decryptedMessage);
    }
}
