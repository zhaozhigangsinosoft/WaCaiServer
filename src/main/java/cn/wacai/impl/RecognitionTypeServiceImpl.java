package cn.wacai.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.util.RegTest;
import cn.wacai.service.RecognitionTypeService;
import cn.wacai.vo.WacaiAccountVo;

/**
 * 交易类型识别接口实现类
 * @author ZhaoZhigang
 *
 */
@Service
public class RecognitionTypeServiceImpl implements RecognitionTypeService {
    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //定义根据交易对象转换类型的map
    private HashMap<String,WacaiAccountVo> tradingPartyMap = null;
    //定义根据商品名称转换类型的map
    private HashMap<String,WacaiAccountVo> commodityMap = null;
    
    /**
     * 自动识别交易类型方法
     * @param accountVos
     */
    @Override
    public void recognitionType(ArrayList<WacaiAccountVo> accountVos) {
        //遍历交易对象列表
        for (Iterator<WacaiAccountVo> iterator = accountVos.iterator();
                iterator.hasNext();) {
            WacaiAccountVo wacaiAccountVo = (WacaiAccountVo) iterator.next();
            if(wacaiAccountVo.getCollectionOrSupport().equals("支出")) {
                //根据交易对象识别交易类型
                this.recognitionTradingParty(wacaiAccountVo);
                //根据商品名称识别交易类型
                this.recognitionCommodity(wacaiAccountVo);
                //根据交易时间识别三餐类型
                this.recognitionHour(wacaiAccountVo);
            }
        }
    }

    /**
     * 根据交易时间识别三餐类型
     * @param wacaiAccountVo
     */
    private void recognitionHour(WacaiAccountVo wacaiAccountVo) {
        if("三餐".equals(wacaiAccountVo.getExpenditureCategory())) {
            //根据交易时间生成日历对象
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(wacaiAccountVo.getConsumptionDate());
            //定义交易时间的小时，用于识别早午晚餐
            int hour=calendar.get(Calendar.HOUR_OF_DAY);
            if(hour>=6&&hour<=10) {
                wacaiAccountVo.setExpenditureCategory("早餐");
            }else if(hour>=11&&hour<=15) {
                wacaiAccountVo.setExpenditureCategory("午餐");
            }else if(hour>=16&&hour<=23) {
                wacaiAccountVo.setExpenditureCategory("晚餐");
            }
        }
    }

    /**
     * 根据商品名称识别交易类型
     * @param wacaiAccountVo
     */
    private void recognitionCommodity(WacaiAccountVo wacaiAccountVo) {
        //先初始化根据商品名称转换类型的map
        this.initCommodityMap();
        //遍历map循环使用正则表达式匹配，如果匹配上，则进行赋值，并跳出循环
        if("漏记款".equals(wacaiAccountVo.getExpenditureCategory())) {
            for(String key :commodityMap.keySet()) {
                if(RegTest.match(wacaiAccountVo.getCommodity(),
                        "^.*("+key+").*$")) {
                    WacaiAccountVo wacaiAccountVoType = commodityMap.get(key);
                    wacaiAccountVo.setExpenditureCategories(
                            wacaiAccountVoType.getExpenditureCategories());
                    wacaiAccountVo.setExpenditureCategory(
                            wacaiAccountVoType.getExpenditureCategory());
                    break;
                }
            }
        }
    }

