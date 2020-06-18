package cn.itcast.service.client;

import cn.itcast.service.pojo.User;
import org.springframework.stereotype.Component;


@Component
public class UserClientFallback implements UserClient {
    @Override
    public User queryUserById(Long id) {
        User user = new User();
        user.setName("服务器正忙,请稍候再试");
        return user;
    }
}
