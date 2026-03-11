package com.kitchen.service.impl;

import com.kitchen.entity.SysFile;
import com.kitchen.exception.BusinessException;
import com.kitchen.mapper.SysFileMapper;
import com.kitchen.service.SysFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 文件服务实现类
 * 将文件二进制数据存储到数据库
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysFileServiceImpl implements SysFileService {

    private final SysFileMapper sysFileMapper;

    /**
     * 保存文件到数据库
     * @param file 文件实体
     * @return 文件ID
     */
    @Override
    public Long saveFile(SysFile file) {
        log.info("保存文件到数据库: fileName={}, size={}", file.getOriginalName(), file.getFileSize());
        sysFileMapper.insert(file);
        return file.getId();
    }

    /**
     * 根据ID获取文件
     * @param id 文件ID
     * @return 文件实体
     * @throws BusinessException 文件不存在
     */
    @Override
    public SysFile getFileById(Long id) {
        SysFile file = sysFileMapper.selectById(id);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }
        return file;
    }

    /**
     * 根据ID删除文件
     * @param id 文件ID
     */
    @Override
    public void deleteFile(Long id) {
        sysFileMapper.deleteById(id);
    }
}
