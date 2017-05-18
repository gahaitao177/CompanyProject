package com.caiyi.financial.nirvana.bank;

import com.google.common.base.Objects;
import scala.math.Ordered;

import java.io.Serializable;

/**
 * 自定义二次排序key
 *
 * * 封装你要进行排序算法需要的几个字段：点击次数、下单次数和支付次数
 * 实现Ordered接口要求的几个方法
 *
 * 跟其他key相比，如何来判定大于、大于等于、小于、小于等于
 *
 * Created by Socean on 2016/12/19.
 */
public class SecondarySortKey implements Ordered<SecondarySortKey>, Serializable {

    private String first;

    private String second;

    public SecondarySortKey(String first, String second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int compare(SecondarySortKey other) {
        if (this.first.compareTo(other.getFirst()) != 0) {
            return this.first.compareTo(other.getFirst());
        } else {
            return this.second.compareTo(other.getSecond());
        }
    }

    @Override
    public int compareTo(SecondarySortKey other) {
        if (this.first.compareTo(other.getFirst()) != 0) {
            return this.first.compareTo(other.getFirst());
        } else {
            return this.second.compareTo(other.getSecond());
        }
    }

    @Override
    public boolean $less(SecondarySortKey other) {
        if (this.first.compareTo(other.getFirst()) < 0) {
            return true;
        } else if (this.first.compareTo(other.getFirst()) == 0 && this.second.compareTo(other.getSecond()) < 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean $less$eq(SecondarySortKey other) {
        if (this.$less(other)) {
            return true;
        } else if (this.first.compareTo(other.getFirst()) == 0 && this.second.compareTo(other.getSecond()) == 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean $greater(SecondarySortKey other) {
        if (this.first.compareTo(other.getFirst()) > 0) {
            return true;
        } else if (this.first.compareTo(other.getFirst()) == 0 && this.second.compareTo(other.getSecond()) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean $greater$eq(SecondarySortKey other) {
        if (this.$greater(other)) {
            return true;
        } else if (this.first.compareTo(other.getFirst()) == 0 && this.second.compareTo(other.getSecond()) == 0) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(first, second);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        SecondarySortKey other = (SecondarySortKey) obj;
        if (first.compareTo(other.first) != 0) {
            return false;
        }
        if (second.compareTo(other.second) != 0) {
            return false;
        }
        return true;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }
}
