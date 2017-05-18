
<!DOCTYPE HTML>
<html lang="zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <base href="">
    <title>My WebSocket</title>
</head>

<body>
Welcome<br/>
<input id="text" type="text" /><button onclick="send()">Send</button>    <button onclick="closeWebSocket()">Close</button>
<div id="message">
</div>
<img width="40" height="30" id="img" src="" />

</body>

<script type="text/javascript">

    function GetQueryString(name)
    {
        var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if(r!=null)return  unescape(r[2]); return null;
    }



    var websocket = null;
//    var cuserid = GetQueryString("cuserid");
    var accessToken = GetQueryString("accessToken");
    var appId = GetQueryString("appId");
    //判断当前浏览器是否支持WebSocket cuserid="+cuserid+"&
    if('WebSocket' in window){
        //'ws://' + window.location.host +
//        websocket = new WebSocket("ws://192.168.1.76/notcontrol/bankbill.go?accessToken="+encodeURI(accessToken)+"&appId="+encodeURI(appId));
        websocket = new WebSocket('ws://' + window.location.host +"/notcontrol/bankbill.go?accessToken="+encodeURI(accessToken)+"&appId="+encodeURI(appId));
//        websocket = new WebSocket('ws://' + window.location.host +"/rest/websocket?accessToken="+encodeURI(accessToken)+"&appId="+encodeURI(appId));
//        websocket = new WebSocket('ws://' + window.location.host +"/rest/websocket?accessToken="+encodeURIComponent(accessToken)+"&appId="+encodeURIComponent(appId));


    }else{
        alert('Not support websocket')
    }

    //连接发生错误的回调方法
    websocket.onerror = function(){
        setMessageInnerHTML("error");
    };

    //连接成功建立的回调方法
    websocket.onopen = function(event){
        setMessageInnerHTML("open");
    }

    //接收到消息的回调方法
    websocket.onmessage = function(result){
//        var obj = eval(event.data.toString);
//        var jb='{method:"0",bankId:"21",cuserid:"yrk"}';
        var json=result.data+"";
        var obj = eval('('+json+')');
        if(obj.code==0&&obj.method!='result'){
            alert(obj.desc+"");
            return;
        }

        if(obj.method=='extracode'){
            document.getElementById('img').setAttribute("src","data:image/jpg;base64,"+obj.imgcode);
            alert(obj.flag+"-"+obj.desc);
        }else if(obj.method=='task'){
//            alert("已生成任务taskid="+obj.desc);
            setMessageInnerHTML("已生成任务taskid="+obj.taskId);
        }else if(obj.method=='result'){
//            alert("taskid["+obj.itaskid+"]进度["+obj.desc+"]");
            setMessageInnerHTML("taskid["+obj.taskId+"]进度["+obj.desc+"]");
        }else{
            setMessageInnerHTML(obj.desc);
        }
//
    }

    //连接关闭的回调方法
    websocket.onclose = function(){
        setMessageInnerHTML("close");
    }

    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function(){
        websocket.close();
    }

    //将消息显示在网页上
    function setMessageInnerHTML(innerHTML){
        document.getElementById('message').innerHTML += innerHTML + '<br/>';
    }

    //关闭连接
    function closeWebSocket(){
        websocket.close();
    }

    //发送消息
    function send(){
        var message = document.getElementById('text').value;
        websocket.send(message);
    }
</script>
</html>