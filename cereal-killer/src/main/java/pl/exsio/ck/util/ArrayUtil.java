package pl.exsio.ck.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author exsio
 */
public class ArrayUtil {

    public static <T extends Object> List<T[]> splitArray(T[] array, int max) {

        int x = array.length / max;

        int lower = 0;
        int upper = 0;

        List<T[]> list = new ArrayList<T[]>();

        for (int i = 0; i <= x; i++) {
            upper += max;
            list.add(Arrays.copyOfRange(array, lower, upper));
            lower = upper;
        }
        if (upper < array.length - 1) {
            lower = upper;
            upper = array.length;
            list.add(Arrays.copyOfRange(array, lower, upper));
        }

        return list;
    }
}
