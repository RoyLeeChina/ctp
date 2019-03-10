package org.hotwheel.smartqq;

import org.hotwheel.smartqq.callback.MessageCallback;
import org.hotwheel.smartqq.client.SmartQQClient;
import org.hotwheel.smartqq.model.Category;
import org.hotwheel.smartqq.model.DiscussMessage;
import org.hotwheel.smartqq.model.Friend;
import org.hotwheel.smartqq.model.Group;
import org.hotwheel.smartqq.model.GroupMessage;
import org.hotwheel.smartqq.model.Message;

import java.io.IOException;
import java.util.List;

/**
 * @author ScienJus
 * @date 2015/12/18.
 */
public class Application {

    public static void main(String[] args) {
        //创建一个新对象时需要扫描二维码登录，并且传一个处理接收到消息的回调，如果你不需要接收消息，可以传null
        SmartQQClient client = new SmartQQClient(new MessageCallback() {
            @Override
            public void onMessage(Message message) {
                System.out.println(message.getContent());
            }

            @Override
            public void onGroupMessage(GroupMessage message) {
                System.out.println(message.getContent());
            }

            @Override
            public void onDiscussMessage(DiscussMessage message) {
                System.out.println(message.getContent());
            }
        });
        //登录成功后便可以编写你自己的业务逻辑了
        List<Category> categories = client.getFriendListWithCategory();
        for (Category category : categories) {
            System.out.println(category.getName());
            for (Friend friend : category.getFriends()) {
                System.out.println("————" + friend.getNickname() + "(" + friend.getUserId() + ")");
            }
        }
        List<Group> groups = client.getGroupList();
        for (Group group : groups) {
            System.out.println("code=" + group.getCode() + ", id=" + group.getId() + ", name=" + group.getName());
        }
        long groupId = 4132949947L;
        for (int i = 0; i < 1; i++) {
            //client.sendMessageToGroup(groupId, "这个群要做其它用途, 侯总、老头、许公子留下，其他人都退了吧~");
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //使用后调用close方法关闭，你也可以使用try-with-resource创建该对象并自动关闭
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
