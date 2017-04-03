package org.hotwheel.weixin;

import org.hotwheel.assembly.Api;
import org.hotwheel.weixin.bean.MessageEntity;
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
	public void start(final WeChat weChat, final WeChatContext context){

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				LOG.info("进入消息监听模式 ...");
				weChat.choiceSyncLine();
				while(true){
					long tmCheckPoint = System.currentTimeMillis();
					try {
						SyncResponse sync = weChat.syncCheck(null);
						LOG.debug("retcode={}, selector={}", sync.retcode, sync.selector);
						if (sync.retcode == 1100) {
							LOG.info("你在手机上登出了微信，再见");
							break;
						} else if (sync.retcode == 1101) {
							LOG.debug("[*] 你在其他地方登录了 WEB 版微信，再见");
							break;
						} else if (sync.retcode == 0) {
							if (sync.selector == 2) {
								// 新消息
								WxMessage wxMessage = weChat.webwxsync();
								List<MessageEntity> msgList = wxMessage.getAddMsgList();
								for (MessageEntity message : msgList) {
									int msgType = message.getMsgType();
									String umFrom = message.getFromUserName();
									String umTo = message.getToUserName();
									String nmFrom = weChat.mapUserToNick.get(message.getFromUserName());
									String nmTo = weChat.mapUserToNick.get(message.getToUserName());
									if (msgType == 10000) {
										// 群管理 消息
									} else if (msgType == WxMsgType.GOTCONTACT) {
										LOG.info("成功获取联系人信息");
									} else if (msgType == WxMsgType.UNDO) {
										// 回撤消息
										weChat.sendGroupMessage(umFrom, umTo, "干嘛撤回消息呢~~~");
									} else if (msgType != WxMsgType.TEXT) {
										// 非 文本消息
										//LOG.info("MsgType[{}]: from={}, to={}, message={}", msgType, nmFrom, nmTo, message.getContent());
										LOG.info("非文本消息");
										continue;
									} else {
										LOG.info("MsgType[{}]: from={}, to={}, message={}", msgType, nmFrom, nmTo, message.getContent());
										// 只处理群消息
										if (message.getFromUserName().startsWith("@@")) {
											String groupId = message.getFromUserName();
											String toUser = message.getToUserName();
											String msg = message.getContent();
											String[] args = msg.split(":<br/>");
											String fromUser = args.length > 1 ? args[0]: msg;
											msg = args.length > 1 ? args[1] : "";
											LOG.info("name={}, message={}", fromUser, msg);
											context.handleMessage(groupId, fromUser, toUser, msg);
										} else if (message.getToUserName().equalsIgnoreCase(weChat.kFromUser)) {
											String groupId = null;
											String fromUser = message.getFromUserName();
											String nickName = weChat.mapUserToNick.get(fromUser);
											if (!Api.isEmpty(nickName)) {
												String toUser = message.getToUserName();
												String msg = message.getContent();
												msg = "@王布衣 " + msg.trim();
												//msgIdList.add(msgId);
												context.handleMessage(groupId, fromUser, toUser, msg);
												//weChat.sendMessage("娘子", "要水了.");
											}
										}
									}
								}
							} else if (sync.selector == 6) {
								// 红包
								weChat.webwxsync();
								//weChat.handleMsg(wechatMeta, data);
							} else if (sync.selector == 7) {
								// 手机上操作
								playWeChat += 1;
								LOG.info("你在手机上玩微信被我发现了 {} 次", playWeChat);
								weChat.webwxsync();
							} else if (sync.selector == 3) {
								continue;
							} else if (sync.selector == 0) {
								continue;
							}
							continue;
						} else {
							//
						}
					} catch (Exception e) {
						LOG.error("", e);
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