    /**
     * 根据商品名称转换类型的map
     */
    private void initCommodityMap() {
        if(this.commodityMap == null) {
            this.commodityMap = new HashMap<>();
            WacaiAccountVo wacaiAccountVoType1= new WacaiAccountVo();
            wacaiAccountVoType1.setExpenditureCategories("餐饮");
            wacaiAccountVoType1.setExpenditureCategory("三餐");
            commodityMap.put("饭", wacaiAccountVoType1);
            commodityMap.put("肉", wacaiAccountVoType1);
            commodityMap.put("面", wacaiAccountVoType1);
            commodityMap.put("米", wacaiAccountVoType1);
            commodityMap.put("鱼", wacaiAccountVoType1);
            commodityMap.put("菜", wacaiAccountVoType1);
            commodityMap.put("美团", wacaiAccountVoType1);
            
            WacaiAccountVo wacaiAccountVoType2= new WacaiAccountVo();
            wacaiAccountVoType2.setExpenditureCategories("娱乐");
            wacaiAccountVoType2.setExpenditureCategory("娱乐其他");
            commodityMap.put("摩摩哒", wacaiAccountVoType2);
            
            WacaiAccountVo wacaiAccountVoType3= new WacaiAccountVo();
            wacaiAccountVoType3.setExpenditureCategories("交通");
            wacaiAccountVoType3.setExpenditureCategory("打车");
            commodityMap.put("滴滴", wacaiAccountVoType3);
            
            WacaiAccountVo wacaiAccountVoType4= new WacaiAccountVo();
            wacaiAccountVoType4.setExpenditureCategories("交通");
            wacaiAccountVoType4.setExpenditureCategory("地铁");
            commodityMap.put("地铁", wacaiAccountVoType4);
            
            WacaiAccountVo wacaiAccountVoType5= new WacaiAccountVo();
            wacaiAccountVoType5.setExpenditureCategories("餐饮");
            wacaiAccountVoType5.setExpenditureCategory("饮料水果");
            commodityMap.put("水果", wacaiAccountVoType5);
            
            WacaiAccountVo wacaiAccountVoType6= new WacaiAccountVo();
            wacaiAccountVoType6.setExpenditureCategories("人情");
            wacaiAccountVoType6.setExpenditureCategory("物品");
            commodityMap.put("鲜花", wacaiAccountVoType6);
            
            WacaiAccountVo wacaiAccountVoType7= new WacaiAccountVo();
            wacaiAccountVoType7.setExpenditureCategories("交通");
            wacaiAccountVoType7.setExpenditureCategory("自行车");
            commodityMap.put("哈啰", wacaiAccountVoType7);
            commodityMap.put("单车", wacaiAccountVoType7);
            
            WacaiAccountVo wacaiAccountVoType8= new WacaiAccountVo();
            wacaiAccountVoType8.setExpenditureCategories("交通");
            wacaiAccountVoType8.setExpenditureCategory("火车");
            commodityMap.put("12306", wacaiAccountVoType8);
            commodityMap.put("火车票", wacaiAccountVoType8);
            
            WacaiAccountVo wacaiAccountVoType9= new WacaiAccountVo();
            wacaiAccountVoType9.setExpenditureCategories("居家");
            wacaiAccountVoType9.setExpenditureCategory("电脑宽带");
            commodityMap.put("中国联通", wacaiAccountVoType9);
            
            WacaiAccountVo wacaiAccountVoType10= new WacaiAccountVo();
            wacaiAccountVoType10.setExpenditureCategories("居家");
            wacaiAccountVoType10.setExpenditureCategory("快递邮政");
            commodityMap.put("顺丰", wacaiAccountVoType10);
            commodityMap.put("邮政", wacaiAccountVoType10);
            
            WacaiAccountVo wacaiAccountVoType11= new WacaiAccountVo();
            wacaiAccountVoType11.setExpenditureCategories("居家");
            wacaiAccountVoType11.setExpenditureCategory("水电燃气");
            commodityMap.put("电费", wacaiAccountVoType11);
            commodityMap.put("天津津滨威立雅水业有限公司", wacaiAccountVoType11);
            
            WacaiAccountVo wacaiAccountVoType12= new WacaiAccountVo();
            wacaiAccountVoType12.setExpenditureCategories("居家");
            wacaiAccountVoType12.setExpenditureCategory("材料建材");
            commodityMap.put("灯管", wacaiAccountVoType12);
        }
    }

    
    /**
     * 根据交易对象识别交易类型
     * @param wacaiAccountVo
     */
    private void recognitionTradingParty(WacaiAccountVo wacaiAccountVo) {
        //先初始化根据交易对象转换类型的map
        this.initTradingPartyMap();
        //遍历map循环使用正则表达式匹配，如果匹配上，则进行赋值，并跳出循环
        for(String key :tradingPartyMap.keySet()) {
            if(RegTest.match(wacaiAccountVo.getTradingParty(),
                    "^("+key+")$")) {
                WacaiAccountVo wacaiAccountVoType = tradingPartyMap.get(key);
                wacaiAccountVo.setExpenditureCategories(
                        wacaiAccountVoType.getExpenditureCategories());
                wacaiAccountVo.setExpenditureCategory(
                        wacaiAccountVoType.getExpenditureCategory());
                break;
            }
        }
    }
    
