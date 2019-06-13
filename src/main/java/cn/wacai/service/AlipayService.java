package cn.wacai.service;

import java.util.ArrayList;

import cn.wacai.vo.WacaiAccountVo;

/**
 * 支付宝账本转换服务接口
 * @author ZhaoZhigang
 *
 */
public interface AlipayService {
    /**
     * 将路径中的支付宝账本文件转换为挖财账本对象列表
     * @param filePath
     * @return ArrayList<WacaiAccountVo>
     */
    public ArrayList<WacaiAccountVo> convertExcel(String filePath);
}
