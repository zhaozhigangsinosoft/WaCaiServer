package cn.wacai.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.util.FileUtils;
import cn.util.RegTest;
import cn.wacai.service.AlipayService;
import cn.wacai.vo.AlipayAccountVo;
import cn.wacai.vo.WacaiAccountVo;

/**
 * 支付宝账本转换服务接口实现类
 * @author ZhaoZhigang
 *
 */
@Service
public class AlipayServiceImpl implements AlipayService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 将路径中的支付宝账本文件转换为挖财账本对象列表
     * @param filePath
     * @return ArrayList<WacaiAccountVo>
     */
    @Override
    public ArrayList<WacaiAccountVo> convertExcel(String filePath) {
        String accountFileName = null;
        ArrayList<WacaiAccountVo> wacaiAccountVoList = new ArrayList<>();
        try {
            //迭代获取路径下所有文件
            ArrayList<File> fileList = FileUtils.getFiles(filePath,true);
            for (Iterator<File> iterator = fileList.iterator(); 
                    iterator.hasNext();) {
                File file = (File) iterator.next();
                String fileName = file.getName();
                //遍历所有文件名，如果是支付宝账本的规则，则进行解析处理
                if(RegTest.match(fileName, "^alipay_record.+\\.csv$")) {
                    accountFileName = file.getPath();
                    ArrayList<AlipayAccountVo> alipayAccountVoList 
                            = this.readFile(accountFileName);
                    wacaiAccountVoList.addAll(
                            this.convertList(alipayAccountVoList));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        
        return wacaiAccountVoList;
    }

    /**
     * 将支付宝账本对象列表转换为挖财账本对象列表，并做初步对象赋值
     * @param alipayAccountVoList
     * @return ArrayList<WacaiAccountVo>
     */
    private ArrayList<WacaiAccountVo> convertList(
            ArrayList<AlipayAccountVo> alipayAccountVoList) {
        
        ArrayList<WacaiAccountVo> wacaiAccountVoList 
                = new ArrayList<WacaiAccountVo>();
        for (Iterator<AlipayAccountVo> iterator = 
                alipayAccountVoList.iterator();iterator.hasNext();) {
            AlipayAccountVo alipayAccountVo = iterator.next();
            //仅对交易类型为收入或支出的数据进行处理
            if(RegTest.match(alipayAccountVo.getCollectionOrSupport(), 
                    "^.*(支出|收入).*$")) {
                WacaiAccountVo wacaiAccountVo = new WacaiAccountVo();
                
                wacaiAccountVo.setCollectionOrSupport(
                        alipayAccountVo.getCollectionOrSupport());
                wacaiAccountVo.setTradingParty(
                        alipayAccountVo.getTradingParty());
                wacaiAccountVo.setCommodity(
                        alipayAccountVo.getTradeName());
                if(alipayAccountVo.getCollectionOrSupport().equals("收入")) {
                    wacaiAccountVo.setExpenditureCategories("退款返款");
                    wacaiAccountVo.setMemberAmount("家庭公用:"+
                            alipayAccountVo.getAmount().toString());
                }else {
                    wacaiAccountVo.setExpenditureCategories("居家");
                    wacaiAccountVo.setExpenditureCategory("漏记款");
                    wacaiAccountVo.setMemberAmount("自己:"+
                            alipayAccountVo.getAmount().toString());
                }
                wacaiAccountVo.setAccount("XX支付宝");
                wacaiAccountVo.setCurrency("人民币");
                wacaiAccountVo.setProject("日常");
                wacaiAccountVo.setBusiness("");
                wacaiAccountVo.setReimbursement("非报销");
                wacaiAccountVo.setConsumptionDate(
                        alipayAccountVo.getTransactionCreationTime());
                wacaiAccountVo.setConsumptionAmount(alipayAccountVo.getAmount());
                wacaiAccountVo.setRemarks(alipayAccountVo.getTradingParty()+"-"+
                        alipayAccountVo.getTradeName()+"-"+
                        alipayAccountVo.getCollectionOrSupport()+"-"+
                        alipayAccountVo.getAmount().toString()
                        );
                wacaiAccountVo.setAccountBook("日常账本");
                //处理完毕后将对象添加到返回列表中
                wacaiAccountVoList.add(wacaiAccountVo);
            }
        }
        return wacaiAccountVoList;
    }

    /**
     * 从csv文件中读取支付宝账本，转换为支付宝账本对象列表
     * @param filePath
     * @return ArrayList<AlipayAccountVo>
     */
    private ArrayList<AlipayAccountVo> readFile(String filePath) {
        //定义一个标识，需要解析此行数据时设置为true,否则为false
        boolean startFlag = false;
        ArrayList<AlipayAccountVo> accountVoList 
                = new ArrayList<AlipayAccountVo>();
        //由于后面需要替换所有空格为空，因此这里的日期格式里也去掉了空格
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(filePath),"GBK"));
            String line;
            // 逐行遍历文件内容，一次读入一行数据
            while ((line = br.readLine()) != null) {
                // 先将空格和制表位替换为空
                line = line.replaceAll(" ", "").replaceAll("\t", "");
                // 遍历到横线行时，说明账本数据已经结束，将标识设置为false,不再进行解析,跳出循环
                if(line.startsWith("------------------------------------")) {
                    startFlag = false;
                    break;
                }
                //标识为true，正式开始解析账单数据
                if(startFlag) {
                    int index = 0;
                    AlipayAccountVo alipayAccountVo= new AlipayAccountVo();
                    String[] splitLines = line.split(",");
                    alipayAccountVo.setTransactionNumber(splitLines[index++]);
                    alipayAccountVo.setMerchantOrderNumber(splitLines[index++]);
                    try {
                        alipayAccountVo.setTransactionCreationTime(
                                sdf.parse(splitLines[index++]));
                    } catch (ParseException e) {
                        logger.error("TransactionCreationTime保存失败");
                        logger.error(e.getMessage(),e);
                    }
                    try {
                        alipayAccountVo.setPaymentTime(
                                sdf.parse(splitLines[index++]));
                    } catch (ParseException e) {
                        logger.warn("PaymentTime保存失败");
                    }
                    try {
                        alipayAccountVo.setLatestRevisionTime(
                                sdf.parse(splitLines[index++]));
                    } catch (ParseException e) {
                        logger.warn("LatestRevisionTime保存失败");
                    }
                    alipayAccountVo.setSourceOfTransaction(splitLines[index++]);
                    alipayAccountVo.setType(splitLines[index++]);
                    alipayAccountVo.setTradingParty(splitLines[index++]);
                    alipayAccountVo.setTradeName(splitLines[index++]);
                    alipayAccountVo.setAmount(
                            new BigDecimal(splitLines[index++]));
                    alipayAccountVo.setCollectionOrSupport(splitLines[index++]);
                    alipayAccountVo.setTradingStatus(splitLines[index++]);
                    alipayAccountVo.setServiceFee(
                            new BigDecimal(splitLines[index++]));
                    alipayAccountVo.setSuccessfulRefund(
                            new BigDecimal(splitLines[index++]));
                    accountVoList.add(alipayAccountVo);
                }
                //“交易号”开头的下一行，账本数据文件正式开始，
                //将标识设置为true,下次循环开始解析
                if(!startFlag&&line.startsWith("交易号")) {
                    startFlag = true;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        } finally {
            //关闭bufferReader
            try {
                br.close();
            } catch (IOException e) {
            }
        }
        return accountVoList;
    }
}
