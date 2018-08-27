package org.talend.dataquality.common.regex;

public class GreekLetters {

    public static final String range =
            // Unicode Characters in the Greek and Coptic Block
            "\\u0370|\\u0372|\\u0374|\\u0376|\\u037F|\\u0386|\\u0389|\\u038A|\\u038C|\\u038E|\\u038F|\\u0391-\\u03AB|\\u03CF|\\u03D2-\\u03D4|\\u03D8|\\u03DA|\\u03DC|\\u03DE|\\u03E0|\\u03E2|\\u03E4|\\u03E6|\\u03E8|\\u03EA|\\u03EC|\\u03EE|\\u03F4|\\u03F7|\\u03F9|\\u03FA|\\u03FD-\\u03FF"
                    // Unicode Characters in the Greek Extended Block
                    + "\\u1F08-\\u1F0F|\\u1F18-\\u1F1D|\\u1F28-\\u1F2F|\\u1F38-\\u1F3F|\\u1F48-\\u1F4D|\\u1F59|\\u1F5B|\\u1F5D|\\u1F5F|\\u1F68-\\u1F6F|\\u1F88-\\u1F8F|\\u1F98-\\u1F9F|\\u1FA8-\\u1FAF|\\u1FB8-\\u1FBC|\\u1FC8-\\u1FCC|\\u1FD8-\\u1FDB|\\u1FE8-\\u1FEC|\\u1FF8-\\u1FFC";
}
