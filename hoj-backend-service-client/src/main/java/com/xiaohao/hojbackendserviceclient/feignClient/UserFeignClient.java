package com.xiaohao.hojbackendserviceclient.feignClient;

import com.xiaohao.hojbackendcommon.common.ErrorCode;
import com.xiaohao.hojbackendcommon.exception.BusinessException;
import com.xiaohao.hojbackendmodel.model.entity.User;
import com.xiaohao.hojbackendmodel.model.enums.UserRoleEnum;
import com.xiaohao.hojbackendmodel.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

import static com.xiaohao.hojbackendcommon.constant.UserConstant.USER_LOGIN_STATE;

@FeignClient(name = "hoj-backend-user-service", path = "/api/user/inner")
public interface UserFeignClient {
    default User getLoginUser(HttpServletRequest request){
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    default boolean isAdmin(HttpServletRequest request){
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    default boolean isAdmin(User user){
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    @GetMapping("/listByIds")
    List<User> listByIds(@RequestParam("userIdSet") Collection<Long> userIdSet);

    default UserVO getUserVO(User user){
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
    @GetMapping("/getById")
    User getById(@RequestParam("userId") Long userId);
}
