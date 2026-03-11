package com.kitchen.service;

import com.kitchen.entity.SysFile;

/**
 * 文件服务接口
 */
public interface SysFileService {

    /**
     * 保存文件到数据库
     * @param file 文件实体
     * @return 文件ID
     */
    Long saveFile(SysFile file);

    /**
     * 根据ID获取文件
     * @param id 文件ID
     * @return 文件实体
     */
    SysFile getFileById(Long id);

    /**
     * 根据ID删除文件
     * @param id 文件ID
     */
    void deleteFile(Long id);
}
