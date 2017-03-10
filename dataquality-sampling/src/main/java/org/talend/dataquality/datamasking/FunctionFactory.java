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
public class FunctionFactory {

    private Function<?> getFunction(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        return (Function<?>) clazz.newInstance();
    }

    private Function<?> getFunction3(FunctionType type, int javaType) throws InstantiationException, IllegalAccessException {
        Function<?> res;
        if (type == FunctionType.REPLACE_LAST_CHARS) {
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
        } else if (type == FunctionType.REPLACE_NUMERIC) {
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
        } else {
            res = getFunction(type.getClazz());
        }
        return res;
    }

    private Function<?> getFunction2(FunctionType type, int javaType) throws InstantiationException, IllegalAccessException {
        Function<?> res;
        if (type == FunctionType.GENERATE_FROM_LIST_HASH) {
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
        } else if (type == FunctionType.GENERATE_FROM_FILE_HASH) {
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
        } else if (type == FunctionType.GENERATE_SEQUENCE) {
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
        } else if (type == FunctionType.NUMERIC_VARIANCE) {

            switch (javaType) {
            case 0:
                res = getFunction(FunctionType.NUMERIC_VARIANCE_INT.getClazz());
                break;
            case 1:
                res = getFunction(FunctionType.NUMERIC_VARIANCE_LONG.getClazz());
                break;
            case 2:
                res = getFunction(FunctionType.NUMERIC_VARIANCE_FlOAT.getClazz());
                break;
            case 3:
                res = getFunction(FunctionType.NUMERIC_VARIANCE_DOUBLE.getClazz());
                break;
            default:
                res = null;
                break;
            }
        } else if (type == FunctionType.REMOVE_FIRST_CHARS) {
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
        } else if (type == FunctionType.REMOVE_LAST_CHARS) {
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
        } else if (type == FunctionType.REPLACE_FIRST_CHARS) {
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
        } else {
            res = getFunction3(type, javaType);
        }
        return res;
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
    public Function<?> getFunction(FunctionType type, int javaType) throws InstantiationException, IllegalAccessException {
        Function<?> res;
        if (type == FunctionType.KEEP_FIRST_AND_GENERATE) {
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
        } else if (type == FunctionType.KEEP_LAST_AND_GENERATE) {
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
        } else if (type == FunctionType.GENERATE_BETWEEN) {
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
        } else if (type == FunctionType.GENERATE_CREDIT_CARD_FORMAT) {
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
        } else if (type == FunctionType.GENERATE_CREDIT_CARD) {
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
        } else if (type == FunctionType.GENERATE_FROM_FILE) {
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

        } else if (type == FunctionType.GENERATE_FROM_LIST) {
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
        } else {
            res = getFunction2(type, javaType);
        }
        return res;
    }
}
