package com.caiyi.financial.nirvana.batch.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import com.caiyi.financial.nirvana.batch.service.UpdateSpePreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhukai on 2016/12/1.
 */
public class SpePreQueryActor extends UntypedActor {
    public Logger logger = LoggerFactory.getLogger(getClass());
    UpdateSpePreService upateSpePreService;

    public SpePreQueryActor(UpdateSpePreService upateSpePreService) {
        this.upateSpePreService = upateSpePreService;
    }

    public static Props props(final UpdateSpePreService upateSpePreService) {
        return Props.create(new Creator<SpePreQueryActor>() {
            private static final long serialVersionUID = 1L;

            @Override
            public SpePreQueryActor create() throws Exception {
                return new SpePreQueryActor(upateSpePreService);
            }
        });
    }

    @Override
    public void onReceive(Object idAndUr) throws Exception {
        Map<String, String> idAndUrl = (Map<String, String>) idAndUr;
        System.out.println(idAndUrl);
        String url = getUrl(idAndUrl);
        String id = getId(idAndUrl);
        String contactId = "-1";
        String summary = "-1";
        //如果没有查询到URl 则停止线程。否则根据url匹配出contactId
        if ("-1".equals(url) || url == null) {
            context().stop(self());
        } else {
            contactId = getIcontactId(url);
        }
        //如果没有查询到icontactid 则停止线程。否则查询summary
        if ("-1".equals(contactId) || contactId == null) {
            context().stop(self());
        } else {
            summary = GetSummary(contactId);
        }

        if (summary.equals("-1")||summary==null) {
            context().stop(self());
        } else {
            ActorRef actorRef = context().actorOf(SpePreUpdateActor.props(upateSpePreService));
            actorRef.tell(id + ":" + summary, self());
        }

    }

    /***
     * 获取URl
     */
    private String getUrl(Map<String, String> idAndUrl) {

        String url = "-1";
        url = (String)idAndUrl.get("CURL");
        return url;
    }

    /***
     * 获取id
     */
    private String getId(Map<String, String> idAndUrl) {
        Object id = "-1";
        id=idAndUrl.get("ID");
        return id.toString();
    }

    /**
     * 用正则表达式截取URl字符串获得通过icontactid。
     */
    private String getIcontactId(String url) {
        String contactId = "-1";
        String regexPattern = "/(\\d*).htm";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            contactId = matcher.group(1);
            System.out.println(contactId);
        }
        return contactId;
    }
    /**
     * 根据icontactid获取SUMMARY
     */
    private String GetSummary(String contactId) {
        String summary = "-1";
        try {
            List<String> summarys = upateSpePreService.query(contactId);
            if (summarys != null && summarys.size() > 0) {
                summary = summarys.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return summary;
        }
    }
}
