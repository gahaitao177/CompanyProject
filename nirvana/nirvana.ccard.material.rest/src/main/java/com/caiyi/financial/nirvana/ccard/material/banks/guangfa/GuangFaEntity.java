package com.caiyi.financial.nirvana.ccard.material.banks.guangfa;

/**
 * Created by lwg
 * 广发信用卡实体类
 */
public class GuangFaEntity {
    private String capTypeNo = "";//银行信用卡卡编号
    private String cardName = "";//信用卡名
    private String cardFormat = "";//

    private String capBrand = "C";//*品牌:C银联UnionPay，M万事达MasterCard，V维萨VISA
    private String capLevel = "J";//卡种：P普卡，J金卡，B白金卡
    private String capAptCommon = "0";//0接受普卡，1不接受普卡
    private String capClass = "Z";//卡别：Z主卡，F附属卡，B主卡+附属卡
    private String capStyle = "H";//*卡版：S竖版，H横版（淘宝卡为竖版）

    private GuangFaEntity(){}

    /**
     * 根据卡ID初始化信用卡
     * @param cardId 信用卡Id
     * @param cardLevel 卡等级1普卡，2金卡，3白金卡
     * @return
     */
    public static GuangFaEntity initByCardId(String cardId, String cardLevel){

        if ("1004".equals(cardId)){//广发深航尊鹏卡
            return initShenHangZunPengKa(cardLevel);
        }else if ("1015".equals(cardId)){//广发携程卡
            return initXieChengKa(cardLevel);
        }else if ("1026".equals(cardId)){//广发新聪明卡
            return initXinCongMingKa(cardLevel);
        }else if ("1035".equals(cardId)){//春秋航空联名卡
            return initChunQiuHangKong(cardLevel);
        }else if ("1037".equals(cardId)){//广发易车联名卡
            return initYiCheLianMing(cardLevel);
        } else if ("1025".equals(cardId)){//广发DIY卡
            return initDIY(new String[]{"A"}, cardLevel);
        } else if ("1030".equals(cardId)){//广发臻尚白金卡
            return initZhenShangBaiJin(new String[]{}, cardLevel);
        }else if ("1017".equals(cardId)){//广发真情卡(女性专属)
            return initZhenQing(cardLevel);
        }else if ("1002".equals(cardId)){//广发南航明珠卡
            return initNanHang(cardLevel,false,"");
        }else if ("1003".equals(cardId)){//广发东航卡
            return initDongHang(cardLevel, false, "");
        }else if ("1008".equals(cardId)){//广发淘宝型男卡
            return initTaoBaoXingNan(cardLevel, "");
        }else if ("1014".equals(cardId)){//广发淘宝潮女卡
            return initTaoBaoChaoNv(cardLevel, "");
        }else if ("1023".equals(cardId)){//广发唯品会联名卡
            return initVIP(cardLevel,"");
        }else {
            return null;
        }
    }

    /**
     * 广发真情卡(女性专属)
     * @param cardLevel
     * @return
     */
    private static GuangFaEntity initZhenQing(String cardLevel) {
        if ("1".equals(cardLevel)){
            cardLevel = "P";
        }else if ("2".equals(cardLevel)){
            cardLevel = "J";
        }else {
            return null;
        }

        GuangFaEntity entity = new GuangFaEntity();
        entity.setCardName("广发真情卡(女性专属)");
        entity.setCapTypeNo("1004");
        entity.setCardFormat("4850");

        //*品牌：万事达  银联
        entity.setCapBrand("C");
        //*卡类：金卡、普卡
        entity.setCapLevel(cardLevel);
        entity.setCapAptCommon("0");
        //*卡别：主卡
        entity.setCapClass("Z");
        entity.setCapStyle("H");
        return entity;
    }

