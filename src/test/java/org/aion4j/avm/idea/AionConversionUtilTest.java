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

import org.aion4j.avm.idea.misc.AionConversionUtil;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class AionConversionUtilTest {

    @Test
    public void testAionTonAmp() {
        BigInteger bi = AionConversionUtil.aionTonAmp(5);

        Assert.assertEquals(new BigInteger("5000000000000000000"), bi);
    }

    @Test
    public void testAionTonAmp2() {
        BigInteger bi = AionConversionUtil.aionTonAmp(100000);

        Assert.assertEquals(new BigInteger("100000000000000000000000"), bi);
    }

    @Test
    public void testAionTonAmp1() {
        BigInteger bi = AionConversionUtil.aionTonAmp(.0005);

        Assert.assertEquals(new BigInteger("500000000000000"), bi);
    }
}
