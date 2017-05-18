package com.caiyi.financial.nirvana.ccard.bill.bank.service;

/**
 * Created by Linxingyu on 2017/1/10.
 */
public class TestGaodeApi extends TestSupport {

//    public static Logger logger;
//
//    @Autowired
//    private BankMapper bankMapper;

//    @Test
////    @Rollback(value = false)
//    public void testZhaoshang() {
//        List<AreaDto> areas = bankMapper.queryCity();
////        String regex = ".+(支行|分行).+";
//        String regex = ".+(私人|停车|ATM|自助).+";//过滤条件
//        String baseUrl = "http://restapi.amap.com/v3/place/text?key=1bc0db7e681f8ca8ae77e57bd97aa3c0&offset=50";
//        try {
//            for (BankBean bank : BankList.BANK_LIST) {
//                int requestNum = 0;
//                List<BankPoint> bankPoints = new ArrayList<>();//存放查询结果
//                for (AreaDto area : areas) {
//                    int pn = 1;//页码
//                    String requestUrl = "";//最终请求地址
//
//                    Integer icityid = area.getIareaid();//城市id
//                    String city = area.getCareaname();//城市名称
//
//                    String initUrl = baseUrl + "&city=" + city;
//                    JSONObject jsonObject = JSONObject.parseObject(loadJSON(initUrl));
//                    requestNum++;
//                    String pois = jsonObject.getString("pois");
//                    while (!"[]".equals(pois)) {
//                        requestUrl = initUrl + "&page=" + pn++ + "&types=" + bank.getGaodeTypes();
//                        JSONObject data = JSONObject.parseObject(loadJSON(requestUrl));
//                        requestNum++;
//                        JSONArray jsonArray = JSONArray.parseArray(data.getString("pois"));
//                        if (!jsonArray.isEmpty()) {
//                            for (Object o : jsonArray) {
//                                JSONObject bankInfo = (JSONObject) o;
//                                BankPoint bankPoint = new BankPoint();
//                                bankPoint.setIbankid(bank.getBankId());
//                                bankPoint.setIcityid(icityid);
//                                bankPoint.setCnetpointname(bankInfo.getString("name"));
//                                bankPoint.setCaddr(bankInfo.getString("address"));
//                                String[] locations = bankInfo.getString("location").split(",");
//                                bankPoint.setClng(locations[0]);//经度
//                                bankPoint.setClat(locations[1]);//纬度
//                                bankPoint.setCphone(bankInfo.getString("tel"));
//                                bankPoints.add(bankPoint);
//                            }
//                        } else {
//                            break;
//                        }
//                    }
//                }
//                //过滤查询出的错误银行
//                List<BankPoint> newBankPoints = new ArrayList<>();
//                for (BankPoint bankPoint : bankPoints) {
//                    if (!bankPoint.getCnetpointname().matches(regex)) {
//                        newBankPoints.add(bankPoint);
//                    }
//                }
//                //插入数据
//                if (newBankPoints.size() > 2000) {
//                    //针对数据量大的时候批量插入失败的处理
//                    int k = newBankPoints.size() / 2000;
//                    for (int i = 0; i <= k; i++) {
//                        List<BankPoint> bankPoint = new ArrayList<>();
//                        if (i == k) {
//                            bankPoint = newBankPoints.subList(2000 * i, newBankPoints.size());
//                        } else {
//                            bankPoint = newBankPoints.subList(2000 * i, 2000 * (i + 1));
//                        }
//                        int j = bankMapper.addBankPointList(bankPoint);
//                    }
//                } else {
//                    int j = bankMapper.addBankPointList(newBankPoints);
//                }
//
//                System.out.println(bank.getBankName() + "数据已插入，共计：" + newBankPoints.size() + "条，查询次数：" + requestNum);
//                Thread.sleep(10000);//间隔10s查询一次
//            }
//        } catch (Exception e) {
//            logger.info(e + "高德api查询时出错，可能的原因为：key使用过程中出错！");
//        }
//    }

//        System.out.println("bankPoints:" + bankPoints.size());
//        List<BankPoint> newBankPoints = new ArrayList<>();
//        List<BankPoint> newBankPoints1 = new ArrayList<>();
//        for (BankPoint bankPoint : bankPoints) {
//            if (!bankPoint.getCnetpointname().matches(regex)) {
//                newBankPoints.add(bankPoint);
//            } else {
//                newBankPoints1.add(bankPoint);
//            }
//        }
//        System.out.println("newBankPoints:" + newBankPoints.size());
//        System.out.println(JSON.toJSON(newBankPoints1));
//        if (newBankPoints.size() > 2000){
//            //针对数据量大的时候批量插入失败的处理
//            int k = newBankPoints.size() / 2000;
//            for (int i = 0; i <= k; i++) {
//                List<BankPoint> bankPoint = new ArrayList<>();
//                if (i == k) {
//                    bankPoint = newBankPoints.subList(2000 * i, newBankPoints.size());
//                } else {
//                    bankPoint = newBankPoints.subList(2000 * i, 2000 * (i + 1));
//                }
//                int j = bankMapper.addBankPointList(bankPoint);
//                System.out.println(j);
//            }
//        }else {
//            int j = bankMapper.addBankPointList(newBankPoints);
//            System.out.println(j);
//        }

//        System.out.println("请求次数：" + requestNum);
//        System.out.println(JSON.toJSON(newBankPoints));
//
//    //解析请求url返回的json数据
//    public static String loadJSON(String url) {
//        StringBuilder sb = new StringBuilder();
//        try {
//            URL newUrl = new URL(url);
//            URLConnection connection = newUrl.openConnection();
//            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String inputLine = null;
//            while ((inputLine = br.readLine()) != null) {
//                sb.append(inputLine);
//            }
//            br.close();
//        } catch (Exception e) {
//            logger.info(e + "解析url异常");
//        }
//        return sb.toString();
//    }

}
