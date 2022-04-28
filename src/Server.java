import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Server {
    public static void main(String[] args) throws IOException {
        //  Занимаемпорт, определяя серверныйсокет
        final ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress("localhost", 23334));
        while (true) {
            //  Ждем подключения клиента и получаем потоки для дальнейшей работы
            try (SocketChannel socketChannel = serverChannel.accept()) {
                //  Определяембуфердляполученияданных
                final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
                while (socketChannel.isConnected()) {
                    //  читаемданныеизканалавбуфер
                    int bytesCount = socketChannel.read(inputBuffer);
                    //  если из потока читать нельзя, перестаем работать с этим клиентом
                    if
                    (bytesCount == -1){
                        break;
                    }
                    //  получаемпереданнуюотклиентастрокувнужнойкодировкеиочищаембуфер
                    final String msg = new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8)
                            .trim()
                            .replaceAll("\\s","");
                    inputBuffer.clear();
                    System.out.println("Получено сообщение от клиента: " + msg);
                    //  отправляем сообщение клиента назад с пометкой ЭХО
                    socketChannel.write(ByteBuffer.wrap(("Текст без пробелов: \n" + msg).getBytes(StandardCharsets.UTF_8)));
                }
            } catch (IOException err) {
                System.out.println(err.getMessage());
            }
        }
    }
}