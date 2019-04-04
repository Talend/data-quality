// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking;

import org.talend.dataquality.datamasking.functions.Function;

/**
 * created by jgonzalez on 18 juin 2015 This class is the factory that will instanciate the correct function.
 *
 */
public class FunctionFactory<T> {

    @SuppressWarnings("unchecked")
    private Function<T> getFunction(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        return (Function<T>) clazz.newInstance();
    }

    private Function<T> getFunction3(FunctionType type, int javaType) throws InstantiationException, IllegalAccessException {
        Function<T> res;
        if (type == FunctionType.REPLACE_LAST_CHARS) {
            res = handleReplaceLastCharsFunction(javaType);
        } else if (type == FunctionType.REPLACE_NUMERIC) {
            res = handleReplaceNumericFunction(javaType);
        } else {
            res = getFunction(type.getClazz());
        }
        return res;
    }

    /**
     * Deal with replace numberic function case
     * 
     * @param javaType
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Function<T> handleReplaceNumericFunction(int javaType) throws InstantiationException, IllegalAccessException {
        Function<T> res;
        switch (javaType) {
        case 0:
            res = getFunction(FunctionType.REPLACE_NUMERIC_INT.getClazz());
            break;
        case 1:
            res = getFunction(FunctionType.REPLACE_NUMERIC_LONG.getClazz());
            break;
        case 2:
            res = getFunction(FunctionType.REPLACE_NUMERIC_FLOAT.getClazz());
            break;
        case 3:
            res = getFunction(FunctionType.REPLACE_NUMERIC_DOUBLE.getClazz());
            break;
        case 4:
            res = getFunction(FunctionType.REPLACE_NUMERIC_STRING.getClazz());
            break;
        default:
            res = null;
            break;
        }
        return res;
    }

    /**
     * Deal with replace last chars function case
     * 
     * @param javaType
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Function<T> handleReplaceLastCharsFunction(int javaType) throws InstantiationException, IllegalAccessException {
        Function<T> res;
        switch (javaType) {
        case 0:
            res = getFunction(FunctionType.REPLACE_LAST_CHARS_INT.getClazz());
            break;
        case 1:
            res = getFunction(FunctionType.REPLACE_LAST_CHARS_LONG.getClazz());
            break;
        case 4:
            res = getFunction(FunctionType.REPLACE_LAST_CHARS_STRING.getClazz());
            break;
        default:
            res = null;
            break;
        }
        return res;
    }

    private Function<T> getFunction2(FunctionType type, int javaType) throws InstantiationException, IllegalAccessException {
        Function<T> res;
        if (type == FunctionType.GENERATE_FROM_LIST_HASH) {
            res = handleGenerateFromListHashFunction(javaType);
        } else if (type == FunctionType.GENERATE_FROM_FILE_HASH) {
            res = handleGenerateFromFileHashFunction(javaType);
        } else if (type == FunctionType.GENERATE_SEQUENCE) {
            res = handleGenerateSequenceFunction(javaType);
        } else if (type == FunctionType.NUMERIC_VARIANCE) {
            res = handleNumbericVarianceFunction(javaType);
        } else if (type == FunctionType.REMOVE_FIRST_CHARS) {
            res = handleRemoveFirstCharsFunction(javaType);
        } else if (type == FunctionType.REMOVE_LAST_CHARS) {
            res = handleRemoveLastCharsFunction(javaType);
        } else if (type == FunctionType.REPLACE_FIRST_CHARS) {
            res = handleReplaceFirstCharsFunction(javaType);
        } else {
            res = getFunction3(type, javaType);
        }
        return res;
    }

    /**
     * Deal with replace first chars function case
     * 
     * @param javaType
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Function<T> handleReplaceFirstCharsFunction(int javaType) throws InstantiationException, IllegalAccessException {
        Function<T> res;
        switch (javaType) {
        case 0:
            res = getFunction(FunctionType.REPLACE_FIRST_CHARS_INT.getClazz());
            break;
        case 1:
            res = getFunction(FunctionType.REPLACE_FIRST_CHARS_LONG.getClazz());
            break;
        case 4:
            res = getFunction(FunctionType.REPLACE_FIRST_CHARS_STRING.getClazz());
            break;
        default:
            res = null;
            break;
        }
        return res;
    }

    /**
     * Deal with remove last chars function case
     * 
     * @param javaType
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Function<T> handleRemoveLastCharsFunction(int javaType) throws InstantiationException, IllegalAccessException {
        Function<T> res;
        switch (javaType) {
        case 0:
            res = getFunction(FunctionType.REMOVE_LAST_CHARS_INT.getClazz());
            break;
        case 1:
            res = getFunction(FunctionType.REMOVE_LAST_CHARS_LONG.getClazz());
            break;
        case 4:
            res = getFunction(FunctionType.REMOVE_LAST_CHARS_STRING.getClazz());
            break;
        default:
            res = null;
            break;
        }
        return res;
    }

    /**
     * Deal with remove first chars function case
     * 
     * @param javaType
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Function<T> handleRemoveFirstCharsFunction(int javaType) throws InstantiationException, IllegalAccessException {
        Function<T> res;
        switch (javaType) {
        case 0:
            res = getFunction(FunctionType.REMOVE_FIRST_CHARS_INT.getClazz());
            break;
        case 1:
            res = getFunction(FunctionType.REMOVE_FIRST_CHARS_LONG.getClazz());
            break;
        case 4:
            res = getFunction(FunctionType.REMOVE_FIRST_CHARS_STRING.getClazz());
            break;
        default:
            res = null;
            break;
        }
        return res;
    }

    /**
     * Deal with numberic variance function case
     * 
     * @param javaType
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Function<T> handleNumbericVarianceFunction(int javaType) throws InstantiationException, IllegalAccessException {
        Function<T> res;
        switch (javaType) {
        case 0:
            res = getFunction(FunctionType.NUMERIC_VARIANCE_INT.getClazz());
            break;
        case 1:
            res = getFunction(FunctionType.NUMERIC_VARIANCE_LONG.getClazz());
            break;
        case 2:
            res = getFunction(FunctionType.NUMERIC_VARIANCE_FLOAT.getClazz());
            break;
        case 3:
            res = getFunction(FunctionType.NUMERIC_VARIANCE_DOUBLE.getClazz());
            break;
        default:
            res = null;
            break;
        }
        return res;
    }

    /**
     * Deal with generate sequence function case
     * 
     * @param javaType
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Function<T> handleGenerateSequenceFunction(int javaType) throws InstantiationException, IllegalAccessException {
        Function<T> res;
        switch (javaType) {
        case 0:
            res = getFunction(FunctionType.GENERATE_SEQUENCE_INT.getClazz());
            break;
        case 1:
            res = getFunction(FunctionType.GENERATE_SEQUENCE_LONG.getClazz());
            break;
        case 2:
            res = getFunction(FunctionType.GENERATE_SEQUENCE_FLOAT.getClazz());
            break;
        case 3:
            res = getFunction(FunctionType.GENERATE_SEQUENCE_DOUBLE.getClazz());
            break;
        case 4:
            res = getFunction(FunctionType.GENERATE_SEQUENCE_STRING.getClazz());
            break;
        default:
            res = null;
            break;
        }
        return res;
    }

    /**
     * Deal with generate from file hash function case
     * 
     * @param javaType
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Function<T> handleGenerateFromFileHashFunction(int javaType) throws InstantiationException, IllegalAccessException {
        Function<T> res;
        switch (javaType) {
        case 0:
            res = getFunction(FunctionType.GENERATE_FROM_FILE_HASH_INT.getClazz());
            break;
        case 1:
            res = getFunction(FunctionType.GENERATE_FROM_FILE_HASH_LONG.getClazz());
            break;
        case 4:
            res = getFunction(FunctionType.GENERATE_FROM_FILE_HASH_STRING.getClazz());
            break;
        default:
            res = null;
            break;
        }
        return res;
    }

    /**
     * Deal with generate from List hash function case
     * 
     * @param javaType
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Function<T> handleGenerateFromListHashFunction(int javaType) throws InstantiationException, IllegalAccessException {
        Function<T> res;
        switch (javaType) {
        case 0:
            res = getFunction(FunctionType.GENERATE_FROM_LIST_HASH_INT.getClazz());
            break;
        case 1:
            res = getFunction(FunctionType.GENERATE_FROM_LIST_HASH_LONG.getClazz());
            break;
        case 4:
            res = getFunction(FunctionType.GENERATE_FROM_LIST_HASH_STRING.getClazz());
            break;
        default:
            res = null;
            break;
        }
        return res;
    }

    /**
     * Instantiate the data masking Function according to the function name, data type and method.
     * This method is mainly used by tDataMasking components in the studio.
     *
     * @param functionName the name of the function
     * @param javaType the data type
     * @param methodName the name of the method
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Function<T> getFunction(String functionName, int javaType, String methodName)
            throws InstantiationException, IllegalAccessException {
        // try to get the FunctionType if it is defined.
        FunctionType type = FunctionType.getByName(functionName);

        // if not defined in FunctionType, handle the specific names.
        if (type == null) {
            if (functionName.contains("EMAIL")) {
                if (FunctionMode.MASK_BY_CHARACTER.name().equals(methodName)) {
                    type = FunctionType.getByName(functionName + "_BY_X");
                } else if (FunctionMode.MASK_FROM_LIST.name().equals(methodName)) {
                    type = FunctionType.getByName(functionName + "_RANDOMLY");
                }
            } else if ("GENERATE_FROM_LIST_OR_FILE".equals(functionName)) {
                if (FunctionMode.RANDOM.name().equals(methodName)) {
                    type = FunctionType.GENERATE_FROM_FILE;
                } else if (FunctionMode.CONSISTENT.name().equals(methodName)) {
                    type = FunctionType.GENERATE_FROM_FILE_HASH;
                }
            }
        }
        if (type != null) {
            return getFunction(type, javaType);
        }
        throw new IllegalArgumentException("No function found with the given parameter.");
    }

    /**
     * DOC jgonzalez Comment method "getFunction". This function is used to res = the correct function according to the
     * user choice.
     * 
     * @param type The function requested by the user.
     * @param javaType Some functions work and several type, this parameter represents the wanted type.
     * @res = A new function.
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Function<T> getFunction(FunctionType type, int javaType) throws InstantiationException, IllegalAccessException {
        Function<T> res;
        if (type == FunctionType.KEEP_FIRST_AND_GENERATE) {
            res = handleKeepFirstAndGenerateFunction(javaType);
        } else if (type == FunctionType.KEEP_LAST_AND_GENERATE) {
            res = handleKeepLastAndGenerateFunction(javaType);
        } else if (type == FunctionType.GENERATE_BETWEEN) {
            res = handleGenerateBetweenFunction(javaType);
        } else if (type == FunctionType.GENERATE_CREDIT_CARD_FORMAT) {
            res = handleGenerateCreditCardFormatFunction(javaType);
        } else if (type == FunctionType.GENERATE_CREDIT_CARD) {
            res = handleGenerateCreditCardFunction(javaType);
        } else if (type == FunctionType.GENERATE_FROM_FILE) {
            res = handleGenerateFromFileFunction(javaType);
        } else if (type == FunctionType.GENERATE_FROM_LIST) {
            res = handleGenerateFromListFunction(javaType);
        } else {
            res = getFunction2(type, javaType);
        }
        return res;
    }

    /**
     * Deal with generate from List function case
     * 
     * @param javaType
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Function<T> handleGenerateFromListFunction(int javaType) throws InstantiationException, IllegalAccessException {
        Function<T> res;
        switch (javaType) {
        case 0:
            res = getFunction(FunctionType.GENERATE_FROM_LIST_INT.getClazz());
            break;
        case 1:
            res = getFunction(FunctionType.GENERATE_FROM_LIST_LONG.getClazz());
            break;
        case 4:
            res = getFunction(FunctionType.GENERATE_FROM_LIST_STRING.getClazz());
            break;
        default:
            res = null;
            break;
        }
        return res;
    }

    /**
     * Deal with generate from file function case
     * 
     * @param javaType
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Function<T> handleGenerateFromFileFunction(int javaType) throws InstantiationException, IllegalAccessException {
        Function<T> res;
        switch (javaType) {
        case 0:
            res = getFunction(FunctionType.GENERATE_FROM_FILE_INT.getClazz());
            break;
        case 1:
            res = getFunction(FunctionType.GENERATE_FROM_FILE_LONG.getClazz());
            break;
        case 4:
            res = getFunction(FunctionType.GENERATE_FROM_FILE_STRING.getClazz());
            break;
        default:
            res = null;
            break;
        }
        return res;
    }

    /**
     * Deal with generate credit card function case
     * 
     * @param javaType
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Function<T> handleGenerateCreditCardFunction(int javaType) throws InstantiationException, IllegalAccessException {
        Function<T> res;
        switch (javaType) {
        case 1:
            res = getFunction(FunctionType.GENERATE_CREDIT_CARD_LONG.getClazz());
            break;
        case 4:
            res = getFunction(FunctionType.GENERATE_CREDIT_CARD_STRING.getClazz());
            break;
        default:
            res = null;
            break;
        }
        return res;
    }

    /**
     * Deal with generate credit card format function case
     * 
     * @param javaType
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Function<T> handleGenerateCreditCardFormatFunction(int javaType)
            throws InstantiationException, IllegalAccessException {
        Function<T> res;
        switch (javaType) {
        case 1:
            res = getFunction(FunctionType.GENERATE_CREDIT_CARD_FORMAT_LONG.getClazz());
            break;
        case 4:
            res = getFunction(FunctionType.GENERATE_CREDIT_CARD_FORMAT_STRING.getClazz());
            break;
        default:
            res = null;
            break;
        }
        return res;
    }

    /**
     * Deal with generate between function case
     * 
     * @param javaType
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Function<T> handleGenerateBetweenFunction(int javaType) throws InstantiationException, IllegalAccessException {
        Function<T> res;
        switch (javaType) {
        case 0:
            res = getFunction(FunctionType.GENERATE_BETWEEN_INT.getClazz());
            break;
        case 1:
            res = getFunction(FunctionType.GENERATE_BETWEEN_LONG.getClazz());
            break;
        case 2:
            res = getFunction(FunctionType.GENERATE_BETWEEN_FLOAT.getClazz());
            break;
        case 3:
            res = getFunction(FunctionType.GENERATE_BETWEEN_DOUBLE.getClazz());
            break;
        case 4:
            res = getFunction(FunctionType.GENERATE_BETWEEN_STRING.getClazz());
            break;
        case 5:
            res = getFunction(FunctionType.GENERATE_BETWEEN_DATE.getClazz());
            break;
        default:
            res = null;
            break;
        }
        return res;
    }

    /**
     * Deal with keep last and generate function case
     * 
     * @param javaType
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Function<T> handleKeepLastAndGenerateFunction(int javaType) throws InstantiationException, IllegalAccessException {
        Function<T> res;
        switch (javaType) {
        case 0:
            res = getFunction(FunctionType.KEEP_LAST_AND_GENERATE_INT.getClazz());
            break;
        case 1:
            res = getFunction(FunctionType.KEEP_LAST_AND_GENERATE_LONG.getClazz());
            break;
        case 4:
            res = getFunction(FunctionType.KEEP_LAST_AND_GENERATE_STRING.getClazz());
            break;
        default:
            res = null;
            break;
        }
        return res;
    }

    /**
     * Deal with keep first and generate function case
     * 
     * @param javaType
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Function<T> handleKeepFirstAndGenerateFunction(int javaType) throws InstantiationException, IllegalAccessException {
        Function<T> res;
        switch (javaType) {
        case 0:
            res = getFunction(FunctionType.KEEP_FIRST_AND_GENERATE_INT.getClazz());
            break;
        case 1:
            res = getFunction(FunctionType.KEEP_FIRST_AND_GENERATE_LONG.getClazz());
            break;
        case 4:
            res = getFunction(FunctionType.KEEP_FIRST_AND_GENERATE_STRING.getClazz());
            break;
        default:
            res = null;
            break;
        }
        return res;
    }
}
