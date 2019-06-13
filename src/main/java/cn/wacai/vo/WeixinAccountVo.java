package cn.wacai.vo;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * 微信账本对象VO类
 * @author ZhaoZhigang
 *
 */
@Data
@SuppressWarnings("unused")
public class WeixinAccountVo {
    /** 交易时间 */
    private Date transactionTime;
    /** 交易类型 */
    private String transactionType;
    /** 交易对方 */
    private String tradingParty;
    /** 商品 */
    private String commodity;
    /** 收/支 */
    private String collectionOrSupport;
    /** 金额(元) */
    private BigDecimal amount;
    /** 支付方式 */
    private String paymentMethod;
    /** 当前状态 */
    private String currentState;
    /** 交易单号 */
    private String transactionNumber;
    /** 商户单号 */
    private String merchantNumber;
    /** 备注 */
    private String remarks;
}
