import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class ServerMainHandler extends ChannelInboundHandlerAdapter {

    private String userId;

    ServerMainHandler(String userId) {
        this.userId = userId;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Клиент подключился");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        try {
            ctx.writeAndFlush(new AuthMessage());
            if (message == null) {
                return;
            }
            System.out.println(message.getClass());
            if (message instanceof DownloadRequest) {
                DownloadRequest downloadRequest = (DownloadRequest) message;
                if (Files.exists(Paths.get("server_storage/" + downloadRequest.getFilename()))) {
                    FileMessage fileMessage = new FileMessage(Paths.get("server_storage/" + downloadRequest.getFilename()));
                    ctx.writeAndFlush(fileMessage);
                }
            }
            if (message instanceof DeleteRequest) {
                DeleteRequest deleteRequest = (DeleteRequest) message;
                Files.delete(Paths.get("server_storage/" + deleteRequest.getFilename()));
                refreshServerListView(ctx);
            }
            if (message instanceof FileMessage) {
                FileMessage fileMessage = (FileMessage) message;
                Files.write(Paths.get("server_storage/" + fileMessage.getFilename()), fileMessage.getData(), StandardOpenOption.CREATE); //StandardOpenOption.CREATE всегда оздаёт/перезаписывает новые объекты
                refreshServerListView(ctx);
            }
            if (message instanceof RefreshServerMessage) {
                refreshServerListView(ctx);
            }
        } finally {
            ReferenceCountUtil.release(message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void refreshServerListView(ChannelHandlerContext ctx) {
        try {
            ArrayList<String> serverFileList = new ArrayList<>();
            Files.list(Paths.get("server_storage")).map(p -> p.getFileName().toString()).forEach(serverFileList::add);
            ctx.writeAndFlush(new RefreshServerMessage(serverFileList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
