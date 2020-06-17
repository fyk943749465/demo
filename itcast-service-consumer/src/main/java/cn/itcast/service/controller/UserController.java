package cn.itcast.service.controller;

import cn.itcast.service.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("consumer/user")
public class UserController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;  //包含了拉取的所有服务信息

    @GetMapping
    @ResponseBody
    public User queryUserById(@RequestParam("id")Long id) {

       List<ServiceInstance> instanceList = discoveryClient.getInstances("service-provider");
       ServiceInstance instance = instanceList.get(0);
       // 硬编码问题
       return this.restTemplate.getForObject("http://" + instance.getHost()+":"+instance.getPort()+"/user/" + id, User.class);
    }
}
