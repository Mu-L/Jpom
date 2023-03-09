package io.jpom.controller.build.repository;

import cn.hutool.db.Page;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * GitHub 工具
 *
 * @author sam
 * @since 2023/3/9
 */
public class GitHubUtil {
    /**
     * GitHub 用户信息实体类
     * <p>
     * 参考：https://docs.github.com/en/rest/users/users#about-the-users-api
     */
    @Data
    public static class GitHubUserInfo {
        // 只列出目前需要用到的字段

        /**
         * 用户名，如：octocat
         */
        private String login;

        /**
         * 公开仓库数量，如：2
         */
        public int public_repos;

        /**
         * 私有的仓库总数，如：100
         */
        public int total_private_repos;

        /**
         * 拥有的私有仓库，如：100
         */
        public int owned_private_repos;
    }

    /**
     * GitHub 头部
     */
    private static final String GITHUB_HEADER_ACCEPT = "application/vnd.github.v3+json";

    /**
     * GitHub 用户 token 前缀
     */
    private static final String GITHUB_TOKEN = "token ";

    /**
     * GitHub API 前缀
     */
    private static final String GITHUB_API_PREFIX = "https://api.github.com";

    /**
     * 获取 GitHub 用户信息
     *
     * @param token 用户 token
     * @return GitHub 用户信息
     */
    public static GitHubUserInfo getGitHubUserInfo(String token) {
        // 参考：https://docs.github.com/en/rest/users/users#about-the-users-api
        HttpResponse response = HttpUtil
            .createGet(GITHUB_API_PREFIX + "/user")
            .header(Header.ACCEPT, GITHUB_HEADER_ACCEPT)
            .header(Header.AUTHORIZATION, GITHUB_TOKEN + token)
            .execute();
        String body = response.body();
        Assert.state(response.isOk(), "令牌信息错误：" + body);
        return JSONObject.parseObject(body, GitHubUserInfo.class);
    }

    /**
     * 获取 GitHub 仓库信息
     *
     * @param token
     */
    public static JSONArray getGitHubUserRepos(String token, Page page) {
        // 参考：https://docs.github.com/en/rest/repos/repos#list-repositories-for-the-authenticated-user
        HttpResponse response = HttpUtil
            .createGet(GITHUB_API_PREFIX + "/user/repos")
            .header(Header.ACCEPT, GITHUB_HEADER_ACCEPT)
            .header(Header.AUTHORIZATION, GITHUB_TOKEN + token)
            .form("access_token", token)
            .form("sort", "pushed")
            .form("page", page.getPageNumber())
            .form("per_page", page.getPageSize())
            .execute();
        String body = response.body();
        Assert.state(response.isOk(), "拉取仓库信息错误：" + body);
        return JSONArray.parseArray(body);
    }

}
