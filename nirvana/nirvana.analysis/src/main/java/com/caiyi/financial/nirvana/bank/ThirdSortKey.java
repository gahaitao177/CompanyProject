package com.caiyi.financial.nirvana.bank;

import com.google.common.base.Objects;
import scala.math.Ordered;

import java.io.Serializable;

/**
 * 自定义三次排序
 * <p/>
 * Created by Socean on 2016/12/23.
 */
public class ThirdSortKey implements Ordered<ThirdSortKey>, Serializable {
    private String first;

    private String second;

    private String third;

    public ThirdSortKey(String first, String second, String third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(first, second, third);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        ThirdSortKey other = (ThirdSortKey) obj;
        if (first.compareTo(other.first) != 0) {
            return false;
        }
        if (second.compareTo(other.second) != 0) {
            return false;
        }
        if (third.compareTo(other.third) != 0) {
            return false;
        }
        return true;
    }

    @Override
    public int compare(ThirdSortKey other) {
        if (this.first.compareTo(other.getFirst()) != 0) {
            return this.first.compareTo(other.getFirst());
        } else if (this.second.compareTo(other.getSecond()) != 0) {
            return this.second.compareTo(other.getSecond());
        } else if (this.third.compareTo(other.getThird()) != 0) {
            return this.third.compareTo(other.getThird());
        }

        return 0;
    }

    @Override
    public int compareTo(ThirdSortKey other) {
        if (this.first.compareTo(other.getFirst()) != 0) {
            return this.first.compareTo(other.getFirst());
        } else if (this.second.compareTo(other.getSecond()) != 0) {
            return this.second.compareTo(other.getSecond());
        } else if (this.third.compareTo(other.getThird()) != 0) {
            return this.third.compareTo(other.getThird());
        }

        return 0;
    }

    @Override
    public boolean $less(ThirdSortKey other) {
        if (this.first.compareTo(other.getFirst()) < 0) {
            return true;
        } else if (this.first.compareTo(other.getFirst()) == 0 && this.second.compareTo(other.getSecond()) < 0) {
            return true;
        } else if (this.first.compareTo(other.getFirst()) == 0 && this.second.compareTo(other.getSecond()) == 0 &&
                this.third.compareTo(other.getThird()) < 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean $less$eq(ThirdSortKey other) {
        if (this.$less(other)) {
            return true;
        } else if (this.first.compareTo(other.getFirst()) == 0 && this.second.compareTo(other.getSecond()) == 0 &&
                this.third.compareTo(other.getThird()) == 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean $greater(ThirdSortKey other) {
        if (this.first.compareTo(other.getFirst()) > 0) {
            return true;
        } else if (this.first.compareTo(other.getFirst()) == 0 && this.second.compareTo(other.getSecond()) > 0) {
            return true;
        } else if (this.first.compareTo(other.getFirst()) == 0 && this.second.compareTo(other.getSecond()) == 0 &&
                this.third.compareTo(other.getThird()) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean $greater$eq(ThirdSortKey other) {
        if (this.$greater(other)) {
            return true;
        } else if (this.first.compareTo(other.getFirst()) == 0 && this.second.compareTo(other.getSecond()) == 0 &&
                this.third.compareTo(other.getThird()) == 0) {
            return true;
        }
        return false;
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

    public String getThird() {
        return third;
    }

    public void setThird(String third) {
        this.third = third;
    }
}
