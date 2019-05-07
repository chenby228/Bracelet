package kpy.chat.netty.star.client;


import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.HashedWheelTimer;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kpy.chat.ChatActivity;
import kpy.chat.PersonChat;
import kpy.chat.netty.star.client.factory.MessageFactory;
import kpy.chat.netty.star.client.listener.OnMsgReceivedListener;

import static kpy.chat.ChatActivity.chatAdapter;
import static kpy.chat.ChatActivity.personChats;

/**
 * Created by chenby on 2019/3/15.
 * <p>
 * <p>
 * * clientMsg --> code :0  info :建立连接失败  detail:
 * *               code :1  info :建立连接成功  detail:
 * *               code :2  info :发送消息失败  detail:
 * *               code :3  info :主动断开连接  detail:
 * *               code :4  info :网络断开重连  detail:
 * *               code :5  info :发送消息成功  detail:
 * *               code :6  info :发送消息超时  detail:
 * *               code :7  info :注册返回成功  detail:
 * *               code :8  info :推送消息返回  detail:
 */
@ChannelHandler.Sharable
public abstract class ChatClient extends Activity {
    private static SocketChannel socketChannel = null;
    private final static InternalLogger log = InternalLoggerFactory.getInstance(ChatClient.class);
    private final HashedWheelTimer timer = new HashedWheelTimer();
    private ConnectorIdleStateTrigger idleStateTrigger;
    private int count = 0;
    private static Bootstrap bootstrap;
    private static int oldValue;
    private LinkedList<String> controlList = new LinkedList<>();
    private String currentMsgId = "";
    private String failMsgId = "";
    private HashMap<String, String> sendMap = new HashMap<>();
    private boolean isReceivedHeartbeat = false;
    private String userId;
    private final static String HOST = "192.168.1.190";
    private final static int PORT = 12321;

    public ChatClient() {
    }

    /**
     * 设置接收心跳log
     *
     * @param receivedHeartbeat true / false
     */
    public void setReceivedHeartbeat(boolean receivedHeartbeat) {
        isReceivedHeartbeat = receivedHeartbeat;
    }

    /**
     * 初始化连接
     */
    private void init() {

        idleStateTrigger = new ConnectorIdleStateTrigger();
        if (!isSessionAvailable()) {
            EventLoopGroup group = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            try {
                bootstrap.group(group)
                        .channel(NioSocketChannel.class)
                        .remoteAddress(new InetSocketAddress(HOST, PORT))
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new LoggingHandler(LogLevel.INFO));
            } catch (Exception e) {
                System.out.println("连接异常" + e.toString());
            }
        } else {
            clientMsg("1", "客户端与服务器连接失败", "客户端与服务器已建立连接！");
        }
    }

    /**
     *
     */
    public void createSession(String userId) {
        this.userId = userId;

        if (isSessionAvailable()){
            destroySession();
        }
        init();
        //重连检测
        final ConnectionWatchdog watchdog = new ConnectionWatchdog(bootstrap, timer, PORT, HOST) {
            @Override
            public ChannelHandler[] handlers() {
                ByteBuf delimiter = Unpooled.copiedBuffer("\r\n".getBytes());
                return new ChannelHandler[]{
                        this,
                        new DelimiterBasedFrameDecoder(4096, delimiter),
                        new LoggingHandler(LogLevel.DEBUG),
                        new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS),
                        new StringDecoder(CharsetUtil.UTF_8),
                        new StringEncoder(),
                        new ClientHandler(),
                        idleStateTrigger
                };
            }
        };

        ChannelFuture future;
        try {
            synchronized (bootstrap) {
                bootstrap.handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(watchdog.handlers());
                    }
                });
                future = bootstrap.connect(HOST, PORT);
            }
            socketChannel = (SocketChannel) future.channel();
        } catch (Exception e) {
            log.info("connects fail!" + e.toString());
        }
    }

    /**
     * 连接状态
     *
     * @return
     */
    public boolean isSessionAvailable() {
        if (socketChannel == null) {
            return false;
        } else {
            return socketChannel.isOpen() && socketChannel.isActive();
        }
    }

    /**
     * 向服务端发送数据
     *
     * @param request 请求数据
     */
    public void sendMessage(String request) {
        if (!isSessionAvailable()) {
            this.clientMsg("2", "发送消息失败", "客户端与服务器端连接不可用");
        } else {
            try {
                JSONObject message = new JSONObject(request);
                String msgType = message.optJSONObject("head").optString("msgType");
//                if ("send".equals(msgType)) {
////                控制消息发送
//                    controlList.add(request);
//                    controlSendMsg(controlList.size());
//                } else {
//                其他消息发送
                    String sendMsgId = message.optJSONObject("head").optString("msgId");
                    sendMap.put(sendMsgId, request);
                    socketChannel.writeAndFlush(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(request + "\r\n", CharsetUtil.UTF_8)).duplicate());
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        Log.d("sendMsgId", sendMsgId);
                        if (sendMap.containsKey(sendMsgId)) {
                            sendMap.remove(sendMsgId);
                            clientMsg("6", "发送消息超时", "");
                        }
                    }, 10000);