    /**
     * 广发深航尊鹏卡
     * @return
     */
    public static GuangFaEntity initShenHangZunPengKa(String cardLevel) {
        if ("1".equals(cardLevel)){
            cardLevel = "P";
        }else if ("2".equals(cardLevel)){
            cardLevel = "J";
        }else {
            return null;
        }
        GuangFaEntity entity = new GuangFaEntity();
        entity.setCardName("广发深航尊鹏卡");
        entity.setCapTypeNo("1004");
        entity.setCardFormat("4850");

        //*品牌：银联
        entity.setCapBrand("C");
        //*卡类：金卡、普卡
        entity.setCapLevel(cardLevel);
        entity.setCapAptCommon("0");
        //*卡别：主卡
        entity.setCapClass("Z");
        entity.setCapStyle("H");
        return entity;
    }

    /**
     * 广发携程卡
     * @return
     */
    public static GuangFaEntity initXieChengKa(String cardLevel) {
        if ("1".equals(cardLevel)){
            cardLevel = "P";
        }else if ("2".equals(cardLevel)){
            cardLevel = "J";
        }else {
            return null;
        }
        GuangFaEntity entity = new GuangFaEntity();
        entity.setCardName("广发携程卡");
        entity.setCapTypeNo("1015");
        entity.setCardFormat("4845");

        //*品牌：银联、万事达
        entity.setCapBrand("C");
        //*卡类：金卡、普卡
        entity.setCapLevel(cardLevel);
        entity.setCapAptCommon("0");
        //*卡别：主卡
        entity.setCapClass("Z");
        entity.setCapStyle("H");
        return entity;
    }

    /**
     * 广发新聪明卡
     * @return
     */
    public static GuangFaEntity initXinCongMingKa(String cardLevel) {
        if ("1".equals(cardLevel)){
            cardLevel = "P";
        }else if ("2".equals(cardLevel)){
            cardLevel = "J";
        }else {
            return null;
        }
        GuangFaEntity entity = new GuangFaEntity();
        entity.setCardName("广发新聪明卡");
        entity.setCapTypeNo("1026");
        entity.setCardFormat("4819");

        //*品牌：银联
        entity.setCapBrand("C");
        //*卡类：金卡、普卡
        entity.setCapLevel(cardLevel);
        entity.setCapAptCommon("0");
        //*卡别：主卡
        entity.setCapClass("Z");
        entity.setCapStyle("H");
        return entity;
    }

    /**
     * 初始化一张春秋航空联名卡
     * @return
     */
    public static GuangFaEntity initChunQiuHangKong(String cardLevel) {
        if ("2".equals(cardLevel)){
            cardLevel = "J";
        }else {
            return null;
        }
        GuangFaEntity entity = new GuangFaEntity();
        entity.setCardName("广发春秋航空联名卡");
        entity.setCapTypeNo("1035");
        entity.setCardFormat("8364");

        //*品牌：银联
        entity.setCapBrand("C");
        //*卡类：金卡
        entity.setCapLevel(cardLevel);
        entity.setCapAptCommon("0");
        //*卡别：主卡
        entity.setCapClass("Z");
        entity.setCapStyle("H");
        return entity;
    }

    /**
     * 初始化一张易车联名卡
     * @return
     */
    public static GuangFaEntity initYiCheLianMing(String cardLevel) {
        if ("1".equals(cardLevel)){
            cardLevel = "P";
        }else if ("2".equals(cardLevel)){
            cardLevel = "J";
        }else {
            return null;
        }
        GuangFaEntity entity = new GuangFaEntity();
        entity.setCardName("广发易车联名卡");
        entity.setCapTypeNo("1037");
        entity.setCardFormat("8364");

        //*品牌：银联
        entity.setCapBrand("C");
        //*卡类：普卡、金卡
        entity.setCapLevel(cardLevel);
        entity.setCapAptCommon("0");
        //*卡别：	主卡
        entity.setCapClass("Z");
        entity.setCapStyle("H");
        return entity;
    }

