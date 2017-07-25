package com.linkloving.dyh08.logic.UI.Groups.baidu;

import java.util.Comparator;

import Trace.GreenDao.Note;

/**
 * Created by Daniel.Xu on 2016/12/19.
 */

public class sort  implements Comparator{
    @Override
    public int compare(Object lhs, Object rhs) {
        Note lhs1 = (Note) lhs;
        Note rhs1 = (Note) rhs;
        int i = rhs1.getStartDate().compareTo(lhs1.getStartDate());
        return i;
    }
}
