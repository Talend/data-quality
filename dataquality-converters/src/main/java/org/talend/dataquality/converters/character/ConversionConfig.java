package org.talend.dataquality.converters.character;

/**
 * configuration object for CharWidthConverter, with a fluid builder.
 */
public class ConversionConfig {

    public enum ConversionMode {
        NFKC,
        HALF_TO_FULL,
        FULL_TO_HALF,
    }

    private ConversionMode mode;

    private boolean convertDigit;

    private boolean convertKatakana;

    private boolean convertLetter;

    private boolean convertOtherChars;

    public static class Builder {

        private ConversionConfig config;

        public Builder() {
            this.config = new ConversionConfig();
        }

        public Builder nfkc() {
            config.setMode(ConversionMode.NFKC);
            return this;
        }

        public Builder halfToFull() {
            config.setMode(ConversionMode.HALF_TO_FULL);
            return this;
        }

        public Builder fullTofhalf() {
            config.setMode(ConversionMode.FULL_TO_HALF);
            return this;
        }

        public Builder withDigit(boolean withDigit) {
            config.setConvertDigit(withDigit);
            return this;
        }

        public Builder withKatanana(boolean withKatakana) {
            config.setConvertKatakana(withKatakana);
            return this;
        }

        public Builder withLetter(boolean withLetter) {
            config.setConvertLetter(withLetter);
            return this;
        }

        public Builder withOtherChars(boolean withOtherChars) {
            config.setConvertOtherChars(withOtherChars);
            return this;
        }

        public ConversionConfig build() {
            if (config.getMode() == null) {
                throw new IllegalArgumentException("The conversion mode must be specified!");
            }
            return config;
        }
    }

    public ConversionMode getMode() {
        return mode;
    }

    public void setMode(ConversionMode mode) {
        this.mode = mode;
    }

    public boolean isConvertDigit() {
        return convertDigit;
    }

    public void setConvertDigit(boolean convertDigit) {
        this.convertDigit = convertDigit;
    }

    public boolean isConvertKatakana() {
        return convertKatakana;
    }

    public void setConvertKatakana(boolean convertKatakana) {
        this.convertKatakana = convertKatakana;
    }

    public boolean isConvertLetter() {
        return convertLetter;
    }

    public void setConvertLetter(boolean convertLetter) {
        this.convertLetter = convertLetter;
    }

    public boolean isConvertOtherChars() {
        return convertOtherChars;
    }

    public void setConvertOtherChars(boolean convertOtherChars) {
        this.convertOtherChars = convertOtherChars;
    }
}
