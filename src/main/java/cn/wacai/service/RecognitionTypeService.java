package cn.wacai.service;

import java.util.ArrayList;

import cn.wacai.vo.WacaiAccountVo;

/**
 * 交易类型识别接口
 * @author ZhaoZhigang
 *
 */
public interface RecognitionTypeService {
    /**
     * 自动识别交易类型方法
     * @param accountVos
     */
    public void recognitionType(ArrayList<WacaiAccountVo> accountVos);
}
