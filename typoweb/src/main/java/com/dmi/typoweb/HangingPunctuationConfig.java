package com.dmi.typoweb;

import com.dmi.util.natv.UsedByNative;

import gnu.trove.list.array.TCharArrayList;
import gnu.trove.list.array.TFloatArrayList;

public class HangingPunctuationConfig {
    @UsedByNative
    final char[] startChars;
    @UsedByNative
    final float[] startCharsHangFactors;
    @UsedByNative
    final char[] endChars;
    @UsedByNative
    final float[] endCharsHangFactors;

    private HangingPunctuationConfig(char[] startChars, float[] startCharsHangFactors, char[] endChars, float[] endCharsHangFactors) {
        this.startChars = startChars;
        this.startCharsHangFactors = startCharsHangFactors;
        this.endChars = endChars;
        this.endCharsHangFactors = endCharsHangFactors;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final TCharArrayList startChars = new TCharArrayList();
        private final TFloatArrayList startCharsHangFactors = new TFloatArrayList();
        private final TCharArrayList endChars = new TCharArrayList();
        private final TFloatArrayList endCharsHangFactors = new TFloatArrayList();

        public Builder startChar(char ch, float hangFactor) {
            startChars.add(ch);
            startCharsHangFactors.add(hangFactor);
            return this;
        }

        public Builder endChar(char ch, float hangFactor) {
            endChars.add(ch);
            endCharsHangFactors.add(hangFactor);
            return this;
        }

        public HangingPunctuationConfig build() {
            return new HangingPunctuationConfig(
                    startChars.toArray(),
                    startCharsHangFactors.toArray(),
                    endChars.toArray(),
                    endCharsHangFactors.toArray()
            );
        }
    }
}
