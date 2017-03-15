package org.hotwheel.stock.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by wangfeng on 2017/3/16.
 */
public class Email {
    private static Logger logger = LoggerFactory.getLogger(Email.class);
    /** 邮件对象 */
    private MimeMessage mimeMsg;
    /** 发送邮件的Session会话 */
    private Session session;
    /** 邮件发送时的一些配置信息的一个属性对象 */
    private Properties props;
    /** 发件人的用户名 */
    private String sendUserName;
    /** 发件人密码 */
    private String sendUserPass;
    /** 附件添加的组件 */
    private Multipart mp;
    /** 存放附件文件 */
    private List<FileDataSource> files = new LinkedList<FileDataSource>();

    public Email(String smtp) {
        sendUserName = "";
        sendUserPass = "";
        setSmtpHost(smtp);// 设置邮件服务器
        createMimeMessage(); // 创建邮件
    }

    /**
     * 设置smtp主机
     *
     * @param hostName
     */
    public void setSmtpHost(String hostName) {
        if (props == null) {
            props = System.getProperties();
        }
        props.put("mail.smtp.host", hostName);
    }

    public boolean createMimeMessage() {
        boolean bRet = false;
        try {
            // 用props对象来创建并初始化session对象
            session = Session.getDefaultInstance(props, null);
            mimeMsg = new MimeMessage(session); // 用session对象来创建并初始化邮件对象
            mp = new MimeMultipart();// 生成附件组件的实例
            bRet = true;
        } catch (Exception e) {
            System.err.println("获取邮件会话对象时发生错误！" + e);
        }
        return bRet;
    }

    /**
     * 设置SMTP的身份认证
     */
    public void setNeedAuth(boolean need) {
        if (props == null) {
            props = System.getProperties();
        }
        if (need) {
            props.put("mail.smtp.auth", "true");
        } else {
            props.put("mail.smtp.auth", "false");
        }
    }

    /**
     * 进行用户身份验证时，设置用户名和密码
     */
    public void setNamePass(String name, String pass) {
        sendUserName = name;
        sendUserPass = pass;
    }

    /**
     * 设置邮件主题
     *
     * @param mailSubject
     * @return
     */
    public boolean setSubject(String mailSubject) {
        try {
            mimeMsg.setSubject(mailSubject);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 设置邮件内容,并设置其为文本格式或HTML文件格式，编码方式为UTF-8
     *
     * @param mailBody
     * @return
     */
    public boolean setBody(String mailBody) {
        try {
            BodyPart bp = new MimeBodyPart();
            bp.setContent("<meta http-equiv=Content-Type content=text/html; charset=UTF-8>" + mailBody,
                    "text/html;charset=UTF-8");
            // 在组件上添加邮件文本
            mp.addBodyPart(bp);
        } catch (Exception e) {
            System.err.println("设置邮件正文时发生错误！" + e);
            return false;
        }
        return true;
    }

    /**
     * 增加发送附件
     *
     * @param filename
     *            邮件附件的地址，只能是本机地址而不能是网络地址，否则抛出异常
     * @return
     */
    public boolean addFileAffix(String filename) {
        try {
            BodyPart bp = new MimeBodyPart();
            FileDataSource fileds = new FileDataSource(filename);
            bp.setDataHandler(new DataHandler(fileds));
            bp.setFileName(MimeUtility.encodeText(fileds.getName(), "utf-8", null)); // 解决附件名称乱码
            mp.addBodyPart(bp);// 添加附件
            files.add(fileds);
        } catch (Exception e) {
            System.err.println("增加邮件附件：" + filename + "发生错误！" + e);
            return false;
        }
        return true;
    }

    public boolean delFileAffix() {
        try {
            FileDataSource fileds = null;
            for (Iterator<FileDataSource> it = files.iterator(); it.hasNext();) {
                fileds = it.next();
                if (fileds != null && fileds.getFile() != null) {
                    fileds.getFile().delete();
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 设置发件人地址
     *
     * @param from
     *            发件人地址
     * @return
     */
    public boolean setFrom(String from) {
        try {
            mimeMsg.setFrom(new InternetAddress(from));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 设置收件人地址
     *
     * @param to 收件人的地址
     * @return
     */
    public boolean setTo(String to) {
        if (to == null)
            return false;
        try {
            mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 发送附件
     *
     * @param copyto
     * @return
     */
    public boolean setCopyTo(String copyto) {
        if (copyto == null)
            return false;
        try {
            mimeMsg.setRecipients(javax.mail.Message.RecipientType.CC, InternetAddress.parse(copyto));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 发送邮件
     *
     * @return
     */
    public boolean send() throws Exception {
        boolean bRet = false;
        try {
            mimeMsg.setContent(mp);
            mimeMsg.saveChanges();
            logger.info("正在发送邮件....");
            Session mailSession = Session.getInstance(props, null);
            Transport transport = mailSession.getTransport("smtp");
            // 连接邮件服务器并进行身份验证
            transport.connect((String) props.get("mail.smtp.host"), sendUserName, sendUserPass);
            // 发送邮件
            transport.sendMessage(mimeMsg, mimeMsg.getRecipients(Message.RecipientType.TO));
            logger.info("发送邮件成功！");
            transport.close();
            bRet = true;
        } catch (Exception e) {
            logger.error("邮件发送失败: ", e);
        }
        return bRet;
    }


}
