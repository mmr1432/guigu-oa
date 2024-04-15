package com.atguigu.auth.activiti;

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

@SpringBootTest
public class ProcessTestDemo {
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    TaskService taskService;
    @Autowired
    RuntimeService runtimeService;

    @Test
    public void deploy(){
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban04.bpmn20.xml")
                .name("请假流程")
                .deploy();

        System.out.println(deploy.getCategory());
        System.out.println("deploy.getId() = " + deploy.getId());
        System.out.println("deploy.getName() = " + deploy.getName());
    }
//启动流程实例
    @Test
    public void startProcess2(){
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("jiaban04");
        System.out.println(instance.getId());
        System.out.println(instance.getProcessInstanceId());
    }
    @Test
    public void taskAssign(){
        TaskQuery taskQuery = taskService.createTaskQuery();
        taskQuery.taskAssignee("tom01").list().forEach(s->{
            System.out.println(s.getId());
            System.out.println(s.getAssignee());
            System.out.println(s.getName());
        });

    }

    //拾取组任务
    @Test
    public void claimTask(){
        Task task = taskService.createTaskQuery()
                .taskCandidateUser("tom01")
                .singleResult();
        if (task!= null) {
            taskService.claim(task.getId(), "tom01");
            System.out.println("拾取成功");
        }
    }
//查询任务
    @Test
    public void taskQuery(){
        TaskQuery taskQuery = taskService.createTaskQuery();
        taskQuery.taskAssignee("tom01")
                .list().forEach(s->{
                    System.out.println(s.getId());
                    System.out.println(s.getAssignee());
                    System.out.println(s.getName());
                });
    }
    @Test
    public void completeTask(){
        TaskQuery taskQuery = taskService.createTaskQuery();
        Task tom01 = taskQuery.taskAssignee("tom01")
                .singleResult();

        taskService.complete(tom01.getId());
        System.out.println("处理任务");
    }
}