    /**
     * 根据交易对象转换类型的map
     */
    private void initTradingPartyMap() {
        if(this.tradingPartyMap == null) {
            this.tradingPartyMap = new HashMap<>();
            WacaiAccountVo wacaiAccountVoType1= new WacaiAccountVo();
            wacaiAccountVoType1.setExpenditureCategories("人情");
            wacaiAccountVoType1.setExpenditureCategory("亲密付");
            tradingPartyMap.put("杨瑞霞", wacaiAccountVoType1);
            
            WacaiAccountVo wacaiAccountVoType2= new WacaiAccountVo();
            wacaiAccountVoType2.setExpenditureCategories("餐饮");
            wacaiAccountVoType2.setExpenditureCategory("三餐");
            tradingPartyMap.put("出门人", wacaiAccountVoType2);
            tradingPartyMap.put("王军", wacaiAccountVoType2);
            tradingPartyMap.put("幸福不远方", wacaiAccountVoType2);
            tradingPartyMap.put("申广涛", wacaiAccountVoType2);
            tradingPartyMap.put("太阳", wacaiAccountVoType2);
            tradingPartyMap.put("餐饮", wacaiAccountVoType2);
            tradingPartyMap.put("板面", wacaiAccountVoType2);
            tradingPartyMap.put("小树林水煮鱼", wacaiAccountVoType2);
            tradingPartyMap.put("张记酱牛肉", wacaiAccountVoType2);
            tradingPartyMap.put("烤全鱼", wacaiAccountVoType2);
            tradingPartyMap.put("锅包肉", wacaiAccountVoType2);
            tradingPartyMap.put("老胜香", wacaiAccountVoType2);
            tradingPartyMap.put("橘和柠", wacaiAccountVoType2);
            tradingPartyMap.put("粥", wacaiAccountVoType2);
            tradingPartyMap.put("心语", wacaiAccountVoType2);
            tradingPartyMap.put("回头一看", wacaiAccountVoType2);
            tradingPartyMap.put("周志伟", wacaiAccountVoType2);
            tradingPartyMap.put("张金梁", wacaiAccountVoType2);
            tradingPartyMap.put("王思铭", wacaiAccountVoType2);
            tradingPartyMap.put("为了生活而奋斗", wacaiAccountVoType2);
            tradingPartyMap.put("吉野家", wacaiAccountVoType2);
            tradingPartyMap.put("金/鑫", wacaiAccountVoType2);
            tradingPartyMap.put("张青橘", wacaiAccountVoType2);
            tradingPartyMap.put("\\*占林", wacaiAccountVoType2);
            tradingPartyMap.put("李兰君", wacaiAccountVoType2);
            tradingPartyMap.put("山东马", wacaiAccountVoType2);
            tradingPartyMap.put("路记石磨煎饼", wacaiAccountVoType2);
            tradingPartyMap.put("和合谷", wacaiAccountVoType2);
            tradingPartyMap.put("可可baby", wacaiAccountVoType2);
            tradingPartyMap.put("爱拼才会赢", wacaiAccountVoType2);
            tradingPartyMap.put("何", wacaiAccountVoType2);
            tradingPartyMap.put("津川鲁饭店", wacaiAccountVoType2);
            tradingPartyMap.put("凉皮", wacaiAccountVoType2);
            tradingPartyMap.put("沈阳市金融商贸开发区城际之星超市", wacaiAccountVoType2);
            tradingPartyMap.put("正新鸡排中华店", wacaiAccountVoType2);
            tradingPartyMap.put("乐旅餐饮店", wacaiAccountVoType2);
            tradingPartyMap.put("木屋烧烤", wacaiAccountVoType2);
            
            WacaiAccountVo wacaiAccountVoType3= new WacaiAccountVo();
            wacaiAccountVoType3.setExpenditureCategories("购物");
            wacaiAccountVoType3.setExpenditureCategory("家居百货");
            tradingPartyMap.put("福众源", wacaiAccountVoType3);
            tradingPartyMap.put("天津津铁豪邦商业管理有限公司", wacaiAccountVoType3);
            tradingPartyMap.put("冀中小武", wacaiAccountVoType3);
            tradingPartyMap.put("家具", wacaiAccountVoType3);
            tradingPartyMap.put("江", wacaiAccountVoType3);

            WacaiAccountVo wacaiAccountVoType4= new WacaiAccountVo();
            wacaiAccountVoType4.setExpenditureCategories("居家");
            wacaiAccountVoType4.setExpenditureCategory("美发美容");
            tradingPartyMap.put("李志杰", wacaiAccountVoType4);
            
            WacaiAccountVo wacaiAccountVoType5= new WacaiAccountVo();
            wacaiAccountVoType5.setExpenditureCategories("居家");
            wacaiAccountVoType5.setExpenditureCategory("住宿房租");
            tradingPartyMap.put("铂涛", wacaiAccountVoType5);
            
            WacaiAccountVo wacaiAccountVoType6= new WacaiAccountVo();
            wacaiAccountVoType6.setExpenditureCategories("餐饮");
            wacaiAccountVoType6.setExpenditureCategory("买菜原料");
            tradingPartyMap.put("李记副食调料", wacaiAccountVoType6);
            tradingPartyMap.put("刘进", wacaiAccountVoType6);
            tradingPartyMap.put("利达鲜切面", wacaiAccountVoType6);
            tradingPartyMap.put("李延辉", wacaiAccountVoType6);
            tradingPartyMap.put("彩丽市场大刀凉皮", wacaiAccountVoType6);
            tradingPartyMap.put("wY", wacaiAccountVoType6);
            tradingPartyMap.put("大名府任记香油坊", wacaiAccountVoType6);
            tradingPartyMap.put("久违", wacaiAccountVoType6);
            tradingPartyMap.put("王礼状", wacaiAccountVoType6);
            tradingPartyMap.put("花自飘零水自流", wacaiAccountVoType6);
            tradingPartyMap.put("幸运的人", wacaiAccountVoType6);
            tradingPartyMap.put("朱家烘培", wacaiAccountVoType6);
            tradingPartyMap.put("任我行", wacaiAccountVoType6);
            tradingPartyMap.put("锋哥", wacaiAccountVoType6);
            tradingPartyMap.put("梅英", wacaiAccountVoType6);
            tradingPartyMap.put("恭喜发财", wacaiAccountVoType6);
            tradingPartyMap.put("\\*桂华", wacaiAccountVoType6);
            tradingPartyMap.put("鲁自春", wacaiAccountVoType6);
            tradingPartyMap.put("鲁", wacaiAccountVoType6);
            tradingPartyMap.put("赵", wacaiAccountVoType6);
            tradingPartyMap.put("\\*进", wacaiAccountVoType6);
            tradingPartyMap.put("世界因你而美", wacaiAccountVoType6);
            tradingPartyMap.put("孟鑫", wacaiAccountVoType6);
            tradingPartyMap.put("梁慧群", wacaiAccountVoType6);
            tradingPartyMap.put("面条哥", wacaiAccountVoType6);
            tradingPartyMap.put("笑看人生", wacaiAccountVoType6);
            tradingPartyMap.put("董明峰13821950376", wacaiAccountVoType6);
            tradingPartyMap.put("杨淑敏", wacaiAccountVoType6);
            tradingPartyMap.put("\\*亚珍", wacaiAccountVoType6);
            tradingPartyMap.put("\\*卫杰", wacaiAccountVoType6);
            tradingPartyMap.put("天津", wacaiAccountVoType6);
            tradingPartyMap.put("\\*宝霞", wacaiAccountVoType6);

            WacaiAccountVo wacaiAccountVoType7= new WacaiAccountVo();
            wacaiAccountVoType7.setExpenditureCategories("餐饮");
            wacaiAccountVoType7.setExpenditureCategory("饮料水果");
            tradingPartyMap.put("好人，彩丽园店", wacaiAccountVoType7);
            tradingPartyMap.put("果生鲜", wacaiAccountVoType7);
            tradingPartyMap.put("精品水果", wacaiAccountVoType7);
            tradingPartyMap.put("宇涵精品水果", wacaiAccountVoType7);
            tradingPartyMap.put("康之旅便利店", wacaiAccountVoType7);
            
            WacaiAccountVo wacaiAccountVoType8= new WacaiAccountVo();
            wacaiAccountVoType8.setExpenditureCategories("餐饮");
            wacaiAccountVoType8.setExpenditureCategory("零食");
            tradingPartyMap.put("等待绽放胖子干货", wacaiAccountVoType8);
            tradingPartyMap.put("朱家烘焙", wacaiAccountVoType8);
            tradingPartyMap.put("叮当响", wacaiAccountVoType8);
            tradingPartyMap.put("夏埠村糕点", wacaiAccountVoType8);
            tradingPartyMap.put("苏顺成纯绿豆饼\\(中华南大街店\\)", wacaiAccountVoType8);
            
            WacaiAccountVo wacaiAccountVoType9= new WacaiAccountVo();
            wacaiAccountVoType9.setExpenditureCategories("交通");
            wacaiAccountVoType9.setExpenditureCategory("公交");
            tradingPartyMap.put("公交", wacaiAccountVoType9);

            WacaiAccountVo wacaiAccountVoType10= new WacaiAccountVo();
            wacaiAccountVoType10.setExpenditureCategories("居家");
            wacaiAccountVoType10.setExpenditureCategory("快递邮政");
            tradingPartyMap.put("菜鸟驿站", wacaiAccountVoType10);
            
            WacaiAccountVo wacaiAccountVoType11= new WacaiAccountVo();
            wacaiAccountVoType11.setExpenditureCategories("交通");
            wacaiAccountVoType11.setExpenditureCategory("打车");
            tradingPartyMap.put("建亮", wacaiAccountVoType11);
            tradingPartyMap.put("滴滴出行", wacaiAccountVoType11);
            
            WacaiAccountVo wacaiAccountVoType12= new WacaiAccountVo();
            wacaiAccountVoType12.setExpenditureCategories("居家");
            wacaiAccountVoType12.setExpenditureCategory("材料建材");
            tradingPartyMap.put("中州商贸", wacaiAccountVoType12);
            
            WacaiAccountVo wacaiAccountVoType13= new WacaiAccountVo();
            wacaiAccountVoType13.setExpenditureCategories("购物");
            wacaiAccountVoType13.setExpenditureCategory("家具家纺");
            tradingPartyMap.put("窗帘", wacaiAccountVoType13);
            tradingPartyMap.put("布", wacaiAccountVoType13);
            tradingPartyMap.put("绍兴理工学长", wacaiAccountVoType13);
            
            WacaiAccountVo wacaiAccountVoType14= new WacaiAccountVo();
            wacaiAccountVoType14.setExpenditureCategories("医教");
            wacaiAccountVoType14.setExpenditureCategory("医疗药品");
            tradingPartyMap.put("药", wacaiAccountVoType14);
            tradingPartyMap.put("天津河西荣皖中医医院", wacaiAccountVoType14);
            
            WacaiAccountVo wacaiAccountVoType15= new WacaiAccountVo();
            wacaiAccountVoType15.setExpenditureCategories("居家");
            wacaiAccountVoType15.setExpenditureCategory("家政服务");
            tradingPartyMap.put("五八到家平台", wacaiAccountVoType15);
            tradingPartyMap.put("老婆儿子我的最爱电话13682087515", 
                    wacaiAccountVoType15);
            
            WacaiAccountVo wacaiAccountVoType16= new WacaiAccountVo();
            wacaiAccountVoType16.setExpenditureCategories("交通");
            wacaiAccountVoType16.setExpenditureCategory("地铁");
            tradingPartyMap.put("深圳市万通顺达科技股份有限公司", wacaiAccountVoType16);
            tradingPartyMap.put("石家庄市轨道交通有限责任公司", wacaiAccountVoType16);
            tradingPartyMap.put("天津地铁", wacaiAccountVoType16);
            
            WacaiAccountVo wacaiAccountVoType17= new WacaiAccountVo();
            wacaiAccountVoType17.setExpenditureCategories("居家");
            wacaiAccountVoType17.setExpenditureCategory("保险费");
            tradingPartyMap.put("中国人民健康保险股份有限公司", wacaiAccountVoType17);
            tradingPartyMap.put("蚂蚁会员\\(北京\\)网络技术服务有限公司", wacaiAccountVoType17);
            
            WacaiAccountVo wacaiAccountVoType18= new WacaiAccountVo();
            wacaiAccountVoType18.setExpenditureCategories("购物");
            wacaiAccountVoType18.setExpenditureCategory("电子数码");
            tradingPartyMap.put("华为商城", wacaiAccountVoType18);
            
            WacaiAccountVo wacaiAccountVoType19= new WacaiAccountVo();
            wacaiAccountVoType19.setExpenditureCategories("居家");
            wacaiAccountVoType19.setExpenditureCategory("电脑宽带");
            tradingPartyMap.put("中国联通", wacaiAccountVoType19);
            tradingPartyMap.put("中国联合网络通信有限公司", wacaiAccountVoType19);
            
            WacaiAccountVo wacaiAccountVoType20= new WacaiAccountVo();
            wacaiAccountVoType20.setExpenditureCategories("居家");
            wacaiAccountVoType20.setExpenditureCategory("水电燃气");
            tradingPartyMap.put("国网天津电力公司（智能表）-电费(自动缴费)", wacaiAccountVoType20);
            
            WacaiAccountVo wacaiAccountVoType21= new WacaiAccountVo();
            wacaiAccountVoType21.setExpenditureCategories("购物");
            wacaiAccountVoType21.setExpenditureCategory("宠物用品");
            tradingPartyMap.put("张玄石", wacaiAccountVoType21);
            
            WacaiAccountVo wacaiAccountVoType22= new WacaiAccountVo();
            wacaiAccountVoType22.setExpenditureCategories("交通");
            wacaiAccountVoType22.setExpenditureCategory("公交");
            tradingPartyMap.put("天津公交易通科技有限公司", wacaiAccountVoType22);
            
            WacaiAccountVo wacaiAccountVoType23= new WacaiAccountVo();
            wacaiAccountVoType23.setExpenditureCategories("购物");
            wacaiAccountVoType23.setExpenditureCategory("购物其他");
            tradingPartyMap.put("家友便利超市", wacaiAccountVoType23);
        }
    }
}
