package com.caiyi.financial.nirvana.batch.akka;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import com.caiyi.financial.nirvana.batch.service.UpdateService;
import com.util.string.StringUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by lichuanshun on 16/10/21.
 */
public class UpdateActor extends UntypedActor {
    UpdateService updateService;

    public UpdateActor(UpdateService updateService) {
        this.updateService = updateService;
    }

    public static Props props(final UpdateService updateService) {
        return Props.create(new Creator<UpdateActor>() {
            private static final long serialVersionUID = 1L;

            @Override
            public UpdateActor create() throws Exception {
                return new UpdateActor(updateService);
            }
        });
    }
    @Override
    public void onReceive(Object message)   {
        String msg = (String)message;
//        System.out.println("updating ......... " + msg);
        if (StringUtil.isNotEmpty(msg) && msg.contains(":")){
            String phone = msg.split(":")[0];
            String areaId = msg.split(":")[1];
            updateService.updateUser(areaId,phone);
        }
        System.out.println("update over: " + now());
        context().stop(self());
        context().stop(sender());
    }

    private String now(){
        LocalDateTime now = LocalDateTime.now();
        String result = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return result ;
    }
}
