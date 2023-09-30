package net.halozy;

import net.minecraft.client.Minecraft;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Protection implements Hook, Native {
    
    public String getHWID() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String s = "";
        final String main = String.valueOf(System.getenv("PROCESSOR_IDENTIFIER")) + System.getenv("COMPUTERNAME") + System.getProperty("user.name").trim();
        final byte[] bytes = main.getBytes("UTF-8");
        final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        final byte[] md5 = messageDigest.digest(bytes);
        int i = 0;
        byte[] array;
        for (int length = (array = md5).length, j = 0; j < length; ++j) {
            final byte b = array[j];
            s = String.valueOf(s) + Integer.toHexString((b & 0xFF) | 0x300).substring(0, 3);
            if (i != md5.length - 1) {
                s = String.valueOf(s) + "-";
            }
            ++i;
        }
        return s;
    }

    @Override
    public String getSource() {
        String lol = "";

        try {
            Document doc = Jsoup.connect("https://biceygay.000webhostapp.com/").userAgent("Mozilla/17.0").get();
            lol = doc.body().toString();
            return lol;
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return lol;
    }

    public class ok {
    }

	@Override
	public void hook() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		String nig = getSource();

        nig = nig.replace("<body> \n", "");
        nig = nig.replace(" <div style=\"text-align: right;position: fixed;z-index:9999999;bottom: 0;width: auto;right: 1%;cursor: pointer;line-height: 0;display:block !important;\">\n"
                + "  <a title=\"Hosted on free web hosting 000webhost.com. Host your own website for FREE.\" target=\"_blank\" href=\"https://www.000webhost.com/?utm_source=000webhostapp&amp;utm_campaign=000_logo&amp;utm_medium=website&amp;utm_content=footer_img\"><img src=\"https://cdn.000webhost.com/000webhost/logo/footer-powered-by-000webhost-white2.png\" alt=\"www.000webhost.com\"></a>\n"
                + " </div>\n"
                + " <script>function getCookie(t){for(var e=t+\"=\",n=decodeURIComponent(document.cookie).split(\";\"),o=0;o<n.length;o++){for(var i=n[o];\" \"==i.charAt(0);)i=i.substring(1);if(0==i.indexOf(e))return i.substring(e.length,i.length)}return\"\"}getCookie(\"hostinger\")&&(document.cookie=\"hostinger=;expires=Thu, 01 Jan 1970 00:00:01 GMT;\",location.reload());var wordpressAdminBody=document.getElementsByClassName(\"wp-admin\")[0],notification=document.getElementsByClassName(\"notice notice-success is-dismissible\"),hostingerLogo=document.getElementsByClassName(\"hlogo\"),mainContent=document.getElementsByClassName(\"notice_content\")[0];if(null!=wordpressAdminBody&&notification.length>0&&null!=mainContent){var googleFont=document.createElement(\"link\");googleFontHref=document.createAttribute(\"href\"),googleFontRel=document.createAttribute(\"rel\"),googleFontHref.value=\"https://fonts.googleapis.com/css?family=Roboto:300,400,600,700\",googleFontRel.value=\"stylesheet\",googleFont.setAttributeNode(googleFontHref),googleFont.setAttributeNode(googleFontRel);var css=\"@media only screen and (max-width: 576px) {#main_content {max-width: 320px !important;} #main_content h1 {font-size: 30px !important;} #main_content h2 {font-size: 40px !important; margin: 20px 0 !important;} #main_content p {font-size: 14px !important;} #main_content .content-wrapper {text-align: center !important;}} @media only screen and (max-width: 781px) {#main_content {margin: auto; justify-content: center; max-width: 445px;}} @media only screen and (max-width: 1325px) {.web-hosting-90-off-image-wrapper {position: absolute; max-width: 95% !important;} .notice_content {justify-content: center;} .web-hosting-90-off-image {opacity: 0.3;}} @media only screen and (min-width: 769px) {.notice_content {justify-content: space-between;} #main_content {margin-left: 5%; max-width: 445px;} .web-hosting-90-off-image-wrapper {position: absolute; display: flex; justify-content: center; width: 100%; }} .web-hosting-90-off-image {max-width: 90%;} .content-wrapper {min-height: 454px; display: flex; flex-direction: column; justify-content: center; z-index: 5} .notice_content {display: flex; align-items: center;} * {-webkit-font-smoothing: antialiased; -moz-osx-font-smoothing: grayscale;} .upgrade_button_red_sale{box-shadow: 0 2px 4px 0 rgba(255, 69, 70, 0.2); max-width: 350px; border: 0; border-radius: 3px; background-color: #ff4546 !important; padding: 15px 55px !important; font-family: 'Roboto', sans-serif; font-size: 16px; font-weight: 600; color: #ffffff;} .upgrade_button_red_sale:hover{color: #ffffff !important; background: #d10303 !important;}\",style=document.createElement(\"style\"),sheet=window.document.styleSheets[0];style.styleSheet?style.styleSheet.cssText=css:style.appendChild(document.createTextNode(css)),document.getElementsByTagName(\"head\")[0].appendChild(style),document.getElementsByTagName(\"head\")[0].appendChild(googleFont);var button=document.getElementsByClassName(\"upgrade_button_red\")[0],link=button.parentElement;link.setAttribute(\"href\",\"https://www.hostinger.com/hosting-starter-offer?utm_source=000webhost&utm_medium=panel&utm_campaign=000-wp\"),link.innerHTML='<button class=\"upgrade_button_red_sale\">Go for it</button>',(notification=notification[0]).setAttribute(\"style\",\"padding-bottom: 0; padding-top: 5px; background-color: #040713; background-size: cover; background-repeat: no-repeat; color: #ffffff; border-left-color: #040713;\"),notification.className=\"notice notice-error is-dismissible\";var mainContentHolder=document.getElementById(\"main_content\");mainContentHolder.setAttribute(\"style\",\"padding: 0;\"),hostingerLogo[0].remove();var h1Tag=notification.getElementsByTagName(\"H1\")[0];h1Tag.className=\"000-h1\",h1Tag.innerHTML=\"Black Friday Prices\",h1Tag.setAttribute(\"style\",'color: white; font-family: \"Roboto\", sans-serif; font-size: 22px; font-weight: 700; text-transform: uppercase;');var h2Tag=document.createElement(\"H2\");h2Tag.innerHTML=\"Get 90% Off!\",h2Tag.setAttribute(\"style\",'color: white; margin: 10px 0 15px 0; font-family: \"Roboto\", sans-serif; font-size: 60px; font-weight: 700; line-height: 1;'),h1Tag.parentNode.insertBefore(h2Tag,h1Tag.nextSibling);var paragraph=notification.getElementsByTagName(\"p\")[0];paragraph.innerHTML=\"Get Web Hosting for $0.99/month + SSL Certificate for FREE!\",paragraph.setAttribute(\"style\",'font-family: \"Roboto\", sans-serif; font-size: 16px; font-weight: 700; margin-bottom: 15px;');var list=notification.getElementsByTagName(\"UL\")[0];list.remove();var org_html=mainContent.innerHTML,new_html='<div class=\"content-wrapper\">'+mainContent.innerHTML+'</div><div class=\"web-hosting-90-off-image-wrapper\"><img class=\"web-hosting-90-off-image\" src=\"https://cdn.000webhost.com/000webhost/promotions/bf-2020-wp-inject-img.png\"></div>';mainContent.innerHTML=new_html;var saleImage=mainContent.getElementsByClassName(\"web-hosting-90-off-image\")[0]}</script> \n"
                + "</body>", "");
        nig = nig.replace(" <p>", "");
        nig = nig.replace("</p>", "");
        // k
        String nigger[] = nig.split(":");
        //hwid.add(nig.split("\n"));
        ArrayList<String> hwid = new ArrayList<String>();

        boolean whitelisted = false;

        for (int i = 0; i < nigger.length; i++) { //hwid.getlength will be the hosted file
            String s = getHWID();

            String niggerv3 = nigger[i];
            System.out.println(nigger[i] + "  lul");
            System.out.println(getHWID());
            if (nigger[i].equals(getHWID())) {
                whitelisted = true;
                System.out.println("niggers");
            } else {

            }
        }

        if (!whitelisted) {
            System.exit(0);
        }
	}
}
