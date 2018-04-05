package io.thedocs.soyuz.validator;

import java.util.*;

/**
 * Created by fbelov on 14.11.16.
 */
class FvUtils {

    public static class to {
        public static <T> List<T> list(T... value) {
            List<T> answer = new ArrayList<T>();
            Collections.addAll(answer, value);
            return answer;
        }

        public static Map<String, Object> map(String key, Object value) {
            Map<String, Object> answer = new HashMap<>();

            answer.put(key, value);

            return answer;
        }
    }

    public static Number addNumbers(Number a, Number b) {
        if(a instanceof Double || b instanceof Double) {
            return new Double(a.doubleValue() + b.doubleValue());
        } else if(a instanceof Float || b instanceof Float) {
            return new Float(a.floatValue() + b.floatValue());
        } else if(a instanceof Long || b instanceof Long) {
            return new Long(a.longValue() + b.longValue());
        } else {
            return new Integer(a.intValue() + b.intValue());
        }
    }

}
