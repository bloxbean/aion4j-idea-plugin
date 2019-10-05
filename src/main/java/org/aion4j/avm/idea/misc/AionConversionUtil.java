/*
 * Copyright (c) 2019 Aion4J Project
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

package org.aion4j.avm.idea.misc;

import java.math.BigDecimal;
import java.math.BigInteger;

public class AionConversionUtil {
    private static final BigInteger ONE_AION = new BigInteger("1000000000000000000"); //1 Aion

    public static BigInteger aionTonAmp(double aion) {
        BigDecimal bigDecimalAmt = new BigDecimal(aion);
        BigDecimal nAmp = new BigDecimal(ONE_AION).multiply(bigDecimalAmt);

        return nAmp.toBigInteger();
    }

    public static float nAmpToAion(double nAmp) {
        BigDecimal bigDecimalAmt = new BigDecimal(nAmp);
        float aion = bigDecimalAmt.divide(new BigDecimal(ONE_AION)).floatValue();

        return aion;
    }

    public static float nAmpToAion(BigInteger nAmp) {
        BigDecimal bigDecimalAmt = new BigDecimal(nAmp);
        float aion = bigDecimalAmt.divide(new BigDecimal(ONE_AION)).floatValue();

        return aion;
    }
}
