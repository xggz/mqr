package com.molicloud.mqr.plugin.dyurl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.molicloud.mqr.plugin.core.AbstractPluginExecutor;
import com.molicloud.mqr.plugin.core.PluginParam;
import com.molicloud.mqr.plugin.core.PluginResult;
import com.molicloud.mqr.plugin.core.annotation.PHook;
import com.molicloud.mqr.plugin.core.define.FaceDef;
import com.molicloud.mqr.plugin.core.enums.ExecuteTriggerEnum;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import com.molicloud.mqr.plugin.core.message.MessageBuild;
import com.molicloud.mqr.plugin.core.message.make.Ats;
import com.molicloud.mqr.plugin.core.message.make.Expression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;


/**
 * @author dlh
 * @version v1.0
 * @date 2020/12/26 11:19
 */
@Slf4j
@Component
public class DyUrlPluginExecutor extends AbstractPluginExecutor {

  private final String USER_AGENT =
      "Dalvik/2.1.0 (Linux; U; Android 10; COL-AL10 Build/HUAWEICOL-AL10)";

  @PHook(
      name = "DyUrl",
      equalsKeywords = {"抖音视频解析"},
      robotEvents = {RobotEventEnum.FRIEND_MSG, RobotEventEnum.GROUP_MSG})
  public PluginResult messageHandler(PluginParam pluginParam) {
    PluginResult pluginResult = new PluginResult();
    pluginResult.setProcessed(true);
    if (ExecuteTriggerEnum.KEYWORD.equals(pluginParam.getExecuteTriggerEnum())) {
      pluginResult.setHold(true);
      MessageBuild messageBuild = new MessageBuild();
      Ats ats = new Ats();
      ats.setMids(CollUtil.toList(pluginParam.getFrom()));
      ats.setContent("请发送抖音视频地址");
      messageBuild.append(ats);
      messageBuild.append(new Expression(FaceDef.feiwen));
      pluginResult.setMessage(messageBuild);
    } else if (ExecuteTriggerEnum.HOLD.equals(pluginParam.getExecuteTriggerEnum())) {
      String message = StrUtil.toString(pluginParam.getData());
      pluginResult.setHold(false);
      MessageBuild messageBuild = new MessageBuild();
      Ats ats = new Ats();
      ats.setMids(CollUtil.toList(pluginParam.getFrom()));
      ats.setContent(getInfo(message));
      messageBuild.append(ats);
      messageBuild.append(new Expression(FaceDef.xia));
      pluginResult.setMessage(messageBuild);
    }
    return pluginResult;
  }

  public String getInfo(String info) {
    if (!StrUtil.contains(info, "https://v.douyin.com")) {
      return "请重试，并发送正确的抖音分享链接";
    }
    String url = ReUtil.findAll("https://v.douyin.com/(.*?)/", info, 0).get(0);
    if (url != null) {
      String src = HttpRequest.get(url).header(HttpHeaders.USER_AGENT, USER_AGENT).execute().body();
      String id = ReUtil.findAll("video/(.*?)/", src, 1).get(0).trim();
      if (id != null) {
        String jk = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + id;
        String json = HttpRequest.get(jk).execute().body();
        String rel_url =
            ReUtil.findAll("play_addr\":\\{\"uri\":\"(.*?)\",\"url_list\":\\[\"(.*?)\"", json, 2)
                .get(0);
        if (rel_url != null) {
          String video_url = rel_url.replaceAll("playwm", "play");
          HttpResponse response =
              HttpRequest.get(video_url).header(HttpHeaders.USER_AGENT, USER_AGENT).execute();
          if (HttpStatus.HTTP_MOVED_TEMP == response.getStatus()) {
            return StrUtil.format(
                "视频下载地址为：【{}】，请复制到浏览器进行下载", response.header(HttpHeaders.LOCATION));
          }
        } else {
          return "获取真正的视频链接失败！";
        }
      } else {
        return "获取视频id失败！";
      }
    }
    return "提取视频链接失败！";
  }
}
