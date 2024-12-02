/*
 * Copyright (c) 2019 Of Him Code Technology Studio
 * Jpom is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * 			http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.dromara.jpom.common.commander;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 命令操作执行结果
 *
 * @author bwcx_jzy
 * @since 2022/11/30
 */
@Data
public class CommandOpResult {

    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 进程id
     */
    private Integer pid;
    /**
     * 多个进程 id
     */
    private Integer[] pids;
    /**
     * 端口
     */
    private String ports;
    /**
     * 状态消息
     */
    private String statusMsg;
    /**
     * 执行结果
     */
    private final List<String> msgs = new ArrayList<>();

    /**
     * 执行是否成功
     *
     * @return true 成功
     */
    public boolean isSuccess() {
        return success != null && success;
    }

    /**
     * 构建结构对象
     *
     * @param msg 结果消息
     * @return result
     */
    public static CommandOpResult of(String msg) {
        int[] pidsArray = null;
        String ports = null;
        if (StrUtil.startWith(msg, AbstractProjectCommander.RUNNING_TAG)) {
            List<String> list = StrUtil.splitTrim(msg, StrUtil.COLON);
            String pids = CollUtil.get(list, 1);
            pidsArray = StrUtil.splitToInt(pids, StrUtil.COMMA);
            //
            ports = CollUtil.get(list, 2);
        }
        int mainPid = ObjectUtil.defaultIfNull(ArrayUtil.get(pidsArray, 0), 0);
        CommandOpResult result = of(mainPid > 0, msg);
        if (ArrayUtil.length(pidsArray) > 1) {
            // 仅有多个进程号，才返回 pids
            result.pids = ArrayUtil.wrap(pidsArray);
        }
        result.pid = mainPid;
        result.ports = ports;
        result.statusMsg = msg;
        return result;
    }

    public static CommandOpResult of(boolean success) {
        return of(success, (List<String>) null);
    }

    public static CommandOpResult of(boolean success, String msg) {
        CommandOpResult commandOpResult = new CommandOpResult();
        commandOpResult.success = success;
        commandOpResult.appendMsg(msg);
        return commandOpResult;
    }

    public static CommandOpResult of(boolean success, List<String> msg) {
        CommandOpResult commandOpResult = new CommandOpResult();
        commandOpResult.success = success;
        Optional.ofNullable(msg).ifPresent(commandOpResult.msgs::addAll);
        return commandOpResult;
    }

    public CommandOpResult appendMsg(String msg) {
        if (StrUtil.isEmpty(msg)) {
            return this;
        }
        msgs.add(msg);
        return this;
    }

    public CommandOpResult appendMsg(List<String> msgs) {
        for (String msg : msgs) {
            this.appendMsg(msg);
        }
        return this;
    }

    public String msgStr() {
        return CollUtil.join(msgs, StrUtil.COMMA);
    }

    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
