package com.atguigu.auth.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
public class GatewayTestDemo02 {
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    TaskService taskService;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    HistoryService historyService;

    @Test
    public void deploy(){
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/bingxing.bpmn20.xml")
                .name("并行网关测试")
                .deploy();

        System.out.println(deploy.getCategory());
        System.out.println("deploy.getId() = " + deploy.getId());
        System.out.println("deploy.getName() = " + deploy.getName());
    }

    @Test
    public void startProcess2(){
        //设置请假天数
//        HashMap<String , Object> map = new HashMap<>();
//        map.put("day",4);
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("bingxing");
        System.out.println(instance.getId());
        System.out.println(instance.getProcessInstanceId());
    }

    @Test
    public void completeTask(){
        TaskQuery taskQuery = taskService.createTaskQuery();
        Task dabian = taskQuery.taskAssignee("xiaoli").singleResult();

        taskService.complete(dabian.getId());
    }
    //查询待办任务
    @Test
    public void taskAssign(){
        TaskQuery taskQuery = taskService.createTaskQuery();
        taskQuery.taskAssignee("xiaoli").list().forEach(s->{
            System.out.println(s.getId());
            System.out.println(s.getAssignee());
            System.out.println(s.getName());
        });

    }
    @Test
    public void historyTask(){
        historyService.createHistoricTaskInstanceQuery()
                .taskAssignee("xiaoli")
                .finished()
                .list().forEach(s->{
                    System.out.println(s.getId());
                    System.out.println(s.getAssignee());
                    System.out.println(s.getName());
                });
    }
}