//                }
            } catch (Exception e) {
                clientMsg("2", "发送消息失败", e.toString());
            }
            //消息回调
            msgSent(request);
        }
    }

    /**
     * 控制消息发送
     *
     * @param newValue
     * @throws JSONException
     */
    private void controlSendMsg(int newValue) throws JSONException {
        log.info("oldValue = " + oldValue + ", newValue = " + newValue);
        if (newValue == 1 || newValue > 1 && newValue < oldValue) {
            JSONObject message = new JSONObject(controlList.getFirst());

            currentMsgId = message.optJSONObject("head").optString("msgId");
            countDownTimer.start();
            socketChannel.writeAndFlush(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(controlList.getFirst() + "\r\n", CharsetUtil.UTF_8)).duplicate());
            oldValue--;
        }
        oldValue = newValue;
    }

    /**
     * 消息发送控制器
     */
    private CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
        @Override
        public void onFinish() {
            if (controlList.size() > 0) {
                try {
                    controlList.removeFirst();
                    controlSendMsg(controlList.size());
                    failMsgId = currentMsgId;
                    clientMsg("6", "发送消息超时", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onTick(long l) {

        }
    };


    /**
     * 断开客户端与服务器连接连接
     */
    public void destroySession() {
        if (isSessionAvailable()) {
            socketChannel.close();
            socketChannel = null;
            idleStateTrigger = null;
            controlList.clear();
            bootstrap = null;
            clientMsg("3", "主动断开连接", "");
        }
    }

    /**
     * 已发送的消息，客户端发送消息时回调
     *
     * @param msg
     */
    public abstract void msgSent(String msg);

    public abstract void msgReceived(String msg);

    /**
     * 异常代码和描述，客户端出现异常时回调
     *
     * @param code
     * @param info
     * @param detail
     */
    public abstract void clientMsg(String code, String info, String detail);

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    @ChannelHandler.Sharable
    private class ClientHandler extends ChannelInboundHandlerAdapter {
        /**
         * @param ctx
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            clientMsg("1", "网络成功建立连接", "");

            log.info("IP : " + socketChannel.remoteAddress().getAddress() + ",  port : " + socketChannel.remoteAddress().getPort());
            log.info("IP : " + socketChannel.localAddress().getAddress() + ",  port : " + socketChannel.localAddress().getPort());

            msgSent(createRegisterMessage());
            ctx.writeAndFlush(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(createRegisterMessage()+ "\r\n", CharsetUtil.UTF_8)).duplicate());
            super.channelActive(ctx);
        }

        /**
         * @param ctx
         * @param msg
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("respond msg :" + msg);



            if ("HB".equals(msg.toString())) {
                //默认不打印HB信息
                if (isReceivedHeartbeat) {
                    log.info("The server responds to the client heartbeat：" + msg.toString());
                }
            } else {

                String msgt = new JSONObject((String) msg).optJSONObject("head").optString("msgType");
                if (!msgt.equals("sendTo")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("处理接收消息");
                            try {
                                PersonChat personChat = new PersonChat();
                                personChat.setMeSend(false);
                                personChat.setChatMessage(new JSONObject((String) msg).optJSONObject("body").optString("value"));
                                personChats.add(personChat);
                                chatAdapter.notifyDataSetChanged();
                                chatAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                JSONObject message = new JSONObject(msg.toString());
                String msgType = message.optJSONObject("head").optString("msgType");
                String sendMsgId = message.optJSONObject("head").optString("msgId");
                // 控制消息十秒内未收到结果，十秒后成功接受，不返回
                if ("send".equals(msgType) && failMsgId.equals(sendMsgId)) {
                    return;
                } else if ("send".equals(msgType) && currentMsgId.equals(sendMsgId)) {
                    // 控制消息接受成功返回
                    clientMsg("5", "发送消息成功", "");
                    countDownTimer.cancel();
                    if (controlList.size() > 0) {
                        controlList.removeFirst();
                        controlSendMsg(controlList.size());
                    }
                    msgReceived(msg.toString());
                    msgReceivedListener.msgReceived(msg.toString());
                } else if (sendMap.containsKey(sendMsgId)) {
                    //其他消息接受成功返回
                    clientMsg("5", "发送消息成功", "");
                    sendMap.remove(sendMsgId);
                    msgReceived(msg.toString());
                    msgReceivedListener.msgReceived(msg.toString());
                } else if ("register".equals(msgType)) {
                    clientMsg("7", "注册信息返回成功", "");
                    msgReceived(msg.toString());
                } else {
                    //推送消息接受成功返回
                    clientMsg("8", "推送消息返回", "");
                    msgReceived(msg.toString());
                    msgReceivedListener.msgReceived(msg.toString());
                }
            }
            ReferenceCountUtil.release(msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.info("channel异常: " + cause.fillInStackTrace().toString());

        }
    }


    private OnMsgReceivedListener msgReceivedListener;

    /**
     * 监听器：监听接收数据
     *
     * @param listener
     */
    public void setOnMsgReceivedListener(OnMsgReceivedListener listener) {
        this.msgReceivedListener = listener;
    }

    /**
     * 注册信息
     *
     * @return
     * @throws JSONException
     */
    public String createRegisterMessage() {
        return MessageFactory.createRegisterMessage(userId);
    }

    public String createSendMessage(String toUserId, String sendMsgValue){
        return MessageFactory.createSendMessage(userId, toUserId, sendMsgValue);
    }
    @ChannelHandler.Sharable
    private abstract class ConnectionWatchdog extends ChannelInboundHandlerAdapter implements TimerTask, ChannelHandlerHolder {

        private final Bootstrap bootstrap;

        private Timer timer;

        private int port;

        private String host;


        ConnectionWatchdog(Bootstrap bootstrap, Timer timer, int port, String host) {
            this.bootstrap = bootstrap;
            this.timer = timer;
            this.port = port;
            this.host = host;

        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            count = 0;
            ctx.fireChannelActive();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {

            if (count == 0) {
                clientMsg("4", "网络断开,正在进行重连", "");
                count++;
            }
            int timeout = 1000;
            timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
            ctx.fireChannelInactive();
        }

        @Override
        public void run(Timeout timeout) {
            ChannelFuture future;
            synchronized (bootstrap) {
                bootstrap.handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(handlers());
                    }
                });
                future = bootstrap.connect(host, port);
            }
            future.addListener((ChannelFutureListener) future1 -> {
                boolean succeed = future1.isSuccess();
                if (!succeed) {
                    future1.channel().pipeline().fireChannelInactive();
                } else {
                    socketChannel = (SocketChannel) future1.channel();
                }
            });
        }
    }

    interface ChannelHandlerHolder {
        ChannelHandler[] handlers();
    }

    @ChannelHandler.Sharable
    class ConnectorIdleStateTrigger extends ChannelInboundHandlerAdapter {
        private final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("HB" + "\r\n", CharsetUtil.UTF_8));

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleState state = ((IdleStateEvent) evt).state();
                if (state == IdleState.WRITER_IDLE) {
                    ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
                }
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }
}