    private String cdfSelectNo = "";//型男：s00019，潮女：s00023
    private String cdfSelectName = "";
    private String cdfOptionNo = "";//1
    private String cdfOptionName = "";
    private String cdfCstInput = "";//支付宝账号
    /**
     * 初始化一张淘宝型男卡
     * @param cardLevel 卡等级
     * @param alipayNo 支付宝账号
     * @return
     */
    public static GuangFaEntity initTaoBaoXingNan(String cardLevel, String alipayNo) {
        if ("1".equals(cardLevel)){
            cardLevel = "P";
        }else if ("2".equals(cardLevel)){
            cardLevel = "J";
        }else {
            return null;
        }
        GuangFaEntity entity = new GuangFaEntity();
        entity.setCardName("广发淘宝型男卡");
        entity.setCapTypeNo("1008");
        entity.setCardFormat("4856");

        //*品牌：银联
        entity.setCapBrand("C");
        //*卡类：普卡、金卡
        entity.setCapLevel(cardLevel);
        entity.setCapAptCommon("1");
        //*卡别：主卡
        entity.setCapClass("Z");
        entity.setCapStyle("S");

        entity.setCdfSelectNo("s00019");
        entity.setCdfOptionNo("1");
        entity.setCdfCstInput(alipayNo);
        return entity;
    }
    /**
     * 初始化一张淘宝潮女卡
     * @param alipayNo 支付宝账号
     * @return
     */
    public static GuangFaEntity initTaoBaoChaoNv(String cardLevel, String alipayNo) {
        if ("1".equals(cardLevel)){
            cardLevel = "P";
        }else if ("2".equals(cardLevel)){
            cardLevel = "J";
        }else {
            return null;
        }
        GuangFaEntity entity = new GuangFaEntity();
        entity.setCardName("广发淘宝潮女卡");
        entity.setCapTypeNo("1014");
        entity.setCardFormat("4858");

        //*品牌：银联
        entity.setCapBrand("C");
        //*卡类：普卡、金卡
        entity.setCapLevel(cardLevel);
        entity.setCapAptCommon("1");
        //*卡别：主卡
        entity.setCapClass("Z");
        entity.setCapStyle("S");

        entity.setCdfSelectNo("s00023");
        entity.setCdfOptionNo("1");
        entity.setCdfCstInput(alipayNo);
        return entity;
    }

    //南航卡
    private String northSelectNo = "";//是否南航明珠俱乐部会员：0是，1否
    private String s00014 = "";//南航明珠俱乐部会员号
    /**
     * 初始化一张南航明珠卡
     * @param isVIP 是否南航明珠俱乐部会员
     * @param vipNo 会员号
     * @return
     */
    public static GuangFaEntity initNanHang(String cardLevel, boolean isVIP, String vipNo) {
        if ("1".equals(cardLevel)){
            cardLevel = "P";
        }else if ("2".equals(cardLevel)){
            cardLevel = "J";
        }else {
            return null;
        }
        GuangFaEntity entity = new GuangFaEntity();
        entity.setCardName("广发南航明珠卡");
        entity.setCapTypeNo("1002");
        entity.setCardFormat("6254");

        //*品牌：万事达
        entity.setCapBrand("M");
        //*卡类：普卡、金卡
        entity.setCapLevel(cardLevel);
        entity.setCapAptCommon("1");
        //*卡别：主卡
        entity.setCapClass("Z");
        entity.setCapStyle("H");

        if(isVIP){
            entity.setNorthSelectNo("0");
            entity.setS00014(vipNo);
        }else{
            entity.setNorthSelectNo("1");
        }
        return entity;
    }


