package org.hotwheel.weixin;

import org.hotwheel.assembly.Api;
import org.hotwheel.weixin.bean.AddMsgListEntity;
import org.hotwheel.weixin.bean.SyncResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 消息监听器
 */
public class WechatListener {
	private static final Logger LOG = LoggerFactory.getLogger(WechatListener.class);
	
	int playWeChat = 0;

	/**
	 * 启动监听器
	 * @param weChat
	 * @param context
	 */
	public void start(final WeChat weChat, WeChatContext context){

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				LOG.info("进入消息监听模式 ...");
				weChat.choiceSyncLine();
				while(true){
					long tmCheckPoint = System.currentTimeMillis();
					SyncResponse sync = weChat.syncCheck(null);
					LOG.debug("retcode={}, selector={}", sync.retcode, sync.selector);
					if(sync.retcode == 1100){
						LOG.info("你在手机上登出了微信，再见");
						break;
					} else if (sync.retcode == 1101) {
						LOG.debug("[*] 你在其他地方登录了 WEB 版微信，再见");
						break;
					} else if(sync.retcode == 0){
						if(sync.selector == 2){
							// 新消息
							List<AddMsgListEntity> data = weChat.webwxsync();
							for (AddMsgListEntity msg : data) {
								//context.handleMessage(data);
								//weChat.handleMsg(wechatMeta, data);
							}
						} else if(sync.selector == 6) {
							// 红包
							weChat.webwxsync();
							//weChat.handleMsg(wechatMeta, data);
						} else if(sync.selector == 7) {
							// 手机上操作
							playWeChat += 1;
							LOG.info("你在手机上玩微信被我发现了 {} 次", playWeChat);
							weChat.webwxsync();
						} else if(sync.selector == 3) {
							continue;
						} else if(sync.selector == 0){
							continue;
						}
					} else {
						// 
					}
					tmCheckPoint = System.currentTimeMillis() - tmCheckPoint;
					tmCheckPoint = WeChat.kHeartSleep - tmCheckPoint;
					if (tmCheckPoint > 0) {
						Api.sleep(tmCheckPoint);
					}
				}
			}
		}, "wechat-listener-thread");
		thread.setDaemon(true);
		thread.start();
	}
}
