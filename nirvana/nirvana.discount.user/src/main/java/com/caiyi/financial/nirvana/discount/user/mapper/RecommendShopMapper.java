package com.caiyi.financial.nirvana.discount.user.mapper;

import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.caiyi.financial.nirvana.discount.user.bean.RecommendShopBean;
import com.caiyi.financial.nirvana.discount.user.bean.ShopIndexBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by dengh on 2016/8/3.
 */
public interface RecommendShopMapper extends BaseDao {


    @Select("select ibusid, clogo, cbusname, iorder,type\n" +
            "\t\tfrom (select t3.*\n" +
            "\t\tfrom (select t1.ibusid, t1.clogo, t1.cbusname, t1.iorder,t1.type\n" +
            "\t\tfrom tb_recommend t1\n" +
            "\t\tinner join tb_store t2\n" +
            "\t\ton t1.ibusid = t2.ibussinessid\n" +
            "\t\twhere t1.istate = 1\n" +
            "\t\tand t1.iorder != 2\n" +
            "\t\tand t2.icityid = #{icityid1,jdbcType=VARCHAR}\n" +
            "\t\tand t2.istate > 0\n" +
            "\t\tand t2.istate != 2\n" +
            "\t\tand t2.camapid > 0) t3\n" +
            "\t\tinner join (select c.*\n" +
            "\t\tfrom tb_cheap c\n" +
            "\t\tleft join tb_city_cheap cc\n" +
            "\t\ton cc.icheapid = c.icheapid\n" +
            "\t\twhere c.iexpire = 0\n" +
            "\t\tand c.istate > 0\n" +
            "\t\tand c.istate != 2\n" +
            "\t\tand (cc.icityid = #{icityid2,jdbcType=VARCHAR} or cc.icityid is null)\n" +
            "\t\t${sqlwhere}) t4\n" +
            "\t\ton t3.ibusid = t4.ibussinessid\n" +
            "\t\tunion all\n" +
            "\t\tselect t3.*\n" +
            "\t\tfrom (select t1.ibusid, t1.clogo, t1.cbusname, t1.iorder,t1.type\n" +
            "\t\tfrom tb_recommend t1\n" +
            "\t\tinner join tb_store t2\n" +
            "\t\ton t1.ibusid = t2.ibussinessid\n" +
            "\t\twhere t1.istate = 1\n" +
            "\t\tand t1.iorder = 2\n" +
            "\t\tand t2.icityid = #{icityid3,jdbcType=VARCHAR}\n" +
            "\t\tand t2.istate > 0\n" +
            "\t\tand t2.istate != 2\n" +
            "\t\tand t2.camapid > 0) t3\n" +
            "\t\tinner join (select c.*\n" +
            "\t\tfrom tb_cheap c\n" +
            "\t\tleft join tb_city_cheap cc\n" +
            "\t\ton cc.icheapid = c.icheapid\n" +
            "\t\twhere c.iexpire = 0\n" +
            "\t\tand c.istate > 0\n" +
            "\t\tand c.istate != 2\n" +
            "\t\tand (cc.icityid = #{icityid4,jdbcType=VARCHAR} or cc.icityid is null)) t4\n" +
            "\t\ton t3.ibusid = t4.ibussinessid)\n" +
            "\t\tgroup by ibusid, clogo, cbusname, iorder,type")
   List<RecommendShopBean>query_recommend_shop(@Param("icityid1")String icityid1,@Param("icityid2")String icityid2,
                                               @Param("icityid3")String icityid3,@Param("icityid4")String icityid4,
                                               @Param("sqlwhere")String sqlwhere);
  // 首页三个连锁店列表 -->
    @Select("select * from \n" +
            "    ( select t1.imarketid as ShopId, t1.cname as ShopName, t1.cslogan as ShopSlogan,\n" +
            "     t1.clogo as ShopPic,t1.clogolist clogolist, nvl(t2.mcount, 0) num from TB_MARKET t1 \n" +
            "    right join (select imarketid, count(icheapid) mcount from tb_market_cheap where cenddate > sysdate - 1 \n" +
            "    and istate = 1 group by imarketid order by mcount desc) t2 on t1.imarketid = t2.imarketid where itype = 1 \n" +
            "    and istate = 1 order by t1.iorder desc, t2.mcount desc ) where rownum <= 3")
   List<ShopIndexBean> query_index_shop();





}