    //广发DIY
    private String integral1 = "";
    private String integral2 = "";
    private String integral3 = "";
    private String cdfSelectNo6 = "";
    private String cdfOptionNo6 = "";
    private String cdfSelectName6 = "";
    private String cdfOptionName6 = "";
    /**
     * 初始化一个广发DIY卡
     * @param integral 可多选。第一个免费，以后每个收费29元<br/>
     * 【A】餐饮娱乐类（包括各项餐饮，娱乐等各类商户）<br/>
     * 【B】购物类（包括百货、超市、服饰鞋包等各类商户）<br/>
     * 【C】旅行类（包括机票、酒店、度假、旅游等各类商户）<br/>
     * @return
     */
    public static GuangFaEntity initDIY(String[] integral, String cardLevel) {
        if ("1".equals(cardLevel)){
            cardLevel = "P";
        }else if ("2".equals(cardLevel)){
            cardLevel = "J";
        }else {
            return null;
        }
        GuangFaEntity entity = new GuangFaEntity();
        entity.setCardName("广发DIY卡");
        entity.setCapTypeNo("1025");
        entity.setCardFormat("4805");

        //*品牌：银联
        entity.setCapBrand("C");
        //*卡类：普卡、金卡
        entity.setCapLevel(cardLevel);
        entity.setCapAptCommon("0");
        //*卡别：主卡
        entity.setCapClass("Z");
        entity.setCapStyle("H");

        entity.setCdfSelectNo6("1025_s1");
        StringBuilder cdfOptionNo6 = new StringBuilder();
        StringBuilder cdfOptionName6 = new StringBuilder();
        if(integral.length==0){
            cdfOptionNo6.append("A#|#|");
            cdfOptionName6.append("【A】餐饮娱乐类#|#|");
        }
        for(String s:integral){
            if(s.equals("A")){
                cdfOptionNo6.append("A#|#|");
                cdfOptionName6.append("【A】餐饮娱乐类#|#|");
            }
            if(s.equals("B")){
                cdfOptionNo6.append("B#|#|");
                cdfOptionName6.append("【B】购物类#|#|");
            }
            if(s.equals("C")){
                cdfOptionNo6.append("C");
                cdfOptionName6.append("【C】旅行类");
            }
        }

        //<input type="hidden" name="cdfOptionNo6" value="A#|#|B#|#|C"/>
        entity.setCdfOptionNo6(cdfOptionNo6.toString());
        entity.setCdfSelectName6("三倍积分优惠商户类型");
        entity.setCdfOptionName6(cdfOptionName6.toString());
        return entity;
    }

    //臻尚白金卡
    private String feature1 = "";//I,全年6次酒后代驾&无限次道路救援（988元/年）
    private String feature2 = "";//J,全年6次机场接送（1288元/年）
    private String feature3 = "";//K,全年24场高尔夫练球（988元/年）
    private String feature4 = "";//L,5小时高尔夫1对1教练课程（2588元/年，限首年，不自动续费）
    //	params.put("warmTips", "Y") = "";
    private String cdfSelectNo4 = "s00117_zs";
    private String cdfOptionNo4 = "";
    private String cdfSelectNo5 = "securityService";
    private String cdfOptionNo5 = "1";
    /**
     * 初始化一个臻尚白金卡
     * @param feature
     * @return
     */
    public static GuangFaEntity initZhenShangBaiJin(String[] feature, String cardLevel) {
        if ("1".equals(cardLevel)){
            cardLevel = "P";
        }else if ("2".equals(cardLevel)){
            cardLevel = "J";
        }else {
            return null;
        }
        GuangFaEntity entity = new GuangFaEntity();
        entity.setCardName("广发臻尚白金卡");
        entity.setCapTypeNo("1030");
        entity.setCardFormat("4934");

        //*品牌：银联、 VISA、 万事达
        entity.setCapBrand("C");
        //*卡类：普卡、金卡
        entity.setCapLevel(cardLevel);
        entity.setCapAptCommon("0");
        //*卡别：主卡
        entity.setCapClass("Z");
        entity.setCapStyle("H");

        for(String s:feature){
            if(s.equals("I")){
                entity.setFeature1("I");//I,全年6次酒后代驾&无限次道路救援（988元/年）
            }else if(s.equals("J")){
                entity.setFeature2("J");//J,全年6次机场接送（1288元/年）
            }else if(s.equals("K")){
                entity.setFeature3("K");//K,全年24场高尔夫练球（988元/年）
            }else if(s.equals("L")){
                entity.setFeature4("L");//L,5小时高尔夫1对1教练课程（2588元/年，限首年，不自动续费）
            }
        }

        entity.setCdfSelectNo4("s00117_zs");
        entity.setCdfOptionNo4("");
        entity.setCdfSelectNo5("securityService");
        entity.setCdfOptionNo5("1");

        return entity;
    }


