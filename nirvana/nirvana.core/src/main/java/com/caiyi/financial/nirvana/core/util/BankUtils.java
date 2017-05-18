package com.caiyi.financial.nirvana.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by liuweiguo on 2016/2/23.
 */
public class BankUtils extends StringUtils{
    /**
     * 校验身份证号
     * @param certNo 身份证号
     * @return true通过，false未通过
     */
    public static boolean checkIdCardNo(String certNo){
        if (isEmpty(certNo)){
            return false;
        }
        certNo = certNo.toLowerCase();
        if(isEmpty(certNo) || (certNo.length()!=18 && certNo.length()!=15)){
            return false;
        }
        String[] RC = {"1","0","x","9","8","7","6","5","4","3","2"};
        boolean checkBirthday = true;
        String birthday = "";

        if(certNo.length() == 18){// 18位
            birthday = certNo.substring(6, 14);
            checkBirthday = isDate(birthday,"yyyyMMdd");

            int[] W = {7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2};
            int sum = 0;
            for(int i=0 ;i < certNo.length()-1;i++){
                int c = Integer.parseInt(certNo.substring(i, i+1));
                sum += W[i]*c;
            }
            int r = sum%11;
            if(RC[r].equals(certNo.substring(17)) && checkBirthday){
                return true;
            }else{
                return false;
            }
        }else{//15位
            birthday = "19" + certNo.substring(6,12);
            checkBirthday = isDate(birthday,"yyyyMMdd");
            if(checkBirthday){
                return true;
            }else{
                return false;
            }
        }
    }

    /**
     * 是否为合法日期
     * @param dateString 日期
     * @param format 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
     * @return
     */
    public static boolean isDate(String dateString,String format){
        boolean convertSuccess=true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat smf = new SimpleDateFormat(format);
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            smf.setLenient(false);
            smf.parse(dateString);
        } catch (ParseException e) {
            //e.printStackTrace();
            //如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess=false;
        }
        return convertSuccess;
    }

    /**
     * 根据身份证获取性别和出生日期
     * @param idCard
     * @return 性别(M/F)|生日yyyyMMdd
     */
    public static String getSexAndBirthday(String idCard){
        String birthday = "";
        String sex = "";

        int idCard_length = idCard.length();
        if(idCard_length==15){
            birthday = idCard.substring(6,12);
            int year = Integer.parseInt(birthday.substring(0, 2));
            if(year>13){
                birthday = "19"+birthday;
            }else{
                birthday = "20"+birthday;
            }
            sex = idCard.substring(14,15);
            if(sex.equals("1")||sex.equals("3")||sex.equals("5")||sex.equals("7")||sex.equals("9")){
                sex = "M";
            }else{
                sex = "F";
            }
        }else{
            birthday = idCard.substring(6,14);
            sex = idCard.substring(16,17);
            if(sex.equals("1")||sex.equals("3")||sex.equals("5")||sex.equals("7")||sex.equals("9")){
                sex = "M";
            }else{
                sex = "F";
            }
        }
        return sex+"|"+birthday;
    }

    /**
     * 根据身份证获取默认身份证有效期
     * <p><b>说明：<b/>16周岁至25周岁的居民发给有效期10年的居民身份证；26周岁至45周岁的居民发给有效期20年的居民身份证；46周岁以上的，发给长期有效的居民身份证。未满16周岁的公民自愿申请领取居民身份证的，有效期5年。<p/>
     * @param idcard
     * @return  格式yyyyMMdd，空代表永久有效。
     */
    public static String getDefaultEffectDate(String idcard) {
        String birthday = getSexAndBirthday(idcard).split("\\|")[1];
        String yearStr = birthday.substring(0,4);
        int age = 0;
        int birth = 0;
        int year = 0;
        if (isInteger(yearStr)){
            birth = Integer.parseInt(yearStr);
            year = Calendar.getInstance().get(Calendar.YEAR);
            age = year - birth;

        }
        if (age<=16){
            return year+3+"0615";
        }else if (age<=25){
            return year+8+"0615";
        }else if (age<=45){
            return year+18+"0615";
        }else if (age>45){
            return "";
        }
        return "0";
    }
}
