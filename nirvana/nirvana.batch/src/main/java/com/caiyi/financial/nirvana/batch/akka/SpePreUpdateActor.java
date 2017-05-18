package com.caiyi.financial.nirvana.batch.akka;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import com.caiyi.financial.nirvana.batch.service.UpdateSpePreService;
import com.util.string.StringUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by zhukai on 16/10/21.
 */
public class SpePreUpdateActor extends UntypedActor {
    UpdateSpePreService upateSpePreService;

    public SpePreUpdateActor(UpdateSpePreService upateSpePreService) {
        this.upateSpePreService = upateSpePreService;
    }

    public static Props props(final UpdateSpePreService upateSpePreService) {
        return Props.create(new Creator<SpePreUpdateActor>() {
            private static final long serialVersionUID = 1L;
            @Override
            public SpePreUpdateActor create() throws Exception {
                return new SpePreUpdateActor(upateSpePreService);
            }
        });
    }
    @Override
    public void onReceive(Object message) {
        String msg = (String) message;
        if (StringUtil.isNotEmpty(msg) && msg.contains(":")) {
            String preferentialId = msg.split(":")[0];
            String content = msg.split(":")[1];
            System.out.println("preferentialId:"+preferentialId+"   content:"+content);
            int result = upateSpePreService.update(content, preferentialId);
            System.out.println(result == 1 ? "更新成功" : "更新失败");
        }
        System.out.println("update over: " + now());
        context().stop(self());
        context().stop(sender());
    }

    private String now() {
        LocalDateTime now = LocalDateTime.now();
        String result = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return result;
    }
}
