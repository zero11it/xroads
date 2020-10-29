package it.zero11.xroads.utils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

/**
 * Code from https://github.com/kaleidos/grails-postgresql-extensions
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ArrayUserType implements UserType, ParameterizedType {
    public static final int INTEGER_ARRAY = 90001;
    public static final int LONG_ARRAY = 90002;
    public static final int STRING_ARRAY = 90003;
    public static final int ENUM_INTEGER_ARRAY = 90004;
    public static final int FLOAT_ARRAY = 90005;
    public static final int DOUBLE_ARRAY = 90006;

	private static final Map<Class, Integer> CLASS_TO_SQL_CODE = new HashMap<Class, Integer>();

    static {
        CLASS_TO_SQL_CODE.put(Integer.class, INTEGER_ARRAY);
        CLASS_TO_SQL_CODE.put(Long.class, LONG_ARRAY);
        CLASS_TO_SQL_CODE.put(String.class, STRING_ARRAY);
        CLASS_TO_SQL_CODE.put(Float.class, FLOAT_ARRAY);
        CLASS_TO_SQL_CODE.put(Double.class, DOUBLE_ARRAY);
    }

    private Class<?> typeClass;
    private BidiEnumMap bidiMap;

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x == null ? y == null : x.equals(y);
    }

    @Override
    public int hashCode(Object value) throws HibernateException {
        return value == null ? 0 : value.hashCode();
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public void setParameterValues(Properties parameters) {
    	try{
    		typeClass = Class.forName((String)parameters.get("type"));
    	}catch(ClassNotFoundException e){
    		throw new RuntimeException("The user type needs to be configured with the type. Unexisting class provided");
    	}
        if (typeClass == null) {
            throw new RuntimeException("The user type needs to be configured with the type. None provided");
        }
    }

    @Override
    public Class<?> returnedClass() {
        return java.lang.reflect.Array.newInstance(typeClass, 0).getClass();
    }

    @Override
    public int[] sqlTypes() {

        Integer type = CLASS_TO_SQL_CODE.get(typeClass);
        if (type != null) {
            return new int[] { type };
        }

        if (typeClass.isEnum()) {
            return new int[]{ENUM_INTEGER_ARRAY};
        }

        throw new RuntimeException("The type " + typeClass + " is not a valid type");
    }

    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        Object[] result = null;
        Class typeArrayClass = java.lang.reflect.Array.newInstance(typeClass, 0).getClass();
        java.sql.Array array = rs.getArray(names[0]);
        if (!rs.wasNull()) {
            if (typeClass.isEnum()) {
                int length = java.lang.reflect.Array.getLength(array);
                Object converted = java.lang.reflect.Array.newInstance(typeClass, length);
                for (int i = 0; i < length; i++) {
                    java.lang.reflect.Array.set(converted, i, idToEnum(java.lang.reflect.Array.get(array, i)));
                }
            } else {
                result = (Object[]) typeArrayClass.cast(array.getArray());
            }
        }
        return result;
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.ARRAY);
            return;
        }

        Object[] valueToSet = (Object[]) value;
        Class typeArrayClass = java.lang.reflect.Array.newInstance(typeClass, 0).getClass();

        if (typeClass.isEnum()) {
            typeArrayClass = Integer[].class;
            Integer[] converted = new Integer[valueToSet.length];

            for (int i = 0; i < valueToSet.length; i++) {
                if (valueToSet[i] instanceof Integer) {
                    converted[i] = (Integer) valueToSet[i];
                } else {
                    converted[i] = ((Enum) valueToSet[i]).ordinal();
                }
            }
            valueToSet = converted;
        }

        java.sql.Array array = st.getConnection().createArrayOf(PgArrayUtils.getNativeSqlType(typeClass), (Object[]) typeArrayClass.cast(valueToSet));
        st.setArray(index, array);
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    private Object idToEnum(Object id) throws HibernateException {
        try {
            if (bidiMap == null) {
                bidiMap = new BidiEnumMap(typeClass);
            }
            return bidiMap.getEnumValue(id);
        } catch (Exception e) {
            throw new HibernateException("Unable to create bidirectional enum map for " + typeClass, e);
        }
    }

    /**
     * Takes an Object and transforms it into a new value.
     */
    private static interface MapFunction {
        /**
         * Transforms an object into some new value.
         *
         * @param o the object
         * @return some new value
         */
        Object map(Object o);
    }
    
    private static class PgArrayUtils {

        private static final Map<Class<?>, String> CLASS_TO_TYPE_NAME = new HashMap<Class<?>, String>();

        static {
            CLASS_TO_TYPE_NAME.put(Integer.class, "int");
            CLASS_TO_TYPE_NAME.put(Long.class, "int8");
            CLASS_TO_TYPE_NAME.put(String.class, "varchar");
            CLASS_TO_TYPE_NAME.put(Float.class, "float");
            CLASS_TO_TYPE_NAME.put(Double.class, "float8");
        }

        /**
         * Returns a new array wrapping the parameter value. The type of the array
         * will be the type passed as parameter
         *
         * @param targetValue  The value we want to wrap as an array
         * @param expectedType The expected type of the returned array
         * @param mapFunction  If non-null, it will transform each object in the array to a given object.
         * @return an array wrapping the parameter value
         */
        public static Object[] getValueAsArrayOfType(Object targetValue, Class<?> expectedType, MapFunction mapFunction) {
            Object[] arrValue;

            if (targetValue instanceof List) {
                List<Object> valueAsList = (List<Object>) targetValue;
                arrValue = (Object[]) Array.newInstance(expectedType, valueAsList.size());

                for (int i = 0, count = valueAsList.size(); i < count; i++) {
                    Object object = valueAsList.get(i);
                    if (expectedType.isInstance(object)) {
                        arrValue[i] = expectedType.cast(object);
                    } else if (mapFunction != null) {
                        arrValue[i] = expectedType.cast(mapFunction.map(object));
                    } else {
                        throw new HibernateException("criteria doesn't support values of type: " +
                                targetValue.getClass().getName() + ". Try: " + expectedType + " or List<" + expectedType + "> instead");
                    }
                }
            } else if (expectedType.isInstance(targetValue) || mapFunction != null) {
                arrValue = (Object[]) Array.newInstance(expectedType, 1);

                if (mapFunction != null) {
                    arrValue[0] = expectedType.cast(mapFunction.map(targetValue));
                } else {
                    arrValue[0] = expectedType.cast(targetValue);
                }
            } else if (targetValue instanceof Object[]) {
                arrValue = (Object[]) targetValue;
            } else {
                throw new HibernateException("criteria doesn't support values of type: " +
                        targetValue.getClass().getName() + ". Try: " + expectedType + " or List<" + expectedType + "> instead");
            }
            return arrValue;
        }

        /**
         * Overloaded version of getValueAsArrayOfType that doesn't use a mapFunction
         */
        @SuppressWarnings("unused")
		public static Object[] getValueAsArrayOfType(Object targetValue, Class<?> expectedType) {
            return getValueAsArrayOfType(targetValue, expectedType, null);
        }

        public static String getNativeSqlType(Class<?> clazz) {
            String typeName = CLASS_TO_TYPE_NAME.get(clazz);
            if (typeName != null) {
                return typeName;
            }

            if (clazz.isEnum()) {
                return "int";
            }

            throw new RuntimeException("Type class not valid: " + clazz);
        }
    }

    private static class BidiEnumMap implements Serializable {
        private static final long serialVersionUID = 3325751131102095834L;

        public static final String ENUM_ID_ACCESSOR = "getId";

		private final Map enumToKey;
        private final Map keytoEnum;

		public BidiEnumMap(Class<?> enumClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
            EnumMap enumToKeyMap = new EnumMap(enumClass);
            HashMap keytoEnumMap = new HashMap();

            Method idAccessor = enumClass.getMethod(ENUM_ID_ACCESSOR);

            Method valuesAccessor = enumClass.getMethod("values");
            Object[] values = (Object[]) valuesAccessor.invoke(enumClass);

            for (Object value : values) {
                Object id = idAccessor.invoke(value);
                enumToKeyMap.put((Enum) value, id);
                keytoEnumMap.put(id, value);
            }

            this.enumToKey = Collections.unmodifiableMap(enumToKeyMap);
            this.keytoEnum = Collections.unmodifiableMap(keytoEnumMap);
        }

        public Object getEnumValue(Object id) {
            return keytoEnum.get(id);
        }

        @SuppressWarnings("unused")
		public Object getKey(Object enumValue) {
            return enumToKey.get(enumValue);
        }
    }
    
}