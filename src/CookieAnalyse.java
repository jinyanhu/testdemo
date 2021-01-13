import com.sun.javafx.binding.StringFormatter;
import javafx.beans.binding.StringExpression;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author IN
 * @version 1.0
 * @date 2021/1/4
 * @description TODO
 **/
public class CookieAnalyse {
    public static final String HOME = "https://dealer.tfschina.com.cn";
    public static final String HOME2 = "http://dportal.tfschina.com.cn";

    //必须步骤
    public static final String INDEX = HOME+"/sys=dportal";
    public static final String LOGIN = HOME+"/idp/authcenter/ActionAuthChain";
    public static final String PASSWORD = HOME+"/idp/cancelPasswordRemind";
    public static final String AUTHN_ENGINE = HOME+"/idp/AuthnEngine?currentAuth=urn_oasis_names_tc_SAML_2.0_ac_classes_BAMUsernamePassword";

    //获取fedAuth
    public static final String SHARE_POINT = HOME2+"/_layouts/15/FTChina.SharePoint.SSO/spsso.aspx?token=%s&amp;Source=";

    //获取会话sesssion
    public static final String LOGIN_BOP = "http://dealer.tfschina.com.cn/tfsbop-web/loginBop/login";

    //测试业务url
    public static final String GET_TASK_TYPE ="http://dealer.tfschina.com.cn/tfsbop-web/getTaskType?mode=PROCESS_NAME&r=0.7994680552698867";

    public static void main(String[] args) throws IOException {
        CookieStore cookieStore = new BasicCookieStore();
        HttpClientBuilder builder = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .setRedirectStrategy(new LaxRedirectStrategy());//利用LaxRedirectStrategy处理POST重定向问题

        CloseableHttpClient client = builder.build();

        HttpGet index = new HttpGet(INDEX);
        CloseableHttpResponse indexResponse = client.execute(index);
        int indexCode = indexResponse.getStatusLine().getStatusCode();
        System.out.println("indexCode==" + indexCode); //返回码

        HttpPost loginPost = new HttpPost(LOGIN);
        List<NameValuePair> pairList = new ArrayList<>(5);
        NameValuePair pair1 = new BasicNameValuePair("j_username","37B70.dfc");
        NameValuePair pair2 = new BasicNameValuePair("j_password","A7KXP4n95QUgCAR3vmakYA==");
        NameValuePair pair3 = new BasicNameValuePair("j_checkcode","验证码");
        NameValuePair pair4 = new BasicNameValuePair("op","login");
        NameValuePair pair5 = new BasicNameValuePair("spAuthChainCode","cc2fdbc3599b48a69d5c82a665256b6b");
        pairList.add(pair1);
        pairList.add(pair2);
        pairList.add(pair3);
        pairList.add(pair4);
        pairList.add(pair5);
        HttpEntity httpEntity = new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8"));
        loginPost.setEntity(httpEntity);
        CloseableHttpResponse loginResponse = client.execute(loginPost);
        int loginCode = loginResponse.getStatusLine().getStatusCode();
        System.out.println(loginCode);
        System.out.println("login返回报文"+EntityUtils.toString(loginResponse.getEntity(), "UTF-8"));


        HttpPost passwordPort = new HttpPost(PASSWORD);

        List<NameValuePair> passwordList = new ArrayList<>(6);
        NameValuePair passwordPair1 = new BasicNameValuePair("opForRemindPwd","remindpassword");
        NameValuePair passwordPair2 = new BasicNameValuePair("remind_j_authMethodID","");
        NameValuePair passwordPair3 = new BasicNameValuePair("remind_j_username","37B70.dfc");
        NameValuePair passwordPair4 = new BasicNameValuePair("remind_show_username","37b70.dfc");
        NameValuePair passwordPair5 = new BasicNameValuePair("remind_servletPath","/authcenter/ActionAuthChain");
        NameValuePair passwordPair6 = new BasicNameValuePair("checkbox_remind","on");
        passwordList.add(passwordPair1);
        passwordList.add(passwordPair2);
        passwordList.add(passwordPair3);
        passwordList.add(passwordPair4);
        passwordList.add(passwordPair5);
        passwordList.add(passwordPair6);
        HttpEntity passwordEntity = new UrlEncodedFormEntity(passwordList, Charset.forName("UTF-8"));
        passwordPort.setEntity(passwordEntity);
        CloseableHttpResponse passwordResponse = client.execute(passwordPort);

        int passwordCode = passwordResponse.getStatusLine().getStatusCode();
        System.out.println(passwordCode);
        System.out.println("password返回报文"+EntityUtils.toString(passwordResponse.getEntity(), "UTF-8"));

        CloseableHttpResponse login2Response = client.execute(loginPost);
        int login2Code = login2Response.getStatusLine().getStatusCode();
        System.out.println(login2Code);
        System.out.println("login返回报文"+EntityUtils.toString(login2Response.getEntity(), "UTF-8"));

        HttpPost enginePost = new HttpPost(AUTHN_ENGINE);
        HttpEntity engineEntity = new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8"));
        enginePost.setEntity(engineEntity);
        CloseableHttpResponse engineResponse = client.execute(enginePost);
        int engineCode = engineResponse.getStatusLine().getStatusCode();
        System.out.println(engineCode);
        String responseContent = EntityUtils.toString(engineResponse.getEntity(), "UTF-8");
        System.out.println(responseContent);
        if (!responseContent.contains("returnUrl")) {
            System.out.println("响应异常"+EntityUtils.toString(engineResponse.getEntity(), "UTF-8"));
        }/*
        String url = responseContent.substring(responseContent.indexOf("http://dportal"), responseContent.indexOf("<"));
        System.out.println("url = " + url);*/

        for (Cookie cookie : cookieStore.getCookies()) {
            System.out.println("k = " + cookie.getName()+",v="+cookie.getValue());
            if("LtpaToken".equals(cookie.getName())){
                String SHARE_POINT_URL = String.format(SHARE_POINT, cookie.getValue());
                HttpGet sharePoint = new HttpGet(SHARE_POINT_URL);
                CloseableHttpResponse sharePointResponse = client.execute(sharePoint);
                int sharePointCode = sharePointResponse.getStatusLine().getStatusCode();
                System.out.println(sharePointCode);
                break;
            }
        }


        for (Cookie cookie : cookieStore.getCookies()) {
            System.out.println("k = " + cookie.getName()+",v="+cookie.getValue());
        }

        HttpGet loginBOP = new HttpGet(LOGIN_BOP);
        CloseableHttpResponse loginBOPResponse = client.execute(loginBOP);
        int loginBOPCode = loginBOPResponse.getStatusLine().getStatusCode();
        System.out.println(loginBOPCode);
        System.out.println("loginBOPResponse响应"+EntityUtils.toString(loginBOPResponse.getEntity(), "UTF-8"));

        HttpGet taskType = new HttpGet(GET_TASK_TYPE);
        CloseableHttpResponse taskTypeResponse = client.execute(taskType);
        int taskTypeCode = taskTypeResponse.getStatusLine().getStatusCode();
        System.out.println(taskTypeCode);
        System.out.println("taskTypeResponse响应"+EntityUtils.toString(taskTypeResponse.getEntity(), "UTF-8"));



    }


}
