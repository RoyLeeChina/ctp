package org.hotwheel.ctp.util;

/**
 * 邮件发送
 *
 * Created by wangfeng on 2017/3/24.
 * @verseion 1.0.2
 */

public class MailSender {
    /*

    private static PropertiesLoader propertiesLoader = new PropertiesLoader("bdsc-web.properties");//读取配置文件
    public static void mailSimple(String toMailSAddress,String subject,String content) {
        // 发送器
        JavaMailSenderImpl mailSender= new JavaMailSenderImpl();
        // 建立邮件消息,发送简单邮件和html邮件的区别
        MimeMessage mailMessage = mailSender.createMimeMessage();
        // 为防止乱码，添加编码集设置
        MimeMessageHelper messageHelper = null;
        try {
            //发送附件 则 参数为 multipart 为 ture
            messageHelper = new MimeMessageHelper(mailMessage,true,"UTF-8");
        } catch (MessagingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        //设定mail server
        mailSender.setHost(propertiesLoader.getProperty("mail.smtp.host"));
        mailSender.setPort(propertiesLoader.getInteger("mail.smtp.port"));
        mailSender.setUsername(propertiesLoader.getProperty("mail.smtp.username"));
        mailSender.setPassword(propertiesLoader.getProperty("mail.smtp.password"));
        //建立邮件消息
        //设置收件人、寄件人
        try {
            messageHelper.setTo(toMailSAddress);
            messageHelper.setFrom(propertiesLoader.getProperty("mail.smtp.username"));
            messageHelper.setSubject(subject);
            messageHelper.setText(content,true);
            //附件内容
            messageHelper.addAttachment("附件1", new File("h:/test/abc.pdf"));
            messageHelper.addAttachment("附件2", new File("h:/test/qwe.gif"));
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //发送邮件
        mailSender.send(mailMessage);
        System.out.println("邮件发送成功!");

    }
    public static void main(String[] args) {
        try {
            MailSender.mailSimple("xxx@sina.com","qwe","qwe");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    */
}