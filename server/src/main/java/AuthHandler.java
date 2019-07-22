import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class AuthHandler extends ChannelInboundHandlerAdapter {
    private boolean authOk = false;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {

        try {
            if (authOk) {
                ctx.fireChannelRead(message);
                return;
            }
            System.out.println(message.getClass());
            if (message instanceof AuthMessage) {
                AuthMessage authMessage = (AuthMessage) message;
                String userId = DataBaseService.getIdByLoginAndPass(authMessage.login, authMessage.password);
                if (userId != null) {
                    authOk = true;
                    ctx.pipeline().addLast(new ServerMainHandler(userId));
                    ctx.writeAndFlush(new AuthMessage());
                }
            }
        } finally {
            ReferenceCountUtil.release(message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}
