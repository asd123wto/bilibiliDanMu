package LabmemFX.xyz;

import java.util.LinkedList;

/**
 * Created by Lty
 * Created in 2:49 2019/4/3
 */
public class SetList<T> extends LinkedList<T> {

    @Override
    public boolean add(T object) {
        if (size() == 0) {
            return super.add(object);
        } else {
            int count = 0;
            for (T t : this) {
                if (t.equals(object)) {
                    count++;
                    break;
                }
            }
            if (count == 0) {
                return super.add(object);
            } else {
                return false;
            }
        }
    }


}
