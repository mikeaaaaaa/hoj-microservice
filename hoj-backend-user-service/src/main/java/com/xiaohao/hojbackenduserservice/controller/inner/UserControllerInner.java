package com.xiaohao.hojbackenduserservice.controller.inner;

import com.xiaohao.hojbackendmodel.model.entity.User;
import com.xiaohao.hojbackenduserservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("inner")
public class UserControllerInner {

    @Resource
    private UserService userService;

    @GetMapping("/listByIds")
    public List<User> listByIds(@RequestParam("userIdSet") Collection<Long> userIdSet){
        return userService.listByIds(userIdSet);
    }

    @GetMapping("/getById")
    public User getById(Long userId){
        return userService.getById(userId);
    }
}
