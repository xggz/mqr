package com.molicloud.mqr.plugin.votekick.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.molicloud.mqr.plugin.votekick.entity.RobotPluginVoteKick;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 投票踢人插件 Mapper 接口
 * </p>
 *
 * @author wispx wisp-x@qq.com
 * @since 2020/12/04 4:16 下午
 */
@Mapper
public interface RobotPluginManagerMapper extends BaseMapper<RobotPluginVoteKick> {
}
