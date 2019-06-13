package cn.wacai.service;

import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import cn.wacai.vo.WacaiAccountVo;

/**
 * 挖财账本文件处理服务接口
 * @author ZhaoZhigang
 *
 */
public interface WaCaiService {
    /**
     * 将挖财账本文件列表转换为excel文件并存到response中进行下载
     * @param wacaiAccountVoList
     * @param response
     * @param saveType 
     * @param filePath 
     */
    public void exportExcel(ArrayList<WacaiAccountVo> wacaiAccountVoList
            , HttpServletResponse response
            , String saveType, String filePath);
}
