package cn.wacai.vo;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;
/**
 * 支付宝账本对象VO类
 * @author ZhaoZhigang
 *
 */
@Data
@SuppressWarnings("unused")
public class AlipayAccountVo {
    /** 交易号 */
    private String transactionNumber;
    /** 商家订单号     */
    private String merchantOrderNumber;
    /** 交易创建时间     */
    private Date transactionCreationTime;
    /** 付款时间     */
    private Date paymentTime;
    /** 最近修改时间     */
    private Date latestRevisionTime;
    /** 交易来源地     */
    private String sourceOfTransaction;
    /** 类型     */
    private String type;
    /** 交易对方     */
    private String tradingParty;
    /** 商品名称     */
    private String tradeName;
    /** 金额（元）     */
    private BigDecimal amount;
    /** 收/支     */
    private String collectionOrSupport;
    /** 交易状态     */
    private String tradingStatus;
    /** 服务费（元）     */
    private BigDecimal serviceFee;
    /** 成功退款（元）     */
    private BigDecimal successfulRefund;
    /** 备注 */
    private String remarks;
    /** 资金状态     */
    private String fundStatus;
}
