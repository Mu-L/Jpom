package cn.keepbx.jpom.controller;

import cn.keepbx.jpom.common.interceptor.NotLogin;
import cn.keepbx.jpom.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;

/**
 * 首页
 *
 * @author Administrator
 */
@Controller
@RequestMapping(value = "/")
public class IndexControl extends BaseController {

    @Resource
    private UserService userService;

    @RequestMapping(value = "error", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @NotLogin
    public String error() {
        return "error";
    }

    /**
     * 加载首页
     *
     * @return page
     */
    @RequestMapping(value = {"index", "", "index.html", "/"}, method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        setAttribute("manager", userService.isManager("", getUserName()));
        return "index";
    }

    /**
     * 退出登录
     *
     * @return page
     */
    @RequestMapping(value = "logout")
    public String logout() {
        getSession().invalidate();
        return "login";
    }
}
