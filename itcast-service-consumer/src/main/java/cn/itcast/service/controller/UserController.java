package cn.itcast.service.controller;

import cn.itcast.service.client.UserClient;
import cn.itcast.service.pojo.User;
import com.google.gson.Gson;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("consumer/user")
//@DefaultProperties(defaultFallback = "fallbackMethod")// 全局熔断方法 //开启feign之后,这里就不要了
public class UserController {

      // 启用feign之后,不需要这个了
//    @Autowired
//    private RestTemplate restTemplate;
// 以前的调用方式
//    @Autowired
//    private DiscoveryClient discoveryClient;  //包含了拉取的所有服务信息
//
//    @GetMapping
//    @ResponseBody
//    public User queryUserById(@RequestParam("id")Long id) {
//
//       List<ServiceInstance> instanceList = discoveryClient.getInstances("service-provider");
//       ServiceInstance instance = instanceList.get(0);
//       // 硬编码问题
//       return this.restTemplate.getForObject("http://" + instance.getHost()+":"+instance.getPort()+"/user/" + id, User.class);
//    }

    // 开启负载均衡后的调用方式
//    @GetMapping
//    @ResponseBody
//    @HystrixCommand(fallbackMethod = "queryUserByIdFallback")  //自定义局部熔断方法
//    public String queryUserById(@RequestParam("id")Long id) {
//        return this.restTemplate.getForObject("http://service-provider/user/" + id, String.class);
//
//    }
//
//    // 上面方法的熔断方法
//    public String queryUserByIdFallback(Long id) {
//        return "服务器正忙,请稍后再试";
//    }

//    @GetMapping
//    @ResponseBody
//    @HystrixCommand  //使用全局熔断方法
//    public String queryUserById(@RequestParam("id")Long id) {
//
//        if (id == 1) {
//            throw new RuntimeException("xxxxx");
//        }
//
//        return this.restTemplate.getForObject("http://service-provider/user/" + id, String.class);
//
//    }
//
//    // 全局熔断方法 熔断方法的返回值一定要跟所有方法的返回值一样,不能定义参数列表
//    public String fallbackMethod() {
//        return "服务器正忙,请稍后再试";
//    }


    @Autowired
    private UserClient userClient;

    // 启用feign之后的玩法
    @GetMapping
    @ResponseBody
    @HystrixCommand  //使用全局熔断方法
    public String queryUserById(@RequestParam("id")Long id) {

        Gson gson = new Gson();
        return gson.toJson(this.userClient.queryUserById(id));

    }
}