    //东航卡
    private String eastSelectNo = "";//--是否东航俱乐部会员：0是，1否
    private String s00016 = "";//东航会员号
    /**
     * 初始化一张东航卡
     * @param isVIP 是否东航卡会员
     * @param vipNo 会员号
     * @return
     */
    public static GuangFaEntity initDongHang(String cardLevel, boolean isVIP, String vipNo) {
        if ("2".equals(cardLevel)){
            cardLevel = "J";
        }else {
            return null;
        }
        GuangFaEntity entity = new GuangFaEntity();
        entity.setCardName("广发东航卡");
        entity.setCapTypeNo("1003");
        entity.setCardFormat("4844");

        //*品牌：银联、 万事达
        entity.setCapBrand("C");
        //*卡类：金卡
        entity.setCapLevel(cardLevel);
        entity.setCapAptCommon("1");
        //*卡别：主卡
        entity.setCapClass("Z");
        entity.setCapStyle("H");

        if(isVIP){
            entity.setEastSelectNo("0");
            entity.setS00016(vipNo);
        }else{
            entity.setEastSelectNo("1");
        }
        return entity;
    }

    //唯品会卡
    private String s00114 = "";//唯品会会员卡号
    /**
     * 初始化一张唯品会联名卡
     * @param vipNo
     * @return
     */
    public static GuangFaEntity initVIP(String cardLevel, String vipNo) {
        if ("2".equals(cardLevel)){
            cardLevel = "J";
        }else {
            return null;
        }

        GuangFaEntity entity = new GuangFaEntity();
        entity.setCardName("广发唯品会联名卡");
        entity.setCapTypeNo("1023");
        entity.setCardFormat("8344");
        entity.setS00114(vipNo);

        //*品牌：银联
        entity.setCapBrand("C");
        //*卡类：金卡
        entity.setCapLevel(cardLevel);
        entity.setCapAptCommon("0");
        //*卡别：主卡
        entity.setCapClass("Z");
        entity.setCapStyle("H");

        return entity;
    }


