package com.atguigu.process.service.impl;

import com.atguigu.process.mapper.ProcessMapper;
import com.atguigu.process.service.ProcessService;
import com.atguigu.vo.process.ProcessQueryVo;
import com.atguigu.vo.process.ProcessVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import com.atguigu.model.process.Process;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-04-16
 */
@Service
public class ProcessServiceImpl extends ServiceImpl<ProcessMapper, Process> implements ProcessService {

    RepositoryService repositoryService;
    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, @Param("vo") ProcessQueryVo processQueryVo) {
        IPage<ProcessVo> selectPage  = baseMapper.selectPage(pageParam,processQueryVo);
        return selectPage;
    }

    @Override
    public void deployByZip(String path) {
        InputStream is = this.getClass().getResourceAsStream(path);
        ZipInputStream zipInputStream = new ZipInputStream(is);
        //部署

        Deployment deploy = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
        System.out.println("deploy.getId()+deploy.getName() = " + deploy.getId() + deploy.getName());
    }
}