    public String getCapTypeNo() {
        return capTypeNo;
    }
    public void setCapTypeNo(String capTypeNo) {
        this.capTypeNo = capTypeNo;
    }
    public String getCardName() {
        return cardName;
    }
    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
    public String getCapStyle() {
        return capStyle;
    }
    public void setCapStyle(String capStyle) {
        this.capStyle = capStyle;
    }
    public String getCdfSelectNo() {
        return cdfSelectNo;
    }
    public void setCdfSelectNo(String cdfSelectNo) {
        this.cdfSelectNo = cdfSelectNo;
    }
    public String getCdfSelectName() {
        return cdfSelectName;
    }
    public void setCdfSelectName(String cdfSelectName) {
        this.cdfSelectName = cdfSelectName;
    }
    public String getCdfOptionNo() {
        return cdfOptionNo;
    }
    public void setCdfOptionNo(String cdfOptionNo) {
        this.cdfOptionNo = cdfOptionNo;
    }
    public String getCdfOptionName() {
        return cdfOptionName;
    }
    public void setCdfOptionName(String cdfOptionName) {
        this.cdfOptionName = cdfOptionName;
    }
    public String getCdfCstInput() {
        return cdfCstInput;
    }
    public void setCdfCstInput(String cdfCstInput) {
        this.cdfCstInput = cdfCstInput;
    }
    public String getNorthSelectNo() {
        return northSelectNo;
    }
    public void setNorthSelectNo(String northSelectNo) {
        this.northSelectNo = northSelectNo;
    }
    public String getS00014() {
        return s00014;
    }
    public void setS00014(String s00014) {
        this.s00014 = s00014;
    }
    public String getCdfSelectNo6() {
        return cdfSelectNo6;
    }
    public void setCdfSelectNo6(String cdfSelectNo6) {
        this.cdfSelectNo6 = cdfSelectNo6;
    }
    public String getCdfOptionNo6() {
        return cdfOptionNo6;
    }
    public void setCdfOptionNo6(String cdfOptionNo6) {
        this.cdfOptionNo6 = cdfOptionNo6;
    }
    public String getCdfSelectName6() {
        return cdfSelectName6;
    }
    public void setCdfSelectName6(String cdfSelectName6) {
        this.cdfSelectName6 = cdfSelectName6;
    }
    public String getCdfOptionName6() {
        return cdfOptionName6;
    }
    public void setCdfOptionName6(String cdfOptionName6) {
        this.cdfOptionName6 = cdfOptionName6;
    }
    public String getFeature1() {
        return feature1;
    }
    public void setFeature1(String feature1) {
        this.feature1 = feature1;
    }
    public String getFeature2() {
        return feature2;
    }
    public void setFeature2(String feature2) {
        this.feature2 = feature2;
    }
    public String getFeature3() {
        return feature3;
    }
    public void setFeature3(String feature3) {
        this.feature3 = feature3;
    }
    public String getFeature4() {
        return feature4;
    }
    public void setFeature4(String feature4) {
        this.feature4 = feature4;
    }
    public String getCdfSelectNo4() {
        return cdfSelectNo4;
    }
    public void setCdfSelectNo4(String cdfSelectNo4) {
        this.cdfSelectNo4 = cdfSelectNo4;
    }
    public String getCdfOptionNo4() {
        return cdfOptionNo4;
    }
    public void setCdfOptionNo4(String cdfOptionNo4) {
        this.cdfOptionNo4 = cdfOptionNo4;
    }
    public String getCdfSelectNo5() {
        return cdfSelectNo5;
    }
    public void setCdfSelectNo5(String cdfSelectNo5) {
        this.cdfSelectNo5 = cdfSelectNo5;
    }
    public String getCdfOptionNo5() {
        return cdfOptionNo5;
    }
    public void setCdfOptionNo5(String cdfOptionNo5) {
        this.cdfOptionNo5 = cdfOptionNo5;
    }
    public String getEastSelectNo() {
        return eastSelectNo;
    }
    public void setEastSelectNo(String eastSelectNo) {
        this.eastSelectNo = eastSelectNo;
    }
    public String getS00016() {
        return s00016;
    }
    public void setS00016(String s00016) {
        this.s00016 = s00016;
    }
    public String getS00114() {
        return s00114;
    }
    public void setS00114(String s00114) {
        this.s00114 = s00114;
    }
    public String getCardFormat() {
        return cardFormat;
    }
    public void setCardFormat(String cardFormat) {
        this.cardFormat = cardFormat;
    }
    public String getIntegral1() {
        return integral1;
    }
    public void setIntegral1(String integral1) {
        this.integral1 = integral1;
    }
    public String getIntegral2() {
        return integral2;
    }
    public void setIntegral2(String integral2) {
        this.integral2 = integral2;
    }
    public String getIntegral3() {
        return integral3;
    }
    public void setIntegral3(String integral3) {
        this.integral3 = integral3;
    }
    public String getCapBrand() {
        return capBrand;
    }
    public void setCapBrand(String capBrand) {
        this.capBrand = capBrand;
    }
    public String getCapLevel() {
        return capLevel;
    }
    public void setCapLevel(String capLevel) {
        this.capLevel = capLevel;
    }
    public String getCapAptCommon() {
        return capAptCommon;
    }
    public void setCapAptCommon(String capAptCommon) {
        this.capAptCommon = capAptCommon;
    }
    public String getCapClass() {
        return capClass;
    }
    public void setCapClass(String capClass) {
        this.capClass = capClass;
    }
}
